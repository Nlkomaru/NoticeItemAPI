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
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object GuiUtils {

    val mm = MiniMessage.miniMessage()
    val openGuiName: Component = mm.deserialize("<color:#4169e1>Notice Inventory")
    val previewGuiName = mm.deserialize("<color:#4169e1>Notice Inventory Preview")

    fun getPrevItem(): ItemStack {
        //前のページに戻るためのガラス
        val prev = ItemStack(Material.RED_STAINED_GLASS_PANE)
        val prevMeta = prev.itemMeta
        prevMeta.displayName(mm.deserialize("<color:#e83929>前のページへ戻る"))
        prev.itemMeta = prevMeta
        return prev
    }

    fun getNextItem(): ItemStack {
        //次のページに進むためのガラス
        val next = ItemStack(Material.BLUE_STAINED_GLASS_PANE)
        val nextMeta = next.itemMeta
        nextMeta.displayName(mm.deserialize("<color:#00bfff>次のページへ進む"))
        next.itemMeta = nextMeta
        return next
    }

    fun getCloseItem(): ItemStack {
        //ページを閉じることを知らせるバリアブロック
        val close = ItemStack(Material.BARRIER)
        val closeMeta = close.itemMeta
        closeMeta.displayName(mm.deserialize("<color:#e83929>閉じる"))
        close.itemMeta = closeMeta
        return close
    }

    fun getNoDataGlassItem(): ItemStack {
        //データがないことを知らせるガラス
        val noDataGlass = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val nodataglassMeta = noDataGlass.itemMeta
        nodataglassMeta.displayName(mm.deserialize("<color:#ffffff>ここには何もありません"))
        noDataGlass.itemMeta = nodataglassMeta
        return noDataGlass
    }

    fun getNowPage(i: Int): ItemStack {
        //現在のページを表示するガラス
        val nowPage = ItemStack(Material.PURPLE_STAINED_GLASS_PANE)
        nowPage.amount = i
        val nowPageMeta = nowPage.itemMeta
        nowPageMeta.displayName(mm.deserialize("<color:#9932cc> Page. $i"))
        nowPage.itemMeta = nowPageMeta
        return nowPage
    }

    fun getReturnFirstItem(): ItemStack {
        val firstPage = ItemStack(Material.YELLOW_STAINED_GLASS_PANE)
        val firstPageMeta = firstPage.itemMeta
        firstPageMeta.displayName(mm.deserialize("<color:#ffff00>最初に戻る"))
        firstPage.itemMeta = firstPageMeta
        return firstPage
    }

    fun getAllItems(): ItemStack {
        val allItems = ItemStack(Material.CYAN_STAINED_GLASS_PANE)
        val allItemsMeta = allItems.itemMeta
        allItemsMeta.displayName(mm.deserialize("<color:#00ffff>すべて受け取る"))
        val itemLore: MutableList<Component> = ArrayList()
        itemLore.add(MiniMessage.miniMessage().deserialize("<color:red>この処理は重い可能性があります"))
        allItemsMeta.lore(itemLore)
        allItems.itemMeta = allItemsMeta
        return allItems
    }

    fun getItem(): ItemStack {
        val allItems = ItemStack(Material.CYAN_STAINED_GLASS_PANE)
        val allItemsMeta = allItems.itemMeta
        allItemsMeta.displayName(mm.deserialize(("<color:#00ffff>クリックで受け取る")))
        allItems.itemMeta = allItemsMeta
        return allItems
    }

    fun getTenItems(): ItemStack {
        val tentems = ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        val tenItemsMeta = tentems.itemMeta
        tenItemsMeta.displayName(mm.deserialize("<color:#afeeee>10件受け取る"))
        val itemLore: MutableList<Component> = ArrayList()
        itemLore.add(MiniMessage.miniMessage().deserialize(""))
        tenItemsMeta.lore(itemLore)
        tentems.itemMeta = tenItemsMeta
        return tentems
    }

    fun getNoticeCantItem(): ItemStack {
        val cant = ItemStack(Material.BARRIER)
        val cantMeta = cant.itemMeta
        cantMeta.displayName(mm.deserialize("<color:#ff0000>その行動はできません"))
        cant.itemMeta = cantMeta
        return cant
    }

}