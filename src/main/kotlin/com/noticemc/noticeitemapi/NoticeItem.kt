/*
 *     Written in 2022  by Nikomaru <nikomaru@nikomaru.dev>
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide.
 *     This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software.
 *     If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package com.noticemc.noticeitemapi

import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import com.github.guepardoapps.kulid.ULID
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.noticemc.noticeitemapi.api.NoticeItemAPI
import com.noticemc.noticeitemapi.commands.OpenCommand
import com.noticemc.noticeitemapi.data.ItemData
import com.noticemc.noticeitemapi.events.GuiClickEvent
import com.noticemc.noticeitemapi.events.PreviewClickEvent
import com.noticemc.noticeitemapi.utils.ChangeItemData.Companion.encode
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.Optional
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.encodeToConfig
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.time.ZonedDateTime

class NoticeItem : JavaPlugin(), NoticeItemAPI {

    companion object {
        lateinit var plugin: NoticeItem
    }

    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        setCommand()
        server.pluginManager.registerSuspendingEvents(GuiClickEvent(), this)
        server.pluginManager.registerSuspendingEvents(PreviewClickEvent(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun setCommand() {
        var commandManager: cloud.commandframework.paper.PaperCommandManager<CommandSender>? = null
        try {
            commandManager = cloud.commandframework.paper.PaperCommandManager(this,
                AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
                java.util.function.Function.identity(),
                java.util.function.Function.identity())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        if (commandManager == null) {
            server.pluginManager.disablePlugin(this)
            return
        }

        if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }
        val annotationParser: cloud.commandframework.annotations.AnnotationParser<CommandSender> =
            cloud.commandframework.annotations.AnnotationParser(commandManager, CommandSender::class.java) {
                SimpleCommandMeta.empty()
            }

        annotationParser.parse(OpenCommand())
    }

    override fun addItem(player: OfflinePlayer,
        item: ItemStack,
        @Optional limit: ZonedDateTime,
        @Optional supplement: HashMap<String, String>,
        @Optional description: String) {

        pushItems(player, listOf(item), limit, supplement, description)

    }

    override fun addItem(player: OfflinePlayer,
        items: Collection<ItemStack>,
        @Optional limit: ZonedDateTime,
        @Optional supplement: HashMap<String, String>,
        @Optional description: String) {
        pushItems(player, items, limit, supplement, description)

    }

    override fun addItem(player: OfflinePlayer,
        inventory: Inventory,
        @Optional limit: ZonedDateTime,
        @Optional supplement: HashMap<String, String>,
        @Optional description: String) {
        val items = inventory.contents ?: return

        val list: ArrayList<ItemStack> = ArrayList()
        items.forEach {
            if (it != null) {
                list.add(it)
            }
        }

        pushItems(player, list, limit, supplement, description)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun pushItems(player: OfflinePlayer,
        items: Collection<ItemStack>,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?) {
        if (items.isEmpty()) {
            return
        }
        val uuid = player.uniqueId
        val managementULID = ULID.random()
        val fileName = "$managementULID.conf"
        val fileDir = File(File(File(plugin.dataFolder, "data"), uuid.toString()), fileName)
        val encodeItems: ArrayList<String> = ArrayList()
        items.forEach {
            encodeItems.add(it.encode())
        }
        val itemData = ItemData("1.0.0", managementULID, player.uniqueId, limit, supplement, description, encodeItems)
        val renderOptions = ConfigRenderOptions.concise().setFormatted(true).setJson(true)
        val string = Hocon.encodeToConfig(itemData).root().render(renderOptions)

        if (!fileDir.parentFile.exists()) {
            fileDir.parentFile.mkdirs()
        }
        fileDir.createNewFile()
        val fw = PrintWriter(BufferedWriter(OutputStreamWriter(FileOutputStream(fileDir), "UTF-8")))
        fw.write(string)
        fw.close()

    }

}