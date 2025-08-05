package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.event.Event;

/**
 * Represents a manager responsible for handling the behavior of a specific {@link Ability}.
 * <p>
 * An {@code AbilityManager} defines how an ability is triggered, its resource cost,
 * cooldown, and lifecycle for a {@link BlightedPlayer}.
 *
 * @param <T> the type of {@link Event} that triggers the ability
 */
public interface AbilityManager<T extends Event> {
  /**
   * Attempts to trigger the ability based on the provided event.
   *
   * @param event the event that may trigger the ability
   * @return {@code true} if the ability was successfully triggered, otherwise {@code false}
   */
  boolean triggerAbility(T event);

  /**
   * Gets the cooldown duration of the ability in seconds.
   *
   * @return the cooldown time in seconds
   */
  int getCooldownSeconds();

  /**
   * Gets the mana cost required to activate the ability.
   *
   * @return the mana cost of the ability
   */
  int getManaCost();

  /**
   * Checks whether the ability can currently be triggered by the specified player.
   *
   * @param player the player attempting to use the ability
   * @return {@code true} if the ability can be triggered, otherwise {@code false}
   */
  boolean canTrigger(BlightedPlayer player);

  /**
   * Starts the ability's effect for the specified player.
   *
   * @param player the player starting the ability
   */
  void start(BlightedPlayer player);

  /**
   * Stops the ability's effect for the specified player.
   *
   * @param player the player stopping the ability
   */
  void stop(BlightedPlayer player);
}
