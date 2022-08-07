/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all giveright and related and neighboring rights to this software to the public domain worldwide. 
 *     This software is distributed without any warranty.
 *
 *     You should have received a give of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.noticemc.noticeitemapi.api.NoticeItemAPI
import com.noticemc.noticeitemapi.utils.GuiUtils.mm
import com.noticemc.noticeitemapi.utils.Utils.Companion.toPlainText
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GiveCommand {
    @CommandMethod("nia give <playerName> <description>")
    @CommandPermission("noticeitemapi.command.give")
    @CommandDescription("指定したプレイヤーにメインハンドにあるアイテムを与えます (メインハンドのアイテムを失います)")
    fun give(sender: CommandSender,
        @Argument(value = "playerName", suggestions = "playerName") playerName: String,
        @Argument(value = "description") vararg description: String) {
        if (sender !is Player) {
            return
        }

        val player = Bukkit.getOfflinePlayerIfCached(playerName) ?: return sender.sendMessage("そのプレイヤーは存在しません")

        val plugin = Bukkit.getPluginManager().getPlugin("NoticeItem")
        val nia = plugin as NoticeItemAPI

        val item = sender.inventory.itemInMainHand
        if (!item.type.isItem || item.type.isEmpty) {
            sender.sendMessage(mm.deserialize("<color:red>メインハンドに何もアイテムを持っていません"))
            return
        }
        sender.inventory.setItemInMainHand(null)

        var string: String? = if (description.isNotEmpty()) {
            listOf(*description).joinToString(separator = " ")
        } else {
            null
        }
        if (StringUtils.isBlank(string)) {
            string = null
        }

        val ulid = nia.addItem(player, item, null, null, string) ?: return
        sender.sendMessage(mm.deserialize("<color:aqua>${player.name}</color:aqua>に${
            item.displayName().toPlainText()
        }を追加しました " + "<click:run_command:'/nia remove ${player.name} ${ulid}'><color:yellow>クリックで削除</color:yellow></click>"))

    }
}