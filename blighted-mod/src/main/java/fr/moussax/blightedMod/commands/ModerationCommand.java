package fr.moussax.blightedMod.commands;

import fr.moussax.bedrock.commands.PlayerCommand;
import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Base abstract class for all moderation commands in BlightedMod.
 * Validates that the sender is a qualified moderator before delegating execution.
 */
public abstract class ModerationCommand extends PlayerCommand {

    @Override
    protected final boolean execute(Player player, Command command, String label, String[] arguments) {
        if (!getModerationManager().isModerator(player)) {
            warn(player, "You do not have permission to execute moderation commands.");
            return true;
        }

        return executeModeration(player, command, label, arguments);
    }

    /**
     * Executes the moderation command logic.
     *
     * @param moderator validated player executing the moderation command
     * @param command   command executed
     * @param label     alias used to invoke the command
     * @param arguments command arguments
     * @return {@code true} if handled; otherwise {@code false}
     */
    protected abstract boolean executeModeration(
            Player moderator,
            Command command,
            String label,
            String[] arguments
    );

    protected ModerationManager getModerationManager() {
        return ModerationManager.getInstance();
    }

    protected PunishmentManager getPunishmentManager() {
        return getModerationManager().getPunishmentManager();
    }
}

