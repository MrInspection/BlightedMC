package fr.moussax.bedrock.ui.title;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Manages per-player title state composition, combining persistent ambient HUD titles
 * and temporary modal alerts with seamless automatic restoration and diff-suppression.
 */
public final class TitleComposer {

    private final PriorityQueue<TitleAlert> modalAlerts = new PriorityQueue<>();
    private final Object alertLock = new Object();

    private volatile PersistentTitle persistentTitle;

    private String lastSentTitle;
    private String lastSentSubtitle;
    private TimeableTitle lastSentTimes;
    private long lastTimingSentTimestamp = 0L;

    /**
     * Sets or replaces the persistent ambient title for this player.
     *
     * @param persistent persistent title configuration, or null to clear
     */
    public void setPersistentTitle(@Nullable PersistentTitle persistent) {
        this.persistentTitle = persistent;
    }

    /**
     * Removes the persistent ambient title.
     */
    public void clearPersistentTitle() {
        this.persistentTitle = null;
    }

    /**
     * Checks if a persistent title is currently active.
     *
     * @return true if a persistent title is configured
     */
    public boolean hasPersistentTitle() {
        return persistentTitle != null;
    }

    /**
     * Queues a temporary modal alert to override existing titles.
     *
     * @param alert modal alert to queue
     */
    public void sendModalAlert(@NonNull TitleAlert alert) {
        Objects.requireNonNull(alert, "alert cannot be null");
        synchronized (alertLock) {
            modalAlerts.add(alert);
        }
    }

    /**
     * Queues a temporary modal alert with specified priority and duration.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param priority alert priority
     * @param duration display duration
     */
    public void sendModalAlert(@Nullable String title, @Nullable String subtitle, int priority, @NonNull Duration duration) {
        sendModalAlert(TitleAlert.of(title, subtitle, priority, duration));
    }

    /**
     * Clears all active modal alerts and immediately restores any underlying persistent title.
     */
    public void clearAlerts() {
        synchronized (alertLock) {
            modalAlerts.clear();
        }
    }

    /**
     * Clears all modal alerts, removes the persistent title, and resets display state.
     *
     * @param player viewing player
     */
    public void clear(@NonNull Player player) {
        clearAlerts();
        clearPersistentTitle();
        player.resetTitle();
        invalidateCache();
    }

    /**
     * Invalidates cached title, subtitle, and timing states so the next render pass resends them.
     */
    public void invalidateCache() {
        lastSentTitle = null;
        lastSentSubtitle = null;
        lastSentTimes = null;
        lastTimingSentTimestamp = 0L;
    }

    /**
     * Renders the current composite title state to the given player.
     *
     * <p>Packet diff suppression ensures that identical titles are not spammed across the network,
     * while timing cache tracking prevents redundant timing packets that would reset client animations.</p>
     *
     * @param player target player
     */
    public void render(@NonNull Player player) {
        if (!player.isOnline()) {
            return;
        }

        TitleAlert activeAlert;
        synchronized (alertLock) {
            modalAlerts.removeIf(TitleAlert::isExpired);
            activeAlert = modalAlerts.peek();
        }

        String rawTitle;
        String rawSubtitle;
        TimeableTitle times;

        if (activeAlert != null) {
            rawTitle = activeAlert.title(player);
            rawSubtitle = activeAlert.subtitle(player);
            times = activeAlert.times();
            activeAlert.playSoundIfNeeded(player);
        } else if (persistentTitle != null) {
            rawTitle = persistentTitle.titleSupplier().apply(player);
            rawSubtitle = persistentTitle.subtitleSupplier().apply(player);
            times = persistentTitle.times();
        } else {
            rawTitle = null;
            rawSubtitle = null;
            times = null;
        }

        if (rawTitle == null && rawSubtitle == null) {
            if (lastSentTitle != null || lastSentSubtitle != null) {
                player.resetTitle();
                lastSentTitle = null;
                lastSentSubtitle = null;
                lastSentTimes = null;
                lastTimingSentTimestamp = 0L;
            }
            return;
        }

        String title = (rawTitle != null && !rawTitle.isEmpty())
                ? rawTitle
                : (rawSubtitle != null && !rawSubtitle.isEmpty() ? " " : "");
        String subtitle = rawSubtitle != null ? rawSubtitle : "";

        long now = System.currentTimeMillis();
        boolean timesChanged = !Objects.equals(times, lastSentTimes);
        boolean textChanged = !Objects.equals(title, lastSentTitle) || !Objects.equals(subtitle, lastSentSubtitle);

        long stayMillis = times.stay() * 50L;
        boolean nearExpiration = (now - lastTimingSentTimestamp) >= Math.max(1000L, (stayMillis * 3) / 4);

        if (timesChanged) {
            TitlePacketSender.sendFull(player, title, subtitle, times.fadeIn(), times.stay(), times.fadeOut());
            lastSentTimes = times;
            lastSentTitle = title;
            lastSentSubtitle = subtitle;
            lastTimingSentTimestamp = now;
        } else if (nearExpiration && (activeAlert != null || persistentTitle != null)) {
            // Stay is nearing expiration. Refresh stay duration with 0 fade-in to prevent opacity dipping.
            TitlePacketSender.sendFull(player, title, subtitle, 0, times.stay(), times.fadeOut());
            lastSentTitle = title;
            lastSentSubtitle = subtitle;
            lastTimingSentTimestamp = now;
        } else if (textChanged) {
            TitlePacketSender.sendTextOnly(player, title, subtitle);
            lastSentTitle = title;
            lastSentSubtitle = subtitle;
        }
    }
}
