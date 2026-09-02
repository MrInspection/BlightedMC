package fr.moussax.bedrock.ui.actionbar;

import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Represents a time-limited text message displayed in place of or over action bar sections.
 */
public final class TimedAlert implements Comparable<TimedAlert> {
    private final String message;
    private final int priority;
    private final long expiresAt;

    /**
     * Creates a timed alert.
     *
     * @param message        alert message text
     * @param priority       alert priority; higher values take precedence
     * @param durationMillis duration in milliseconds before expiration
     */
    public TimedAlert(@NonNull String message, int priority, long durationMillis) {
        this.message = message;
        this.priority = priority;
        this.expiresAt = System.currentTimeMillis() + durationMillis;
    }

    /**
     * Creates a default-priority timed alert.
     *
     * @param message  alert message text
     * @param duration duration before expiration
     * @return created timed alert
     */
    public static TimedAlert of(@NonNull String message, @NonNull Duration duration) {
        return new TimedAlert(message, 0, duration.toMillis());
    }

    /**
     * Creates a timed alert with custom priority.
     *
     * @param message  alert message text
     * @param priority alert priority
     * @param duration duration before expiration
     * @return created timed alert
     */
    public static TimedAlert of(@NonNull String message, int priority, @NonNull Duration duration) {
        return new TimedAlert(message, priority, duration.toMillis());
    }

    /**
     * Checks whether this alert has expired.
     *
     * @return {@code true} if current system time has reached or passed expiration time
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }

    /**
     * Returns the alert message text.
     *
     * @return message text
     */
    @NonNull
    public String message() {
        return message;
    }

    /**
     * Returns the priority of this alert.
     *
     * @return priority value
     */
    public int priority() {
        return priority;
    }

    @Override
    public int compareTo(@NonNull TimedAlert other) {
        return Integer.compare(other.priority, this.priority); // Higher priority first
    }
}
