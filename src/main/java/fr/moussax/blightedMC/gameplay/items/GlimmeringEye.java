package fr.moussax.blightedMC.gameplay.items;

import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.core.items.rules.PreventInteractionRule;
import fr.moussax.blightedMC.core.items.rules.PreventProjectileLaunchRule;
import fr.moussax.blightedMC.gameplay.abilities.VoidStepAbility;
import org.bukkit.Material;

import java.util.List;

public class GlimmeringEye implements ItemRegistry {
    @Override
    public List<ItemTemplate> defineItems() {
        ItemTemplate glimmeringEye = new ItemTemplate(
                "GLIMMERING_EYE",
                ItemType.UNCATEGORIZED,
                ItemRarity.RARE,
                Material.ENDER_EYE,
                "Glimmering Eye"
        );
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
        glimmeringEye.addRule(new PreventInteractionRule());
        glimmeringEye.addRule(new PreventProjectileLaunchRule());

        return ItemRegistry.add(glimmeringEye);
    }
}
