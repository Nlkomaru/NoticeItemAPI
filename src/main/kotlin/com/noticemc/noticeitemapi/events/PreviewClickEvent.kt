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

import com.noticemc.noticeitemapi.NoticeItem
import com.noticemc.noticeitemapi.data.ItemData
import com.noticemc.noticeitemapi.events.GuiClickEvent.Companion.addItem
import com.noticemc.noticeitemapi.utils.GuiUtils
import com.noticemc.noticeitemapi.utils.OpenGui
import com.noticemc.noticeitemapi.utils.PreviewGui
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import java.io.File
import java.nio.file.Files
import kotlin.math.ceil

class PreviewClickEvent : Listener {

    @EventHandler
    suspend fun previewClick(e: InventoryClickEvent) {
        if (e.view.title() != GuiUtils.previewGuiName) {
            return
        }

        if (e.clickedInventory?.type != InventoryType.CHEST) {
            return
        }
        e.isCancelled = true
        if (!e.isLeftClick) {
            return
        }

        val inventory = e.clickedInventory ?: return
        val player = e.whoClicked as Player
        val uuid = player.uniqueId
        val clickedSlot: Int = e.slot
        val pages = inventory.getItem(48)?.amount ?: 1
        val beforePages = inventory.getItem(49)?.amount ?: 1
        val ulid = PlainTextComponentSerializer.plainText().serialize(inventory.getItem(49)?.itemMeta?.displayName()!!)
        val file = File(File(File(NoticeItem.plugin.dataFolder, "data"), uuid.toString()), "$ulid.json")
        val itemData = withContext(Dispatchers.IO) {
            Json.decodeFromString<ItemData>(Files.readString(file.toPath()))
        }
        val maxPage = ceil(itemData.items.size.toDouble() / 45).toInt()

        when (clickedSlot) {
            45 -> {
                player.openInventory(PreviewGui.getPreviewGui(player, ulid, pages, beforePages))
            }
            46 -> {
                if (pages <= 1) {
                    inventory.setItem(46, GuiUtils.getNoticeCantItem())
                    delay(1000)
                    inventory.setItem(46, GuiUtils.getPrevItem())
                } else {
                    player.openInventory(PreviewGui.getPreviewGui(player, ulid, pages - 1, beforePages))

                }
            }
            50 -> {
                if (maxPage <= pages) {
                    inventory.setItem(50, GuiUtils.getNoticeCantItem())
                    delay(1000)
                    inventory.setItem(50, GuiUtils.getPrevItem())
                } else {
                    player.openInventory(PreviewGui.getPreviewGui(player, ulid, pages + 1, beforePages))

                }
            }
            52 -> {
                file.delete()
                player.openInventory(OpenGui.inventory(player, beforePages))
                itemData.items.forEach {
                    val item = it
                    player.addItem(item)
                }

            }
            53 -> {
                player.openInventory(OpenGui.inventory(player, beforePages))
            }

        }

    }
}