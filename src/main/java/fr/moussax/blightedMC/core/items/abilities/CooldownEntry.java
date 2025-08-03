package fr.moussax.blightedMC.core.items.abilities;

import java.time.Instant;

public record CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType, Instant endTime) {
  public CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType) {
    this(abilityManager, abilityType, Instant.now());
  }

  public long getRemainingTicks() {
    long remainingMillis = endTime.toEpochMilli() - Instant.now().toEpochMilli();
    return Math.max(0, remainingMillis / 50); // Convert to ticks (20 ticks = 1 second)
  }

  public double getRemainingSeconds() {
    return getRemainingTicks() / 20.0;
  }
}
