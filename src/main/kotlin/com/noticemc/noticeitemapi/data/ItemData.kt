/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi.data

import com.noticemc.noticeitemapi.utils.Utils.Companion.decode
import com.noticemc.noticeitemapi.utils.Utils.Companion.encode
import com.noticemc.noticeitemapi.utils.GuiUtils.mm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.bukkit.inventory.ItemStack
import java.time.ZonedDateTime
import java.util.*

@Serializable
data class ItemData(val version: String,
    val managementULID: String,
    @Serializable(with = UUIDSerializer::class) val player: UUID,
    @Serializable(with = KZonedDateTimeSerializer::class) val limit: ZonedDateTime?,
    val supplement: HashMap<String, String>?,
    val description: String?,
    val items: ArrayList<@Serializable(with = ItemStackSerializer::class) ItemStack>)

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

val json = Json {
    isLenient = true
    prettyPrint = true
}

object KZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val string = decoder.decodeString()
        return ZonedDateTime.parse(string)
    }
}

object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ItemStack", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ItemStack) {
        require(encoder is JsonEncoder)

        val encode = ItemStackData(value.type.name, mm.serialize(value.displayName()), value.encode())
        encoder.encodeJsonElement(json.encodeToJsonElement(encode))
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        val itemData = json.decodeFromJsonElement<ItemStackData>(element)
        return itemData.item.decode()
    }

}

@Serializable
data class ItemStackData(val kind: String, val name: String?, val item: String)