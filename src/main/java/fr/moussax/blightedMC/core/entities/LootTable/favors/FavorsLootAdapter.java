package fr.moussax.blightedMC.core.entities.LootTable.favors;

import fr.moussax.blightedMC.core.entities.LootTable.ItemLoot;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class FavorsLootAdapter extends ItemLoot {
  private final FavorsLoot favorsLoot;

  public FavorsLootAdapter(FavorsLoot favors, ItemStack displayItem) {
    super(displayItem, favors.amount(), favors.amount());
    this.favorsLoot = favors;
  }

  @Override
  public int generateAmount() {
    return 1; 
  }

  @Override
  public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer, int amount) {
    favorsLoot.consume(killer, dropLocation, toPlayer);
  }

  @Override
  public String name() {
    return favorsLoot.name();
  }
}
