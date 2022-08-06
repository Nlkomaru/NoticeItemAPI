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
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.noticemc.noticeitemapi.api.NoticeItemAPI
import com.noticemc.noticeitemapi.commands.*
import com.noticemc.noticeitemapi.data.ItemData
import com.noticemc.noticeitemapi.events.*
import com.noticemc.noticeitemapi.utils.GuiUtils.mm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.time.ZonedDateTime
import java.util.function.Function.*

class NoticeItem : JavaPlugin(), NoticeItemAPI {

    companion object {
        lateinit var plugin: NoticeItem
    }

    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        setCommand()
        with(server.pluginManager) {
            registerSuspendingEvents(GuiClickEvent(), plugin)
            registerSuspendingEvents(PreviewClickEvent(), plugin)
            registerSuspendingEvents(LoginPopUpEvent(), plugin)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun setCommand() {

        val commandManager = cloud.commandframework.paper.PaperCommandManager(this,
            AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
            identity(),
            identity())


        if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }
        val annotationParser: cloud.commandframework.annotations.AnnotationParser<CommandSender> =
            cloud.commandframework.annotations.AnnotationParser(commandManager, CommandSender::class.java) {
                SimpleCommandMeta.empty()
            }

        with(annotationParser) {
            parse(CommandSuggestion())
            parse(CopyCommand())
            parse(GiveCommand())
            parse(RemoveCommand())
            parse(OpenCommand())
            parse(CopyAllCommand())
        }

    }

    override fun addItem(player: OfflinePlayer,
        item: ItemStack,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?): String {
        return pushItems(player, listOf(item), limit, supplement, description)
    }

    override fun addItem(player: OfflinePlayer,
        items: Collection<ItemStack>,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?): String {

        return pushItems(player, items, limit, supplement, description)

    }

    override fun addItem(player: OfflinePlayer,
        inventory: Inventory,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?): String? {
        val items = inventory.contents ?: return null

        val list: ArrayList<ItemStack> = ArrayList()
        items.forEach {
            if (it != null) {
                list.add(it)
            }
        }
        return pushItems(player, list, limit, supplement, description)

    }

    override fun removeItem(player: OfflinePlayer, ulid: String): Boolean {

        val uuid = player.uniqueId

        val fileName = "$ulid.json"
        val fileDir = File(File(File(plugin.dataFolder, "data"), uuid.toString()), fileName)
        if (!fileDir.exists()) {
            return false
        }
        return fileDir.delete()
    }

    private fun pushItems(player: OfflinePlayer,
        items: Collection<ItemStack>,
        limit: ZonedDateTime?,
        supplement: HashMap<String, String>?,
        description: String?): String {

        val managementULID = ULID.random()
        plugin.launch {
            if (items.isEmpty()) {
                return@launch
            }
            withContext(Dispatchers.IO) {
                val uuid = player.uniqueId

                val fileName = "$managementULID.json"
                val fileDir = File(File(File(plugin.dataFolder, "data"), uuid.toString()), fileName)
                val itemStacks = ArrayList<ItemStack>()

                withContext(Dispatchers.Default) {
                    items.forEach {
                        if (it.type.isItem && !it.type.isAir) {
                            itemStacks.add(it)
                        }
                    }
                }

                val itemData = ItemData("1.0.0", managementULID, player.uniqueId, limit, supplement, description, itemStacks)
                val json = Json {
                    isLenient = true
                    prettyPrint = true
                }

                val string = json.encodeToString(itemData)

                if (!fileDir.parentFile.exists()) {
                    fileDir.parentFile.mkdirs()
                }

                fileDir.createNewFile()

                val fw = PrintWriter(BufferedWriter(OutputStreamWriter(FileOutputStream(fileDir), "UTF-8")))
                fw.write(string)
                fw.close()

                if (player.isOnline) {
                    (player as Player).sendMessage(mm.deserialize("<color:yellow>受け取ることが可能なアイテムがあります</color> <click:run_command:'/nia open'>クリックで開く</click>"))
                }
            }
        }
        return managementULID
    }
}

