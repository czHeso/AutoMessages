import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class ConfigManager(private val plugin: JavaPlugin) {

    fun loadTimeDelay(): Int {
        return plugin.config.getInt("timeDelay", 280)
    }

    fun shouldRandomizeMessages(): Boolean {
        return plugin.config.getBoolean("random", false)
    }
    fun useRaw(): Boolean {
        return plugin.config.getBoolean("allowRaw", false)
    }
    fun rawMessages(): List<String> {
        val config: FileConfiguration = plugin.config
        val rawMessages: List<String> = config.getStringList("RawMessages")

        // Text edit '\' for messages
        val sanitizedRawMessages = rawMessages.map { it.replace("\\", "") }

        return sanitizedRawMessages
    }

    fun loadSoundEffect(): String {
        return plugin.config.getString("sound", "test") ?: "test"
    }
    fun loadConfigVer(): String {
        return plugin.config.getString("configVersion", "3.2") ?: "3.2"
    }

    fun loadMessages(): List<String> {
        val config: FileConfiguration = plugin.config
        val messages: List<String> = config.getStringList("Messages")
        return messages
    }

    fun saveDefaultConfig() {
        plugin.saveDefaultConfig()
    }
}
