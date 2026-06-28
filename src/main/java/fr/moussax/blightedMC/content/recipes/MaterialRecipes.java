package fr.moussax.blightedMC.content.recipes;

import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistryHandler;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Material;

import static fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistry.shapedRecipe;

public final class MaterialRecipes implements RegistryModule<RecipeRegistryHandler> {

    @Override
    public void register(RecipeRegistryHandler registry) {

        BlightedRecipe enchantedCoalRecipe = shapedRecipe("ENCHANTED_COAL", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.COAL, 8)
                .build();

        BlightedRecipe enchantedCopperIngotRecipe = shapedRecipe("ENCHANTED_COPPER_INGOT", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.COPPER_INGOT, 8)
                .build();

        BlightedRecipe enchantedIronIngotRecipe = shapedRecipe("ENCHANTED_IRON_INGOT", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.IRON_INGOT, 8)
                .build();

        BlightedRecipe enchantedIronBlockRecipe = shapedRecipe("ENCHANTED_IRON_BLOCK", 1)
                .shape(" e ", "eee", " e ")
                .bind('e', ItemRegistry.getItem("ENCHANTED_IRON_INGOT"), 8)
                .build();

        BlightedRecipe enchantedGoldIngotRecipe = shapedRecipe("ENCHANTED_GOLD_INGOT", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.GOLD_INGOT, 8)
                .build();

        BlightedRecipe enchantedLapisLazuliRecipe = shapedRecipe("ENCHANTED_LAPIS_LAZULI", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.LAPIS_LAZULI, 10)
                .build();

        BlightedRecipe enchantedRedstoneRecipe = shapedRecipe("ENCHANTED_REDSTONE", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.REDSTONE, 12)
                .build();

        BlightedRecipe enchantedAmethystShardRecipe = shapedRecipe("ENCHANTED_AMETHYST_SHARD", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.AMETHYST_SHARD, 8)
                .build();

        BlightedRecipe enchantedEmeraldRecipe = shapedRecipe("ENCHANTED_EMERALD", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.EMERALD, 8)
                .build();

        BlightedRecipe enchantedRottenFleshRecipe = shapedRecipe("ENCHANTED_ROTTEN_FLESH", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.ROTTEN_FLESH, 8)
                .build();

        BlightedRecipe enchantedBoneRecipe = shapedRecipe("ENCHANTED_BONE", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.BONE, 8)
                .build();

        BlightedRecipe enchantedStringRecipe = shapedRecipe("ENCHANTED_STRING", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.STRING, 8)
                .build();

        BlightedRecipe enchantedGunpowderRecipe = shapedRecipe("ENCHANTED_GUNPOWDER", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.GUNPOWDER, 8)
                .build();

        BlightedRecipe enchantedSpiderEyeRecipe = shapedRecipe("ENCHANTED_SPIDER_EYE", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.SPIDER_EYE, 8)
                .build();

        BlightedRecipe enchantedSlimeBallRecipe = shapedRecipe("ENCHANTED_SLIME_BALL", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.SLIME_BALL, 12)
                .build();

        BlightedRecipe enchantedPhantomMembraneRecipe = shapedRecipe("ENCHANTED_PHANTOM_MEMBRANE", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.PHANTOM_MEMBRANE, 8)
                .build();

        BlightedRecipe enchantedResinClumpRecipe = shapedRecipe("ENCHANTED_RESIN_CLUMP", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.RESIN_CLUMP, 8)
                .build();

        BlightedRecipe enchantedCobblestoneRecipe = shapedRecipe("ENCHANTED_COBBLESTONE", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.COBBLESTONE, 12)
                .build();

        BlightedRecipe enchantedObsidianRecipe = shapedRecipe("ENCHANTED_OBSIDIAN", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.OBSIDIAN, 8)
                .build();

        // Ench. Paper

        BlightedRecipe enchantedCodRecipe = shapedRecipe("ENCHANTED_COD", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.COD, 5)
                .build();

        BlightedRecipe enchantedSalmonRecipe = shapedRecipe("ENCHANTED_SALMON", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.SALMON, 5)
                .build();

        BlightedRecipe enchantedTropicalFishRecipe = shapedRecipe("ENCHANTED_TROPICAL_FISH", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.TROPICAL_FISH, 5)
                .build();

        BlightedRecipe enchantedPufferfishRecipe = shapedRecipe("ENCHANTED_PUFFERFISH", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.PUFFERFISH, 5)
                .build();

        BlightedRecipe enchantedSeaPickleRecipe = shapedRecipe("ENCHANTED_SEA_PICKLE", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.SEA_PICKLE, 10)
                .build();

        BlightedRecipe enchantedClayBallRecipe = shapedRecipe("ENCHANTED_CLAY_BALL", 1)
                .shape(" i ", "iii", " i ")
                .bind('i', Material.CLAY_BALL, 12)
                .build();

        var enchantedLavaBucketRecipe = shapedRecipe("ENCHANTED_LAVA_BUCKET", 1)
                .shape("aaa", "bcb", " b ")
                .bind('a', ItemRegistry.getItem("ENCHANTED_COAL"), 1)
                .bind('b', Material.IRON_INGOT, 10)
                .bind('c', Material.LAVA_BUCKET, 1)
                .build();

        var magmaBucketRecipe = shapedRecipe("MAGMA_BUCKET", 1)
                .shape("aaa", "bcb", " b ")
                .bind('a', Material.MAGMA_BLOCK, 64)
                .bind('b', ItemRegistry.getItem("ENCHANTED_IRON_INGOT"), 4)
                .bind('c', ItemRegistry.getItem("ENCHANTED_LAVA_BUCKET"), 1)
                .build();

        var blightedCraftingTable = shapedRecipe("BLIGHTED_WORKBENCH", 1)
                .shape(" a ", "bcb", "ddd")
                .bind('a', Material.BOOK, 1)
                .bind('b', Material.RED_WOOL, 1)
                .bind('c', Material.CRAFTING_TABLE, 1)
                .bind('d', Material.OBSIDIAN, 1)
                .build();

        var blightedForgeRecipe = shapedRecipe("BLIGHTED_FORGE", 1)
                .shape("aba", "cdc", "aea")
                .bind('a', Material.IRON_BLOCK, 1)
                .bind('b', Material.BLAST_FURNACE, 1)
                .bind('c', ItemRegistry.getItem("ENCHANTED_COAL"), 2)
                .bind('d', Material.LAVA_BUCKET, 1)
                .bind('e', ItemRegistry.getItem("BLIGHTED_WORKBENCH"), 1)
                .build();

        BlightedRecipe magmaRodRecipe = shapedRecipe("MAGMA_ROD", 1)
                .shape("  a", " ab", "abc")
                .bind('a', Material.BLAZE_ROD, 1)
                .bind('b', Material.MAGMA_CREAM, 1)
                .bind('c', Material.GHAST_TEAR, 1)
                .build();

        registry.register(enchantedCoalRecipe);
        registry.register(enchantedCopperIngotRecipe);
        registry.register(enchantedIronIngotRecipe);
        registry.register(enchantedIronBlockRecipe);
        registry.register(enchantedGoldIngotRecipe);
        registry.register(enchantedLapisLazuliRecipe);
        registry.register(enchantedRedstoneRecipe);
        registry.register(enchantedAmethystShardRecipe);
        registry.register(enchantedEmeraldRecipe);
        registry.register(enchantedRottenFleshRecipe);
        registry.register(enchantedBoneRecipe);
        registry.register(enchantedStringRecipe);
        registry.register(enchantedGunpowderRecipe);
        registry.register(enchantedSpiderEyeRecipe);
        registry.register(enchantedSlimeBallRecipe);
        registry.register(enchantedPhantomMembraneRecipe);
        registry.register(enchantedResinClumpRecipe);
        registry.register(enchantedCobblestoneRecipe);
        registry.register(enchantedObsidianRecipe);
        registry.register(enchantedClayBallRecipe);
        registry.register(enchantedCodRecipe);
        registry.register(enchantedSalmonRecipe);
        registry.register(enchantedTropicalFishRecipe);
        registry.register(enchantedPufferfishRecipe);
        registry.register(enchantedSeaPickleRecipe);
        registry.register(enchantedLavaBucketRecipe);
        registry.register(magmaBucketRecipe);
        registry.register(blightedCraftingTable);
        registry.register(blightedForgeRecipe);
        registry.register(magmaRodRecipe);
    }
}
