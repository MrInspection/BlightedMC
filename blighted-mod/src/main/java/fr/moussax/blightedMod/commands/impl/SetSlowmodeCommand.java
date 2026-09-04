package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

public final class SetSlowmodeCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            int currentSlowmode = getModerationManager().getSlowmodeDelaySeconds();
            if (currentSlowmode > 0) {
                inform(moderator, " §fChat Slowmode §eis currently §aENABLED §7(§e" + currentSlowmode + "s§7).");
            } else {
                inform(moderator, " §fChat Slowmode §eis currently §cDISABLED§e.");
            }
            warn(moderator, "Usage: /slowmode <seconds|off>");
            return true;
        }

        String input = arguments[0];
        if (input.equalsIgnoreCase("off") || input.equalsIgnoreCase("disable") || input.equalsIgnoreCase("0")) {
            getModerationManager().setSlowmodeDelaySeconds(0);
            getModerationManager().broadcastToModerators(" §d§lSTAFF! §9" + moderator.getName() + " §ehas disabled §fChat Slowmode§e.");
            return true;
        }

        Long secondsDuration = DurationParser.parseDurationSeconds(input);
        if (secondsDuration == null || secondsDuration <= 0) {
            getModerationManager().setSlowmodeDelaySeconds(0);
            getModerationManager().broadcastToModerators(" §d§lSTAFF! §9" + moderator.getName() + " §ehas disabled §fChat Slowmode§e.");
            return true;
        }

        int seconds = (int) Math.min(secondsDuration, Integer.MAX_VALUE);
        getModerationManager().setSlowmodeDelaySeconds(seconds);
        getModerationManager().broadcastToModerators(" §d§lSTAFF! §9" + moderator.getName() + " §eset §fChat Slowmode §eto §6" + seconds + "s§e.");
        return true;
    }
}
