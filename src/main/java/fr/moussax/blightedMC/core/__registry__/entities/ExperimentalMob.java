package fr.moussax.blightedMC.core.__registry__.entities;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.LootTable.LootDropRarity;
import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ExperimentalMob extends BlightedEntity {
  public ExperimentalMob() {
    super("Experimental Fanatic", 45, EntityType.HUSK);
    setLootTable(craftLootTable());

    itemInMainHand = new ItemStack(Material.IRON_HOE);
    armor = new ItemStack[]{
        new ItemStack(Material.IRON_BOOTS),
        new ItemStack(Material.IRON_LEGGINGS),
        new ItemStack(Material.IRON_CHESTPLATE),
        new ItemStack(Material.IRON_HELMET)
    };
  }

  private LootTable craftLootTable() {
    return new LootTable(new ItemStack(Material.NETHERITE_SCRAP), 0.025, LootDropRarity.EXTRAORDINARY);
  }
}
