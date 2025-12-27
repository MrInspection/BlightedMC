package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.Ability;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import fr.moussax.blightedMC.smp.features.abilities.VoidStepAbility;
import org.bukkit.Material;

public class GlimmeringEye implements ItemProvider {

    @Override
    public void register() {
        BlightedItem glimmeringEye = new BlightedItem("GLIMMERING_EYE", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.ENDER_EYE);
        glimmeringEye.setDisplayName("Glimmering Eye");
        glimmeringEye.addLore(
            "§8Consumable Item",
            "",
            "§5 Ability: Voidstep  §d§lRIGHT CLICK ",
            "§7 Teleport through the void to the ",
            "§7 block you're looking at, up to §e40",
            "§7 blocks away.",
            "§8 Mana Cost: §35",
            "",
            ItemRarity.RARE.getName()
        );
        glimmeringEye.addEnchantmentGlint();
        glimmeringEye.addAbility(new Ability(new VoidStepAbility(), "Voidstep", AbilityType.RIGHT_CLICK));
        glimmeringEye.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);

        add(glimmeringEye);
    }
}
