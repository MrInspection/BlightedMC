package fr.moussax.bedrock.ui.actionbar;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Time-limited text message displayed as a modal alert or section override.
 *
 * <p>Supports static text, dynamic player-aware suppliers, and automated decrementing countdowns.
 * When two alerts have identical priority, the newer alert takes precedence.</p>
 */
public final class TimedAlert implements Comparable<TimedAlert> {

    private final Function<Player, @Nullable String> messageSupplier;
    private final int priority;
    private final long expiresAt;
    private final long createdAt;

    /**
     * Constructs a timed alert with dynamic text evaluation, priority, and duration.
     *
     * @param messageSupplier dynamic text evaluation function
     * @param priority        precedence weight (higher values take precedence)
     * @param durationMillis  duration in milliseconds before expiration
     */
    public TimedAlert(@NonNull Function<Player, @Nullable String> messageSupplier, int priority, long durationMillis) {
        this.messageSupplier = Objects.requireNonNull(messageSupplier, "messageSupplier cannot be null");
        this.priority = priority;
        this.createdAt = System.nanoTime();
        this.expiresAt = System.currentTimeMillis() + durationMillis;
    }

    /**
     * Constructs a timed alert with static text, priority, and duration.
     *
     * @param message        alert message text
     * @param priority       precedence weight (higher values take precedence)
     * @param durationMillis duration in milliseconds before expiration
     */
    public TimedAlert(@NonNull String message, int priority, long durationMillis) {
        this(_ -> message, priority, durationMillis);
    }

    /**
     * Creates a default-priority timed alert with static text.
     *
     * @param message  alert message text
     * @param duration display duration
     * @return created alert
     */
    @NonNull
    public static TimedAlert of(@NonNull String message, @NonNull Duration duration) {
        return new TimedAlert(message, 0, duration.toMillis());
    }

    /**
     * Creates a timed alert with static text and custom priority.
     *
     * @param message  alert message text
     * @param priority precedence weight
     * @param duration display duration
     * @return created alert
     */
    @NonNull
    public static TimedAlert of(@NonNull String message, int priority, @NonNull Duration duration) {
        return new TimedAlert(message, priority, duration.toMillis());
    }

    /**
     * Creates a timed alert with dynamic text supplier and custom priority.
     *
     * @param supplier dynamic text supplier
     * @param priority precedence weight
     * @param duration display duration
     * @return created alert
     */
    @NonNull
    public static TimedAlert of(@NonNull Supplier<@Nullable String> supplier, int priority, @NonNull Duration duration) {
        Objects.requireNonNull(supplier, "supplier cannot be null");
        return new TimedAlert(_ -> supplier.get(), priority, duration.toMillis());
    }

    /**
     * Creates a timed alert with dynamic player evaluation function and custom priority.
     *
     * @param function dynamic text function
     * @param priority precedence weight
     * @param duration display duration
     * @return created alert
     */
    @NonNull
    public static TimedAlert of(@NonNull Function<Player, @Nullable String> function, int priority, @NonNull Duration duration) {
        return new TimedAlert(function, priority, duration.toMillis());
    }

    /**
     * Creates an automated decrementing countdown alert.
     *
     * @param formatter function receiving remaining integer seconds and returning formatted text
     * @param priority  precedence weight
     * @param seconds   initial countdown duration in seconds
     * @return created countdown alert
     */
    @NonNull
    public static TimedAlert countdown(@NonNull IntFunction<@Nullable String> formatter, int priority, int seconds) {
        Objects.requireNonNull(formatter, "formatter cannot be null");
        long durationMillis = seconds * 1000L;
        long targetExpiry = System.currentTimeMillis() + durationMillis;

        return new TimedAlert(_ -> {
            long remainingMillis = Math.max(0, targetExpiry - System.currentTimeMillis());
            int remainingSeconds = (int) Math.ceil(remainingMillis / 1000.0);
            return formatter.apply(remainingSeconds);
        }, priority, durationMillis);
    }

    /**
     * Creates an automated decrementing countdown alert with a format string.
     *
     * @param format   string format containing {@code %d} (e.g. {@code "Rebooting in %ds..."})
     * @param priority precedence weight
     * @param seconds  initial countdown duration in seconds
     * @return created countdown alert
     */
    @NonNull
    public static TimedAlert countdown(@NonNull String format, int priority, int seconds) {
        Objects.requireNonNull(format, "format cannot be null");
        return countdown(format::formatted, priority, seconds);
    }

    /**
     * Checks whether this alert has passed its expiration time.
     *
     * @return {@code true} if expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }

    /**
     * Evaluates and returns the alert text for a viewing player.
     *
     * @param player viewing player, or {@code null} during detached evaluation
     * @return evaluated text, or {@code null} if empty
     */
    @Nullable
    public String message(@Nullable Player player) {
        return messageSupplier.apply(player);
    }

    /**
     * Returns the alert text for detached evaluation.
     *
     * @return message text
     */
    @Nullable
    public String message() {
        return message(null);
    }

    /**
     * Returns the alert precedence priority.
     *
     * @return priority value
     */
    public int priority() {
        return priority;
    }

    /**
     * Returns the creation timestamp in nanoseconds.
     *
     * @return nanosecond creation timestamp
     */
    public long createdAt() {
        return createdAt;
    }

    @Override
    public int compareTo(@NonNull TimedAlert other) {
        int priorityDifference = Integer.compare(other.priority, this.priority);
        if (priorityDifference != 0) return priorityDifference;
        return Long.compare(other.createdAt, this.createdAt);
    }
}
