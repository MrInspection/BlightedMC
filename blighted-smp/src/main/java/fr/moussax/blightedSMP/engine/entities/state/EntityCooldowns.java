package fr.moussax.blightedSMP.engine.entities.state;

import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongSupplier;

/**
 * Tracks named ability cooldowns with thread-safe timestamp management.
 */
public final class EntityCooldowns {

    private final LongSupplier timeSupplier;
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    /**
     * Creates a cooldown tracker using {@link System#currentTimeMillis()} as time source.
     */
    public EntityCooldowns() {
        this(System::currentTimeMillis);
    }

    /**
     * Creates a cooldown tracker with an injectable time source for deterministic testing.
     *
     * @param timeSupplier time supplier providing current epoch milliseconds
     */
    public EntityCooldowns(@NonNull LongSupplier timeSupplier) {
        this.timeSupplier = Objects.requireNonNull(timeSupplier, "timeSupplier cannot be null");
    }

    /**
     * Checks if the specified ability cooldown has elapsed.
     *
     * @param abilityKey     unique identifier for the ability
     * @param cooldownMillis duration in milliseconds
     * @return {@code true} if ready to use, {@code false} otherwise
     */
    public boolean isReady(@NonNull String abilityKey, long cooldownMillis) {
        Long lastTriggered = cooldowns.get(abilityKey);
        return lastTriggered == null || (timeSupplier.getAsLong() - lastTriggered) >= cooldownMillis;
    }

    /**
     * Triggers the cooldown for the specified ability, setting its timestamp to the current time.
     *
     * @param abilityKey unique identifier for the ability
     */
    public void trigger(@NonNull String abilityKey) {
        cooldowns.put(abilityKey, timeSupplier.getAsLong());
    }

    /**
     * Atomically checks if the ability is ready and triggers it if so.
     *
     * @param abilityKey     unique identifier for the ability
     * @param cooldownMillis duration in milliseconds
     * @return {@code true} if ready and triggered, {@code false} otherwise
     */
    public boolean checkAndTrigger(@NonNull String abilityKey, long cooldownMillis) {
        Objects.requireNonNull(abilityKey, "abilityKey cannot be null");
        long now = timeSupplier.getAsLong();
        AtomicBoolean triggered = new AtomicBoolean(false);
        cooldowns.compute(abilityKey, (_, last) -> {
            if (last == null || (now - last) >= cooldownMillis) {
                triggered.set(true);
                return now;
            }
            triggered.set(false);
            return last;
        });
        return triggered.get();
    }

    /**
     * Resets the cooldown for the specified ability, allowing immediate reuse.
     *
     * @param abilityKey unique identifier for the ability
     */
    public void reset(@NonNull String abilityKey) {
        cooldowns.remove(abilityKey);
    }

    /**
     * Resets all tracked ability cooldowns.
     */
    public void resetAll() {
        cooldowns.clear();
    }

    /**
     * Returns the remaining cooldown duration in milliseconds.
     *
     * @param abilityKey     unique identifier for the ability
     * @param cooldownMillis total cooldown duration in milliseconds
     * @return remaining milliseconds, or 0 if cooldown has expired
     */
    public long getRemainingMillis(@NonNull String abilityKey, long cooldownMillis) {
        Long lastTriggered = cooldowns.get(abilityKey);
        if (lastTriggered == null) {
            return 0;
        }
        long elapsed = timeSupplier.getAsLong() - lastTriggered;
        return Math.max(0, cooldownMillis - elapsed);
    }
}
