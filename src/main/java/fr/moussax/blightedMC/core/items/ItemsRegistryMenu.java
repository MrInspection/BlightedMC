package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.MenuItemInteraction;
import fr.moussax.blightedMC.core.menus.MenuElementPreset;
import fr.moussax.blightedMC.core.menus.MenuManager;
import fr.moussax.blightedMC.utils.Formatter;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ItemsRegistryMenu {
  // Slots for categories
  private static final int[] CATEGORY_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 40, 41, 42, 43};
  private static final int SEARCH_SLOT = 41;

  // Main menu: shows categories
  public static class ItemCategoriesMenu extends Menu {
    public ItemCategoriesMenu() {
      super("§rItem Categories", 45);
    }

    @Override
    public void build(Player player) {
      // Show all categories, even if empty
      List<ItemType.Category> categories = Arrays.asList(ItemType.Category.values());

      int slotIdx = 0;
      for (ItemType.Category category : categories) {
        if (slotIdx >= CATEGORY_SLOTS.length) break;
        ItemStack icon = getCategoryIcon(category);
        String name = "§b" + formatCategoryName(category);
        List<String> lore = getCategoryLore(category);

        ItemStack display = new ItemBuilder(icon)
          .setDisplayName(name)
          .addLore(lore)
          .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
          .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
          .addItemFlag(ItemFlag.HIDE_ENCHANTS)
          .addItemFlag(ItemFlag.HIDE_DESTROYS)
          .addItemFlag(ItemFlag.HIDE_PLACED_ON)
          .toItemStack();
        setItem(CATEGORY_SLOTS[slotIdx], display, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(new ItemsCategoryPaginatedMenu(category, this), p));
        slotIdx++;
      }

      // Search button (Birch Sign)
      setItem(SEARCH_SLOT, new ItemBuilder(Material.BIRCH_SIGN, "§eSearch Items")
        .addLore("§7Click to search for items!")
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> openSearchSign(p, this));

      // Close button
      setItem(40, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
    }

    private ItemStack getCategoryIcon(ItemType.Category category) {
      return switch (category) {
        case ARMOR -> new ItemStack(Material.DIAMOND_CHESTPLATE);
        case MELEE_WEAPON -> new ItemStack(Material.DIAMOND_SWORD);
        case RANGE_WEAPON -> new ItemStack(Material.BOW);
        case TOOLS -> new ItemStack(Material.DIAMOND_PICKAXE);
        case BLOCKS -> new ItemStack(Material.GRASS_BLOCK);
        case MATERIAL -> new ItemStack(Material.EMERALD);
        case MISCELLANEOUS -> new ItemStack(Material.CHEST);
      };
    }

    private String formatCategoryName(ItemType.Category category) {
      return switch (category) {
        case ARMOR -> "§aArmor Categories";
        case MELEE_WEAPON -> "§aMelee Weapons";
        case RANGE_WEAPON -> "§aRanged Weapons";
        case TOOLS -> "§aTools";
        case BLOCKS -> "§aBlocks";
        case MATERIAL -> "§aMaterials";
        case MISCELLANEOUS -> "§aMiscellaneous";
      };
    }

    private List<String> getCategoryLore(ItemType.Category category) {
      return switch (category) {
        case ARMOR -> List.of("§7View all custom armor pieces.");
        case MELEE_WEAPON -> List.of("§7View all custom melee weapons.");
        case RANGE_WEAPON -> List.of("§7View all custom ranged weapons.");
        case TOOLS -> List.of("§7View all custom tools.");
        case BLOCKS -> List.of("§7View all custom blocks.");
        case MATERIAL -> List.of("§7View all custom materials and resources.");
        case MISCELLANEOUS -> List.of("§7View all miscellaneous items.");
      };
    }
  }

  // Paginated menu for items in a category
  public static class ItemsCategoryPaginatedMenu extends fr.moussax.blightedMC.core.menus.PaginatedMenu {
    private final ItemType.Category category;
    private final Menu previousMenu;
    private final List<ItemManager> itemsInCategory;

    public ItemsCategoryPaginatedMenu(ItemType.Category category, Menu previousMenu) {
      super("§r" + Formatter.formatEnumName(category.name()) + " Items", 54);
      this.category = category;
      this.previousMenu = previousMenu;
      this.itemsInCategory = ItemsRegistry.BLIGHTED_ITEMS.values().stream()
        .filter(item -> item.getItemType() != null && item.getItemType().getCategory() == category)
        .collect(Collectors.toList());
    }

    @Override
    protected int getTotalItems(Player player) {
      return itemsInCategory.size();
    }

    @Override
    protected int getItemsPerPage() {
      return 27;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
      if (index >= itemsInCategory.size()) return new ItemStack(Material.AIR);
      ItemManager item = itemsInCategory.get(index);
      ItemStack stack = item.toItemStack().clone();
      var meta = stack.getItemMeta();
      if (meta != null) {
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add("§eClick to add to inventory!");
        meta.setLore(lore);
        stack.setItemMeta(meta);
      }
      // Hide all item flags for menu display
      ItemBuilder builder = new ItemBuilder(stack)
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
        .addItemFlag(ItemFlag.HIDE_DESTROYS)
        .addItemFlag(ItemFlag.HIDE_PLACED_ON);
      return builder.toItemStack();
    }

    @Override
    public void build(Player player) {
      totalItems = getTotalItems(player);
      int start = currentPage * getItemsPerPage();
      int end = Math.min(start + getItemsPerPage(), totalItems);
      int[] itemSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 40, 41, 42, 43};

      // Clear all slots
      for (int i = 0; i < size; i++) {
        setItem(i, new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }

      if (itemsInCategory.isEmpty()) {
        // Empty state: barrier in the center
        setItem(22, new ItemBuilder(Material.RED_STAINED_GLASS_PANE, "§cNo Items Found")
          .addLore("§7There are no items registered")
          .addLore("§7in this category.")
          .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
          .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
          .addItemFlag(ItemFlag.HIDE_ENCHANTS)
          .addItemFlag(ItemFlag.HIDE_DESTROYS)
          .addItemFlag(ItemFlag.HIDE_PLACED_ON)
          .toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      } else {
        int itemIdx = 0;
        for (int i = start; i < end && itemIdx < itemSlots.length; i++) {
          final int itemIndex = i;
          setItem(itemSlots[itemIdx], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK, (p, t) -> onItemClick(p, itemIndex, t));
          itemIdx++;
        }
      }

      // Navigation
      if (currentPage > 0) {
        setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
          currentPage--;
          MenuManager.openMenu(this, p);
        });
      } else {
        setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(previousMenu, p));
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
      if (index < itemsInCategory.size()) {
        ItemManager item = itemsInCategory.get(index);
        player.getInventory().addItem(item.toItemStack());
      }
    }
  }

  // --- Search System ---
  private static void openSearchSign(Player player, Menu previousMenu) {
    player.closeInventory();
    player.sendMessage("§8 ■ §7Type your §f§lSEARCH INPUT §7into the chat:");
    ItemsRegistrySearch.awaitingSearch.put(player.getUniqueId(), previousMenu);
  }

  // Paginated menu for search results
  public static class SearchResultsPaginatedMenu extends fr.moussax.blightedMC.core.menus.PaginatedMenu {
    private final String searchTerm;
    private final Menu previousMenu;
    private final List<ItemManager> results;

    public SearchResultsPaginatedMenu(String searchTerm, Menu previousMenu) {
      super("§rSearch: " + searchTerm, 54);
      this.searchTerm = searchTerm.toLowerCase();
      this.previousMenu = previousMenu;
      this.results = ItemsRegistry.BLIGHTED_ITEMS.values().stream()
        .filter(item -> item.getItemId().toLowerCase().contains(this.searchTerm)
          || (item.getDisplayName() != null && item.getDisplayName().toLowerCase().contains(this.searchTerm)))
        .collect(Collectors.toList());
    }

    @Override
    protected int getTotalItems(Player player) {
      return results.size();
    }

    @Override
    protected int getItemsPerPage() {
      return 27;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
      if (index >= results.size()) return new ItemStack(Material.AIR);
      ItemManager item = results.get(index);
      ItemStack stack = item.toItemStack().clone();
      var meta = stack.getItemMeta();
      if (meta != null) {
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add("§eClick to add to inventory!");
        meta.setLore(lore);
        stack.setItemMeta(meta);
      }
      // Hide all item flags for menu display
      ItemBuilder builder = new ItemBuilder(stack)
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
        .addItemFlag(ItemFlag.HIDE_DESTROYS)
        .addItemFlag(ItemFlag.HIDE_PLACED_ON);
      return builder.toItemStack();
    }

    @Override
    public void build(Player player) {
      totalItems = getTotalItems(player);
      int start = currentPage * getItemsPerPage();
      int end = Math.min(start + getItemsPerPage(), totalItems);
      int[] itemSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 40, 41, 42, 43};

      // Clear all slots
      for (int i = 0; i < size; i++) {
        setItem(i, new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }

      if (results.isEmpty()) {
        // Empty state: barrier in the center
        setItem(22, new ItemBuilder(Material.RED_STAINED_GLASS_PANE, "§cNo Items Found")
          .addLore("§7No items match your search")
          .addLore("§7into the registry.")
          .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
          .addItemFlag(ItemFlag.HIDE_UNBREAKABLE)
          .addItemFlag(ItemFlag.HIDE_ENCHANTS)
          .addItemFlag(ItemFlag.HIDE_DESTROYS)
          .addItemFlag(ItemFlag.HIDE_PLACED_ON)
          .toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
      } else {
        int itemIdx = 0;
        for (int i = start; i < end && itemIdx < itemSlots.length; i++) {
          final int itemIndex = i;
          setItem(itemSlots[itemIdx], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK, (p, t) -> onItemClick(p, itemIndex, t));
          itemIdx++;
        }
      }

      // Navigation
      setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(previousMenu, p));
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
      if (index < results.size()) {
        ItemManager item = results.get(index);
        player.getInventory().addItem(item.toItemStack());
      }
    }
  }
}
