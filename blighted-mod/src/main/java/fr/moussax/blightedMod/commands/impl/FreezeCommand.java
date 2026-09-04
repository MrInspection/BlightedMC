package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Moderation command to toggle freeze status for a player (/freeze <player>).
 */
@CommandArgument(position = 0, suggestions = {"$players"})
public final class FreezeCommand extends ModerationCommand {
    private static final String PREFIX = " §9§lMOD §f| §7";

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /freeze <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot freeze yourself.");
            return false;
        }

        if (getModerationManager().isModerator(target)) {
            warn(moderator, "You cannot freeze another moderator.");
            return false;
        }

        boolean isNowFrozen = getModerationManager().toggleFreeze(target);

        if (isNowFrozen) {
            moderator.sendMessage(PREFIX + target.getName() + " is now §bFROZEN");
            target.sendMessage("§c§lYOU HAVE BEEN FROZEN BY A MODERATOR!");
            target.sendMessage("§cDo not log out or you will be banned.");

            String notification = PREFIX + "§9" + moderator.getName() + "§7 froze §9" + target.getName();
            getModerationManager().broadcastToModerators(notification);
        } else {
            moderator.sendMessage(PREFIX + target.getName() + " is now §aUNFROZEN");
            target.sendMessage("§aYou have been unfrozen.");

            String notification = PREFIX + "§9" + moderator.getName() + "§7 unfroze §9" + target.getName();
            getModerationManager().broadcastToModerators(notification);
        }

        return true;
    }
}
