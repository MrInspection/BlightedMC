package fr.moussax.blightedMC.registry.armors;

import fr.moussax.blightedMC.core.items.registry.ItemsRegistry;
import fr.moussax.blightedMC.registry.abilities.HomodeusFlightAbility;
import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class SpecialArmorRegistry implements ItemCategory {
  @Override
  public void registerItems() {
    FullSetBonus homodeusFlightBonus = new HomodeusFlightAbility();

    ItemTemplate homodeusHelmet = new ItemTemplate(
      "HOMODEUS_HELMET", ItemType.HELMET, ItemRarity.LEGENDARY, Material.LEATHER_HELMET, "Homodeus Helmet"
    );
    homodeusHelmet.addEnchantmentGlint();
    homodeusHelmet.setLeatherColor("#ffffff");
    homodeusHelmet.addItemFlag(List.of(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE));
    homodeusHelmet.setUnbreakable(true);
    homodeusHelmet.addLore(
      "",
      " §5Full Set Bonus: Homodeus",
      " §7Ascend beyond mortal limits, harnessing",
      " §7divine technology to defy gravity and soar",
      " §7through the skies.",
      "",
      ItemRarity.LEGENDARY.getName()
    );
    homodeusHelmet.setFullSetBonus(homodeusFlightBonus);
    ItemsRegistry.addItem(homodeusHelmet);

    ItemTemplate homodeusChestplate = new ItemTemplate(
      "HOMODEUS_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.LEGENDARY, Material.LEATHER_CHESTPLATE, "Homodeus Chestplate"
    );
    homodeusChestplate.addEnchantmentGlint();
    homodeusChestplate.setLeatherColor("#ffffff");
    homodeusChestplate.setUnbreakable(true);
    homodeusChestplate.addItemFlag(List.of(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE));
    homodeusChestplate.addLore(
      "",
      " §5Full Set Bonus: Homodeus",
      " §7Ascend beyond mortal limits, harnessing",
      " §7divine technology to defy gravity and soar",
      " §7through the skies.",
      "",
      ItemRarity.LEGENDARY.getName()
    );
    homodeusChestplate.setFullSetBonus(homodeusFlightBonus);
    ItemsRegistry.addItem(homodeusChestplate);

    ItemTemplate homodeusLeggings = new ItemTemplate(
      "HOMODEUS_LEGGINGS", ItemType.LEGGINGS, ItemRarity.LEGENDARY, Material.LEATHER_LEGGINGS, "Homodeus Leggings"
    );
    homodeusLeggings.addEnchantmentGlint();
    homodeusLeggings.setLeatherColor("#ffffff");
    homodeusLeggings.setUnbreakable(true);
    homodeusLeggings.addItemFlag(List.of(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE));
    homodeusLeggings.addLore(
      "",
      " §5Full Set Bonus: Homodeus",
      " §7Ascend beyond mortal limits, harnessing",
      " §7divine technology to defy gravity and soar",
      " §7through the skies.",
      "",
      ItemRarity.LEGENDARY.getName()
    );
    homodeusLeggings.setFullSetBonus(homodeusFlightBonus);
    ItemsRegistry.addItem(homodeusLeggings);

    ItemTemplate homodeusBoots = new ItemTemplate(
      "HOMODEUS_BOOTS", ItemType.BOOTS, ItemRarity.LEGENDARY, Material.LEATHER_BOOTS, "Homodeus Boots"
    );
    homodeusBoots.addEnchantmentGlint();
    homodeusBoots.setLeatherColor("#ffffff");
    homodeusBoots.addItemFlag(List.of(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE));
    homodeusBoots.setUnbreakable(true);
    homodeusBoots.addLore(
      "",
      " §5Full Set Bonus: Homodeus",
      " §7Ascend beyond mortal limits, harnessing",
      " §7divine technology to defy gravity and soar",
      " §7through the skies.",
      "",
      ItemRarity.LEGENDARY.getName()
    );
    homodeusBoots.setFullSetBonus(homodeusFlightBonus);
    ItemsRegistry.addItem(homodeusBoots);
  }
}
