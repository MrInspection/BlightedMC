package fr.moussax.blightedMC.core.events;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.event.Event;

public abstract class BlightedPlayerEvent extends Event {
  public abstract BlightedPlayer getBlightedPlayer();
}
