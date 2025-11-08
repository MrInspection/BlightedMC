package fr.moussax.blightedMC.core.entities.loot.favors;

import fr.moussax.blightedMC.core.entities.loot.DroppableConsumable;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;

import java.util.Objects;

public record FavorsLoot(int amount) implements DroppableConsumable {
  @Override
  public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer) {
    if(toPlayer) {
      killer.addFavors(amount);
    } else {
      Objects.requireNonNull(dropLocation.getWorld()).dropItemNaturally(dropLocation, new FavorsItem(amount).createItemStack());
    }
  }

  @Override
  public String name() {
    return "§5Blighted Gemstone";
  }
}
