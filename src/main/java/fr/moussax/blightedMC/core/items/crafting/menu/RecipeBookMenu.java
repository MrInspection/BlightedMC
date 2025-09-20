package fr.moussax.blightedMC.core.items.crafting.menu;

import fr.moussax.blightedMC.core.items.ItemFactory;
import fr.moussax.blightedMC.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.MenuItemInteraction;
import fr.moussax.blightedMC.core.menus.MenuElementPreset;
import fr.moussax.blightedMC.core.menus.PaginatedMenu;
import fr.moussax.blightedMC.core.menus.MenuManager;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeBookMenu {
  public static class RecipeListMenu extends PaginatedMenu {
    private final Menu previousMenu;

    public RecipeListMenu(Menu previousMenu) {
      super("§rBlighted Recipe Book", 54);
      this.previousMenu = previousMenu;
    }

    @Override
    protected int getTotalItems(Player player) {
      return BlightedRecipe.REGISTERED_RECIPES.size();
    }

    @Override
    protected int getItemsPerPage() {
      return 27;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
      List<BlightedRecipe> recipes = new ArrayList<>(BlightedRecipe.REGISTERED_RECIPES);
      if (index >= recipes.size()) return new ItemStack(Material.AIR);

      BlightedRecipe recipe = recipes.get(index);
      ItemStack resultItem = recipe.getResult().toItemStack().clone();

      var meta = resultItem.getItemMeta();
      if (meta != null) {
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add("§eClick to view recipe!");
        meta.setLore(lore);
        resultItem.setItemMeta(meta);
      }

      return resultItem;
    }

    @Override
    public void build(Player player) {
      totalItems = getTotalItems(player);
      int start = currentPage * getItemsPerPage();
      int end = Math.min(start + getItemsPerPage(), totalItems);

      int[] recipeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 40, 41, 42, 43};

      for (int i = 0; i < size; i++) {
        setItem(i, new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }

      int recipeIndex = 0;
      for (int i = start; i < end && recipeIndex < recipeSlots.length; i++) {
        final int itemIndex = i;
        setItem(recipeSlots[recipeIndex], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK, (p, t) -> onItemClick(p, itemIndex, t));
        recipeIndex++;
      }

      int[] fillerSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 51, 52, 53};
      for (int slot : fillerSlots) {
        setItem(slot, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }

      if (currentPage > 0) {
        setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
          currentPage--;
          MenuManager.openMenu(this, p);
        });
      } else {
        setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
          close();
          p.openInventory(CraftingTableMenu.createInventory());
        });
        setItem(50, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (p, t) -> {});
      }

      if (end < totalItems) {
        setItem(50, MenuElementPreset.NEXT_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
          currentPage++;
          MenuManager.openMenu(this, p);
        });
      } else if (currentPage > 0) {
        setItem(50, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }
      setItem(49, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
    }

    @Override
    protected void onItemClick(Player player, int index, org.bukkit.event.inventory.ClickType clickType) {
      List<BlightedRecipe> recipes = new ArrayList<>(BlightedRecipe.REGISTERED_RECIPES);
      if (index < recipes.size()) {
        MenuManager.openMenu(new RecipeDetailMenu(recipes.get(index), this), player);
      }
    }

    private String getRecipeType(BlightedRecipe recipe) {
      if (recipe instanceof BlightedShapedRecipe) return "Shaped";
      if (recipe instanceof BlightedShapelessRecipe) return "Shapeless";
      return "Unknown";
    }
  }

  public static class RecipeDetailMenu extends Menu {
    private final BlightedRecipe recipe;
    private final Menu previousMenu;

    public RecipeDetailMenu(BlightedRecipe recipe, Menu previousMenu) {
      super(recipe.getResult().getDisplayName().replaceAll("§[0-9A-FK-ORa-fk-or]", "") + " Recipe", 54);
      this.recipe = recipe;
      this.previousMenu = previousMenu;
    }

    @Override
    public void build(Player player) {
      setupRecipeVisualization();
      setupNavigation();
    }

    private void setupRecipeVisualization() {
      if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
        setupShapedRecipeGrid(shapedRecipe);
      } else if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
        setupShapelessRecipeGrid(shapelessRecipe);
      }

      setItem(23, new ItemBuilder(Material.FLETCHING_TABLE, "§aBlighted Crafting Table")
        .addLore("§7Craft this recipe by using a", "§7blighted crafting table.")
        .toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
      });
      setItem(25, recipe.getResult().toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {});
    }

    private void setupShapedRecipeGrid(BlightedShapedRecipe shapedRecipe) {
      List<CraftingObject> pattern = shapedRecipe.getRecipe();
      int[] gridSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};

      for (int i = 0; i < pattern.size() && i < gridSlots.length; i++) {
        CraftingObject craftingObject = pattern.get(i);
        if (craftingObject != null) {
          ItemStack ingredientItem = getCraftingObjectItem(craftingObject);
          ingredientItem.setAmount(Math.max(1, craftingObject.getAmount()));
          setItem(gridSlots[i], ingredientItem, MenuItemInteraction.ANY_CLICK, (p, t) -> {
            if (craftingObject.isCustom()) {
              BlightedRecipe ingredientRecipe = findRecipeForItem(craftingObject.getManager());
              if (ingredientRecipe != null) {
                MenuManager.openMenu(new RecipeDetailMenu(ingredientRecipe, this), p);
              }
            }
          });
        } else {
          setItem(gridSlots[i], new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
          });
        }
      }
    }

    private void setupShapelessRecipeGrid(BlightedShapelessRecipe shapelessRecipe) {
      List<CraftingObject> ingredients = shapelessRecipe.getIngredients();
      int[] gridSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};

      for (int i = 0; i < ingredients.size() && i < gridSlots.length; i++) {
        CraftingObject ingredient = ingredients.get(i);
        ItemStack ingredientItem = getCraftingObjectItem(ingredient);
        ingredientItem.setAmount(Math.max(1, ingredient.getAmount()));
        setItem(gridSlots[i], ingredientItem, MenuItemInteraction.ANY_CLICK, (p, t) -> {
          if (ingredient.isCustom()) {
            BlightedRecipe ingredientRecipe = findRecipeForItem(ingredient.getManager());
            if (ingredientRecipe != null) {
              MenuManager.openMenu(new RecipeDetailMenu(ingredientRecipe, this), p);
            }
          }
        });
      }
    }

    private BlightedRecipe findRecipeForItem(ItemFactory itemFactory) {
      for (BlightedRecipe recipe : BlightedRecipe.REGISTERED_RECIPES) {
        if (recipe.getResult().equals(itemFactory)) {
          return recipe;
        }
      }
      return null;
    }

    private ItemStack getCraftingObjectItem(CraftingObject craftingObject) {
      if (craftingObject.isCustom()) {
        return craftingObject.getManager().toItemStack();
      } else if (craftingObject.isVanilla()) {
        return craftingObject.getVanillaItem();
      }
      return new ItemStack(Material.AIR);
    }

    private void setupNavigation() {
      setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(previousMenu, p));
      setItem(49, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
      fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
    }
  }
} 