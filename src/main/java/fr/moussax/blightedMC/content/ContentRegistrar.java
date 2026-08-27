package fr.moussax.blightedMC.content;

import fr.moussax.blightedMC.content.entities.BlightedEntities;
import fr.moussax.blightedMC.content.fishing.EndFishing;
import fr.moussax.blightedMC.content.fishing.NetherFishing;
import fr.moussax.blightedMC.content.fishing.OverworldFishing;
import fr.moussax.blightedMC.content.fishing.OverworldLavaFishing;
import fr.moussax.blightedMC.content.items.BlightedItems;
import fr.moussax.blightedMC.content.items.BlightedTools;
import fr.moussax.blightedMC.content.items.Bonemerang;
import fr.moussax.blightedMC.content.items.GlimmeringEye;
import fr.moussax.blightedMC.content.items.Hyperion;
import fr.moussax.blightedMC.content.items.KnightsSword;
import fr.moussax.blightedMC.content.items.ThermalFuels;
import fr.moussax.blightedMC.content.items.armors.FishingArmors;
import fr.moussax.blightedMC.content.items.armors.HomodeusArmor;
import fr.moussax.blightedMC.content.items.armors.RocketBoots;
import fr.moussax.blightedMC.content.items.blocks.BlightedBlockItems;
import fr.moussax.blightedMC.content.items.blocks.BlightedBlocks;
import fr.moussax.blightedMC.content.items.materials.BlightedMaterials;
import fr.moussax.blightedMC.content.items.materials.EndMaterials;
import fr.moussax.blightedMC.content.items.materials.FishingMaterials;
import fr.moussax.blightedMC.content.items.materials.NetherMaterials;
import fr.moussax.blightedMC.content.recipes.EndRecipes;
import fr.moussax.blightedMC.content.recipes.EquipmentRecipes;
import fr.moussax.blightedMC.content.recipes.ForgeRecipes;
import fr.moussax.blightedMC.content.recipes.MaterialRecipes;
import fr.moussax.blightedMC.content.recipes.NetherMaterialRecipes;
import fr.moussax.blightedMC.content.rituals.AncientRituals;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.engine.fishing.registry.FishingRegistryHandler;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;

import java.util.List;
import java.util.function.Consumer;

/**
 * Central registry provider for all content modules in BlightedMC.
 *
 * <p>Decouples core engine registries from concrete content implementations.</p>
 */
public final class ContentRegistrar {

    private ContentRegistrar() {
    }

    public static final List<RegistryModule<Consumer<BlightedItem>>> ITEM_MODULES = List.of(
            new BlightedMaterials(),
            new Bonemerang(),
            new GlimmeringEye(),
            new KnightsSword(),
            new HomodeusArmor(),
            new RocketBoots(),
            new ThermalFuels(),
            new FishingArmors(),
            new BlightedItems(),
            new FishingMaterials(),
            new NetherMaterials(),
            new EndMaterials(),
            new Hyperion(),
            new BlightedTools(),
            new BlightedBlockItems()
    );

    public static final List<RegistryModule<Consumer<BlightedBlock>>> BLOCK_MODULES = List.of(
            new BlightedBlocks()
    );

    public static final List<RegistryModule<Consumer<BlightedRecipe>>> RECIPE_MODULES = List.of(
            new MaterialRecipes(),
            new NetherMaterialRecipes(),
            new EndRecipes(),
            new EquipmentRecipes()
    );

    public static final List<RegistryModule<Consumer<ForgeRecipe>>> FORGE_MODULES = List.of(
            new ForgeRecipes()
    );

    public static final List<RegistryModule<Consumer<BlightedEntity>>> ENTITY_MODULES = List.of(
            new BlightedEntities()
    );

    public static final List<RegistryModule<Consumer<AncientRitual>>> RITUAL_MODULES = List.of(
            new AncientRituals()
    );

    public static final List<RegistryModule<FishingRegistryHandler>> FISHING_MODULES = List.of(
            new NetherFishing(),
            new OverworldLavaFishing(),
            new OverworldFishing(),
            new EndFishing()
    );
}
