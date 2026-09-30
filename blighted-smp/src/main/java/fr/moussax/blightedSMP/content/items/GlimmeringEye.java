package fr.moussax.blightedSMP.content.items;

import fr.moussax.blightedSMP.content.items.abilities.VoidStepAbility;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.ItemRarity;
import fr.moussax.blightedSMP.engine.items.ItemType;
import fr.moussax.blightedSMP.registry.RegistryModule;
import java.util.function.Consumer;
import fr.moussax.blightedSMP.engine.items.rules.ItemRule;
import org.bukkit.Material;

public class GlimmeringEye implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {
        BlightedItem glimmeringEye = new BlightedItem("GLIMMERING_EYE", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.ENDER_EYE);
        glimmeringEye.setDisplayName("Glimmering Eye");

        glimmeringEye.glow();
        glimmeringEye.addAbility(new VoidStepAbility());
        glimmeringEye.addLore("", ItemRarity.RARE.getName());
        glimmeringEye.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);

        registry.accept(glimmeringEye);
    }
}
