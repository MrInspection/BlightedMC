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
        if (label.equalsIgnoreCase("mtp")) {
            return handleTeleport(moderator, arguments);
        } else if (label.equalsIgnoreCase("mtphere")) {
            return handleTeleportHere(moderator, arguments);
        }
        return false;
    }

    private boolean handleTeleport(Player moderator, String[] arguments) {
        if (arguments.length == 0) {
            warn(moderator, "Usage: /mtp <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot teleport to yourself.");
            return false;
        }

        moderator.teleport(target.getLocation());
        getModerationManager().getModerator(moderator).setTargetPlayer(target);

        InteractiveMessage.text(" §eTeleported to §d" + target.getName() + "§e. ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + target.getName() + "§f.", "/userinfo " + target.getName())
                .send(moderator);
        return true;
    }

    private boolean handleTeleportHere(Player moderator, String[] arguments) {
        if (arguments.length == 0) {
            warn(moderator, "Usage: /mtphere <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot teleport yourself to yourself.");
            return false;
        }

        target.teleport(moderator.getLocation());
        getModerationManager().getModerator(moderator).setTargetPlayer(target);

        InteractiveMessage.text(" §eTeleported §d" + target.getName() + "§e to you. ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + target.getName() + "§f.", "/userinfo " + target.getName())
                .send(moderator);
        return true;
    }
}
