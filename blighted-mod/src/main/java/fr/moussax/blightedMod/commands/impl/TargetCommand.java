package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.BlightedModerator;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;

/**
 * Moderation command to set or clear the targeted player for the moderation actionbar HUD (/target <player>).
 */
@CommandArgument(position = 0, suggestions = {"$players"})
public final class TargetCommand extends ModerationCommand {
    private static final String PREFIX = " §9§lMOD §f| §7";

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        BlightedModerator blightedModerator = getModerationManager().getModerator(moderator);

        if (arguments.length == 0) {
            blightedModerator.setTargetPlayer(null);
            inform(moderator, PREFIX + "Cleared moderation HUD target.");
            return true;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        blightedModerator.setTargetPlayer(target);
        InteractiveMessage.text(PREFIX + "Now targeting §e" + target.getName() + " §7for moderation HUD.  ")
                .hoverAndExecute("§e§l[INFO]", "§7Click to view user information for §e" + target.getName() + "§7 (/userinfo)", "/userinfo " + target.getName())
                .send(moderator);
        return true;
    }
}
