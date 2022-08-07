/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi.events

import com.github.shynixn.mccoroutine.bukkit.launch
import com.noticemc.noticeitemapi.NoticeItem.Companion.plugin
import com.noticemc.noticeitemapi.data.ItemData
import com.noticemc.noticeitemapi.utils.GuiUtils
import com.noticemc.noticeitemapi.utils.OpenGui
import com.noticemc.noticeitemapi.utils.OpenGui.getItemData
import com.noticemc.noticeitemapi.utils.PreviewGui
import com.noticemc.noticeitemapi.utils.coroutines.minecraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import java.io.File
import java.nio.file.Files
import kotlin.math.ceil

class GuiClickEvent : Listener {

    @EventHandler
    suspend fun guiLeftClickEvent(e: InventoryClickEvent) {
        plugin.launch {
            if (e.view.title() != GuiUtils.openGuiName) {
                return@launch
            }

            if (e.clickedInventory?.type != InventoryType.CHEST) {
                return@launch
            }
            e.isCancelled = true
            if (e.isShiftClick) {

                return@launch
            }

            val inventory = e.clickedInventory ?: return@launch
            val player = e.whoClicked as Player
            val uuid = player.uniqueId
            val clickedSlot: Int = e.slot
            val pages = inventory.getItem(48)?.amount ?: 1
            val clickedItemNum: Int = clickedSlot + (pages - 1) * 45
            val maxPage = ceil(getItemData(uuid).size.toDouble() / 45).toInt()

            if (e.isLeftClick) {
                when (clickedSlot) {
                    in 0..44 -> {

                        val items = getItemData(uuid)
                        if (items.size - 1 < clickedItemNum) {
                            return@launch
                        }

                        lateinit var itemData: ItemData
                        withContext(Dispatchers.IO) {
                            itemData = Json.decodeFromString(Files.readString(items[clickedItemNum].toPath()))
                            plugin.dataFolder.resolve("data").resolve(uuid.toString()).resolve("${itemData.managementULID}.json").delete()
                        }
                        itemData.items.forEach {
                            player.addItem(it)
                        }
                        player.openInventory(OpenGui.inventory(player, pages))

                    }
                    45 -> {
                        //return page no.1
                        player.openInventory(OpenGui.inventory(player, 1))
                    }
                    46 -> {
                        // before page
                        if (pages <= 1) {
                            inventory.setItem(46, GuiUtils.getNoticeCantItem())
                            delay(1000)
                            inventory.setItem(46, GuiUtils.getPrevItem())

                        } else {
                            player.openInventory(OpenGui.inventory(player, pages - 1))

                        }
                    }
                    50 -> {
                        // next page
                        if (maxPage <= pages) {
                            inventory.setItem(50, GuiUtils.getNoticeCantItem())
                            delay(1000)
                            inventory.setItem(50, GuiUtils.getNextItem())

                        } else {
                            player.openInventory(OpenGui.inventory(player, pages + 1))

                        }
                    }
                    51 -> {
                        // get 10 items
                        val giveItems: ArrayList<ItemData> = ArrayList()

                        withContext(Dispatchers.IO) {
                            repeat(10) {
                                val items = getItemData(uuid)
                                if (items.isNotEmpty()) {
                                    val item = Json.decodeFromString<ItemData>(Files.readString(items[0].toPath()))
                                    plugin.dataFolder.resolve("data").resolve(uuid.toString()).resolve("${item.managementULID}.json").delete()
                                    giveItems.add(item)
                                }
                            }
                        }

                        giveItems.forEach { itemData ->
                            itemData.items.forEach {
                                player.addItem(it)
                            }
                            delay(50)
                        }

                        player.openInventory(OpenGui.inventory(player, pages))

                    }
                    52 -> {
                        // get all items
                        player.closeInventory()
                        val giveItems: ArrayList<ItemData> = ArrayList()

                        withContext(Dispatchers.IO) {
                            while (getItemData(uuid).isNotEmpty()) {
                                val items = getItemData(uuid)

                                val item = Json.decodeFromString<ItemData>(Files.readString(items[0].toPath()))
                                plugin.dataFolder.resolve("data").resolve(uuid.toString()).resolve("${item.managementULID}.json").delete()
                                giveItems.add(item)

                            }
                        }

                        giveItems.forEach { itemData ->
                            withContext(Dispatchers.minecraft) {
                                itemData.items.forEach {
                                    player.addItem(it)
                                }
                            }
                            delay(10)
                        }

                    }
                    53 -> {
                        // close inventory
                        player.closeInventory()

                    }
                }

            } else {
                when (clickedSlot) {
                    in 0..44 -> {
                        val items = withContext(Dispatchers.IO) { getItemData(uuid) }

                        if (items.size - 1 < clickedItemNum) {
                            return@launch
                        }

                        val item = Json.decodeFromString<ItemData>(withContext(Dispatchers.IO) {
                            Files.readString(items[clickedItemNum].toPath())
                        })
                        val ulid = item.managementULID
                        player.openInventory(PreviewGui.getPreviewGui(player, ulid, 1, pages))

                    }
                }
            }
        }
    }

    companion object {
        fun Player.addItem(item: ItemStack) {
            if (player?.inventory?.firstEmpty() == -1) {
                this.world.dropItem(this.location, item)
            } else {
                player?.inventory?.addItem(item)
            }
        }
    }

}