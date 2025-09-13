package fr.moussax.blightedMC.registry.items;

import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.registry.ItemsRegistry;
import fr.moussax.blightedMC.core.items.rules.PreventInteractionRule;
import fr.moussax.blightedMC.core.items.rules.PreventProjectileLaunchRule;
import fr.moussax.blightedMC.registry.abilities.InstantTransmissionAbility;
import org.bukkit.Material;

public class BlightedItems implements ItemCategory {
  @Override
  public void registerItems() {
    ItemManager glimmeringEye = new ItemManager(
      "GLIMMERING_EYE",
      ItemType.UNCATEGORIZED,
      ItemRarity.RARE,
      Material.ENDER_EYE,
      "Glimmering Eye"
    );
    glimmeringEye.addLore(
      "§8Consumable Item",
      "",
      "§5 Ability: Instant Transmission §d§lRIGHT CLICK ",
      "§7 Teleport to the block you're looking at",
      "§7 up to §a40 §7blocks away.",
      "§8 Mana Cost: §35",
      "",
      ItemRarity.RARE.getName()
    );
    glimmeringEye.addEnchantmentGlint();
    glimmeringEye.addAbility(new Ability(new InstantTransmissionAbility(), "Instant Transmission", AbilityType.RIGHT_CLICK));
    glimmeringEye.addRule(new PreventInteractionRule());
    glimmeringEye.addRule(new PreventProjectileLaunchRule());

    ItemsRegistry.addItem(glimmeringEye);
  }
}
