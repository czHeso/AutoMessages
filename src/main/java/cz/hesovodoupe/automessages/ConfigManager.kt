import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class ConfigManager(private val plugin: JavaPlugin) {

    fun loadTimeDelay(): Int {
        return plugin.config.getInt("timeDelay", 280)
    }

    fun shouldRandomizeMessages(): Boolean {
        return plugin.config.getBoolean("random", false)
    }

    fun loadSoundEffect(): String {
        return plugin.config.getString("sound", "test") ?: "test"
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
