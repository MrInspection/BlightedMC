package fr.moussax.blightedMC.smp.features.rituals;

import fr.moussax.blightedMC.smp.core.entities.rituals.registry.RitualProvider;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.features.entities.bosses.Goldor;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;

public class AncientRituals implements RitualProvider {

    @Override
    public void register() {

        var goldorRitual = ritual(new Goldor())
            .summoningItem(new ItemBuilder(Material.WITHER_SKELETON_SKULL, "§6Goldor").toItemStack())
            .addOffering(new CraftingObject(ItemRegistry.getItem("PLASMA_BUCKET"), 1))
            .addOffering(new CraftingObject(Material.WITHER_SKELETON_SKULL, 64))
            .gemstoneCost(120)
            .experienceLevelCost(20)
            .build();

        add(goldorRitual);
    }
}
