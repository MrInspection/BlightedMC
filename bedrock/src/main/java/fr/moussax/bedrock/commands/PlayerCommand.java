package fr.moussax.bedrock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Base class for commands that require a {@link Player} as the command sender.
 *
 * <p>Validates that the sender is a player before delegating execution to
 * {@link #execute(Player, Command, String, String[])}. Subclasses can therefore
 * assume the sender is always a valid {@link Player}.
 */
public abstract class PlayerCommand implements CommandExecutor {

    /**
     * {@inheritDoc}
     *
     * <p>If the sender is not a player, the command is rejected and
     * {@code false} is returned.
     */
    @Override
    public final boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String @NonNull [] arguments
    ) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        return execute(player, command, label, arguments);
    }

    /**
     * Executes this command.
     *
     * <p>Implementations may safely assume the sender has already been verified
     * to be a {@link Player}.
     *
     * @param player    the validated player executing the command
     * @param command   the executed command
     * @param label     the alias used to invoke the command
     * @param arguments the command arguments
     * @return {@code true} if the command was handled; otherwise {@code false}
     */
    protected abstract boolean execute(
            Player player,
            Command command,
            String label,
            String[] arguments
    );

    /**
     * Resolves an online player by their exact username.
     *
     * <p>If no matching player is found, a warning is sent to the command sender.
     *
     * @param sender the command sender requesting the lookup
     * @param name   the exact username to resolve
     * @return the matching online player, or {@code null} if no player exists
     */
    protected final @Nullable Player requireTarget(CommandSender sender, String name) {
        Player target = Bukkit.getPlayerExact(name);

        if (target == null) {
            warn(sender, "Unable to find player §4" + name);
        }

        return target;
    }

    /**
     * Resolves an online player by their exact username.
     *
     * <p>If no matching player is found, a warning is sent to the command sender.
     *
     * @param sender the player requesting the lookup
     * @param name   the exact username to resolve
     * @return the matching online player, or {@code null} if no player exists
     */
    protected final @Nullable Player requireTarget(Player sender, String name) {
        return requireTarget((CommandSender) sender, name);
    }
}
