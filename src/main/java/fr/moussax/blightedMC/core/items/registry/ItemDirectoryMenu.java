package fr.moussax.blightedMC.core.items.registry;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.menus.*;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemDirectoryMenu {
    private static final int[] CATEGORY_SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 40, 41, 42, 43};
    private static final int SEARCH_SLOT = 41;
    private static final int[] ITEM_SLOTS = CATEGORY_SLOTS;

    private static ItemBuilder hideAllItemFlags(ItemBuilder builder) {
        return builder.addItemFlag(List.of(
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_DESTROYS,
            ItemFlag.HIDE_PLACED_ON
        ));
    }

    private static ItemStack buildMenuItem(ItemStack base, String name, List<String> lore) {
        ItemBuilder builder = new ItemBuilder(base).setDisplayName(name);
        if (lore != null) lore.forEach(builder::addLore);
        hideAllItemFlags(builder);
        return builder.toItemStack();
    }

    private static ItemStack buildMenuItem(ItemStack base, String name) {
        return buildMenuItem(base, name, null);
    }

    public static class ItemCategoriesMenu extends Menu {
        public ItemCategoriesMenu() {
            super("§rItem Categories", 45);
        }

        @Override
        public void build(Player player) {
            List<ItemType.Category> categories = Arrays.asList(ItemType.Category.values());
            for (int i = 0; i < categories.size() && i < CATEGORY_SLOTS.length; i++) {
                ItemType.Category category = categories.get(i);
                ItemStack item = buildMenuItem(getCategoryIcon(category), "§b" + formatCategoryName(category), getCategoryLore(category));
                setItem(CATEGORY_SLOTS[i], item, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(
                    new BlightedItemsPaginatedMenu(category, this,
                        itemObj -> itemObj.getItemType() != null && itemObj.getItemType().getCategory() == category,
                        "§r" + Formatter.formatEnumName(category.name()) + " Items"), p));
            }

            setItem(SEARCH_SLOT, buildMenuItem(new ItemStack(Material.BIRCH_SIGN), "§eSearch Items", List.of("§7Click to search for items!")),
                MenuItemInteraction.ANY_CLICK, (p, t) -> openSearchSign(p, this));
            setItem(40, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
        }

        private static ItemStack getCategoryIcon(ItemType.Category category) {
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

        private static String formatCategoryName(ItemType.Category category) {
            return switch (category) {
                case ARMOR -> "Armor Categories";
                case MELEE_WEAPON -> "Melee Weapons";
                case RANGE_WEAPON -> "Ranged Weapons";
                case TOOLS -> "Tools";
                case BLOCKS -> "Blocks";
                case MATERIAL -> "Materials";
                case MISCELLANEOUS -> "Miscellaneous";
            };
        }

        private static List<String> getCategoryLore(ItemType.Category category) {
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

    private static void openSearchSign(Player player, Menu previousMenu) {
        player.closeInventory();
        player.sendMessage("§8 ■ §7Type your §f§lSEARCH INPUT §7into the chat:");
        ItemDirectorySearch.awaitingSearch.put(player.getUniqueId(), previousMenu);
    }

    public static class BlightedItemsPaginatedMenu extends PaginatedMenu {
        private final Menu previousMenu;
        private final List<ItemTemplate> itemTemplates;

        public BlightedItemsPaginatedMenu(Menu previousMenu, Predicate<ItemTemplate> filter, String title) {
            super(title, 54);
            this.previousMenu = previousMenu;
            this.itemTemplates = ItemDirectory.getAllItems().stream().filter(filter).collect(Collectors.toList());
        }

        public BlightedItemsPaginatedMenu(ItemType.Category category, Menu previousMenu, Predicate<ItemTemplate> filter, String title) {
            this(previousMenu, filter, title);
        }

        @Override
        protected int getTotalItems(Player player) {
            return itemTemplates.size();
        }

        @Override
        protected int getItemsPerPage() {
            return 27;
        }

        @Override
        protected ItemStack getItem(Player player, int index) {
            if (index >= itemTemplates.size()) return new ItemStack(Material.AIR);
            ItemTemplate itemTemplate = itemTemplates.get(index);

            ItemStack stack = itemTemplate.toItemStack().clone();
            var meta = stack.getItemMeta();
            if (meta != null) {
                List<String> lore = Optional.ofNullable(meta.getLore()).orElse(new ArrayList<>());
                if (lore.isEmpty() || !lore.getLast().equals("§eClick to add to inventory!")) {
                    lore.add("");
                    lore.add("§eClick to add to inventory!");
                }
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }

            return hideAllItemFlags(new ItemBuilder(stack)).toItemStack();
        }

        @Override
        public void build(Player player) {
            clearMenu();
            int start = currentPage * getItemsPerPage();
            int end = Math.min(start + getItemsPerPage(), getTotalItems(player));

            if (itemTemplates.isEmpty()) {
                setItem(22, buildMenuItem(new ItemStack(Material.RED_STAINED_GLASS_PANE),
                    "§cNo Items Found",
                    List.of("§7No items match the criteria")), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
            } else {
                for (int slotIdx = 0, i = start; i < end && slotIdx < ITEM_SLOTS.length; i++, slotIdx++) {
                    final int idx = i;
                    setItem(ITEM_SLOTS[slotIdx], getItem(player, idx), MenuItemInteraction.ANY_CLICK,
                        (p, t) -> onItemClick(p, idx, t));
                }
            }
            setNavigation();
        }

        private void clearMenu() {
            for (int i = 0; i < size; i++)
                setItem(i, new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
        }

        private void setNavigation() {
            if (currentPage > 0) setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                currentPage--;
                MenuManager.openMenu(this, p);
            });
            else
                setItem(48, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(previousMenu, p));

            if ((currentPage + 1) * getItemsPerPage() < getTotalItems(null))
                setItem(50, MenuElementPreset.NEXT_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                    currentPage++;
                    MenuManager.openMenu(this, p);
                });
            else if (currentPage > 0)
                setItem(50, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });

            setItem(49, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
        }

        @Override
        protected void onItemClick(Player player, int index, ClickType clickType) {
            if (index < itemTemplates.size()) player.getInventory().addItem(itemTemplates.get(index).toItemStack());
        }
    }

    public static class SearchResultsPaginatedMenu extends BlightedItemsPaginatedMenu {
        private final String searchTerm;

        public SearchResultsPaginatedMenu(String searchTerm, Menu previousMenu) {
            super(previousMenu,
                item -> {
                    if (item.getItemId().toLowerCase().contains(searchTerm.toLowerCase())) return true;

                    var meta = item.getItemMeta();
                    return meta != null && meta.getDisplayName().toLowerCase().contains(searchTerm.toLowerCase());
                },
                "§rSearch: " + searchTerm);
            this.searchTerm = searchTerm.toLowerCase();
        }
    }
}
