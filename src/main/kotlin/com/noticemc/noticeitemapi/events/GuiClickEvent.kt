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

import com.github.shynixn.mccoroutine.launch
import com.noticemc.noticeitemapi.NoticeItem.Companion.plugin
import com.noticemc.noticeitemapi.data.ItemData
import com.noticemc.noticeitemapi.utils.ChangeItemData.Companion.decode
import com.noticemc.noticeitemapi.utils.GuiUtils
import com.noticemc.noticeitemapi.utils.OpenGui
import com.noticemc.noticeitemapi.utils.OpenGui.getItemData
import com.noticemc.noticeitemapi.utils.PreviewGui
import com.noticemc.noticeitemapi.utils.coroutines.minecraft
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import java.io.File
import kotlin.math.ceil

class GuiClickEvent : Listener {

    @OptIn(ExperimentalSerializationApi::class)
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

                        lateinit var item: ItemData
                        withContext(Dispatchers.IO) {
                            item = Hocon.decodeFromConfig(ConfigFactory.parseFile(items[clickedItemNum]))
                            File(File(File(plugin.dataFolder, "data"), uuid.toString()), "${item.managementULID}.conf").delete()
                        }
                        item.items.forEach {
                            player.addItem(it.decode())
                        }
                        player.openInventory(OpenGui.inventory(player, pages))

                    }
                    45 -> {
                        player.openInventory(OpenGui.inventory(player, 1))
                    }
                    46 -> {
                        if (pages <= 1) {
                            inventory.setItem(46, GuiUtils.getNoticeCantItem())
                            delay(1000)
                            inventory.setItem(46, GuiUtils.getPrevItem())

                        } else {
                            player.openInventory(OpenGui.inventory(player, pages - 1))

                        }
                    }
                    50 -> {
                        if (maxPage <= pages) {
                            inventory.setItem(50, GuiUtils.getNoticeCantItem())
                            delay(1000)
                            inventory.setItem(50, GuiUtils.getNextItem())

                        } else {
                            player.openInventory(OpenGui.inventory(player, pages + 1))

                        }
                    }
                    51 -> {
                        val giveItems: ArrayList<ItemData> = ArrayList()

                        withContext(Dispatchers.IO) {
                            repeat(10) {
                                val items = getItemData(uuid)
                                if (items.size > 0) {
                                    val item = Hocon.decodeFromConfig<ItemData>(ConfigFactory.parseFile(items[0]))
                                    File(File(File(plugin.dataFolder, "data"), uuid.toString()), "${item.managementULID}.conf").delete()
                                    giveItems.add(item)
                                }
                            }
                        }

                        giveItems.forEach { itemData ->
                            itemData.items.forEach { binary ->
                                player.addItem(binary.decode())
                            }
                            delay(50)
                        }

                        player.openInventory(OpenGui.inventory(player, pages))

                    }
                    52 -> {
                        player.closeInventory()
                        val giveItems: ArrayList<ItemData> = ArrayList()

                        withContext(Dispatchers.IO) {
                            while (getItemData(uuid).size > 0) {
                                val items = getItemData(uuid)

                                val item = Hocon.decodeFromConfig<ItemData>(ConfigFactory.parseFile(items[0]))
                                File(File(File(plugin.dataFolder, "data"), uuid.toString()), "${item.managementULID}.conf").delete()
                                giveItems.add(item)

                            }
                        }

                        giveItems.forEach { itemData ->
                            itemData.items.forEach { binary ->
                                async(Dispatchers.minecraft) {
                                    player.addItem(binary.decode())
                                }
                            }
                        }

                    }
                    53 -> {
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

                        val item = Hocon.decodeFromConfig<ItemData>(ConfigFactory.parseFile(items[clickedItemNum]))
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