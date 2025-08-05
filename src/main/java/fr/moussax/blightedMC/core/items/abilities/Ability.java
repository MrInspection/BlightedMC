package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.Event;

/**
 * Represents a gameplay ability that can be attached to items, blocks, or entities.
 * <p>
 * Each ability is associated with an {@link AbilityManager} handling its event logic,
 * a unique name for identification, and an {@link AbilityType} describing its behavior.
 */
public final class Ability {
  private final AbilityManager<? extends Event> manager;
  private final String name;
  private final AbilityType type;

  /**
   * Constructs a new ability.
   *
   * @param manager the manager that handles the ability's events
   * @param name    the unique name of the ability
   * @param type    the type of the ability
   */
  public Ability(AbilityManager<? extends Event> manager, String name, AbilityType type) {
    this.manager = manager;
    this.name = name;
    this.type = type;
  }

  /**
   * Gets the manager responsible for handling this ability.
   *
   * @return the ability manager
   */
  public AbilityManager<? extends Event> getManager() {
    return manager;
  }

  /**
   * Gets the unique name of this ability.
   *
   * @return the ability name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the type of this ability, which defines when or how it is triggered.
   *
   * @return the ability type
   */
  public AbilityType getType() {
    return type;
  }
}
