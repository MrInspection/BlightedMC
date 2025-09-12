package fr.moussax.blightedMC.registry.items;

import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.ItemCategory;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import fr.moussax.blightedMC.core.items.rules.PreventProjectileLaunchRule;
import org.bukkit.Material;

public class BlightedMaterials implements ItemCategory {
  @Override
  public void registerItems() {
    ItemManager enchantedIronIngot = new ItemManager(
      "ENCHANTED_IRON_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON,
      Material.IRON_INGOT, "Enchanted Iron Ingot"
    );
    enchantedIronIngot.addEnchantmentGlint();
    enchantedIronIngot.addLore(ItemRarity.UNCOMMON.getName());
    ItemsRegistry.addItem(enchantedIronIngot);

    ItemManager enchantedIronBlock = new ItemManager(
      "ENCHANTED_IRON_BLOCK", ItemType.MATERIAL, ItemRarity.RARE,
      Material.IRON_BLOCK, "Enchanted Iron Block"
    );
    enchantedIronBlock.addLore(ItemRarity.RARE.getName());
    enchantedIronBlock.addEnchantmentGlint();
    enchantedIronBlock.addRule(new PreventPlacementRule());
    ItemsRegistry.addItem(enchantedIronBlock);

    ItemManager enchantedEnderPearl = new ItemManager(
      "ENCHANTED_ENDER_PEARL", ItemType.MATERIAL, ItemRarity.UNCOMMON,
      Material.ENDER_PEARL, "Enchanted Ender Pearl"
    );
    enchantedEnderPearl.addLore(ItemRarity.UNCOMMON.getName());
    enchantedEnderPearl.addEnchantmentGlint();
    enchantedEnderPearl.addRule(new PreventProjectileLaunchRule());
    ItemsRegistry.addItem(enchantedEnderPearl);

    ItemManager enchantedGhastTear = new ItemManager(
      "ENCHANTED_GHAST_TEAR", ItemType.MATERIAL, ItemRarity.UNCOMMON,
      Material.GHAST_TEAR, "Enchanted Ghast Tear"
    );
    enchantedGhastTear.addLore(ItemRarity.RARE.getName());
    enchantedGhastTear.addEnchantmentGlint();
    ItemsRegistry.addItem(enchantedGhastTear);
  }
}
