package fr.moussax.blightedMC.content.items;

import fr.moussax.blightedMC.content.items.abilities.VoidStepAbility;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.registry.RegistryModule;
import java.util.function.Consumer;
import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import org.bukkit.Material;

public class GlimmeringEye implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {
        BlightedItem glimmeringEye = new BlightedItem("GLIMMERING_EYE", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.ENDER_EYE);
        glimmeringEye.setDisplayName("Glimmering Eye");

        Ability voidStep = Ability.rightClick("Voidstep", new VoidStepAbility());
        glimmeringEye.addEnchantmentGlint();
        glimmeringEye.addAbility(voidStep);
        glimmeringEye.addLore("", ItemRarity.RARE.getName());
        glimmeringEye.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);

        registry.accept(glimmeringEye);
    }
}
