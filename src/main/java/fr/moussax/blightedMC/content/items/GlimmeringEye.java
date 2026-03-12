package fr.moussax.blightedMC.content.items;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.engine.items.abilities.AbilityType;
import fr.moussax.blightedMC.engine.items.registry.ItemProvider;
import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import fr.moussax.blightedMC.content.items.abilities.VoidStepAbility;
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
