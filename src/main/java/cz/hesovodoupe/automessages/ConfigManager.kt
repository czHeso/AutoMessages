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

    fun actionBar(): Boolean {
        return plugin.config.getBoolean("actionBar", false)
    }
    fun barMessages(): List<String> {
        val config: FileConfiguration = plugin.config
        val rawMessages: List<String> = config.getStringList("barMessages")


        return rawMessages
    }

    fun loadSoundEffect(): String {
        return plugin.config.getString("sound", "test") ?: "test"
    }

    fun soundPitch(): String {
        return plugin.config.getString("pitch", "1.0f") ?: "1.0f"
    }

    fun soundVolume(): String {
        return plugin.config.getString("volume", "1.0f") ?: "1.0f"
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
