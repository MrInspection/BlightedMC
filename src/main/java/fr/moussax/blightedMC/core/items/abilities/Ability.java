package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.Event;

public final class Ability {

  private final AbilityManager<? extends Event> manager;
  private final String name;
  private final AbilityType type;

  public Ability(AbilityManager<? extends Event> manager, String name, AbilityType type) {
    this.manager = manager;
    this.name = name;
    this.type = type;
  }

  public AbilityManager<? extends Event> getManager() {
    return manager;
  }

  public String getName() {
    return name;
  }

  public AbilityType getType() {
    return type;
  }
}
