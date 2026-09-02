package fr.moussax.bedrock.text;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Utility for sending informational and warning messages to command recipients.
 */
public final class Messenger {

    private Messenger() {
    }

    /**
     * Sends a list of informational messages to a command sender.
     *
     * @param sender   recipient receiving the messages
     * @param messages list of messages to send
     */
    public static void inform(CommandSender sender, @NonNull List<String> messages) {
        messages.forEach(sender::sendMessage);
    }

    /**
     * Sends one or more informational messages to a command sender.
     *
     * @param sender   recipient receiving the messages
     * @param messages messages to send
     */
    public static void inform(@NonNull CommandSender sender, @NonNull String... messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    /**
     * Sends a single informational message to a command sender.
     *
     * @param sender  recipient receiving the message
     * @param message message to send
     */
    public static void inform(@NonNull CommandSender sender, @NonNull String message) {
        inform(sender, Collections.singletonList(message));
    }

    /**
     * Sends a list of red-formatted warning messages to a command sender, playing an alert sound if the recipient is a player.
     *
     * @param sender   recipient receiving the warning messages
     * @param messages list of warning messages to send
     */
    public static void warn(@NonNull CommandSender sender, @NonNull List<String> messages) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        }
        messages.forEach(message -> sender.sendMessage(" §c" + message));
    }

    /**
     * Sends one or more unformatted warning messages to a command sender, playing an alert sound if the recipient is a player.
     *
     * @param sender   recipient receiving the warning messages
     * @param messages warning messages to send
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
     * Sends a single red-formatted warning message to a command sender, playing an alert sound if the recipient is a player.
     *
     * @param sender  recipient receiving the warning message
     * @param message warning message to send
     */
    public static void warn(@NonNull CommandSender sender, @NonNull String message) {
        warn(sender, Collections.singletonList(message));
    }
}
