package fr.moussax.blightedMC.shared.ui.actionbar;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.utils.debug.Log;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Manages player action bar lifecycle, section registrations, timed alerts, and periodic rendering.
 */
public final class ActionbarService implements Listener {

    private final Plugin plugin;
    private final Map<UUID, ActionbarComposer> composers = new ConcurrentHashMap<>();
    private final Map<String, ActionbarSection> globalSections = new ConcurrentHashMap<>();

    private BukkitTask tickerTask;
    private volatile boolean running = false;

    /**
     * Constructs an action bar service and registers its listener with the plugin manager.
     *
     * @param plugin owning plugin instance
     */
    public ActionbarService(@NonNull Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Starts the periodic action bar render task.
     *
     * @param periodTicks interval between renders in server ticks
     */
    public void start(long periodTicks) {
        if (running) return;
        running = true;

        this.tickerTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAll, periodTicks, periodTicks);
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
    }

    /**
     * Registers an action bar section globally for all present and future player composers.
     *
     * @param section section to register
     */
    public void registerSection(@NonNull ActionbarSection section) {
        globalSections.put(section.id(), section);
        composers.values().forEach(composer -> composer.registerSection(section));
    }

    /**
     * Unregisters a section globally across all active player composers.
     *
     * @param sectionId identifier of section to unregister
     */
    public void unregisterSection(@NonNull String sectionId) {
        globalSections.remove(sectionId);
        composers.values().forEach(composer -> composer.unregisterSection(sectionId));
    }

    /**
     * Executes an action with the active action bar service if present.
     *
     * @param action consumer accepting the active service instance
     */
    public static void ifPresent(@NonNull Consumer<ActionbarService> action) {
        ActionbarService service = BlightedMC.actionBarService();
        if (service != null) {
            action.accept(service);
        }
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
        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.sendModalAlert(message, priority, duration);
        renderPlayer(player);
    }

    /**
     * Sends a slot-specific alert replacing a section's text for a player and renders immediately.
     *
     * @param player    target player
     * @param sectionId target section identifier
     * @param message   alert text
     * @param duration  alert display duration
     */
    public void sendSlotAlert(@NonNull Player player, @NonNull String sectionId, @NonNull String message, @NonNull Duration duration) {
        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.sendSlotAlert(sectionId, message, duration);
        renderPlayer(player);
    }

    /**
     * Listens for player disconnects to clear cached composer instances.
     *
     * @param event player quit event
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleQuit(event.getPlayer());
    }

    /**
     * Removes and cleans up cached composer state for a player.
     *
     * @param player player to clean up
     */
    public void handleQuit(@NonNull Player player) {
        composers.remove(player.getUniqueId());
    }

    /**
     * Compiles and sends the current action bar content packet to an online player.
     *
     * @param player target player to render
     */
    public void renderPlayer(@NonNull Player player) {
        if (!player.isOnline()) return;

        ActionbarComposer composer = getOrCreateComposer(player.getUniqueId());
        String content = composer.compile(player);

        sendRawPacket(player, content);
    }

    private void tickAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                renderPlayer(player);
            } catch (Exception exception) {
                Log.warn("ActionbarService", "Failed to render for " + player.getName() + ": " + exception.getMessage());
            }
        }
    }

    private ActionbarComposer getOrCreateComposer(UUID uuid) {
        return composers.computeIfAbsent(uuid, k -> {
            ActionbarComposer composer = new ActionbarComposer();
            globalSections.values().forEach(composer::registerSection);
            return composer;
        });
    }

    private void sendRawPacket(@NonNull Player player, @NonNull String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
