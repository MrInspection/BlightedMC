package fr.moussax.blightedMC.gameplay.items;

import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemTemplate;
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
        ItemTemplate knightSword = new ItemTemplate("ANCIENT_KNIGHT_SWORD", ItemType.LONGSWORD, ItemRarity.LEGENDARY, Material.NETHERITE_SWORD, "Knight's Sword");
        knightSword.addLore(
                "",
                "§5 Ability: Knight's Slam  §d§lRIGHT CLICK ",
                "§7 Slam your sword into the ground dealing ",
                "§7 §c50 §7damage to nearby enemies.",
                "§8 Mana Cost: §390",
                "§8 Cooldown: §a30s",
                "",
                ItemRarity.LEGENDARY.getName() + " LONGSWORD"
        );

        knightSword.addAbility(new Ability(new KnightsSlamAbility(), "Knight's Slam", AbilityType.RIGHT_CLICK));
        knightSword.addAttributeModifier(Attribute.ATTACK_DAMAGE, 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
        knightSword.addAttributeModifier(Attribute.ATTACK_SPEED, 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);

        return ItemRegistry.add(knightSword);
    }
}
