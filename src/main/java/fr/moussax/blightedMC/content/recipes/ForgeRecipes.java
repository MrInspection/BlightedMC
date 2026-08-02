package fr.moussax.blightedMC.content.recipes;

import fr.moussax.blightedMC.engine.items.crafting.CraftingObject;
import fr.moussax.blightedMC.engine.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.items.forging.registry.ForgeRegistryHandler;
import org.bukkit.Material;

public class ForgeRecipes implements RegistryModule<ForgeRegistryHandler> {
    @Override
    public void register(ForgeRegistryHandler registry) {
        var plasmaBucket = ForgeRecipe.Builder.of("PLASMA_BUCKET", 1)
                .fuelCost(10000)
                .ingredients(
                        new CraftingObject(ItemRegistry.getItem("MAGMA_BUCKET"), 1),
                        new CraftingObject(ItemRegistry.getItem("MAGMA_BUCKET"), 1),
                        new CraftingObject(Material.NETHER_STAR, 1)
                )
                .build();

        registry.register(plasmaBucket);
    }
}
