package fr.moussax.blightedMC.game.recipes;

import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.ShapeEncoder;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipeRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesDirectory;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import org.bukkit.Material;

public final class MaterialRecipes implements RecipeRegistry {
    @Override
    public void defineRecipes() {

        ShapeEncoder encoder = new ShapeEncoder(" i ", "iii", " i ");
        encoder.bindKey('i', Material.IRON_INGOT, 8);
        BlightedShapedRecipe enchantedIronIngotRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 1
        );
        enchantedIronIngotRecipe.setRecipe(encoder.encodeCraftingRecipe());

        ShapeEncoder encoder2 = new ShapeEncoder(" e ", "eee", " e ");
        encoder2.bindKey('e', ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 8);
        BlightedShapedRecipe enchantedIronBlockRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_IRON_BLOCK"), 1
        );
        enchantedIronBlockRecipe.setRecipe(encoder2.encodeCraftingRecipe());

        ShapeEncoder encoder3 = new ShapeEncoder(" i ", "iii", " i ");
        encoder3.bindKey('i', Material.ENDER_PEARL, 16);
        BlightedShapedRecipe enchantedEnderPearlRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_ENDER_PEARL"), 2
        );
        enchantedEnderPearlRecipe.setRecipe(encoder3.encodeCraftingRecipe());

        ShapeEncoder encoder4 = new ShapeEncoder(" i ", "iji", " i ");
        encoder4.bindKey('i', ItemDirectory.getItem("ENCHANTED_ENDER_PEARL"), 1);
        encoder4.bindKey('j', Material.OPEN_EYEBLOSSOM, 1);
        BlightedShapedRecipe glimmeringEyeRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("GLIMMERING_EYE"), 1
        );
        glimmeringEyeRecipe.setRecipe(encoder4.encodeCraftingRecipe());

        ShapeEncoder encoder5 = new ShapeEncoder(" i ", "iii", " i ");
        encoder5.bindKey('i', Material.GHAST_TEAR, 1);
        BlightedShapedRecipe enchantedGhastTearRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_GHAST_TEAR"), 1
        );
        enchantedGhastTearRecipe.setRecipe(encoder5.encodeCraftingRecipe());

        ShapeEncoder encoder6 = new ShapeEncoder(" i ", "iii", " i ");
        encoder6.bindKey('i', Material.COAL, 8);
        BlightedShapedRecipe enchantedCoalRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_COAL"), 1
        );
        enchantedCoalRecipe.setRecipe(encoder6.encodeCraftingRecipe());

        ShapeEncoder encoder7 = new ShapeEncoder("aaa", "bcb", " b ");
        encoder7.bindKey('a', ItemDirectory.getItem("ENCHANTED_COAL"), 1);
        encoder7.bindKey('b', Material.IRON_INGOT, 10);
        encoder7.bindKey('c', Material.LAVA_BUCKET, 1);
        BlightedShapedRecipe enchantedLavaBucketRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_LAVA_BUCKET"), 1
        );
        enchantedLavaBucketRecipe.setRecipe(encoder7.encodeCraftingRecipe());

        ShapeEncoder encoder8 = new ShapeEncoder("aaa", "bcb", " b ");
        encoder8.bindKey('a', Material.MAGMA_BLOCK, 64);
        encoder8.bindKey('b', ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 4);
        encoder8.bindKey('c', ItemDirectory.getItem("ENCHANTED_LAVA_BUCKET"), 1);
        BlightedShapedRecipe magmaBucketRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("MAGMA_BUCKET"), 1
        );
        magmaBucketRecipe.setRecipe(encoder8.encodeCraftingRecipe());

        ShapeEncoder encoder9 = new ShapeEncoder(" a ", "bcb", "ddd");
        encoder9.bindKey('a', Material.BOOK, 1);
        encoder9.bindKey('b', Material.RED_WOOL, 1);
        encoder9.bindKey('c', Material.CRAFTING_TABLE, 1);
        encoder9.bindKey('d', Material.OBSIDIAN, 1);
        BlightedShapedRecipe blightedCraftingTable = new BlightedShapedRecipe(
            ItemDirectory.getItem("BLIGHTED_WORKBENCH"), 1
        );
        blightedCraftingTable.setRecipe(encoder9.encodeCraftingRecipe());

        ShapeEncoder encoder10 = new ShapeEncoder("aba", "cdc", "aea");
        encoder10.bindKey('a', Material.IRON_BLOCK, 1);
        encoder10.bindKey('b', Material.BLAST_FURNACE, 1);
        encoder10.bindKey('c', ItemDirectory.getItem("ENCHANTED_COAL"), 2);
        encoder10.bindKey('d', Material.LAVA_BUCKET, 1);
        encoder10.bindKey('e', ItemDirectory.getItem("BLIGHTED_WORKBENCH"), 1);
        BlightedShapedRecipe blightedForgeRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("BLIGHTED_FORGE"), 1
        );
        blightedForgeRecipe.setRecipe(encoder10.encodeCraftingRecipe());

        RecipesDirectory.add(
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
