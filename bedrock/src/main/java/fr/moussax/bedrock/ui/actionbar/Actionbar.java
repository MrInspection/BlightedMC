package fr.moussax.bedrock.ui.actionbar;

import fr.moussax.bedrock.ui.animation.TextAnimation;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Static entry point for sending and managing player action bar displays.
 *
 * <p>Supports one-shot flash notifications, timed alerts, automated decrementing countdowns,
 * dynamic text suppliers, targeted slot alerts, real-time HUD invalidation, and modular persistent sections.</p>
 */
public final class Actionbar {

    /** Default duration for temporary notifications (2 seconds). */
    public static final Duration DEFAULT_DURATION = Duration.ofSeconds(2);

    private Actionbar() {
    }

    /**
     * Initializes or retrieves the shared action bar service for the given plugin.
     *
     * @param plugin      owning plugin
     * @param periodTicks interval in ticks between periodic HUD renders
     * @return the active action bar service
     */
    @NonNull
    public static ActionbarService initialize(@NonNull Plugin plugin, long periodTicks) {
        ActionbarService service = ActionbarService.getOrCreate(plugin);
        service.start(periodTicks);
        return service;
    }

    /**
     * Sets the default delimiter string rendered between adjacent standard sections.
     *
     * @param separator delimiter text
     */
    public static void setSeparator(@NonNull String separator) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.setSeparator(separator);
        }
    }

    /**
     * Sets the default delimiter string rendered between adjacent standard sections for a specific player.
     *
     * @param player    target player
     * @param separator delimiter text
     */
    public static void setSeparator(@NonNull Player player, @NonNull String separator) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.setSeparator(player, separator);
        }
    }

    /**
     * Returns the current section delimiter, or default 5 spaces if service is not initialized.
     *
     * @return separator string
     */
    @NonNull
    public static String getSeparator() {
        ActionbarService service = ActionbarService.getInstance();
        return service != null ? service.getSeparator() : "     ";
    }

    /**
     * Returns the section delimiter configured for a specific player, or the global default.
     *
     * @param player target player
     * @return separator string
     */
    @NonNull
    public static String getSeparator(@NonNull Player player) {
        ActionbarService service = ActionbarService.getInstance();
        return service != null ? service.getSeparator(player) : getSeparator();
    }

    /**
     * Sends a temporary notification to a player using the default duration (2 seconds).
     *
     * @param player  target player
     * @param message text to display
     */
    public static void send(@NonNull Player player, @NonNull String message) {
        send(player, message, DEFAULT_DURATION);
    }

    /**
     * Sends a temporary notification to a player for a specified duration.
     *
     * @param player   target player
     * @param message  text to display
     * @param duration display duration
     */
    public static void send(@NonNull Player player, @NonNull String message, @NonNull Duration duration) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendAlert(player, message, duration);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }

    /**
     * Sends a dynamic temporary notification evaluating text on every render pass.
     *
     * @param player   target player
     * @param supplier dynamic text supplier
     * @param duration display duration
     */
    public static void send(@NonNull Player player, @NonNull Supplier<String> supplier, @NonNull Duration duration) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendAlert(player, supplier, duration);
        } else {
            String text = supplier.get();
            if (text != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
            }
        }
    }

    /**
     * Sends a modal alert to a player using the default duration (2 seconds).
     * Overrides all sections while active.
     *
     * @param player  target player
     * @param message alert text
     */
    public static void sendAlert(@NonNull Player player, @NonNull String message) {
        sendAlert(player, message, 0, DEFAULT_DURATION);
    }

    /**
     * Sends a default-priority modal alert to a player for a specified duration.
     *
     * @param player   target player
     * @param message  alert text
     * @param duration display duration
     */
    public static void sendAlert(@NonNull Player player, @NonNull String message, @NonNull Duration duration) {
        sendAlert(player, message, 0, duration);
    }

    /**
     * Sends a dynamic modal alert that evaluates text on every render pass.
     *
     * @param player   target player
     * @param supplier dynamic text supplier
     * @param duration display duration
     */
    public static void sendAlert(@NonNull Player player, @NonNull Supplier<String> supplier, @NonNull Duration duration) {
        sendAlert(player, supplier, 0, duration);
    }

    /**
     * Sends a dynamic modal alert with custom priority that evaluates text on every render pass.
     *
     * @param player   target player
     * @param supplier dynamic text supplier
     * @param priority precedence priority
     * @param duration display duration
     */
    public static void sendAlert(@NonNull Player player, @NonNull Supplier<String> supplier, int priority, @NonNull Duration duration) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendAlert(player, supplier, priority, duration);
        } else {
            String text = supplier.get();
            if (text != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
            }
        }
    }

    /**
     * Sends a modal alert with custom priority to a player for a specified duration.
     * Higher priority alerts take precedence over lower priority alerts.
     *
     * @param player   target player
     * @param message  alert text
     * @param priority precedence priority
     * @param duration display duration
     */
    public static void sendAlert(@NonNull Player player, @NonNull String message, int priority, @NonNull Duration duration) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendAlert(player, message, priority, duration);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }

    /**
     * Sends an automated decrementing countdown alert to a player (e.g. {@code "Rebooting in %ds..."}).
     *
     * <p>Decrements each second in real-time and clears automatically when time expires.
     * Diff-suppression ensures packets are only dispatched when the remaining second decrements.</p>
     *
     * @param player  target player
     * @param format  format string containing {@code %d}
     * @param seconds countdown duration in seconds
     */
    public static void sendCountdown(@NonNull Player player, @NonNull String format, int seconds) {
        sendCountdown(player, format, seconds, 0);
    }

    /**
     * Sends an automated decrementing countdown alert with custom priority.
     *
     * @param player   target player
     * @param format   format string containing {@code %d}
     * @param seconds  countdown duration in seconds
     * @param priority precedence priority
     */
    public static void sendCountdown(@NonNull Player player, @NonNull String format, int seconds, int priority) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendCountdown(player, format, seconds, priority);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(format.formatted(seconds)));
        }
    }

    /**
     * Sends an automated decrementing countdown alert with a custom second formatter function.
     *
     * @param player    target player
     * @param formatter function receiving remaining integer seconds
     * @param seconds   countdown duration in seconds
     */
    public static void sendCountdown(@NonNull Player player, @NonNull IntFunction<String> formatter, int seconds) {
        sendCountdown(player, formatter, seconds, 0);
    }

    /**
     * Sends an automated decrementing countdown alert with a custom second formatter function and priority.
     *
     * @param player    target player
     * @param formatter function receiving remaining integer seconds
     * @param seconds   countdown duration in seconds
     * @param priority  precedence priority
     */
    public static void sendCountdown(@NonNull Player player, @NonNull IntFunction<String> formatter, int seconds, int priority) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendCountdown(player, formatter, seconds, priority);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formatter.apply(seconds)));
        }
    }

    /**
     * Sends a slot-specific alert replacing a registered section's text using the default duration (2 seconds).
     * If the section is not found, automatically falls back to a modal alert to prevent dropping the message.
     *
     * @param player    target player
     * @param sectionId identifier of the section to temporarily replace
     * @param message   alert text
     */
    public static void sendSlotAlert(@NonNull Player player, @NonNull String sectionId, @NonNull String message) {
        sendSlotAlert(player, sectionId, message, DEFAULT_DURATION);
    }

    /**
     * Sends a slot-specific alert replacing a registered section's text for a player.
     * If the section is not found, automatically falls back to a modal alert to prevent dropping the message.
     *
     * @param player    target player
     * @param sectionId identifier of the section to temporarily replace
     * @param message   alert text
     * @param duration  display duration
     */
    public static void sendSlotAlert(@NonNull Player player, @NonNull String sectionId, @NonNull String message, @NonNull Duration duration) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendSlotAlert(player, sectionId, message, duration);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }

    /**
     * Sends a slot-specific alert with dynamic text evaluation for a player.
     *
     * @param player    target player
     * @param sectionId identifier of the section to temporarily replace
     * @param supplier  dynamic text supplier
     * @param duration  display duration
     */
    public static void sendSlotAlert(@NonNull Player player, @NonNull String sectionId, @NonNull Supplier<String> supplier, @NonNull Duration duration) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendSlotAlert(player, sectionId, supplier, duration);
        } else {
            String text = supplier.get();
            if (text != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
            }
        }
    }

    /**
     * Sends an automated decrementing countdown alert replacing a specific section.
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param format    format string containing {@code %d}
     * @param seconds   countdown duration in seconds
     */
    public static void sendSlotCountdown(@NonNull Player player, @NonNull String sectionId, @NonNull String format, int seconds) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.sendSlotCountdown(player, sectionId, format, seconds);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(format.formatted(seconds)));
        }
    }

    /**
     * Triggers an immediate action bar recompilation and packet dispatch for a player.
     *
     * <p>Packet diff suppression ensures duplicate packets are skipped if the rendered text is unchanged.</p>
     *
     * @param player target player
     */
    public static void update(@NonNull Player player) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.renderPlayer(player);
        }
    }

    /**
     * Registers a global persistent action bar section.
     *
     * @param section section to register
     */
    public static void register(@NonNull ActionbarSection section) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.registerSection(section);
        }
    }

    /**
     * Registers a global persistent section owned by a specific plugin.
     *
     * @param plugin  owning plugin
     * @param section section to register
     */
    public static void register(@NonNull Plugin plugin, @NonNull ActionbarSection section) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.registerSection(plugin, section);
        }
    }

    /**
     * Registers a section visible exclusively to a specific player.
     *
     * @param player  target player
     * @param section section to register
     */
    public static void register(@NonNull Player player, @NonNull ActionbarSection section) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.registerSection(player, section);
        }
    }

    /**
     * Unregisters a global action bar section by identifier.
     *
     * @param sectionId section identifier
     */
    public static void unregister(@NonNull String sectionId) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.unregisterSection(sectionId);
        }
    }

    /**
     * Unregisters a player-specific section.
     *
     * @param player    target player
     * @param sectionId section identifier
     */
    public static void unregister(@NonNull Player player, @NonNull String sectionId) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.unregisterSection(player, sectionId);
        }
    }

    /**
     * Unregisters all sections registered by an owning plugin.
     *
     * <p>If the plugin is the service's owning plugin, stops the service.</p>
     *
     * @param plugin owning plugin to clean up
     */
    public static void unregisterAll(@NonNull Plugin plugin) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.unregisterAll(plugin);
        }
    }

    /**
     * Clears all active modal and slot alerts for a player and immediately refreshes their display.
     *
     * @param player target player
     */
    public static void clearAlerts(@NonNull Player player) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.clearAlerts(player);
        }
    }

    /**
     * Plays a high-performance text animation on the player's action bar.
     *
     * @param player    target player
     * @param animation text animation to play
     * @return the running Bukkit task handle
     */
    public static BukkitTask animate(@NonNull Player player, @NonNull TextAnimation animation) {
        return animate(player, animation, null);
    }

    /**
     * Plays a high-performance text animation on the player's action bar with a completion callback.
     *
     * @param player     target player
     * @param animation  text animation to play
     * @param onComplete optional action executed upon animation completion
     * @return the running Bukkit task handle
     */
    public static BukkitTask animate(@NonNull Player player, @NonNull TextAnimation animation, @Nullable Runnable onComplete) {
        return animation.playActionbar(player, onComplete);
    }

    /**
     * Plays a text animation across a collection of players' action bars.
     *
     * @param players   target players
     * @param animation text animation to play
     */
    public static void animate(@NonNull Collection<? extends Player> players, @NonNull TextAnimation animation) {
        animate(players, animation, null);
    }

    /**
     * Plays a text animation across a collection of players' action bars with a completion callback.
     *
     * @param players    target players
     * @param animation  text animation to play
     * @param onComplete optional action executed when all animations complete
     */
    public static void animate(@NonNull Collection<? extends Player> players, @NonNull TextAnimation animation, @Nullable Runnable onComplete) {
        animation.playActionbar(players, onComplete);
    }
}
