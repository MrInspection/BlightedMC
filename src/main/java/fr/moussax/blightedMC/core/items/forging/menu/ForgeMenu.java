package fr.moussax.blightedMC.core.items.forging.menu;

import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.forging.ForgeFuel;
import fr.moussax.blightedMC.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.MenuElementPreset;
import fr.moussax.blightedMC.core.menus.MenuItemInteraction;
import fr.moussax.blightedMC.core.menus.MenuManager;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the interactive forge menu for players.
 *
 * <p>The menu displays a forge recipe, checks ingredient and fuel requirements,
 * allows inserting fuel, and performs the forging operation. Integrates with
 * the player's inventory and provides status via GUI indicators.</p>
 */
public class ForgeMenu extends Menu {
    private static final int[] GRID_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final int[] REQUIRED_ITEM_INDICATOR_SLOTS = {10, 11, 12, 13};
    private static final int[] FORGED_ITEM_INDICATOR_SLOTS = {15, 16};
    private static final int ITEM_INDICATOR = 23;
    private static final int MAXIMUM_FORGE_FUEL = 50000;

    private final ForgeRecipe recipe;
    private final Menu previousMenu;
    private boolean canForge = false;

    /**
     * Creates a forge menu for a specific recipe with a previous menu reference.
     *
     * @param recipe       the forge recipe to display
     * @param previousMenu the menu to return to after closing
     */
    public ForgeMenu(ForgeRecipe recipe, Menu previousMenu) {
        super("Forge Item", 54);
        this.recipe = recipe;
        this.previousMenu = previousMenu;
    }

    /**
     * Creates a forge menu for a specific recipe without a previous menu.
     *
     * @param recipe the forge recipe to display
     */
    public ForgeMenu(ForgeRecipe recipe) {
        super("Forge Item", 54);
        this.recipe = recipe;
        previousMenu = null;
    }

    /**
     * Builds the GUI for the player including grid, background,
     * status indicators, result display, and action buttons.
     *
     * @param player the player viewing the menu
     */
    @Override
    public void build(Player player) {
        if (recipe != null) {
            checkRequirements(player);
        }

        fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
        setupGrid();

        if (recipe != null) {
            displayRequiredIngredients();
        }

        setupStatusPanes();
        setupResultDisplay();
        setupActionButtons(player);
    }

    private void setupGrid() {
        ItemBuilder recipeSlot = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§cLocked Slot");
        if (recipe == null) {
            recipeSlot.addLore("§7Select a recipe to view", "§7the required materials.");
        } else {
            recipeSlot.addLore("§7This slot isn't used for", "§7the selected recipe.");
        }

        ItemStack gridItem = recipeSlot.toItemStack();
        fillSlots(GRID_SLOTS, gridItem);
    }

    private void setupResultDisplay() {
        if (recipe == null) {
            ItemStack noRecipe = new ItemBuilder(Material.BARRIER, "§cRecipe Required")
                .addLore("§7Select a recipe from the", "§7recipe book to start forging.")
                .toItemStack();
            setItem(25, noRecipe, MenuItemInteraction.ANY_CLICK, (_, _) -> {
            });
            return;
        }

        ItemStack result = recipe.getForgedItem().toItemStack().clone();
        result.setAmount(recipe.getForgedAmount());
        setItem(25, result, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private void checkRequirements(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        Map<String, Integer> requiredCounts = aggregateRequirements();

        boolean hasAllIngredients = requiredCounts.entrySet().stream()
            .allMatch(entry -> hasEnoughIngredient(player, entry.getKey(), entry.getValue()));

        boolean hasSufficientFuel = blightedPlayer.getForgeFuel() >= recipe.getFuelCost();

        this.canForge = hasAllIngredients && hasSufficientFuel;
    }

    private Map<String, Integer> aggregateRequirements() {
        Map<String, Integer> counts = new HashMap<>();
        for (CraftingObject ingredient : recipe.getIngredients()) {
            String id = ingredient.getId();
            counts.put(id, counts.getOrDefault(id, 0) + ingredient.getAmount());
        }
        return counts;
    }

    private boolean hasEnoughIngredient(Player player, String itemId, int requiredAmount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            String currentId = Utilities.resolveItemId(item, itemId);
            if (currentId.equals(itemId)) {
                count += item.getAmount();
            }
        }
        return count >= requiredAmount;
    }

    private void displayRequiredIngredients() {
        for (int i = 0; i < recipe.getIngredients().size() && i < GRID_SLOTS.length; i++) {
            CraftingObject ingredient = recipe.getIngredients().get(i);
            ItemStack displayItem = createDisplayItem(ingredient);
            setItem(GRID_SLOTS[i], displayItem, MenuItemInteraction.ANY_CLICK, (_, _) -> {
            });
        }
    }

    private ItemStack createDisplayItem(CraftingObject ingredient) {
        ItemStack displayItem = ingredient.isCustom()
            ? ingredient.getManager().toItemStack().clone()
            : ingredient.getVanillaItem().clone();
        displayItem.setAmount(ingredient.getAmount());
        return displayItem;
    }

    private void setupStatusPanes() {
        Material indicator = determineIndicatorMaterial();

        ItemStack sacrificedItemPane = new ItemBuilder(indicator, "§6Items to Process")
            .addLore("§7The materials required to forge", "§7the item are displayed in this side.")
            .toItemStack();

        ItemStack forgedItemPane = new ItemBuilder(indicator, "§6Item to Forge")
            .addLore("§7The item you will create", "§7with the required materials.")
            .toItemStack();

        ItemStack pane = new ItemBuilder(indicator, "§r").setHideTooltip().toItemStack();

        fillSlots(REQUIRED_ITEM_INDICATOR_SLOTS, sacrificedItemPane);
        fillSlots(FORGED_ITEM_INDICATOR_SLOTS, forgedItemPane);

        setItem(ITEM_INDICATOR, pane, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });

        ItemStack forgeIcon = new ItemBuilder(Material.BLAST_FURNACE, "§dBlighted Forge")
            .addLore("§7Combine materials to forge ", "§7items beyond the reach of", "§7ordinary furnaces.")
            .toItemStack();
        setItem(14, forgeIcon, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private Material determineIndicatorMaterial() {
        if (recipe == null) {
            return Material.WHITE_STAINED_GLASS_PANE;
        }
        return canForge ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
    }

    private void setupActionButtons(Player player) {
        setupForgeButton();
        setupNavigationButtons();
        setupFuelButtons(player);
    }

    private void setupForgeButton() {
        ItemBuilder builder = new ItemBuilder(Material.ANVIL);

        if (recipe == null) {
            builder.setDisplayName("§aForge Item")
                .addLore("§7Select a recipe from the", "§7Recipe Book to start forging.");
        } else {
            builder.setDisplayName("§aConfirm")
                .addLore(canForge ? "§eClick to confirm!" : "§cYou don't have the required items!")
                .setEnchantmentGlint(canForge);
        }

        setItem(32, builder.toItemStack(), MenuItemInteraction.ANY_CLICK, (p, _) -> {
            if (recipe != null && canForge) {
                forgeItem(p);
            } else {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
            }
        });
    }

    private void setupNavigationButtons() {
        ItemStack recipeBook = new ItemBuilder(Material.KNOWLEDGE_BOOK, "§6Forge Recipes")
            .addLore("§7View all §6Forge Recipes §7and ", "§7their requirements.", "", "§eClick to view!")
            .toItemStack();

        setItem(49, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (_, _) -> close());
        setItem(50, recipeBook, MenuItemInteraction.ANY_CLICK, (p, _) -> MenuManager.openMenu(new ForgeRecipesMenu(this), p));
    }

    private void setupFuelButtons(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        int currentFuel = blightedPlayer.getForgeFuel();
        int requiredFuel = recipe != null ? recipe.getFuelCost() : 0;

        setItem(34, createFuelMeter(currentFuel, requiredFuel), MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
        setItem(51, createFuelGuide(), MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
        setItem(53, createInsertFuelButton(player), MenuItemInteraction.ANY_CLICK, (p, _) -> handleFuelInsertion(blightedPlayer));
    }

    private ItemStack createFuelMeter(int currentFuel, int requiredFuel) {
        String formattedCurrent = Formatter.formatDecimalWithCommas(currentFuel);
        String formattedMax = Formatter.formatDecimalWithCommas(MAXIMUM_FORGE_FUEL);
        String formattedRequired = Formatter.formatDecimalWithCommas(requiredFuel);

        int barLength = 20;
        double valuePerSegment = MAXIMUM_FORGE_FUEL / (double) barLength;

        ItemBuilder builder = new ItemBuilder(Material.CAMPFIRE, "§6Fuel Meter")
            .addLore(
                "§r " + Formatter.createProgressBar(currentFuel, barLength, valuePerSegment) + " §e" + formattedCurrent + "§6/§e" + formattedMax + " mB ", "",
                " §7Fill your forge with §c🔥 heat fuel §7like",
                " §7§fLava Bucket§7, to power the forge",
                " §7and craft advanced items."
            );

        if (recipe != null) {
            builder.addLore(
                "",
                " §7The forge must have §6🪣 " + formattedRequired + " mB §7of",
                " §7fuel stored to start forging items.",
                ""
            );
        } else {
            builder.addLore("");
        }

        return builder.toItemStack();
    }

    private ItemStack createFuelGuide() {
        return new ItemBuilder(Material.BLAZE_POWDER, "§6Forge Fuel")
            .addLore(
                "§8Measured in millibuckets (mB)", "", " §7There are various types of §6Fuel",
                " §7that you can use to power your Forge. ",
                " §7Each offers various §6🪣 mB §7of fuel",
                " §7based on their §c🔥 heat strength§7.",
                " ",
                "   §8‣ §fCoal §8- §610 mB",
                "   §8‣ §fMagma Block §8- §640 mB",
                "   §8‣ §fBlaze Rod §8- §6200 mB",
                "   §8‣ §fLava Bucket §8- §61,000 mB",
                "   §8‣ §eEnchanted Coal §8- §63,000 mB",
                "   §8‣ §bEnchanted Lava Bucket §8- §610,000 mB ",
                "   §8‣ §dMagma Lava Bucket §8- §620,000 mB",
                "   §8‣ §cPlasma Lava Bucket §8- §650,000 mB",
                ""
            )
            .addEnchantmentGlint()
            .toItemStack();
    }

    private ItemStack createInsertFuelButton(Player player) {
        int fuelInInventory = calculateInventoryFuel(player);

        return new ItemBuilder(Material.CAULDRON, "§aInsert Fuel from Inventory")
            .addLore(
                "§7Grab as much fuel that will fit into",
                "§7the forge from your inventory.", "",
                "§7In your inventory: §6🪣 " + Formatter.formatDecimalWithCommas(fuelInInventory) + " mB", "",
                "§eClick to insert!"
            )
            .toItemStack();
    }

    private int calculateInventoryFuel(Player player) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().isAir()) {
                total += ForgeFuel.getFuelAmount(item);
            }
        }
        return total;
    }

    private void handleFuelInsertion(BlightedPlayer blightedPlayer) {
        Player player = blightedPlayer.getPlayer();

        int currentFuel = blightedPlayer.getForgeFuel();
        int availableSpace = MAXIMUM_FORGE_FUEL - currentFuel;

        if (availableSpace <= 0) {
            Formatter.warn(player, "§cYour forge is already full!");
            return;
        }

        int addedFuel = consumeFuelFromInventory(player, availableSpace);

        if (addedFuel > 0) {
            blightedPlayer.addForgeFuel(addedFuel);
            blightedPlayer.saveData();
            player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 1f, 0f);
            super.refresh(player);
        } else {
            Formatter.warn(player, "No fuel found in inventory!");
        }
    }

    private int consumeFuelFromInventory(Player player, int availableSpace) {
        int totalAdded = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir()) continue;

            final int fuelPerItem = ForgeFuel.getFuelPerItem(item);
            if (fuelPerItem <= 0) continue;

            int remainingSpace = availableSpace - totalAdded;
            if (remainingSpace <= 0) break;

            int stackAmount = item.getAmount();

            // consume as many full items as fit
            int fullItemsThatFit = remainingSpace / fuelPerItem;
            int consumeFull = Math.min(stackAmount, fullItemsThatFit);
            if (consumeFull > 0) {
                totalAdded += consumeFull * fuelPerItem;
                stackAmount -= consumeFull;
            }

            // if there's still space but no full item would fit, allow consuming one item to top up
            remainingSpace = availableSpace - totalAdded;
            if (remainingSpace > 0 && stackAmount > 0) {
                totalAdded = availableSpace; // top up (partially allowed)
                stackAmount -= 1;
            }

            if (stackAmount > 0) {
                item.setAmount(stackAmount);
                player.getInventory().setItem(slot, item);
            } else {
                player.getInventory().setItem(slot, null);
            }

            if (totalAdded >= availableSpace) break;
        }
        return totalAdded;
    }

    private void forgeItem(Player player) {
        checkRequirements(player);
        if (!canForge) {
            Formatter.warn(player, "Not enough resources!");
            super.refresh(player);
            return;
        }
        this.canForge = false;

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);

        consumeIngredients(player);
        blightedPlayer.removeForgeFuel(recipe.getFuelCost());
        blightedPlayer.saveData();

        SoundSequence.FORGE_ITEM.play(player.getLocation());
        Utilities.delay(() -> setStatusSlot(10, Material.YELLOW_STAINED_GLASS_PANE), 0L);
        Utilities.delay(() -> setStatusSlot(11, Material.YELLOW_STAINED_GLASS_PANE), 2L);
        Utilities.delay(() -> setStatusSlot(12, Material.YELLOW_STAINED_GLASS_PANE), 4L);
        Utilities.delay(() -> setStatusSlot(13, Material.YELLOW_STAINED_GLASS_PANE), 6L);
        Utilities.delay(() -> updateStatusPanes(Material.ORANGE_STAINED_GLASS_PANE, REQUIRED_ITEM_INDICATOR_SLOTS), 8L);
        Utilities.delay(() -> setStatusSlot(15, Material.ORANGE_STAINED_GLASS_PANE), 10L);
        Utilities.delay(() -> setStatusSlot(16, Material.ORANGE_STAINED_GLASS_PANE), 12L);
        Utilities.delay(() -> setStatusSlot(ITEM_INDICATOR, Material.ORANGE_STAINED_GLASS_PANE), 14L);

        Utilities.delay(() -> {
            updateStatusPanes(Material.RED_STAINED_GLASS_PANE, REQUIRED_ITEM_INDICATOR_SLOTS);
            updateStatusPanes(Material.RED_STAINED_GLASS_PANE, FORGED_ITEM_INDICATOR_SLOTS);
            setStatusSlot(ITEM_INDICATOR, Material.RED_STAINED_GLASS_PANE);
        }, 16L);

        Utilities.delay(() -> {
            updateStatusPanes(Material.LIME_STAINED_GLASS_PANE, REQUIRED_ITEM_INDICATOR_SLOTS);
            updateStatusPanes(Material.LIME_STAINED_GLASS_PANE, FORGED_ITEM_INDICATOR_SLOTS);
            setStatusSlot(ITEM_INDICATOR, Material.LIME_STAINED_GLASS_PANE);
        }, 18L);

        Utilities.delay(() -> {
            ItemStack result = recipe.getForgedItem().toItemStack().clone();
            result.setAmount(recipe.getForgedAmount());

            if (player.isOnline()) {
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(result);
                if (!leftover.isEmpty()) {
                    for (ItemStack item : leftover.values()) {
                        player.getWorld().dropItem(player.getLocation(), item);
                    }
                }
                // Refresh to reset UI for next craft
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof ForgeMenu) {
                    super.refresh(player);
                }
            }
        }, 22L);
    }

    /**
     * Updates a specific array of slots with a glass pane color.
     */
    private void updateStatusPanes(Material material, int[] slots) {
        if (!(inventory.getHolder() instanceof ForgeMenu)) return;

        ItemStack pane = new ItemBuilder(material, "§6Forging...")
            .setHideTooltip()
            .toItemStack();

        for (int slot : slots) {
            inventory.setItem(slot, pane);
        }
    }

    /**
     * Updates a single slot with a glass pane color.
     */
    private void setStatusSlot(int slot, Material material) {
        if (!(inventory.getHolder() instanceof ForgeMenu)) return;

        ItemStack pane = new ItemBuilder(material, "§6Forging...")
            .setHideTooltip()
            .toItemStack();

        inventory.setItem(slot, pane);
    }

    private void consumeIngredients(Player player) {
        for (CraftingObject ingredient : recipe.getIngredients()) {
            Utilities.consumeItemsFromInventory(player, ingredient);
        }
    }
}
