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

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.noticemc.noticeitemapi.api.NoticeItemAPI
import com.noticemc.noticeitemapi.utils.GuiUtils.mm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RemoveCommand {
    @CommandMethod("nia remove <playerName> <ulid>")
    @CommandPermission("noticeitemapi.command.remove")
    @CommandDescription("プレイヤーから与えたアイテムを削除します")
    fun remove(sender: CommandSender,
        @Argument(value = "playerName", suggestions = "playerName") playerName: String,
        @Argument(value = "ulid") ulid: String) {
        if (sender !is Player) {
            return
        }

        val player = Bukkit.getOfflinePlayerIfCached(playerName) ?: return sender.sendMessage("そのプレイヤーは存在しません")

        val plugin = Bukkit.getPluginManager().getPlugin("NoticeItem")
        val nia = plugin as NoticeItemAPI

        val result = nia.removeItem(player, ulid)

        if (result) {
            sender.sendMessage(mm.deserialize("<color:green>${playerName}から指定したアイテムの削除が完了しました</color:green>"))
        } else {
            sender.sendMessage(mm.deserialize("<color:red>存在しなかったため削除できませんでした</color:red>"))
        }

    }
}