package fr.moussax.blightedMC.registry.recipes;

import fr.moussax.blightedMC.core.items.registry.ItemsRegistry;
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
    BlightedShapedRecipe enchantedIronIngotRecipe = new BlightedShapedRecipe(
      ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_IRON_INGOT"), 1
    );
    enchantedIronIngotRecipe.setRecipe(encoder.encodeCraftingRecipe());
    RecipesRegistry.add(enchantedIronIngotRecipe);

    ShapeEncoder encoder2 = new ShapeEncoder(" e ", "eee", " e ");
    encoder2.bindKey('e', ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_IRON_INGOT"), 16);
    BlightedShapedRecipe enchantedIronBlockRecipe = new BlightedShapedRecipe(
      ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_IRON_BLOCK"), 1
    );
    enchantedIronBlockRecipe.setRecipe(encoder2.encodeCraftingRecipe());
    RecipesRegistry.add(enchantedIronBlockRecipe);

    ShapeEncoder encoder3 = new ShapeEncoder(" i ", "iii", " i ");
    encoder3.bindKey('i', Material.ENDER_PEARL, 16);
    BlightedShapedRecipe enchantedEnderPearlRecipe = new BlightedShapedRecipe(
      ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_ENDER_PEARL"), 2
    );
    enchantedEnderPearlRecipe.setRecipe(encoder3.encodeCraftingRecipe());
    RecipesRegistry.add(enchantedEnderPearlRecipe);

    ShapeEncoder encoder4 = new ShapeEncoder(" i ", "iji", " i ");
    encoder4.bindKey('i', ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_ENDER_PEARL"), 1);
    encoder4.bindKey('j', Material.OPEN_EYEBLOSSOM, 1);
    BlightedShapedRecipe glimmeringEyeRecipe = new BlightedShapedRecipe(
      ItemsRegistry.BLIGHTED_ITEMS.get("GLIMMERING_EYE"), 1
    );
    glimmeringEyeRecipe.setRecipe(encoder4.encodeCraftingRecipe());
    RecipesRegistry.add(glimmeringEyeRecipe);

    ShapeEncoder encoder5 = new ShapeEncoder(" i ", "iii", " i ");
    encoder5.bindKey('i', Material.GHAST_TEAR, 1);
    BlightedShapedRecipe enchantedGhastTearRecipe = new BlightedShapedRecipe(
      ItemsRegistry.BLIGHTED_ITEMS.get("ENCHANTED_GHAST_TEAR"), 1
    );
    enchantedGhastTearRecipe.setRecipe(encoder5.encodeCraftingRecipe());
    RecipesRegistry.add(enchantedGhastTearRecipe);
  }
}
