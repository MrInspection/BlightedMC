package fr.moussax.blightedMC.engine.entities.rituals.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.engine.entities.rituals.RitualAnimations;
import fr.moussax.blightedMC.engine.items.crafting.CraftingObject;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.Formatter;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RitualAltarMenu extends Menu {
    private static final int[] GRID_SLOTS = {19, 20, 21, 28, 29, 30};
    private static final int[] REQUIRED_ITEM_INDICATOR_SLOTS = {10, 11, 12, 13};
    private static final int[] INVOKED_MOB_INDICATOR_SLOTS = {15, 16};
    private static final int[] ITEM_INDICATOR = {23, 45, 46, 47, 48, 51, 52, 53};

    private final AncientRitual ritual;
    private final Menu previousMenu;
    private boolean canInvoke = false;

    public RitualAltarMenu(AncientRitual ritual, Menu previousMenu) {
        super(ritual == null ? "Rituals Altar" : "Invoke Creature", 54);
        this.ritual = ritual;
        this.previousMenu = previousMenu;
    }

    public RitualAltarMenu(AncientRitual ritual) {
        this(ritual, null);
    }

    @Override
    public void build(Player player) {
        if (ritual != null) {
            checkRequirements(player);
        }

        fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
        setupGrid();

        if (ritual != null) {
            displayRequiredIngredients();
        }

        setupStatusPanes();
        setupResultDisplay();
        setupActionButtons(player);
    }

    private void setupGrid() {
        ItemBuilder builder = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§cLocked Slot");
        if (ritual == null) {
            builder.addLore("§7Select an ancient ritual to view", "§7the required offerings.");
        } else {
            builder.addLore("§7This slot isn't used for", "§7the selected ritual.");
        }

        fillSlots(GRID_SLOTS, builder.toItemStack());
    }

    private void setupResultDisplay() {
        if (ritual == null) {
            ItemStack barrier = new ItemBuilder(Material.BARRIER, "§cRitual Required")
                    .addLore("§7Select an ancient ritual to start", "§7the invocation process.")
                    .toItemStack();
            setItem(25, barrier, (_, _) -> {
            });
            return;
        }

        ItemStack result = ritual.getDisplayedItem().clone();
        if (!result.hasItemMeta() || !Objects.requireNonNull(result.getItemMeta()).hasDisplayName()) {
            ItemBuilder builder = new ItemBuilder(result);
            if (ritual.getSummonedCreature() != null) {
                builder.setDisplayName("§5" + ritual.getSummonedCreature().getName());
            }
            result = builder.toItemStack();
        }

        setItem(25, result, (_, _) -> {
        });
    }

    private void checkRequirements(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        Map<String, Integer> requiredCounts = aggregateRequirements();

        Map<String, Integer> inventoryCounts = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            for (String reqId : requiredCounts.keySet()) {
                if (Utilities.resolveItemId(item, reqId).equals(reqId)) {
                    inventoryCounts.put(reqId, inventoryCounts.getOrDefault(reqId, 0) + item.getAmount());
                }
            }
        }

        boolean hasItems = requiredCounts.entrySet().stream()
                .allMatch(e -> inventoryCounts.getOrDefault(e.getKey(), 0) >= e.getValue());
        boolean hasGems = blightedPlayer.getGemsManager().hasEnoughGems(ritual.getGemsCost());
        boolean hasXp = player.getLevel() >= ritual.getLevelCost();

        this.canInvoke = hasItems && hasGems && hasXp;
    }

    private Map<String, Integer> aggregateRequirements() {
        Map<String, Integer> counts = new HashMap<>();
        for (CraftingObject ingredient : ritual.getOfferings()) {
            counts.merge(ingredient.getId(), ingredient.getAmount(), Integer::sum);
        }
        return counts;
    }

    private void displayRequiredIngredients() {
        for (int i = 0; i < ritual.getOfferings().size() && i < GRID_SLOTS.length; i++) {
            CraftingObject ingredient = ritual.getOfferings().get(i);
            setItem(GRID_SLOTS[i], createDisplayItem(ingredient), (_, _) -> {
            });
        }
    }

    private ItemStack createDisplayItem(CraftingObject ingredient) {
        ItemStack item = ingredient.isCustom()
                ? Objects.requireNonNull(ingredient.getManager()).toItemStack().clone()
                : Objects.requireNonNull(ingredient.getVanillaItem()).clone();
        item.setAmount(ingredient.getAmount());
        return item;
    }

    private void setupStatusPanes() {
        Material indicator = determineIndicatorMaterial();
        ItemStack sacrificedPane = new ItemBuilder(indicator, "§5Offerings to Sacrifice")
                .addLore("§7The items required to invoke", "§7the §4⚚ Ancient Creature §7are", "§7displayed in this side.")
                .toItemStack();
        ItemStack invokedPane = new ItemBuilder(indicator, "§5Creature to Invoke")
                .addLore("§7The §4⚚ Ancient Creature §7you will", "§7invoke with your offerings.")
                .toItemStack();
        ItemStack fillerPane = new ItemBuilder(indicator, "§r").hideTooltip().toItemStack();

        fillSlots(REQUIRED_ITEM_INDICATOR_SLOTS, sacrificedPane);
        fillSlots(INVOKED_MOB_INDICATOR_SLOTS, invokedPane);
        fillSlots(ITEM_INDICATOR, fillerPane);

        ItemStack shriekerIcon = new ItemBuilder(Material.SCULK_SHRIEKER, "§5Blighted Altar")
                .addLore(
                        "§7The Ritual Altar allows you to offer",
                        "§7rare sacrifices, gems, and experience",
                        "§7to invoke ancient forgotten creatures."
                )
                .toItemStack();
        setItem(14, shriekerIcon, (_, _) -> {
        });
    }

    private Material determineIndicatorMaterial() {
        if (ritual == null) return Material.WHITE_STAINED_GLASS_PANE;
        return canInvoke ? Material.PURPLE_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
    }

    private void setupActionButtons(Player player) {
        setupInvokeButton();
        setupNavigationButtons();
    }

    private void setupInvokeButton() {
        ItemBuilder builder = new ItemBuilder(Material.END_PORTAL_FRAME);
        if (ritual == null) {
            builder.setDisplayName("§5Initiate Invocation")
                    .addLore("§7Select an ancient ritual to start", "§7the invocation process.");
        } else {
            String creatureName = ritual.getSummonedCreature() != null
                    ? ritual.getSummonedCreature().getName()
                    : "Creature";
            builder.setDisplayName("§5Invoke " + creatureName)
                    .addLore("", " §7Offerings required:");

            for (CraftingObject ingredient : ritual.getOfferings()) {
                builder.addLore(" §8‣ " + Utilities.extractIngredientName(ingredient) + " §8x" + ingredient.getAmount());
            }

            if (ritual.getGemsCost() > 0) {
                builder.addLore(" §8‣ §d" + Formatter.formatDecimalWithCommas(ritual.getGemsCost()) + "✵ Gems");
            }
            if (ritual.getLevelCost() > 0) {
                builder.addLore(" §8‣ §3" + Formatter.formatDecimalWithCommas(ritual.getLevelCost()) + "◎ EXP Levels");
            }

            builder.addLore(
                    "",
                    " §c§lBEWARE!",
                    " §cSuch rituals demand sacrifice,",
                    " §cand the Ancients do not return",
                    " §cunchanged.",
                    "",
                    canInvoke ? "§eClick to confirm!" : "§cYou don't meet the requirements!"
            ).setEnchantmentGlint(canInvoke);
        }

        setItem(32, builder.toItemStack(), (p, _) -> {
            if (ritual != null && canInvoke) {
                invokeMob(p);
            } else {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
            }
        });
    }

    private void setupNavigationButtons() {
        ItemStack recipeBook = new ItemBuilder(Material.WRITABLE_BOOK, "§5Ancient Rituals")
                .addLore(
                        "", " §7Browse ancient rituals written eons",
                        " §7ago by the §5Voidling Mages§7, devised",
                        " §7to invoke forgotten creatures",
                        " §7back into existence.",
                        "",
                        "§eClick to browse!"
                ).toItemStack();

        setCloseButton(49);
        setItem(50, recipeBook, (p, _) -> openSubMenu(new RitualsDirectoryMenu(this)));
    }

    private void consumeIngredients(Player player) {
        for (CraftingObject ingredient : ritual.getOfferings()) {
            Utilities.consumeItemsFromInventory(player, ingredient);
        }
        BlightedPlayer.getBlightedPlayer(player).removeGems(ritual.getGemsCost());
        player.setLevel(player.getLevel() - ritual.getLevelCost());
    }

    private void invokeMob(Player player) {
        checkRequirements(player);
        if (!canInvoke) {
            Formatter.warn(player, "You don't have the required ingredients!");
            refresh(player);
            return;
        }

        this.canInvoke = false;
        consumeIngredients(player);
        close();

        Bukkit.broadcastMessage("§5 ☤ §f" + player.getName() + " §dhas started an §5Ancient Ritual§d!");

        Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(3));
        spawnLoc.setY(player.getLocation().getY());

        SoundSequence.ANCIENT_MOB_SPAWN.play(spawnLoc);
        RitualAnimations.playRiteAnimation(BlightedMC.getInstance(), spawnLoc, () -> handleFinalImpact(spawnLoc));
    }

    private void handleFinalImpact(Location loc) {
        Player player = getPlayer();
        if (ritual.getSummonedCreature() == null || player == null) {
            return;
        }

        Bukkit.broadcastMessage("§5 ☤ §dThe §c" + ritual.getSummonedCreature().getName() + " §dhas been summoned by §f" + player.getName() + "§d.");
        ritual.getSummonedCreature().clone().spawn(loc);
    }
}
