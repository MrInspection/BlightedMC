package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.shared.text.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Base class for administrative commands.
 * <p>
 * In addition to the guarantees provided by {@link PlayerCommand}, this class
 * verifies that the executing player has administrative permissions before
 * delegating execution to {@link #executeAdmin(Player, Command, String, String[])}.
 * Implementations may therefore assume both a valid player sender and
 * sufficient permissions.
 */
public abstract class AdminCommand extends PlayerCommand {

    /**
     * Verifies administrative permission for a player.
     *
     * @param player the player to check
     * @return {@code true} if the player is an operator
     */
    protected boolean hasRequiredPermission(@NonNull Player player) {
        if (!player.isOp()) {
            Messenger.warn(player, "You must be an administrator to use this command.");
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the executing player does not have the required administrative
     * permission, the command is rejected and {@code false} is returned.
     */
    @Override
    protected final boolean execute(
            Player player,
            Command command,
            String label,
            String[] args
    ) {
        if (!hasRequiredPermission(player)) {
            return false;
        }

        return executeAdmin(player, command, label, args);
    }

    /**
     * Executes this administrative command.
     * <p>
     * Implementations may safely assume the sender is a valid {@link Player}
     * and has already passed the required permission check.
     *
     * @param player the validated player executing the command
     * @param command the executed command
     * @param label the alias used to invoke the command
     * @param args the command arguments
     * @return {@code true} if the command was handled; otherwise {@code false}
     */
    protected abstract boolean executeAdmin(
            Player player,
            Command command,
            String label,
            String[] args
    );
}
