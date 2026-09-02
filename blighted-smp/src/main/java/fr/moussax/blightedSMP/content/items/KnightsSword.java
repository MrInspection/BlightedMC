package fr.moussax.blightedSMP.content.items;

import fr.moussax.blightedSMP.content.items.abilities.KnightsSlamAbility;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.ItemRarity;
import fr.moussax.blightedSMP.engine.items.ItemType;
import fr.moussax.blightedSMP.engine.items.abilities.Ability;
import fr.moussax.blightedSMP.registry.RegistryModule;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public class KnightsSword implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {
        BlightedItem knightSword = new BlightedItem("ANCIENT_KNIGHT_SWORD", ItemType.LONGSWORD, ItemRarity.LEGENDARY, Material.NETHERITE_SWORD);
        knightSword.setDisplayName("Knight's Sword");
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

        knightSword.addAbility(Ability.rightClick("Knight's Slam", new KnightsSlamAbility()), false);
        knightSword.addAttributeModifier(Attribute.ATTACK_DAMAGE, 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
        knightSword.addAttributeModifier(Attribute.ATTACK_SPEED, 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);

        registry.accept(knightSword);
    }
}
