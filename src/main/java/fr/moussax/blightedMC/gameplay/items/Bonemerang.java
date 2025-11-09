package fr.moussax.blightedMC.gameplay.items;

import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.gameplay.abilities.BonemerangAbility;
import org.bukkit.Material;

import java.util.List;

public class Bonemerang implements ItemRegistry {
  @Override
  public List<ItemTemplate> defineItems() {
    ItemTemplate bonemerang = new ItemTemplate("BONEMERANG", ItemType.BOW, ItemRarity.EPIC, Material.BONE, "Bonemerang");
    bonemerang.addLore(
      "",
      "§5 Ability: Bonemerang  §d§lRIGHT CLICK",
      "§7 Throw your bonemerang, dealing §c16 ",
      "§7 damage to hit enemies on the path as ",
      "§7 it flies outward and back.",
      "",
      ItemRarity.EPIC.getName()
    );
    bonemerang.addAbility(new Ability(new BonemerangAbility(), "Bonemerang", AbilityType.RIGHT_CLICK));
    bonemerang.addEnchantmentGlint();
    bonemerang.isUnstackable();

    return ItemRegistry.add(bonemerang);
  }
}
