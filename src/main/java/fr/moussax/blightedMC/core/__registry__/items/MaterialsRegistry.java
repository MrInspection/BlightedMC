package fr.moussax.blightedMC.core.__registry__.items;

import fr.moussax.blightedMC.core.__registry__.abilities.TestAbility;
import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.ItemCategory;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import org.bukkit.Material;

public class MaterialsRegistry implements ItemCategory {
  @Override
  public void registerItems() {
    ItemManager enchantedIronIngot = new ItemManager(
        "ENCHANTED_IRON_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.IRON_INGOT,"Enchanted Iron Ingot"
    );

    enchantedIronIngot.addEnchantmentGlint();
    enchantedIronIngot.addLore(ItemRarity.UNCOMMON.getName());
    ItemsRegistry.addItem(enchantedIronIngot);

    ItemManager enchantedIronBlock = new ItemManager(
        "ENCHANTED_IRON_BLOCK", ItemType.MATERIAL, ItemRarity.RARE, Material.IRON_BLOCK,"Enchanted Iron Block"
    );

    enchantedIronBlock.addEnchantmentGlint();
    enchantedIronBlock.addRule(new PreventPlacementRule());
    ItemsRegistry.addItem(enchantedIronBlock);
  }
}
