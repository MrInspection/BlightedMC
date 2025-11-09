package fr.moussax.blightedMC.gameplay.items;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.gameplay.abilities.KnightsSlamAbility;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.List;

public class KnightsSword implements ItemRegistry {
  @Override
  public List<ItemTemplate> defineItems() {
    ItemTemplate ancientKnightSword = new ItemTemplate("ANCIENT_KNIGHT_SWORD", ItemType.LONGSWORD, ItemRarity.LEGENDARY, Material.NETHERITE_SWORD, "Ancient Knight's Sword");
    ancientKnightSword.addLore(
      "",
      "§5 Ability: Ancient Knight's Slam  §d§lRIGHT CLICK ",
      "§7 Slam your sword into the ground dealing",
      "§c 50 §7damage to nearby enemies.",
      "§8 Mana Cost: §360",
      "§8 Cooldown: §a30s",
      "",
      ItemRarity.LEGENDARY.getName() + " LONGSWORD"
    );

    ancientKnightSword.addAbility(new Ability(new KnightsSlamAbility(), "Ancient Knight's Slam", AbilityType.RIGHT_CLICK));
    ancientKnightSword.addAttributeModifier(Attribute.ATTACK_DAMAGE, 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
    ancientKnightSword.addAttributeModifier(Attribute.ATTACK_SPEED, 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);

    return ItemRegistry.add(ancientKnightSword);
  }
}
