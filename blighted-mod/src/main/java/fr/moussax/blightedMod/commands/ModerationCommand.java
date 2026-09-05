package fr.moussax.blightedMod.commands;

import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Base abstract class for all moderation commands in BlightedMod.
 * Validates that the sender is a qualified moderator or console before delegating execution.
 */
public abstract class ModerationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] arguments) {
        if (sender instanceof Player player) {
            if (!getModerationManager().isModerator(player)) {
                warn(player, "You must be MODERATOR or higher to use this command.");
                return true;
            }
            return executeModeration(player, command, label, arguments);
        }

        if (isConsoleAllowed()) {
            return executeModeration(sender, command, label, arguments);
        }

        warn(sender, "This command can only be executed by in-game moderators.");
        return true;
    }

    /**
     * Executes the moderation command logic for in-game moderators.
     *
     * @param moderator validated player executing the moderation command
     * @param command   command executed
     * @param label     alias used to invoke the command
     * @param arguments command arguments
     * @return {@code true} if handled; otherwise {@code false}
     */
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        return executeModeration((CommandSender) moderator, command, label, arguments);
    }

    /**
     * Executes the moderation command logic for any command sender (Player or Console).
     *
     * @param moderator validated command sender executing the moderation command
     * @param command   command executed
     * @param label     alias used to invoke the command
     * @param arguments command arguments
     * @return {@code true} if handled; otherwise {@code false}
     */
    protected boolean executeModeration(CommandSender moderator, Command command, String label, String[] arguments) {
        if (moderator instanceof Player player) {
            return executeModeration(player, command, label, arguments);
        }
        warn(moderator, "This command can only be executed by in-game moderators.");
        return true;
    }

    /**
     * Returns whether this moderation command can be executed from the server console.
     *
     * @return {@code true} if console execution is allowed; otherwise {@code false}
     */
    protected boolean isConsoleAllowed() {
        return false;
    }

    protected final @Nullable Player requireTarget(CommandSender sender, String name) {
        Player target = Bukkit.getPlayerExact(name);

        if (target == null) {
            warn(sender, "Unable to find player §4" + name);
        }

        return target;
    }

    protected final @Nullable Player requireTarget(Player sender, String name) {
        return requireTarget((CommandSender) sender, name);
    }

    protected ModerationManager getModerationManager() {
        return ModerationManager.getInstance();
    }

    protected PunishmentManager getPunishmentManager() {
        return getModerationManager().getPunishmentManager();
    }
}

