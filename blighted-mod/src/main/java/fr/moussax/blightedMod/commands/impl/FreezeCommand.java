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
            moderator.sendMessage("§b ❄ §eYou froze §d" + target.getName() + "§e.");
            target.sendMessage("§b ❄ §c§lYOU HAVE BEEN FROZEN BY A MODERATOR!");
            target.sendMessage(" §7Do not log out or you will be permanently banned.");
        } else {
            moderator.sendMessage("§b ❄ §eYou unfroze §d" + target.getName() + "§e.");
            target.sendMessage("§b ❄ §a§lYOU HAVE BEEN UNFROZEN!");
            target.sendMessage(" §7You are now free to move again.");
        }

        return true;
    }
}
