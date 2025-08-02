package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.event.Event;

public interface AbilityManager<T extends Event> {
  boolean triggerAbility(T event);

  long getCooldownTicks();
  int getManaCost();
  boolean canTrigger(BlightedPlayer player);

  void start(BlightedPlayer player);
  void stop(BlightedPlayer player);
}
