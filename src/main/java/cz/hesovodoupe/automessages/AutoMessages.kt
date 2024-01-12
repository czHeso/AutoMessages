package cz.hesovodoupe.automessages

import ConfigManager
import cz.foresttech.api.ColorAPI
import cz.hesovodoupe.automessages.commands.reloadCmd
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


class AutoMessages : JavaPlugin() {

    private lateinit var configManager: ConfigManager
    private var messageIndex = 0
    private var timeDelay: Int = 0
    private var randomizeMessages: Boolean = false
    private lateinit var messages: List<String>
    private lateinit var soundEffect: String
    private lateinit var configVer: String
    private var allowRaw: Boolean = false
    private lateinit var rawMessages: List<String>

    override fun onEnable() {
        saveDefaultConfig()
        println("Thank you for using my AutoMessage plugin.")
        println("You can request support on dsc.gg/hesodev")

        /* Metrics system */
        val pluginId = 20493
        val metrics = Metrics(this, pluginId)

        configManager = ConfigManager(this)
        loadConfiguration()

        val currectVersion = description.version

        if(configVer != currectVersion)
        {
            println("Your config is $configVer but plugin is now $currectVersion")
            println("Please remove config and let it generate again!")

        }


        startMessageSendingTask()
        registerCommands()

    }

    private fun loadConfiguration() {
        timeDelay = configManager.loadTimeDelay()
        randomizeMessages = configManager.shouldRandomizeMessages()
        messages = configManager.loadMessages()
        soundEffect = configManager.loadSoundEffect()
        rawMessages = configManager.rawMessages()
        allowRaw = configManager.useRaw()
        configVer = configManager.loadConfigVer()
    }

    fun startMessageSendingTask() {
        object : BukkitRunnable() {
            override fun run() {
                loadConfiguration() // Reload the configuration values

                val messageToSend = if (randomizeMessages) {
                    if (allowRaw) rawMessages.random() else messages.random()
                } else {
                    messageIndex = (messageIndex + 1) % if (allowRaw) rawMessages.size else messages.size
                    if (allowRaw) rawMessages[messageIndex] else messages[messageIndex]
                }

                if (allowRaw && Bukkit.getOnlinePlayers().isNotEmpty()) {
                    val command = "tellraw @a $messageToSend"
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)

                    Bukkit.getOnlinePlayers().forEach { playSoundForPlayer(it, soundEffect) }

                }
                else {
                    Bukkit.getOnlinePlayers().forEach {
                        playSoundForPlayer(it, soundEffect)
                        it.sendMessage(ColorAPI.colorize(messageToSend))

                    }
                }
            }
        }.runTaskTimer(this, 0L, timeDelay * 20L)
    }


    private fun registerCommands() {
        getCommand("automessage")?.setExecutor(reloadCmd(this))
    }

}

    fun playSoundForPlayer(player: Player, soundEffect: String) {
        val soundVolume: Float = 1.0f
        val soundPitch: Float = 1.0f

        player.playSound(player.location, Sound.valueOf(soundEffect), soundVolume, soundPitch)
    }




