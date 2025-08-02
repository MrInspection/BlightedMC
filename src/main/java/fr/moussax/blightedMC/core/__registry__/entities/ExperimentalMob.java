package fr.moussax.blightedMC.core.__registry__.entities;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.LootTable.*;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ExperimentalMob extends BlightedEntity {
  public ExperimentalMob() {
    super("Dummy", 15, EntityType.HUSK);
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
    return new LootTable()
        .addLoot("ENCHANTED_IRON_INGOT", 2, 7, 0.35, LootDropRarity.RARE)
        .addLoot("ENCHANTED_IRON_BLOCK", 1, 2, 0.10, LootDropRarity.INSANE)
        .addLoot(Material.ROTTEN_FLESH, 1, 3, 1, LootDropRarity.COMMON);
  }
}
