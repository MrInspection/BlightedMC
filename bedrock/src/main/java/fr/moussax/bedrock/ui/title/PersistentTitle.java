package fr.moussax.bedrock.ui.title;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an ambient or persistent title display maintained continuously on a player's screen.
 *
 * <p>Designed for minigame HUDs (waiting lobbies, spectator view, boss encounters, mission objectives).
 * When higher-priority modal alerts or countdowns finish, the persistent title is automatically
 * restored without flickering or manual bookkeeping.</p>
 *
 * @param titleSupplier    computes the main title string for a player
 * @param subtitleSupplier computes the subtitle string for a player
 * @param times            timing configuration used during display and periodic refresh
 */
public record PersistentTitle(
        @NonNull Function<Player, @Nullable String> titleSupplier,
        @NonNull Function<Player, @Nullable String> subtitleSupplier,
        @NonNull TimeableTitle times
) {

    public PersistentTitle {
        Objects.requireNonNull(titleSupplier, "titleSupplier cannot be null");
        Objects.requireNonNull(subtitleSupplier, "subtitleSupplier cannot be null");
        Objects.requireNonNull(times, "times cannot be null");
    }

    /**
     * Constructs a static persistent title with constant text strings and default permanent timings.
     *
     * @param title    constant title text
     * @param subtitle constant subtitle text
     * @return persistent title instance
     */
    public static PersistentTitle of(@Nullable String title, @Nullable String subtitle) {
        return of(title, subtitle, TimeableTitle.PERMANENT);
    }

    /**
     * Constructs a static persistent title with constant text strings and custom timings.
     *
     * @param title    constant title text
     * @param subtitle constant subtitle text
     * @param times    custom display timings
     * @return persistent title instance
     */
    public static PersistentTitle of(@Nullable String title, @Nullable String subtitle, @NonNull TimeableTitle times) {
        String finalTitle = title != null ? title : "";
        String finalSubtitle = subtitle != null ? subtitle : "";
        return new PersistentTitle(_ -> finalTitle, _ -> finalSubtitle, times);
    }

    /**
     * Constructs a static persistent title with constant text strings and a custom stay duration.
     *
     * @param title        constant title text
     * @param subtitle     constant subtitle text
     * @param stayDuration duration to remain visible between periodic refreshes
     * @return persistent title instance
     */
    public static PersistentTitle of(@Nullable String title, @Nullable String subtitle, @NonNull Duration stayDuration) {
        return of(title, subtitle, TimeableTitle.of(Duration.ZERO, stayDuration, Duration.ofMillis(500)));
    }

    /**
     * Constructs a persistent title evaluating dynamic parameterless suppliers on each render cycle with default permanent timings.
     *
     * @param titleSupplier    supplier for title text
     * @param subtitleSupplier supplier for subtitle text
     * @return persistent title instance
     */
    public static PersistentTitle of(
            @NonNull Supplier<@Nullable String> titleSupplier,
            @NonNull Supplier<@Nullable String> subtitleSupplier
    ) {
        return of(titleSupplier, subtitleSupplier, TimeableTitle.PERMANENT);
    }

    /**
     * Constructs a persistent title evaluating dynamic parameterless suppliers with custom timings.
     *
     * @param titleSupplier    supplier for title text
     * @param subtitleSupplier supplier for subtitle text
     * @param times            custom display timings
     * @return persistent title instance
     */
    public static PersistentTitle of(
            @NonNull Supplier<@Nullable String> titleSupplier,
            @NonNull Supplier<@Nullable String> subtitleSupplier,
            @NonNull TimeableTitle times
    ) {
        Objects.requireNonNull(titleSupplier, "titleSupplier cannot be null");
        Objects.requireNonNull(subtitleSupplier, "subtitleSupplier cannot be null");
        return new PersistentTitle(_ -> titleSupplier.get(), _ -> subtitleSupplier.get(), times);
    }

    /**
     * Constructs a persistent title evaluating per-player dynamic functions on each render cycle with default permanent timings.
     *
     * @param titleSupplier    per-player title function
     * @param subtitleSupplier per-player subtitle function
     * @return persistent title instance
     */
    public static PersistentTitle of(
            @NonNull Function<Player, @Nullable String> titleSupplier,
            @NonNull Function<Player, @Nullable String> subtitleSupplier
    ) {
        return new PersistentTitle(titleSupplier, subtitleSupplier, TimeableTitle.PERMANENT);
    }

    /**
     * Constructs a persistent title with per-player dynamic functions and custom timing parameters.
     *
     * @param titleSupplier    per-player title function
     * @param subtitleSupplier per-player subtitle function
     * @param times            custom timing specification
     * @return persistent title instance
     */
    public static PersistentTitle of(
            @NonNull Function<Player, @Nullable String> titleSupplier,
            @NonNull Function<Player, @Nullable String> subtitleSupplier,
            @NonNull TimeableTitle times
    ) {
        return new PersistentTitle(titleSupplier, subtitleSupplier, times);
    }
}
