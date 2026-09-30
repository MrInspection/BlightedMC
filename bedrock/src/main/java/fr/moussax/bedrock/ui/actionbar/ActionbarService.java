package fr.moussax.bedrock.ui.actionbar;

import fr.moussax.bedrock.utils.debug.Log;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Manages player action bar lifecycle, section registrations, timed alerts, and periodic rendering.
 *
 * <p>Centralizes action bar rendering across all plugins to prevent packet collisions and flickering.
 * Incorporates packet diff suppression to avoid sending redundant network packets when displayed
 * content is unchanged.</p>
 */
public final class ActionbarService implements Listener {

    private static final long FADE_TIMEOUT_MILLIS = 1750L;

    @Getter
    @Setter
    private static volatile ActionbarService instance;

    private final Plugin plugin;
    private final Map<UUID, ActionbarComposer> composers = new ConcurrentHashMap<>();
    private final Map<String, ActionbarSection> globalSections = new ConcurrentHashMap<>();
    private final Map<Plugin, Set<String>> pluginSections = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastSentTexts = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSentTimestamps = new ConcurrentHashMap<>();

    private volatile String separator = "     ";
    private BukkitTask tickerTask;
    private volatile boolean running = false;
    private long currentPeriodTicks = 20L;

    /**
     * Constructs an action bar service and registers quit events with the Bukkit plugin manager.
     *
     * @param plugin owning plugin instance
     */
    public ActionbarService(@NonNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        if (instance == null) {
            instance = this;
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Obtains the shared {@link ActionbarService} singleton, or constructs one if not yet initialized.
     *
     * @param plugin owning plugin
     * @return the active action bar service
     */
    @NonNull
    public static ActionbarService getOrCreate(@NonNull Plugin plugin) {
        if (instance == null) {
            synchronized (ActionbarService.class) {
                if (instance == null) {
                    new ActionbarService(plugin);
                }
            }
        }
        return instance;
    }

    /**
     * Starts or updates the periodic action bar render task.
     * If already running with a slower refresh rate, speeds up to match the faster requested period.
     *
     * @param periodTicks interval between renders in server ticks
     */
    public void start(long periodTicks) {
        if (running && tickerTask != null && !tickerTask.isCancelled()) {
            if (periodTicks < currentPeriodTicks) {
                this.currentPeriodTicks = periodTicks;
                tickerTask.cancel();
                this.tickerTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAll, 0L, periodTicks);
            }
            return;
        }

        this.currentPeriodTicks = periodTicks;
        this.running = true;

        if (tickerTask != null) {
            tickerTask.cancel();
        }

        this.tickerTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAll, 0L, periodTicks);
    }

    /**
     * Stops the periodic action bar render task and clears composer state.
     */
    public void stop() {
        running = false;
        if (tickerTask != null) {
            tickerTask.cancel();
            tickerTask = null;
        }
        composers.clear();
        lastSentTexts.clear();
        lastSentTimestamps.clear();
    }

    /**
     * Sets the default delimiter string rendered between adjacent standard sections.
     *
     * @param separator delimiter text
     */
    public void setSeparator(@NonNull String separator) {
        this.separator = Objects.requireNonNull(separator, "separator cannot be null");
        composers.values().forEach(composer -> composer.setSeparator(separator));
    }

    /**
     * Sets the default delimiter string rendered between adjacent standard sections for a specific player.
     *
     * @param player    target player
     * @param separator delimiter text
     */
    public void setSeparator(@NonNull Player player, @NonNull String separator) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(separator, "separator cannot be null");
        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.setSeparator(separator);
    }

    /**
     * Returns the current section delimiter.
     *
     * @return separator string
     */
    @NonNull
    public String getSeparator() {
        return separator;
    }

    /**
     * Returns the section delimiter configured for a specific player, or the global default.
     *
     * @param player target player
     * @return separator string
     */
    @NonNull
    public String getSeparator(@NonNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        ActionbarComposer composer = composers.get(player.getUniqueId());
        return composer != null ? composer.getSeparator() : separator;
    }

    /**
     * Registers a persistent section globally across all players.
     *
     * @param section section to register
     */
    public void registerSection(@NonNull ActionbarSection section) {
        globalSections.put(section.id(), section);
    }

    /**
     * Registers a persistent global section associated with an owning plugin.
     * Allows bulk unregistration when the owning plugin disables or reloads.
     *
     * @param plugin  owning plugin
     * @param section section to register
     */
    public void registerSection(@NonNull Plugin plugin, @NonNull ActionbarSection section) {
        pluginSections.computeIfAbsent(plugin, _ -> ConcurrentHashMap.newKeySet()).add(section.id());
        registerSection(section);
    }

    /**
     * Registers a section visible exclusively to a specific player.
     *
     * @param player  target player
     * @param section section to register
     */
    public void registerSection(@NonNull Player player, @NonNull ActionbarSection section) {
        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.registerSection(section);
    }

    /**
     * Unregisters a section globally.
     *
     * @param sectionId identifier of section to unregister
     */
    public void unregisterSection(@NonNull String sectionId) {
        globalSections.remove(sectionId);
        composers.values().forEach(composer -> composer.unregisterSection(sectionId));
    }

    /**
     * Unregisters a player-specific section.
     *
     * @param player    target player
     * @param sectionId section identifier
     */
    public void unregisterSection(@NonNull Player player, @NonNull String sectionId) {
        ActionbarComposer composer = composers.get(player.getUniqueId());
        if (composer != null) {
            composer.unregisterSection(sectionId);
        }
    }

    /**
     * Unregisters all sections associated with an owning plugin without stopping the service.
     *
     * @param plugin owning plugin to clean up
     */
    public void unregisterAll(@NonNull Plugin plugin) {
        Set<String> sectionIds = pluginSections.remove(plugin);
        if (sectionIds != null) {
            sectionIds.forEach(this::unregisterSection);
        }
    }

    /**
     * Sends a modal alert to a player using the default duration (2 seconds) and immediately renders.
     *
     * @param player  target player
     * @param message alert text
     */
    public void sendAlert(@NonNull Player player, @NonNull String message) {
        sendAlert(player, message, 0, Actionbar.DEFAULT_DURATION);
    }

    /**
     * Sends a default-priority modal alert to a player and immediately renders their action bar.
     *
     * @param player   target player
     * @param message  alert text
     * @param duration alert display duration
     */
    public void sendAlert(@NonNull Player player, @NonNull String message, @NonNull Duration duration) {
        sendAlert(player, message, 0, duration);
    }

    /**
     * Sends a modal alert with custom priority to a player and immediately renders their action bar.
     *
     * @param player   target player
     * @param message  alert text
     * @param priority alert priority
     * @param duration alert display duration
     */
    public void sendAlert(@NonNull Player player, @NonNull String message, int priority, @NonNull Duration duration) {
        sendAlert(player, TimedAlert.of(message, priority, duration));
    }

    /**
     * Sends a dynamic modal alert that evaluates text dynamically on every render pass.
     *
     * @param player   target player
     * @param supplier dynamic text supplier
     * @param priority alert priority
     * @param duration alert display duration
     */
    public void sendAlert(@NonNull Player player, @NonNull Supplier<String> supplier, int priority, @NonNull Duration duration) {
        sendAlert(player, TimedAlert.of(supplier, priority, duration));
    }

    /**
     * Sends a dynamic modal alert with default priority (0).
     *
     * @param player   target player
     * @param supplier dynamic text supplier
     * @param duration alert display duration
     */
    public void sendAlert(@NonNull Player player, @NonNull Supplier<String> supplier, @NonNull Duration duration) {
        sendAlert(player, supplier, 0, duration);
    }

    /**
     * Sends a prepared {@link TimedAlert} as a modal alert to a player.
     *
     * @param player target player
     * @param alert  timed alert
     */
    public void sendAlert(@NonNull Player player, @NonNull TimedAlert alert) {
        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.sendModalAlert(alert);
        renderPlayer(player);
    }

    /**
     * Sends an automated decrementing countdown alert to a player.
     *
     * @param player   target player
     * @param format   string format containing {@code %d}
     * @param seconds  countdown seconds
     * @param priority alert priority
     */
    public void sendCountdown(@NonNull Player player, @NonNull String format, int seconds, int priority) {
        sendAlert(player, TimedAlert.countdown(format, priority, seconds));
    }

    /**
     * Sends an automated decrementing countdown alert to a player with default priority (0).
     *
     * @param player  target player
     * @param format  string format containing {@code %d}
     * @param seconds countdown seconds
     */
    public void sendCountdown(@NonNull Player player, @NonNull String format, int seconds) {
        sendCountdown(player, format, seconds, 0);
    }

    /**
     * Sends an automated decrementing countdown alert with a custom formatter.
     *
     * @param player    target player
     * @param formatter function receiving remaining seconds
     * @param seconds   countdown seconds
     * @param priority  alert priority
     */
    public void sendCountdown(@NonNull Player player, @NonNull IntFunction<String> formatter, int seconds, int priority) {
        sendAlert(player, TimedAlert.countdown(formatter, priority, seconds));
    }

    /**
     * Sends an automated decrementing countdown alert with a custom formatter and default priority (0).
     *
     * @param player    target player
     * @param formatter function receiving remaining seconds
     * @param seconds   countdown seconds
     */
    public void sendCountdown(@NonNull Player player, @NonNull IntFunction<String> formatter, int seconds) {
        sendCountdown(player, formatter, seconds, 0);
    }

    /**
     * Sends a slot-specific alert replacing a section's text using the default duration (2 seconds).
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param message   alert text
     */
    public void sendSlotAlert(@NonNull Player player, @NonNull String sectionId, @NonNull String message) {
        sendSlotAlert(player, sectionId, message, Actionbar.DEFAULT_DURATION);
    }

    /**
     * Sends a slot-specific alert replacing a section's text for a player and renders immediately.
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param message   alert text
     * @param duration  alert display duration
     */
    public void sendSlotAlert(
            @NonNull Player player,
            @NonNull String sectionId,
            @NonNull String message,
            @NonNull Duration duration
    ) {
        sendSlotAlert(player, sectionId, TimedAlert.of(message, duration));
    }

    /**
     * Sends a slot-specific alert with dynamic text evaluation for a player.
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param supplier  dynamic text supplier
     * @param duration  alert display duration
     */
    public void sendSlotAlert(
            @NonNull Player player,
            @NonNull String sectionId,
            @NonNull Supplier<String> supplier,
            @NonNull Duration duration
    ) {
        sendSlotAlert(player, sectionId, TimedAlert.of(supplier, 0, duration));
    }

    /**
     * Sends a prepared {@link TimedAlert} to override a specific section for a player.
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param alert     timed alert
     */
    public void sendSlotAlert(@NonNull Player player, @NonNull String sectionId, @NonNull TimedAlert alert) {
        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.sendSlotAlert(sectionId, alert);
        renderPlayer(player);
    }

    /**
     * Sends an automated decrementing countdown alert to override a specific section.
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param format    format string containing {@code %d}
     * @param seconds   countdown seconds
     */
    public void sendSlotCountdown(@NonNull Player player, @NonNull String sectionId, @NonNull String format, int seconds) {
        sendSlotAlert(player, sectionId, TimedAlert.countdown(format, 0, seconds));
    }

    /**
     * Clears all active modal and slot alerts for a player and immediately updates their action bar.
     *
     * @param player target player
     */
    public void clearAlerts(@NonNull Player player) {
        ActionbarComposer composer = composers.get(player.getUniqueId());
        if (composer != null) {
            composer.clearAlerts();
            renderPlayer(player);
        }
    }

    /**
     * Removes and cleans up cached composer and tracking state when a player leaves the server.
     *
     * @param event player quit event
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    /**
     * Removes and cleans up cached composer and tracking state for a player.
     *
     * @param player player to clean up
     */
    public void handleQuit(@NonNull Player player) {
        UUID uuid = player.getUniqueId();
        composers.remove(uuid);
        lastSentTexts.remove(uuid);
        lastSentTimestamps.remove(uuid);
    }

    /**
     * Compiles and sends the action bar content packet to an online player.
     * Skips sending duplicate packets if the content has not changed and has not neared client fade-out.
     *
     * @param player target player to render
     */
    public void renderPlayer(@NonNull Player player) {
        if (!player.isOnline()) return;

        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        String content = composer.compile(player);
        UUID uuid = player.getUniqueId();

        long now = System.currentTimeMillis();
        String lastText = lastSentTexts.get(uuid);
        Long lastSentTimestamp = lastSentTimestamps.get(uuid);

        if (content.equals(lastText) && lastSentTimestamp != null && (now - lastSentTimestamp) < FADE_TIMEOUT_MILLIS) {
            return;
        }

        sendRawPacket(player, content);
        lastSentTexts.put(uuid, content);
        lastSentTimestamps.put(uuid, now);
    }

    private void tickAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                renderPlayer(player);
            } catch (Exception exception) {
                Log.warn("ActionbarService", "Failed to render actionbar for " + player.getName() + ": " + exception.getMessage());
            }
        }
    }

    private ActionbarComposer getOrCreateComposer(UUID uuid) {
        return composers.computeIfAbsent(uuid, _ -> {
            ActionbarComposer composer = new ActionbarComposer(() -> globalSections);
            composer.setSeparator(separator);
            return composer;
        });
    }

    private void sendRawPacket(@NonNull Player player, @NonNull String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
