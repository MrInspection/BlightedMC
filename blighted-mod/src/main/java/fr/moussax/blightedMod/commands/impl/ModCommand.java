package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class ModCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length > 0) {
            String message = String.join(" ", arguments);
            String formattedMessage = " §9§lMOD §9" + moderator.getName() + " §f§l» §e§l" + message;
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(formattedMessage);
            Bukkit.broadcastMessage(" ");
            return true;
        }

        getModerationManager().toggleModerationMode(moderator);
        return true;
    }
}
