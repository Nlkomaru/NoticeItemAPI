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

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.shynixn.mccoroutine.bukkit.launch
import com.noticemc.noticeitemapi.NoticeItem.Companion.plugin
import com.noticemc.noticeitemapi.utils.OpenGui
import com.noticemc.noticeitemapi.utils.OpenGui.purgeFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OpenCommand {

    @CommandMethod("nia open")
    @CommandPermission("noticeitemapi.command.open")
    @CommandDescription("GUIを開きます")
    fun open(sender: CommandSender) {
        plugin.launch {
            if (sender !is Player) {
                return@launch
            }
            sender.openInventory(OpenGui.inventory(sender, 1))
            async(Dispatchers.IO) { purgeFile(sender.uniqueId) }
        }
    }
}