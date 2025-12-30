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

        Ability voidStep = new Ability(new VoidStepAbility(), "Voidstep", AbilityType.RIGHT_CLICK);

        glimmeringEye.addLore("§8Consumable Item", "");
        glimmeringEye.addLore(voidStep.getAbilityLore());
        glimmeringEye.addLore("", ItemRarity.RARE.getName());
        glimmeringEye.addEnchantmentGlint();
        glimmeringEye.addAbility(voidStep);
        glimmeringEye.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);

        add(glimmeringEye);
    }
}
