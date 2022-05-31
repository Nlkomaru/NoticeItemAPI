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

import com.noticemc.noticeitemapi.utils.GuiUtils.mm
import com.noticemc.noticeitemapi.utils.OpenGui
import kotlinx.coroutines.delay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class LoginPopUpEvent : Listener {

    @EventHandler
    suspend fun playerLoginEvent(event: PlayerJoinEvent) {
        val player = event.player
        OpenGui.purgeFile(player.uniqueId)

        val count = OpenGui.getItemData(player.uniqueId).size
        if (count < 1) {
            return
        }
        delay(5000)

        val component = mm.deserialize("<color:yellow>受け取ることが可能なアイテムが${count}件あります</color> <click:run_command:'/nia open'>クリックで開く</click>")
        player.sendMessage(component)
    }

}