/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi.api

import org.bukkit.OfflinePlayer
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.time.ZonedDateTime

interface NoticeItemAPI {

    fun addItem(player: OfflinePlayer, item: ItemStack, limit: ZonedDateTime?, supplement: HashMap<String, String>?, description: String?): String?

    fun addItem(player: OfflinePlayer,
        items: Collection<ItemStack>,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?): String?

    fun addItem(player: OfflinePlayer,
        inventory: Inventory,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?): String?

    fun removeItem(player: OfflinePlayer, ulid: String): Boolean
}