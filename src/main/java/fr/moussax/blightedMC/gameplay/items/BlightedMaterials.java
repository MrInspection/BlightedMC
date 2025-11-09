package fr.moussax.blightedMC.gameplay.items;

import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import fr.moussax.blightedMC.core.items.rules.PreventProjectileLaunchRule;
import org.bukkit.Material;

import java.util.List;

public class BlightedMaterials implements ItemRegistry {

  @Override
  public List<ItemTemplate> defineItems() {

    ItemTemplate enchantedIronIngot = new ItemTemplate(
      "ENCHANTED_IRON_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON,
      Material.IRON_INGOT, "Enchanted Iron Ingot"
    );
    enchantedIronIngot.addEnchantmentGlint();
    enchantedIronIngot.addLore(ItemRarity.UNCOMMON.getName());

    ItemTemplate enchantedIronBlock = new ItemTemplate(
      "ENCHANTED_IRON_BLOCK", ItemType.MATERIAL, ItemRarity.RARE,
      Material.IRON_BLOCK, "Enchanted Iron Block"
    );
    enchantedIronBlock.addLore(ItemRarity.RARE.getName());
    enchantedIronBlock.addEnchantmentGlint();
    enchantedIronBlock.addRule(new PreventPlacementRule());

    ItemTemplate enchantedEnderPearl = new ItemTemplate(
      "ENCHANTED_ENDER_PEARL", ItemType.MATERIAL, ItemRarity.UNCOMMON,
      Material.ENDER_PEARL, "Enchanted Ender Pearl"
    );
    enchantedEnderPearl.addLore(ItemRarity.UNCOMMON.getName());
    enchantedEnderPearl.addEnchantmentGlint();
    enchantedEnderPearl.addRule(new PreventProjectileLaunchRule());

    ItemTemplate enchantedGhastTear = new ItemTemplate(
      "ENCHANTED_GHAST_TEAR", ItemType.MATERIAL, ItemRarity.UNCOMMON,
      Material.GHAST_TEAR, "Enchanted Ghast Tear"
    );
    enchantedGhastTear.addLore(ItemRarity.UNCOMMON.getName());
    enchantedGhastTear.addEnchantmentGlint();

    return ItemRegistry.add(List.of(enchantedIronIngot, enchantedIronBlock, enchantedEnderPearl, enchantedGhastTear));
  }
}
