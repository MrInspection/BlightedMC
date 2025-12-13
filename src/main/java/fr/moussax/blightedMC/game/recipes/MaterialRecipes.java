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
        encoder.bindKey('i', Material.IRON_INGOT, 16);
        BlightedShapedRecipe enchantedIronIngotRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 1
        );
        enchantedIronIngotRecipe.setRecipe(encoder.encodeCraftingRecipe());
        RecipesDirectory.add(enchantedIronIngotRecipe);

        ShapeEncoder encoder2 = new ShapeEncoder(" e ", "eee", " e ");
        encoder2.bindKey('e', ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 16);
        BlightedShapedRecipe enchantedIronBlockRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_IRON_BLOCK"), 1
        );
        enchantedIronBlockRecipe.setRecipe(encoder2.encodeCraftingRecipe());
        RecipesDirectory.add(enchantedIronBlockRecipe);

        ShapeEncoder encoder3 = new ShapeEncoder(" i ", "iii", " i ");
        encoder3.bindKey('i', Material.ENDER_PEARL, 16);
        BlightedShapedRecipe enchantedEnderPearlRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_ENDER_PEARL"), 2
        );
        enchantedEnderPearlRecipe.setRecipe(encoder3.encodeCraftingRecipe());
        RecipesDirectory.add(enchantedEnderPearlRecipe);

        ShapeEncoder encoder4 = new ShapeEncoder(" i ", "iji", " i ");
        encoder4.bindKey('i', ItemDirectory.getItem("ENCHANTED_ENDER_PEARL"), 1);
        encoder4.bindKey('j', Material.OPEN_EYEBLOSSOM, 1);
        BlightedShapedRecipe glimmeringEyeRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("GLIMMERING_EYE"), 1
        );
        glimmeringEyeRecipe.setRecipe(encoder4.encodeCraftingRecipe());
        RecipesDirectory.add(glimmeringEyeRecipe);

        ShapeEncoder encoder5 = new ShapeEncoder(" i ", "iii", " i ");
        encoder5.bindKey('i', Material.GHAST_TEAR, 1);
        BlightedShapedRecipe enchantedGhastTearRecipe = new BlightedShapedRecipe(
            ItemDirectory.getItem("ENCHANTED_GHAST_TEAR"), 1
        );
        enchantedGhastTearRecipe.setRecipe(encoder5.encodeCraftingRecipe());
        RecipesDirectory.add(enchantedGhastTearRecipe);
    }
}
