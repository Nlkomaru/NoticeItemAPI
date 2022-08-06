/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi.commands

import cloud.commandframework.annotations.*
import com.noticemc.noticeitemapi.NoticeItem.Companion.plugin
import com.noticemc.noticeitemapi.api.NoticeItemAPI
import com.noticemc.noticeitemapi.utils.GuiUtils
import com.noticemc.noticeitemapi.utils.Utils.Companion.toPlainText
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CopyAllCommand {

    @CommandMethod("nia copyAll")
    @CommandPermission("noticeitemapi.command.copyall")
    fun copy(sender: CommandSender) {
        if (sender !is Player) {
            return
        }

        val list = ArrayList<OfflinePlayer>()

        plugin.server.onlinePlayers.forEach {
            list.add(it)
        }

        val plugin = Bukkit.getPluginManager().getPlugin("NoticeItem")
        val nia = plugin as NoticeItemAPI

        val item = sender.inventory.itemInMainHand
        if (!item.type.isItem || item.type.isEmpty) {
            sender.sendMessage(GuiUtils.mm.deserialize("<color:red>メインハンドに何もアイテムを持っていません"))
            return
        }

        list.forEach {
            val ulid = nia.addItem(it, item, null, null, "${sender.name}からのアイテム")

            sender.sendMessage(GuiUtils.mm.deserialize("<color:aqua>${it.name}</color:aqua>に${
                item.displayName().toPlainText()
            }を追加しました <click:run_command:'/nia remove ${it.name} ${ulid}'><color:yellow>クリックで削除</color:yellow></click>"))
        }

    }
}