package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.event.Event;

/**
 * Handles the behavior and lifecycle of a specific {@link Ability} for a {@link BlightedPlayer}.
 * <p>
 * Defines triggering, resource cost, cooldown, and activation/deactivation of the ability.
 *
 * @param <T> the type of {@link Event} that triggers the ability
 */
public interface AbilityManager<T extends Event> {
  boolean triggerAbility(T event);

  int getCooldownSeconds();

  int getManaCost();

  boolean canTrigger(BlightedPlayer player);

  void start(BlightedPlayer player);

  void stop(BlightedPlayer player);
}
