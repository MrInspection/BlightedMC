package fr.moussax.blightedMC.smp.core.items.forging.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.forging.ForgeFuel;
import fr.moussax.blightedMC.smp.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.Menu;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
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

public final class ForgeMenu extends Menu {
    private static final int[] GRID_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final int[] REQUIRED_ITEM_INDICATOR_SLOTS = {10, 11, 12, 13};
    private static final int[] FORGED_ITEM_INDICATOR_SLOTS = {15, 16};
    private static final int ITEM_INDICATOR = 23;
    private static final int MAXIMUM_FORGE_FUEL = 50000;

    private final ForgeRecipe recipe;
    private final Menu previousMenu;
    private boolean canForge = false;
    private boolean isForging = false;

    public ForgeMenu(ForgeRecipe recipe, Menu previousMenu) {
        super("Forge Item", 54);
        this.recipe = recipe;
        this.previousMenu = previousMenu;
    }

    public ForgeMenu(ForgeRecipe recipe) {
        super("Forge Item", 54);
        this.recipe = recipe;
        previousMenu = null;
    }

    @Override
    public void build(Player player) {
        if (recipe != null && !isForging) {
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

        fillSlots(GRID_SLOTS, recipeSlot.toItemStack());
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

        fillSlots(REQUIRED_ITEM_INDICATOR_SLOTS, sacrificedItemPane);
        fillSlots(FORGED_ITEM_INDICATOR_SLOTS, forgedItemPane);

        setItem(ITEM_INDICATOR, new ItemBuilder(indicator, "§r").setHideTooltip().toItemStack(), MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });

        ItemStack forgeIcon = new ItemBuilder(Material.BLAST_FURNACE, "§dBlighted Forge")
            .addLore("§7Combine materials to forge ", "§7items beyond the reach of", "§7ordinary furnaces.")
            .toItemStack();
        setItem(14, forgeIcon, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private Material determineIndicatorMaterial() {
        if (recipe == null) return Material.WHITE_STAINED_GLASS_PANE;
        if (isForging) return Material.ORANGE_STAINED_GLASS_PANE;
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
        } else if (isForging) {
            builder.setDisplayName("§6Forging...");
        } else {
            builder.setDisplayName("§aConfirm process").addLore("", " §7Cost: ");
            for (CraftingObject ingredient : recipe.getIngredients()) {
                builder.addLore(" §8‣ " + Utilities.extractIngredientName(ingredient) + " §8x" + ingredient.getAmount());
            }

            builder.addLore("");
            builder.addLore(canForge ? "§eClick to confirm!" : "§cYou don't have the required items!")
                .setEnchantmentGlint(canForge);
        }

        setItem(32, builder.toItemStack(), MenuItemInteraction.ANY_CLICK, (p, _) -> {
            if (isForging) return;

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
        setItem(50, recipeBook, MenuItemInteraction.ANY_CLICK, (p, _) ->
            BlightedMC.menuManager().openMenu(new ForgeRecipesMenu(this), p));
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
                " §7Fill your forge with §c🔥 thermal fuel §7like",
                " §7§fLava Bucket§7, to power the forge and",
                " §7craft advanced items."
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
        return new ItemBuilder(Material.BLAZE_POWDER, "§6Thermal Fuel")
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
                "   §8‣ §dMagma Bucket §8- §620,000 mB",
                "   §8‣ §cPlasma Bucket §8- §650,000 mB",
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
        if (player == null) return;

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
            refresh(player);
        } else {
            Formatter.warn(player, "No suitable fuel found in your inventory!");
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

            if (fuelPerItem > remainingSpace) continue;
            int stackAmount = item.getAmount();

            int itemsToConsume = Math.min(stackAmount, remainingSpace / fuelPerItem);

            if (itemsToConsume > 0) {
                totalAdded += itemsToConsume * fuelPerItem;

                int newAmount = stackAmount - itemsToConsume;
                if (newAmount > 0) {
                    item.setAmount(newAmount);
                    player.getInventory().setItem(slot, item);
                } else {
                    player.getInventory().setItem(slot, null);
                }
            }

            if (totalAdded >= availableSpace) break;
        }
        return totalAdded;
    }

    private void forgeItem(Player player) {
        if (isForging) return;

        checkRequirements(player);
        if (!canForge) {
            Formatter.warn(player, "Not enough resources!");
            refresh(player);
            return;
        }

        this.isForging = true;
        this.canForge = false;

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);

        consumeIngredients(player);
        blightedPlayer.removeForgeFuel(recipe.getFuelCost());
        blightedPlayer.saveData();

        refresh(player);
        SoundSequence.FORGE_ITEM.play(player.getLocation());
        refresh(player);

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

            Player p = getPlayer();
            if (p != null && p.isOnline()) {
                HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(result);
                if (!leftover.isEmpty()) {
                    for (ItemStack item : leftover.values()) {
                        p.getWorld().dropItem(p.getLocation(), item);
                    }
                }

                this.isForging = false;
                if (p.getOpenInventory().getTopInventory().getHolder() instanceof ForgeMenu) {
                    refresh(p);
                }
            }
        }, 22L);
    }

    private void updateStatusPanes(Material material, int[] slots) {
        if (!(inventory.getHolder() instanceof ForgeMenu)) return;

        ItemStack pane = new ItemBuilder(material, "§6Forging...")
            .setHideTooltip()
            .toItemStack();

        for (int slot : slots) {
            inventory.setItem(slot, pane);
        }
    }

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
