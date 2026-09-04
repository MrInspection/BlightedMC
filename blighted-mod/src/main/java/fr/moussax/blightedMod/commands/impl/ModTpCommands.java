package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.commands.ModerationCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class ModTpCommands extends ModerationCommand {
    private static final String PREFIX = " §9§lMOD §f| §7";

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

        InteractiveMessage.text(PREFIX + "Teleported to §9" + target.getName() + "§7.  ")
                .hoverAndExecute("§e§l[INFO]", "§7Click to view user information for §e" + target.getName() + "§7 (/userinfo)", "/userinfo " + target.getName())
                .send(moderator);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 teleported to §9" + target.getName();
        getModerationManager().broadcastToModerators(notification);
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

        InteractiveMessage.text(PREFIX + "Teleported §9" + target.getName() + "§7 to you.  ")
                .hoverAndExecute("§e§l[INFO]", "§7Click to view user information for §e" + target.getName() + "§7 (/userinfo)", "/userinfo " + target.getName())
                .send(moderator);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 teleported §9" + target.getName() + "§7 to them";
        getModerationManager().broadcastToModerators(notification);
        return true;
    }
}
