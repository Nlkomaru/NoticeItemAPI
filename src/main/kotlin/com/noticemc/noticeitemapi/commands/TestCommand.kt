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

import cloud.commandframework.annotations.CommandMethod
import com.noticemc.noticeitemapi.api.NoticeItemAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.ZonedDateTime
import kotlin.random.Random

@CommandMethod("nia")
class TestCommand {
    @CommandMethod("test")
    fun test(sender: CommandSender) {
        val offlinePlayer: OfflinePlayer = if (sender !is Player) {
            Bukkit.getOfflinePlayer("_NIKOMARU")
        } else {
            sender
        }

        val plugin = Bukkit.getPluginManager().getPlugin("NoticeItem")
        val nia = plugin as NoticeItemAPI

        repeat(10) {
            val list: ArrayList<ItemStack> = ArrayList()
            val limit = Random.nextInt(1, 100)
            var i = 0
            while (i < limit) {

                val item = ItemStack(randomEnum(Material::class.java))
                item.amount = Random.nextInt(1, 10)

                if (!item.type.isAir && !item.type.isEmpty && !item.type.isLegacy) {
                    list.add(item)
                    i++
                }
            }
            nia.addItem(offlinePlayer, list, ZonedDateTime.now().plusMinutes(10), hashMapOf("one" to "aaa", "two" to "bbb"), "test")
        }
        Bukkit.getLogger().info("test complited")
    }

    private fun <T : Enum<*>?> randomEnum(clazz: Class<T>): T {
        val x: Int = Random.Default.nextInt(clazz.enumConstants.size)
        return clazz.enumConstants[x]
    }
}