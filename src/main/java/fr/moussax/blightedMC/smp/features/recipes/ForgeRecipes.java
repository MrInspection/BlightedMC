package fr.moussax.blightedMC.smp.features.recipes;

import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.forging.registry.ForgeProvider;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;

public class ForgeRecipes implements ForgeProvider {
    @Override
    public void register() {
        var plasmaBucket = forgeRecipe("PLASMA_BUCKET", 1)
            .fuelCost(10000)
            .ingredients(
                new CraftingObject(ItemRegistry.getItem("MAGMA_BUCKET"), 1),
                new CraftingObject(ItemRegistry.getItem("MAGMA_BUCKET"), 1),
                new CraftingObject(Material.NETHER_STAR, 1)
            )
            .build();

        add(plasmaBucket);
    }
}
