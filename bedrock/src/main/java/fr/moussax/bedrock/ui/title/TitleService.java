package fr.moussax.bedrock.ui.title;

import fr.moussax.bedrock.utils.debug.Log;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player title lifecycles, composers, modal alert queues, and periodic rendering passes.
 *
 * <p>Centralizes title packet dispatch to eliminate flicker, coordinate modal alert priorities,
 * and seamlessly restore persistent titles (e.g. minigame HUDs) upon alert expiration.</p>
 */
public final class TitleService implements Listener {

    @Getter
    @Setter
    private static volatile TitleService instance;

    @Getter
    private final Plugin plugin;
    private final Map<UUID, TitleComposer> composers = new ConcurrentHashMap<>();
    private final Set<UUID> animatingPlayers = ConcurrentHashMap.newKeySet();
    private BukkitTask tickerTask;
    private volatile boolean running = false;
    private long currentPeriodTicks = 10L;

    /**
     * Constructs a title service and registers quit listeners with Bukkit.
     *
     * @param plugin owning plugin
     */
    public TitleService(@NonNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        if (instance == null) {
            instance = this;
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Obtains the shared {@link TitleService} singleton, or constructs one if not yet initialized.
     *
     * @param plugin owning plugin
     * @return active title service instance
     */
    @NonNull
    public static TitleService getOrCreate(@NonNull Plugin plugin) {
        if (instance == null) {
            synchronized (TitleService.class) {
                if (instance == null) {
                    new TitleService(plugin);
                }
            }
        }
        return instance;
    }

    /**
     * Starts or updates the periodic title render task.
     *
     * @param periodTicks interval between periodic renders in server ticks
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
     * Stops the periodic render task and cleans up all active composers.
     */
    public void stop() {
        running = false;
        if (tickerTask != null) {
            tickerTask.cancel();
            tickerTask = null;
        }
        composers.clear();
        HandlerList.unregisterAll(this);
    }

    /**
     * Retrieves or creates a title composer for the specified player UUID.
     *
     * @param uuid player unique ID
     * @return player's title composer
     */
    @NonNull
    public TitleComposer getOrCreateComposer(@NonNull UUID uuid) {
        return composers.computeIfAbsent(uuid, _ -> new TitleComposer());
    }

    /**
     * Sets a persistent ambient title on a player and immediately renders it.
     *
     * @param player          target player
     * @param persistentTitle persistent title configuration
     */
    public void setPersistent(@NonNull Player player, @NonNull PersistentTitle persistentTitle) {
        TitleComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.setPersistentTitle(persistentTitle);
        renderPlayer(player);
    }

    /**
     * Clears any active persistent title from a player.
     *
     * @param player target player
     */
    public void clearPersistent(@NonNull Player player) {
        TitleComposer composer = composers.get(player.getUniqueId());
        if (composer != null) {
            composer.clearPersistentTitle();
            renderPlayer(player);
        }
    }

    /**
     * Checks if a player has an active persistent title.
     *
     * @param player target player
     * @return true if persistent title is active
     */
    public boolean hasPersistent(@NonNull Player player) {
        TitleComposer composer = composers.get(player.getUniqueId());
        return composer != null && composer.hasPersistentTitle();
    }

    /**
     * Queues a modal alert and immediately forces a render pass.
     *
     * @param player target player
     * @param alert  modal alert
     */
    public void sendAlert(@NonNull Player player, @NonNull TitleAlert alert) {
        TitleComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.sendModalAlert(alert);
        renderPlayer(player);
    }

    /**
     * Clears all active modal alerts and restores any persistent title.
     *
     * @param player target player
     */
    public void clearAlerts(@NonNull Player player) {
        TitleComposer composer = composers.get(player.getUniqueId());
        if (composer != null) {
            composer.clearAlerts();
            renderPlayer(player);
        }
    }

    /**
     * Clears both alerts and persistent titles for a player, resetting the client title.
     *
     * @param player target player
     */
    public void clearAll(@NonNull Player player) {
        TitleComposer composer = composers.get(player.getUniqueId());
        if (composer != null) {
            composer.clear(player);
        } else {
            player.resetTitle();
        }
    }

    /**
     * Marks whether a player currently has an active text animation running on their title.
     * When animating, periodic persistent title rendering is suspended to prevent frame stutter.
     *
     * @param uuid      target player UUID
     * @param animating whether an animation is currently executing
     */
    public void setAnimating(@NonNull UUID uuid, boolean animating) {
        if (animating) {
            animatingPlayers.add(uuid);
        } else {
            animatingPlayers.remove(uuid);
        }
    }

    /**
     * Checks if a player currently has an active text animation running on their title.
     *
     * @param uuid target player UUID
     * @return true if an animation is active
     */
    public boolean isAnimating(@NonNull UUID uuid) {
        return animatingPlayers.contains(uuid);
    }

    /**
     * Immediately evaluates and renders title updates for a player.
     *
     * @param player target player
     */
    public void renderPlayer(@NonNull Player player) {
        if (!player.isOnline()) {
            return;
        }
        if (isAnimating(player.getUniqueId())) {
            return;
        }
        TitleComposer composer = getOrCreateComposer(player.getUniqueId());
        composer.render(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        animatingPlayers.remove(uuid);
        composers.remove(uuid);
    }

    private void tickAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                renderPlayer(player);
            } catch (Exception exception) {
                Log.warn("TitleService", "Failed to render title for " + player.getName() + ": " + exception.getMessage());
            }
        }
    }
}
