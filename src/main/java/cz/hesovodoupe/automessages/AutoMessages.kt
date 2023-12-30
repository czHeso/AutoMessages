package cz.hesovodoupe.automessages

import ConfigManager
import cz.hesovodoupe.automessages.commands.reloadCmd
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable


class AutoMessages : JavaPlugin() {

    private lateinit var configManager: ConfigManager
    private var messageIndex = 0
    private var timeDelay: Int = 0
    private var randomizeMessages: Boolean = false
    private lateinit var messages: List<String>
    private lateinit var soundEffect: String
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

        println("Time Delay: $timeDelay seconds")
        println("Randomize Messages: $randomizeMessages")
        println("Sound Effect: $soundEffect")
        println("Messages:")
        messages.forEach { println(" - $it") }
        if(allowRaw)
        {
            println("Raw: $allowRaw")
            println("RawMessages")
            rawMessages.forEach { println(" - $it") }

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
    }

    fun startMessageSendingTask() {
        object : BukkitRunnable() {
            override fun run() {
                loadConfiguration() // Reload the configuration values

                val messageToSend: String = if (randomizeMessages) {

                    if (allowRaw){
                        rawMessages.random()
                    }
                    else {
                        messages.random()
                    }
                } else {
                    if (allowRaw){
                        messageIndex = (messageIndex + 1) % rawMessages.size
                        rawMessages[messageIndex]
                    }
                    else {
                        messageIndex = (messageIndex + 1) % messages.size
                        messages[messageIndex]
                    }
                }
                if(allowRaw)
                {
                    if(Bukkit.getOnlinePlayers().size > 0)
                    {
                        val command = "tellraw @a $messageToSend"
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
                        for (player in Bukkit.getOnlinePlayers()) {
                            playSoundForPlayer(player, soundEffect)
                        }
                    }
                }
                else {
                    for (player in Bukkit.getOnlinePlayers()) {
                        playSoundForPlayer(player, soundEffect)
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageToSend))
                    }
                }
            }
        }.runTaskTimer(this, 0L, (timeDelay * 20L))
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


