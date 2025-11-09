package fr.moussax.blightedMC.core.entities.loot.gems;

import fr.moussax.blightedMC.core.entities.loot.ItemLoot;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class GemsLootAdapter extends ItemLoot {
  private final GemsLoot gemsLoot;

  public GemsLootAdapter(GemsLoot favors, ItemStack displayItem) {
    super(displayItem, favors.amount(), favors.amount());
    this.gemsLoot = favors;
  }

  @Override
  public int generateAmount() {
    return 1; 
  }

  @Override
  public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer, int amount) {
    gemsLoot.consume(killer, dropLocation, toPlayer);
  }

  @Override
  public String name() {
    return gemsLoot.name();
  }
}
