package fr.moussax.blightedMC.core.items.abilities;

import java.time.Instant;

/**
 * Represents a cooldown entry for a specific ability, tracking its manager, type, and end time.
 * <p>
 * Provides utilities to check remaining cooldown in ticks or seconds.
 *
 * @param abilityManager the class of the {@link AbilityManager} that owns this cooldown
 * @param abilityType    the type of ability under cooldown
 * @param endTime        the instant when the cooldown expires
 */
public record CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType, Instant endTime) {
  /**
   * Constructs a new cooldown entry starting from the current instant.
   *
   * @param abilityManager the class managing this ability
   * @param abilityType    the type of ability
   */
  public CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType) {
    this(abilityManager, abilityType, Instant.now());
  }

  /**
   * Calculates the remaining cooldown time in Minecraft ticks.
   *
   * @return the number of ticks remaining until the cooldown ends; 0 if expired
   */
  public long getRemainingTicks() {
    long remainingMillis = endTime.toEpochMilli() - Instant.now().toEpochMilli();
    return Math.max(0, remainingMillis / 50); // Convert to ticks (20 ticks = 1 second)
  }

  /**
   * Calculates the remaining cooldown time in seconds.
   *
   * @return the remaining cooldown in seconds, or 0 if expired
   */
  public double getRemainingSeconds() {
    return getRemainingTicks() / 20.0;
  }
}
