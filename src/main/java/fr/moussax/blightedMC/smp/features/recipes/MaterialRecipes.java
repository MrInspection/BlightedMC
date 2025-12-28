package fr.moussax.blightedMC.smp.features.recipes;

import fr.moussax.blightedMC.smp.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.registry.RecipeProvider;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;

public final class MaterialRecipes implements RecipeProvider {

    @Override
    public void register() {
        var enchantedIronIngotRecipe = shapedRecipe("ENCHANTED_IRON_INGOT", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.IRON_INGOT, 8)
            .build();

        var enchantedIronBlockRecipe = shapedRecipe("ENCHANTED_IRON_BLOCK", 1)
            .shape(" e ", "eee", " e ")
            .bind('e', ItemRegistry.getItem("ENCHANTED_IRON_INGOT"), 8)
            .build();

        var enchantedCoalRecipe = shapedRecipe("ENCHANTED_COAL", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.COAL, 8)
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

        add(
            enchantedIronIngotRecipe,
            enchantedIronBlockRecipe,
            enchantedCoalRecipe,
            enchantedLavaBucketRecipe,
            magmaBucketRecipe,
            blightedCraftingTable,
            blightedForgeRecipe,
            magmaRodRecipe
        );
    }
}
