package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.BlightedModerator;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Moderation command to set or clear the targeted player for the moderation actionbar HUD (/target <player>).
 */
@CommandArgument(position = 0, suggestions = {"$players"})
public final class TargetCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (!getModerationManager().isInModerationMode(moderator)) {
            warn(moderator, "You must be in §dModeration Mode §cto target a player.");
            return true;
        }

        BlightedModerator blightedModerator = getModerationManager().getModerator(moderator);

        if (arguments.length == 0) {
            blightedModerator.setTargetPlayer(null);
            inform(moderator, " §eTarget cleared from §fModeration HUD§e.");
            return true;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot target yourself.");
            return true;
        }

        blightedModerator.setTargetPlayer(target);
        InteractiveMessage.text(" §eTargeting §d" + target.getName() + " §ewith §fModeration HUD§e. ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + target.getName() + "§f.", "/userinfo " + target.getName())
                .send(moderator);
        return true;
    }
}
