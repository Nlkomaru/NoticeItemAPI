/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi.utils

import com.noticemc.noticeitemapi.NoticeItem.Companion.plugin
import com.noticemc.noticeitemapi.data.ItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.io.File
import java.nio.file.Files
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object OpenGui {

    suspend fun inventory(player: Player, page: Int): Inventory {
        val uuid = player.uniqueId

        val inventory = Bukkit.createInventory(player, 54, GuiUtils.openGuiName)

        val itemList: List<File> = getItemData(uuid)

        for (i in 0..53) {
            inventory.setItem(i, GuiUtils.getNoDataGlassItem())
        }

        inventory.setItem(45, GuiUtils.getReturnFirstItem())
        inventory.setItem(46, GuiUtils.getPrevItem())
        inventory.setItem(48, GuiUtils.getNowPage(page))
        inventory.setItem(50, GuiUtils.getNextItem())
        inventory.setItem(51, GuiUtils.getTenItems())
        inventory.setItem(52, GuiUtils.getAllItems())
        inventory.setItem(53, GuiUtils.getCloseItem())
        var i = 0
        while (i <= 44) {

            if (itemList.size - 1 < (page - 1) * 45 + i) {
                break
            }
            val itemData = Json.decodeFromString<ItemData>(withContext(Dispatchers.IO) {
                Files.readString(itemList[(page - 1) * 45 + i].toPath())
            })
            val limit = itemData.limit
            if (limit != null && limit.isBefore(ZonedDateTime.now())) {
                continue
            }
            val item = itemData.items[0]
            val itemMeta = item.itemMeta
            val itemLore: MutableList<Component> = ArrayList()

            val data = limit?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd日 HH:mm:ss", Locale.ENGLISH)) ?: "期限なし"
            val supplement = itemData.supplement
            val description = itemData.description ?: "説明なし"

            itemLore.add(MiniMessage.miniMessage().deserialize("<color:gold>期間: </color:gold>  <color:#32cd32> $data"))
            itemLore.add(MiniMessage.miniMessage().deserialize("<color:gold>個数: </color:gold>  <color:#32cd32> ${itemData.items.size}"))
            supplement?.forEach { (k, v) ->
                itemLore.add(MiniMessage.miniMessage().deserialize("<color:gold>$k: </color:gold>  <color:#32cd32> $v"))
            }
            itemLore.add(MiniMessage.miniMessage().deserialize("<color:gold>説明: </color:gold>  <color:#32cd32> $description"))
            itemLore.add(MiniMessage.miniMessage().deserialize("<color:#40e0d0> 左クリックで受け取る事ができます"))
            itemLore.add(MiniMessage.miniMessage().deserialize("<color:#40e0d0> 右クリックでプレビューを見ることができます"))
            itemMeta.lore(itemLore)
            item.itemMeta = itemMeta

            inventory.setItem(i, item)
            i++
        }

        return inventory
    }

    suspend fun getItemData(uuid: UUID): List<File> {
        return withContext(Dispatchers.IO) {
            File(File(plugin.dataFolder, "data"), uuid.toString()).listFiles().asList()
        }
    }

    suspend fun purgeFile(uuid: UUID) = withContext(Dispatchers.IO) {
        val file = File(File(plugin.dataFolder, "data"), uuid.toString())
        file.listFiles()?.forEach {
            val itemData = Json.decodeFromString<ItemData>(Files.readString(it.toPath()))
            val limit = itemData.limit

            if (limit?.isBefore(ZonedDateTime.now()) == true) {
                it.delete()
            }
        }
    }
}


