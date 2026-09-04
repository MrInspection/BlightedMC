package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

public final class SetSlowmodeCommand extends ModerationCommand {

    private static final String PREFIX = " §9§lMOD §f| §7";

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            int currentSlowmode = getModerationManager().getSlowmodeDelaySeconds();
            if (currentSlowmode > 0) {
                inform(moderator, PREFIX + "Chat slowmode is currently §aENABLED §7(§e" + currentSlowmode + "s§7).");
            } else {
                inform(moderator, PREFIX + "Chat slowmode is currently §cDISABLED§7.");
            }
            warn(moderator, "Usage: /slowmode <seconds|off>");
            return true;
        }

        String input = arguments[0];
        if (input.equalsIgnoreCase("off") || input.equalsIgnoreCase("disable") || input.equalsIgnoreCase("0")) {
            getModerationManager().setSlowmodeDelaySeconds(0);
            Bukkit.broadcastMessage(PREFIX + "Chat slowmode has been §cDISABLED§7 by §e" + moderator.getName() + "§7.");
            return true;
        }

        Long secondsDuration = DurationParser.parseDurationSeconds(input);
        if (secondsDuration == null || secondsDuration <= 0) {
            getModerationManager().setSlowmodeDelaySeconds(0);
            Bukkit.broadcastMessage(PREFIX + "Chat slowmode has been §cDISABLED§7 by §e" + moderator.getName() + "§7.");
            return true;
        }

        int seconds = (int) Math.min(secondsDuration, Integer.MAX_VALUE);
        getModerationManager().setSlowmodeDelaySeconds(seconds);
        Bukkit.broadcastMessage(PREFIX + "Chat slowmode set to §e" + seconds + "§7 second(s) by §e" + moderator.getName() + "§7.");
        return true;
    }
}
