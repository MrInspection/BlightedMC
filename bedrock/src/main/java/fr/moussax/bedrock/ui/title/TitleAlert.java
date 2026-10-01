package fr.moussax.bedrock.ui.title;

import fr.moussax.bedrock.sound.SoundCue;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a high-priority, temporary modal title display.
 *
 * <p>Modal alerts temporarily override persistent titles or lower-priority alerts.
 * When the alert expires, underlying persistent titles are automatically restored.</p>
 */
public final class TitleAlert implements Comparable<TitleAlert> {

    private final Function<Player, @Nullable String> titleSupplier;
    private final Function<Player, @Nullable String> subtitleSupplier;
    private final int priority;
    private final long expiresAt;
    private final TimeableTitle times;
    private final SoundCue soundCue;
    private final Set<UUID> playedPlayers = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a modal title alert with explicit parameters.
     *
     * @param titleSupplier    title text function
     * @param subtitleSupplier subtitle text function
     * @param priority         alert precedence priority
     * @param durationMillis   lifespan in milliseconds
     * @param times            timing configuration
     * @param soundCue         optional sound cue played on initial render
     */
    public TitleAlert(
            @NonNull Function<Player, @Nullable String> titleSupplier,
            @NonNull Function<Player, @Nullable String> subtitleSupplier,
            int priority,
            long durationMillis,
            @NonNull TimeableTitle times,
            @Nullable SoundCue soundCue
    ) {
        this.titleSupplier = Objects.requireNonNull(titleSupplier, "titleSupplier cannot be null");
        this.subtitleSupplier = Objects.requireNonNull(subtitleSupplier, "subtitleSupplier cannot be null");
        this.priority = priority;
        this.expiresAt = System.currentTimeMillis() + Math.max(0L, durationMillis);
        this.times = Objects.requireNonNull(times, "times cannot be null");
        this.soundCue = soundCue;
    }

    /**
     * Creates a default-priority modal alert with default alert timings.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param duration display duration
     * @return new title alert
     */
    public static TitleAlert of(@Nullable String title, @Nullable String subtitle, @NonNull Duration duration) {
        return of(title, subtitle, 0, duration, TimeableTitle.ALERT, (SoundCue) null);
    }

    /**
     * Creates a modal alert with custom priority and default alert timings.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param priority alert priority
     * @param duration display duration
     * @return new title alert
     */
    public static TitleAlert of(@Nullable String title, @Nullable String subtitle, int priority, @NonNull Duration duration) {
        return of(title, subtitle, priority, duration, TimeableTitle.ALERT, (SoundCue) null);
    }

    /**
     * Creates a modal alert with custom priority and timings.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param priority alert priority
     * @param duration display duration
     * @param times    timing configuration
     * @return new title alert
     */
    public static TitleAlert of(
            @Nullable String title,
            @Nullable String subtitle,
            int priority,
            @NonNull Duration duration,
            @NonNull TimeableTitle times
    ) {
        return of(title, subtitle, priority, duration, times, (SoundCue) null);
    }

    /**
     * Creates a modal alert with custom priority, timings, and an associated sound cue.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param priority alert priority
     * @param duration display duration
     * @param times    timing configuration
     * @param soundCue sound cue played when shown
     * @return new title alert
     */
    public static TitleAlert of(
            @Nullable String title,
            @Nullable String subtitle,
            int priority,
            @NonNull Duration duration,
            @NonNull TimeableTitle times,
            @Nullable SoundCue soundCue
    ) {
        String finalTitle = title != null ? title : "";
        String finalSubtitle = subtitle != null ? subtitle : "";
        return new TitleAlert(_ -> finalTitle, _ -> finalSubtitle, priority, duration.toMillis(), times, soundCue);
    }

    /**
     * Creates a modal alert with custom priority, timings, and an associated Bukkit sound.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param priority alert priority
     * @param duration display duration
     * @param times    timing configuration
     * @param sound    sound to play when displayed
     * @return new title alert
     */
    public static TitleAlert of(
            @Nullable String title,
            @Nullable String subtitle,
            int priority,
            @NonNull Duration duration,
            @NonNull TimeableTitle times,
            @Nullable Sound sound
    ) {
        SoundCue soundCue = sound != null ? new SoundCue(sound, 1.0f, 1.0f, 0L) : null;
        return of(title, subtitle, priority, duration, times, soundCue);
    }

    /**
     * Creates a dynamic modal alert evaluating dynamic suppliers.
     *
     * @param titleSupplier    title text supplier
     * @param subtitleSupplier subtitle text supplier
     * @param priority         alert priority
     * @param duration         display duration
     * @param times            timing configuration
     * @return new title alert
     */
    public static TitleAlert of(
            @NonNull Supplier<@Nullable String> titleSupplier,
            @NonNull Supplier<@Nullable String> subtitleSupplier,
            int priority,
            @NonNull Duration duration,
            @NonNull TimeableTitle times
    ) {
        Objects.requireNonNull(titleSupplier, "titleSupplier cannot be null");
        Objects.requireNonNull(subtitleSupplier, "subtitleSupplier cannot be null");
        return new TitleAlert(_ -> titleSupplier.get(), _ -> subtitleSupplier.get(), priority, duration.toMillis(), times, null);
    }

    /**
     * Checks whether this alert has reached the end of its lifespan.
     *
     * @return true if expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }

    /**
     * Returns the remaining time in milliseconds before expiration.
     *
     * @return remaining milliseconds
     */
    public long remainingMillis() {
        return Math.max(0L, expiresAt - System.currentTimeMillis());
    }

    /**
     * Evaluates the title string for the given player.
     *
     * @param player viewing player
     * @return evaluated title string
     */
    public @Nullable String title(@Nullable Player player) {
        return titleSupplier.apply(player);
    }

    /**
     * Evaluates the subtitle string for the given player.
     *
     * @param player viewing player
     * @return evaluated subtitle string
     */
    public @Nullable String subtitle(@Nullable Player player) {
        return subtitleSupplier.apply(player);
    }

    /**
     * Returns the alert priority.
     *
     * @return priority value
     */
    public int priority() {
        return priority;
    }

    /**
     * Returns the timing configuration for this alert.
     *
     * @return timing configuration
     */
    public TimeableTitle times() {
        return times;
    }

    /**
     * Plays the associated sound cue for the given player if not already played.
     *
     * @param player target player
     */
    public void playSoundIfNeeded(@NonNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        if (soundCue != null && playedPlayers.add(player.getUniqueId())) {
            soundCue.play(player);
        }
    }

    @Override
    public int compareTo(@NonNull TitleAlert other) {
        int precedence = Integer.compare(other.priority, this.priority);
        if (precedence != 0) {
            return precedence;
        }
        return Long.compare(this.expiresAt, other.expiresAt);
    }
}
