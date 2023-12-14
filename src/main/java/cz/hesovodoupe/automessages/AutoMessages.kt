package cz.hesovodoupe.automessages

import ConfigManager
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable


class AutoMessages : JavaPlugin() {

    private lateinit var configManager: ConfigManager
    private var messageIndex = 0


    override fun onEnable() {
        saveDefaultConfig()
        println("Thank you for using my AutoMessage plugin.")
        println("You can request support on dsc.gg/hesodev")



        /* Metrics system */
        val pluginId = 20493
        val metrics = Metrics(this, pluginId)

        configManager = ConfigManager(this)

        val timeDelay: Int = configManager.loadTimeDelay()
        val randomizeMessages: Boolean = configManager.shouldRandomizeMessages()
        val messages: List<String> = configManager.loadMessages()
        val soundEffect = configManager.loadSoundEffect()

        println("Time Delay: $timeDelay seconds")
        println("Randomize Messages: $randomizeMessages")
        println("Sound Effect: $soundEffect")
        println("Messages:")
        for (message in messages) {
            println(" - $message")
        }

        startMessageSendingTask()
    }


    override fun onDisable() {
        getLogger().info("Plugin is shutting down")
    }

    fun startMessageSendingTask() {
        val timeDelay: Int = configManager.loadTimeDelay()
        val randomizeMessages: Boolean = configManager.shouldRandomizeMessages()
        val messages: List<String> = configManager.loadMessages()
        val soundEffect = configManager.loadSoundEffect()

        object : BukkitRunnable() {
            override fun run() {
                // Zde můžete provádět akce, které se mají opakovat v daném intervalu
                val messageToSend: String = if (randomizeMessages) {
                    // Náhodné vybrání zprávy
                    messages.random()
                } else {
                    messageIndex = (messageIndex + 1) % messages.size
                    messages[messageIndex]
                }


                for (player in Bukkit.getOnlinePlayers()) {
                    playSoundForPlayer(player, soundEffect)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageToSend))
                }
            }
        }.runTaskTimer(this, 0L, (timeDelay * 20L)) // Čas se udává v tiktech, 20 tiků = 1 sekunda
    }

    fun playSoundForPlayer(player: Player, soundEffect: String) {
        val soundVolume: Float = 1.0f // Hlasitost zvuku
        val soundPitch: Float = 1.0f // Výška tónu zvuku

        player.playSound(player.location, Sound.valueOf(soundEffect), soundVolume, soundPitch)
    }


}

