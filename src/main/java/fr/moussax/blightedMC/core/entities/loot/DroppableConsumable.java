package fr.moussax.blightedMC.core.entities.loot;

import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;

public interface DroppableConsumable {
    /**
     * Consumes or drops the item/loot, either directly to the player or at a specified location.
     *
     * @param killer       player responsible for the loot (nullable)
     * @param dropLocation location where the loot should be dropped if not given to the player
     * @param toPlayer     whether to give the loot directly to the player's inventory
     */
    void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer);

    /**
     * Returns the display name of this droppable consumable.
     *
     * @return the name of the item/loot
     */
    String name();
}
