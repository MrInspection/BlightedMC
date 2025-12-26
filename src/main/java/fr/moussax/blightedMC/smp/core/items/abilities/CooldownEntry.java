package fr.moussax.blightedMC.smp.core.items.abilities;

import java.time.Instant;

/**
 * Tracks a cooldown for a specific ability.
 *
 * <p>Stores the ability's manager class, type, and expiration time.
 * Provides methods to check remaining cooldown in ticks or seconds.</p>
 *
 * @param abilityManager the class of the {@link AbilityManager} owning this cooldown
 * @param abilityType    the ability type under cooldown
 * @param endTime        when the cooldown expires
 */
public record CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType, Instant endTime) {
    public CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType) {
        this(abilityManager, abilityType, Instant.now());
    }

    public long getRemainingTicks() {
        long remainingMillis = endTime.toEpochMilli() - Instant.now().toEpochMilli();
        return Math.max(0, remainingMillis / 50); // Convert to ticks (20 ticks = 1 second)
    }

    public double getRemainingCooldownTimeInSeconds() {
        return getRemainingTicks() / 20.0;
    }
}
