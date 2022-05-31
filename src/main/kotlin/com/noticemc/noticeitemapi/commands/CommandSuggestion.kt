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

import cloud.commandframework.annotations.suggestions.Suggestions
import cloud.commandframework.context.CommandContext
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CommandSuggestion {

    @Suggestions("playerName")
    fun suggestPlayerName(sender: CommandContext<CommandSender>, input: String?): List<String> {
        val list = ArrayList<String>()
        Bukkit.getServer().onlinePlayers.forEach {
            list.add(it.name)
        }
        return list
    }
}