package fr.moussax.blightedMC.shared.text;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Utility for sending informational and warning messages to command senders.
 */
public final class Messenger {

    private Messenger() {
    }

    /**
     * Sends informational messages to a command sender with a gray prefix.
     *
     * @param sender   the recipient
     * @param messages list of messages to send
     */
    public static void inform(CommandSender sender, @NonNull List<String> messages) {
        messages.forEach(sender::sendMessage);
    }

    /**
     * Sends informational messages to a command sender.
     *
     * @param sender   the recipient
     * @param messages messages to send (no prefix applied)
     */
    public static void inform(@NonNull CommandSender sender, @NonNull String... messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    /**
     * Sends a single informational message to a command sender.
     *
     * @param sender  the recipient
     * @param message the message to send
     */
    public static void inform(@NonNull CommandSender sender, @NonNull String message) {
        inform(sender, Collections.singletonList(message));
    }

    /**
     * Sends warning messages to a command sender with red prefix and error sound.
     *
     * @param sender   the recipient
     * @param messages list of warning messages
     */
    public static void warn(@NonNull CommandSender sender, @NonNull List<String> messages) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        }
        messages.forEach(message -> sender.sendMessage(" §c" + message));
    }

    /**
     * Sends warning messages to a command sender with error sound.
     *
     * @param sender   the recipient
     * @param messages warning messages (no prefix applied)
     */
    public static void warn(@NonNull CommandSender sender, @NonNull String... messages) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        }
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    /**
     * Sends a single warning message to a command sender.
     *
     * @param sender  the recipient
     * @param message the warning message
     */
    public static void warn(@NonNull CommandSender sender, @NonNull String message) {
        warn(sender, Collections.singletonList(message));
    }
}
