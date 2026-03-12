package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.entities.rituals.registry.RitualRegistry;
import fr.moussax.blightedMC.engine.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.engine.items.blocks.registry.BlockRegistry;
import fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistry;
import fr.moussax.blightedMC.engine.items.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;

public final class RegistrySystem {

    public static void initialize() {
        ItemRegistry.initialize();
        BlockRegistry.initialize();
        RecipeRegistry.initialize();
        ForgeRegistry.initialize();
        EntitiesRegistry.initialize();
        RitualRegistry.initialize();
        FishingLootRegistry.initialize();
    }

    public static void clear() {
        ItemRegistry.clear();
        BlockRegistry.clear();
        RecipeRegistry.clear();
        ForgeRegistry.clear();
        EntitiesRegistry.clear();
        RitualRegistry.clear();
        FishingLootRegistry.clear();
    }
}
