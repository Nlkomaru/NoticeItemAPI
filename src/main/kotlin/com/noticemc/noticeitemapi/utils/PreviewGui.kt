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

import com.noticemc.noticeitemapi.NoticeItem
import com.noticemc.noticeitemapi.data.ItemData
import com.noticemc.noticeitemapi.utils.ChangeItemData.Companion.decode
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.io.File

object PreviewGui {

    suspend fun getPreviewGui(player: Player, ulid: String, page: Int, beforePage: Int): Inventory {
        val inventory = Bukkit.createInventory(player, 54, GuiUtils.previewGuiName)
        val uuid = player.uniqueId
        val itemData = withContext(Dispatchers.IO) {
            val file = File(File(File(NoticeItem.plugin.dataFolder, "data"), uuid.toString()), "$ulid.conf")
            Hocon.decodeFromConfig<ItemData>(ConfigFactory.parseFile(file))
        }
        for (i in 0..53) {
            inventory.setItem(i, GuiUtils.getNoDataGlassItem())
        }

        val ulidGlass = GuiUtils.getNoDataGlassItem()
        val itemMeta = ulidGlass.itemMeta
        ulidGlass.amount = beforePage
        itemMeta.displayName(GuiUtils.mm.deserialize(ulid))
        ulidGlass.itemMeta = itemMeta


        inventory.setItem(45, GuiUtils.getReturnFirstItem())
        inventory.setItem(46, GuiUtils.getPrevItem())
        inventory.setItem(48, GuiUtils.getNowPage(page))
        inventory.setItem(49, ulidGlass)
        inventory.setItem(50, GuiUtils.getNextItem())
        inventory.setItem(52, GuiUtils.getItem())
        inventory.setItem(53, GuiUtils.getCloseItem())

        for (i in 0..44) {

            if (itemData.items.size - 1 < (page - 1) * 45 + i) {
                break
            }

            val item = (itemData.items[(page - 1) * 45 + i]).decode()

            inventory.setItem(i, item)
        }
        return inventory
    }
}