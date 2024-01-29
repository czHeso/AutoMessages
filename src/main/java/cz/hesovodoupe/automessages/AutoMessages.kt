package cz.hesovodoupe.automessages

import ConfigManager
import cz.foresttech.api.ColorAPI
import cz.hesovodoupe.automessages.commands.reloadCmd
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

private const val PLUGIN_ID = 20493
private const val DEFAULT_SOUND_VOLUME = 1.0f
private const val DEFAULT_SOUND_PITCH = 1.0f

class AutoMessages : JavaPlugin() {

    private lateinit var configManager: ConfigManager
    private lateinit var config: AutoMessagesConfig

    override fun onEnable() {
        saveDefaultConfig()
        println("Thank you for using my AutoMessage plugin.")
        println("You can request support on dsc.gg/hesodev")

        /* Metrics system */
        val metrics = Metrics(this, PLUGIN_ID)

        configManager = ConfigManager(this)
        loadConfiguration()

        startMessageSendingTask()
        registerCommands()
    }

    private fun loadConfiguration() {
        config = AutoMessagesConfig(configManager)
    }

    fun startMessageSendingTask() {
        object : BukkitRunnable() {
            override fun run() {
                config.messageIndex = config.messageIndex % if (config.allowRaw) config.rawMessages.size else config.messages.size
                val messageToSend = if (config.randomizeMessages) {
                    if (config.allowRaw) config.rawMessages.random() else config.messages.random()
                } else {
                    if (config.allowRaw) config.rawMessages[config.messageIndex] else config.messages[config.messageIndex]
                }

                Bukkit.getOnlinePlayers().forEach {
                    playSoundForPlayer(it, config.soundEffect)
                    if (config.allowRaw) {
                        val commandRaw = "tellraw @a $messageToSend"
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandRaw)
                    } else {
                        it.sendMessage(ColorAPI.colorize(messageToSend))
                    }
                }

                config.messageIndex += 1  // Increment after accessing the message
            }
        }.runTaskTimer(this, 0L, config.timeDelay * 20L)
    }


    private fun registerCommands() {
        getCommand("automessage")?.setExecutor(reloadCmd(this))
    }

    fun playSoundForPlayer(player: Player, soundEffect: String) {
        player.playSound(player.location, Sound.valueOf(soundEffect), DEFAULT_SOUND_VOLUME, DEFAULT_SOUND_PITCH)
    }
}

data class AutoMessagesConfig(
    val timeDelay: Int,
    val randomizeMessages: Boolean,
    val messages: List<String>,
    val soundEffect: String,
    val allowRaw: Boolean,
    val rawMessages: List<String>,
    val actionBar: Boolean,
    val barMessages: List<String>,
    var messageIndex: Int = 0
) {
    constructor(configManager: ConfigManager) : this(
        timeDelay = configManager.loadTimeDelay(),
        randomizeMessages = configManager.shouldRandomizeMessages(),
        messages = configManager.loadMessages(),
        soundEffect = configManager.loadSoundEffect(),
        allowRaw = configManager.useRaw(),
        rawMessages = configManager.rawMessages(),
        actionBar = configManager.actionBar(),
        barMessages = configManager.barMessages()
    )
}
