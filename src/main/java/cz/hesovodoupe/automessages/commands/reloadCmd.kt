package cz.hesovodoupe.automessages.commands

import cz.hesovodoupe.automessages.AutoMessages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class reloadCmd(private val plugin: AutoMessages) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("automessage", ignoreCase = true)) {
            // Příkaz pro načtení konfigurace
            if (sender.hasPermission("am.reload")) {
                plugin.reloadConfig()
                sender.sendMessage("Configuration reloaded successfully.")
            } else {
                sender.sendMessage("You do not have permission to reload the configuration.")
            }
            return true
        }
        return false
    }
}