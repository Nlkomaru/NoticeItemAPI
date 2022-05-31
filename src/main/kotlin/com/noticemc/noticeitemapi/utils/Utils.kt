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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

class Utils {
    //アイテムをエンコード、デコードするクラス
    companion object {

        fun ItemStack.encode(): String {
            //エンコード
            val baos = ByteArrayOutputStream()
            val boos = BukkitObjectOutputStream(baos)
            boos.writeObject(this)
            boos.flush()
            val serializedObject = baos.toByteArray()

            return Base64.getEncoder().encodeToString(serializedObject)
        }

        fun String.decode(): ItemStack {
            //デコード
            val serializedObject: ByteArray = Base64.getDecoder().decode(this)
            val bais = ByteArrayInputStream(serializedObject)
            val bois = BukkitObjectInputStream(bais)
            return bois.readObject() as ItemStack

        }

        fun Component.toPlainText(): String {
            return PlainTextComponentSerializer.plainText().serialize(this)
        }
    }
}