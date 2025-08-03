package fr.moussax.blightedMC.core.registry.entities;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.LootTable.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ExperimentalMob extends BlightedEntity {
  public ExperimentalMob() {
    super("Dummy", 15, EntityType.HUSK);
    setLootTable(craftLootTable());
    setDroppedExp(15);

    itemInMainHand = new ItemStack(Material.IRON_HOE);
    armor = new ItemStack[]{
      new ItemStack(Material.IRON_BOOTS),
      new ItemStack(Material.IRON_LEGGINGS),
      new ItemStack(Material.IRON_CHESTPLATE),
      new ItemStack(Material.IRON_HELMET)
    };
  }

  @Override
  public String getEntityId() {
    return "EXPERIMENTAL_MOB";
  }

  private LootTable craftLootTable() {
    return new LootTable()
      .setMaxDrop(2) // Maximum 2 different items can drop
      .addLoot("ENCHANTED_IRON_BLOCK", 1, 2, 0.010, LootDropRarity.RARE)
      .addFavorsLoot(10, 0.02, LootDropRarity.EXTRAORDINARY)
      .addLoot(Material.ROTTEN_FLESH, 1, 3, 1, LootDropRarity.COMMON);
  }
}
