package fr.moussax.blightedMC.engine.items.abilities;

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
public record CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType, long expirationTimeMillis) {

    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTimeMillis;
    }

    public double getRemainingCooldownTimeInSeconds() {
        long remainingMillis = expirationTimeMillis - System.currentTimeMillis();
        return Math.max(0, remainingMillis / 1000.0);
    }
}
