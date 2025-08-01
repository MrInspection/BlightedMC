package fr.moussax.blightedMC.core.entities.LootTable;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;

public interface DroppableConsumable {
  void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer);
  String name();
}
