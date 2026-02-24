package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.smp.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.smp.core.entities.rituals.registry.RitualRegistry;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.items.blocks.registry.BlockRegistry;
import fr.moussax.blightedMC.smp.core.items.crafting.registry.RecipeRegistry;
import fr.moussax.blightedMC.smp.core.items.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;

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
