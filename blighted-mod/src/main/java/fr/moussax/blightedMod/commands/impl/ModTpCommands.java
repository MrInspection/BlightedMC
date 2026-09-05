package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.commands.ModerationCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class ModTpCommands extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        boolean isHere = label.equalsIgnoreCase("mtphere");

        if (arguments.length == 0) {
            warn(moderator, "Usage: /" + (isHere ? "mtphere" : "mtp") + " <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, isHere ? "You cannot teleport yourself to yourself." : "You cannot teleport to yourself.");
            return false;
        }

        if (isHere) {
            target.teleport(moderator.getLocation());
        } else {
            moderator.teleport(target.getLocation());
        }

        getModerationManager().getModerator(moderator).setTargetPlayer(target);

        String textMessage = isHere
                ? " §eTeleported §d" + target.getName() + "§e to you. "
                : " §eTeleported to §d" + target.getName() + "§e. ";

        InteractiveMessage.text(textMessage)
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + target.getName() + "§f.", "/userinfo " + target.getName())
                .send(moderator);

        return true;
    }
}
