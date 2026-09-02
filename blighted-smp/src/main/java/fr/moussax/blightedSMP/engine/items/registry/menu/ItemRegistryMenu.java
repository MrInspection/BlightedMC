package fr.moussax.blightedSMP.engine.items.registry.menu;

import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.ItemType;
import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
import fr.moussax.bedrock.text.Formatter;
import fr.moussax.bedrock.ui.menu.Menu;
import fr.moussax.bedrock.ui.menu.types.PaginatedMenu;
import fr.moussax.bedrock.ui.menu.interaction.MenuElementPreset;
import fr.moussax.bedrock.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.bedrock.ui.sign.SignInputMenu;
import fr.moussax.bedrock.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class ItemRegistryMenu {
    private static final int[] CATEGORY_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private static final int SEARCH_SLOT = 41;
    private static final int[] ITEM_SLOTS = CATEGORY_SLOTS;

    private static ItemBuilder hideAllItemFlags(ItemBuilder builder) {
        return builder.addItemFlag(
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_DESTROYS,
            ItemFlag.HIDE_PLACED_ON
        );
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
                setItem(CATEGORY_SLOTS[i], item, MenuItemInteraction.ANY_CLICK, (clickingPlayer, _) ->
                    new BlightedItemsPaginatedMenu(this,
                        registeredItem -> registeredItem.getItemType() != null && registeredItem.getItemType().getCategory() == category,
                        "§r" + Formatter.formatEnumName(category.name()) + " Items").open(clickingPlayer));
            }

            setItem(SEARCH_SLOT, buildMenuItem(new ItemStack(Material.PALE_OAK_SIGN), "§eSearch Items", List.of("§7Click to search for items!")),
                MenuItemInteraction.ANY_CLICK, (clickingPlayer, _) -> openSearchSign(clickingPlayer, this));
            setCloseButton(40);
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
                case ARMOR -> "Armors";
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
        SignInputMenu.builder()
            .lines("", "^^^^^^", "Enter your", "search!")
            .onComplete(result -> {
                String search = result.getFirstLine().trim();
                if (search.isEmpty()) {
                    if (previousMenu != null) previousMenu.open(player);
                    else player.closeInventory();
                    return;
                }
                new SearchResultsPaginatedMenu(search, previousMenu).open(player);
            })
            .open(player);
    }

    public static class BlightedItemsPaginatedMenu extends PaginatedMenu {
        private final Menu previousMenu;
        private final List<BlightedItem> blightedItems;

        public BlightedItemsPaginatedMenu(Menu previousMenu, Predicate<BlightedItem> filter, String title) {
            super(title, 54);
            this.previousMenu = previousMenu;
            this.blightedItems = ItemRegistry.getAllItems().stream()
                    .filter(filter)
                    .sorted((firstItem, secondItem) -> {
                        String firstName = firstItem.getDisplayName();
                        String secondName = secondItem.getDisplayName();
                        return (firstName != null ? firstName : "").compareTo(secondName != null ? secondName : "");
                    })
                    .toList();
        }

        @Override
        protected int getTotalItems(@NonNull Player player) {
            return blightedItems.size();
        }

        @Override
        protected int getItemsPerPage() {
            return 28;
        }

        @Override
        protected ItemStack getItem(@NonNull Player player, int index) {
            if (index >= blightedItems.size()) return new ItemStack(Material.AIR);
            BlightedItem blightedItem = blightedItems.get(index);

            ItemStack stack = blightedItem.toItemStack().clone();
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
        public void build(@NonNull Player player) {
            int totalCount = getTotalItems(player);
            int start = currentPage * getItemsPerPage();
            int end = Math.min(start + getItemsPerPage(), totalCount);

            if (blightedItems.isEmpty()) {
                setItem(22, buildMenuItem(new ItemStack(Material.RED_STAINED_GLASS_PANE),
                    "§cNo Items Found",
                    List.of("§7No items match the criteria")), MenuItemInteraction.ANY_CLICK, (clickingPlayer, _) -> {
                });
            } else {
                for (int slotIndex = 0, i = start; i < end && slotIndex < ITEM_SLOTS.length; i++, slotIndex++) {
                    final int itemIndex = i;
                    setItem(ITEM_SLOTS[slotIndex], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK,
                        (clickingPlayer, clickType) -> onItemClick(clickingPlayer, itemIndex, clickType));
                }
            }
            setNavigation(player, totalCount);
        }

        private void setNavigation(Player player, int totalItemsCount) {
            if (currentPage > 0) {
                setBackButton(48, (clickingPlayer, _) -> {
                    currentPage--;
                    refresh(clickingPlayer);
                });
            } else if (previousMenu != null) {
                setBackButton(48, previousMenu);
            }

            if ((currentPage + 1) * getItemsPerPage() < totalItemsCount) {
                setItem(50, MenuElementPreset.NEXT_BUTTON, (clickingPlayer, _) -> {
                    currentPage++;
                    refresh(clickingPlayer);
                });
            }

            setCloseButton(49);
        }

        @Override
        protected void onItemClick(@NonNull Player player, int index, @NonNull ClickType clickType) {
            if (index < blightedItems.size()) player.getInventory().addItem(blightedItems.get(index).toItemStack());
        }
    }

    public static class SearchResultsPaginatedMenu extends BlightedItemsPaginatedMenu {
        public SearchResultsPaginatedMenu(String searchTerm, Menu previousMenu) {
            super(previousMenu,
                item -> {
                    String term = searchTerm.toLowerCase();
                    if (item.getItemId().toLowerCase().contains(term)) return true;
                    var meta = item.getItemMeta();
                    return meta != null && meta.getDisplayName().toLowerCase().contains(term);
                },
                "§rSearch: " + searchTerm);
        }
    }
}
