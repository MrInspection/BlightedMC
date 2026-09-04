package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.ModerationManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class SocialSpyCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        ModerationManager manager = getModerationManager();
        boolean enabled = manager.toggleMessageInspect(moderator);
        if (enabled) {
            inform(moderator, "§9[SPY] §7Private message inspection is now §aENABLED§7.");
        } else {
            inform(moderator, "§9[SPY] §7Private message inspection is now §cDISABLED§7.");
        }
        return true;
    }
}
