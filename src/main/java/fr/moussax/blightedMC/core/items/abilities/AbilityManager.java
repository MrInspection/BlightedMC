package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.Event;
import org.bukkit.entity.Player;

public interface AbilityManager<T extends Event> {
  AbilityType getType();

  /**
   * Called to trigger the ability for a given event.
   *
   * @param event the Bukkit event that triggered this ability
   * @return true if executed successfully, false otherwise
   */
  boolean triggerAbility(T event);

  /**
   * Check if the ability can currently be triggered for a player.
   *
   * @param player the player trying to trigger the ability
   * @return true if allowed, false otherwise
   */
  default boolean canTrigger(Player player) {
    return true;
  }

  /**
   * Optional cooldown in ticks (0 = no cooldown).
   */
  default long getCooldownTicks() {
    return 0L;
  }

  /**
   * Optional mana or resource cost (0 = free).
   */
  default int getManaCost() {
    return 0;
  }
}
