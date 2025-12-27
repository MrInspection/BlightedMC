package fr.moussax.blightedMC.smp.features.recipes;

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

        var enchantedEnderPearlRecipe = shapedRecipe("ENCHANTED_ENDER_PEARL", 2)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.ENDER_PEARL, 16)
            .build();

        var glimmeringEyeRecipe = shapedRecipe("GLIMMERING_EYE", 1)
            .shape(" i ", "iji", " i ")
            .bind('i', ItemRegistry.getItem("ENCHANTED_ENDER_PEARL"), 1)
            .bind('j', Material.OPEN_EYEBLOSSOM, 1)
            .build();

        var enchantedGhastTearRecipe = shapedRecipe("ENCHANTED_GHAST_TEAR", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.GHAST_TEAR, 1)
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

        // TODO : Lava Fishing Rod Recipe
        /*var lavaRod = shapedRecipe("LAVA_FISHING_ROD", 1)
            .shape("  a", " ab", "acb")
            .bind('a', Material.BLAZE_ROD, 1)
            .bind('b', Material.MAGMA_CREAM, 1)
            .bind('c', Material.GHAST_TEAR, 1)
            .build();*/

        add(
            enchantedIronIngotRecipe,
            enchantedIronBlockRecipe,
            enchantedEnderPearlRecipe,
            glimmeringEyeRecipe,
            enchantedGhastTearRecipe,
            enchantedCoalRecipe,
            enchantedLavaBucketRecipe,
            magmaBucketRecipe,
            blightedCraftingTable,
            blightedForgeRecipe
        );
    }
}
