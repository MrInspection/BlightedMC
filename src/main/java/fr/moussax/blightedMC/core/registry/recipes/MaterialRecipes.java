package fr.moussax.blightedMC.core.registry.recipes;

import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.ShapeEncoder;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipeCategory;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesRegistry;
import org.bukkit.Material;

public final class MaterialRecipes implements RecipeCategory {
  @Override
  public void registerRecipes() {
    ShapeEncoder encoder = new ShapeEncoder(" i ", "iii", " i ");
    encoder.bindKey('i', Material.IRON_INGOT, 16);
    BlightedShapedRecipe shapedRecipe = new BlightedShapedRecipe(ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_IRON_INGOT"), 1);
    shapedRecipe.setRecipe(encoder.encodeCraftingRecipe());
    RecipesRegistry.add(shapedRecipe);

    ShapeEncoder encoder2 = new ShapeEncoder(" e ", "eee", " e ");
    encoder2.bindKey('e', ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_IRON_INGOT"), 16);
    BlightedShapedRecipe enchantedIronBlock = new BlightedShapedRecipe(ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_IRON_BLOCK"), 1);
    enchantedIronBlock.setRecipe(encoder2.encodeCraftingRecipe());
    RecipesRegistry.add(enchantedIronBlock);
  }
}
