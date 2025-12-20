package fr.moussax.blightedMC.core.entities.rituals.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.core.entities.rituals.RitualAnimations;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.MenuElementPreset;
import fr.moussax.blightedMC.core.menus.MenuItemInteraction;
import fr.moussax.blightedMC.core.menus.MenuManager;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.formatting.Formatter;
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

public class RitualAltarMenu extends Menu {
    private static final int[] GRID_SLOTS = {19, 20, 21, 28, 29, 30};
    private static final int[] REQUIRED_ITEM_INDICATOR_SLOTS = {10, 11, 12, 13};
    private static final int[] INVOKED_MOB_INDICATOR_SLOTS = {15, 16};
    private static final int[] ITEM_INDICATOR = {23, 45, 46, 47, 48, 51, 52, 53};

    private final AncientRitual ritual;
    private final Menu previousMenu;
    private boolean canInvoke = false;

    public RitualAltarMenu(AncientRitual ritual, Menu previousMenu) {
        super("Rituals Altar", 54);
        this.ritual = ritual;
        this.previousMenu = previousMenu;
    }

    public RitualAltarMenu(AncientRitual ritual) {
        super("Rituals Altar", 54);
        this.ritual = ritual;
        this.previousMenu = null;
    }

    @Override
    public void build(Player player) {
        if (ritual != null) checkRequirements(player);

        fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);

        // Setup grids and indicators
        setupGrid();
        if (ritual != null) displayRequiredIngredients();
        setupStatusPanes();
        setupResultDisplay();
        setupActionButtons(player);
    }

    private void setupGrid() {
        ItemBuilder builder = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§cLocked Slot");
        if (ritual == null) builder.addLore("§7Select an ancient ritual to view", "§7view the required offerings.");
        else builder.addLore("§7This slot isn't used for", "§7the selected ritual.");

        fillSlots(GRID_SLOTS, builder.toItemStack());
    }

    private void setupResultDisplay() {
        ItemStack result;
        if (ritual == null) {
            result = new ItemBuilder(Material.BARRIER, "§cRitual Required")
                .addLore("§7Select an ancient ritual to start", "§7the invocation process.")
                .toItemStack();
        } else {
            result = ritual.getSummoningItem().clone();
            if (!result.hasItemMeta() || !Objects.requireNonNull(result.getItemMeta()).hasDisplayName()) {
                ItemBuilder builder = new ItemBuilder(result);
                if (ritual.getSummonedCreature() != null)
                    builder.setDisplayName("§5" + ritual.getSummonedCreature().getName());
                result = builder.toItemStack();
            }
        }
        setItem(25, result, MenuItemInteraction.ANY_CLICK, (p, t) -> {});
    }

    private void checkRequirements(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        Map<String, Integer> requiredCounts = aggregateRequirements();
        boolean hasItems = requiredCounts.entrySet().stream()
            .allMatch(e -> hasEnoughIngredient(player, e.getKey(), e.getValue()));
        boolean hasGems = blightedPlayer.getGemsManager().hasEnoughGems(ritual.getGemstoneCost());
        boolean hasXp = player.getLevel() >= ritual.getExperienceLevelCost();
        this.canInvoke = hasItems && hasGems && hasXp;
    }

    private Map<String, Integer> aggregateRequirements() {
        Map<String, Integer> counts = new HashMap<>();
        for (CraftingObject ingredient : ritual.getOfferings()) {
            counts.merge(ingredient.getId(), ingredient.getAmount(), Integer::sum);
        }
        return counts;
    }

    private boolean hasEnoughIngredient(Player player, String itemId, int requiredAmount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (Utilities.resolveItemId(item, itemId).equals(itemId)) count += item.getAmount();
        }
        return count >= requiredAmount;
    }

    private void displayRequiredIngredients() {
        for (int i = 0; i < ritual.getOfferings().size() && i < GRID_SLOTS.length; i++) {
            CraftingObject ingredient = ritual.getOfferings().get(i);
            setItem(GRID_SLOTS[i], createDisplayItem(ingredient), MenuItemInteraction.ANY_CLICK, (p, t) -> {});
        }
    }

    private ItemStack createDisplayItem(CraftingObject ingredient) {
        ItemStack item = ingredient.isCustom() ? ingredient.getManager().toItemStack().clone() : ingredient.getVanillaItem().clone();
        item.setAmount(ingredient.getAmount());
        return item;
    }

    private void setupStatusPanes() {
        Material indicator = determineIndicatorMaterial();
        ItemStack sacrificedPane = new ItemBuilder(indicator, "§5Offerings to Sacrifice")
            .addLore("§7The items required to invoke", "§7the §4⚚ Ancient Creature §7are ", "§7displayed in this side.").toItemStack();
        ItemStack invokedPane = new ItemBuilder(indicator, "§5Creature to Invoke")
            .addLore("§7The §4⚚ Ancient Creature §7you will", "§7invoke with your offerings.").toItemStack();
        ItemStack fillerPane = new ItemBuilder(indicator, "§r").setHideTooltip().toItemStack();

        fillSlots(REQUIRED_ITEM_INDICATOR_SLOTS, sacrificedPane);
        fillSlots(INVOKED_MOB_INDICATOR_SLOTS, invokedPane);
        fillSlots(ITEM_INDICATOR, fillerPane);

        ItemStack forgeIcon = new ItemBuilder(Material.SCULK_SHRIEKER, "§5Rituals Altar")
            .addLore("§7Sacrifice offerings to the ", "§7Rituals Altar to invoke an", "§7ancient creature.").toItemStack();
        setItem(14, forgeIcon, MenuItemInteraction.ANY_CLICK, (p, t) -> {});
    }

    private Material determineIndicatorMaterial() {
        if (ritual == null) return Material.WHITE_STAINED_GLASS_PANE;
        return canInvoke ? Material.PURPLE_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
    }

    private void setupActionButtons(Player player) {
        setupInvokeButton(player);
        setupNavigationButtons();
    }

    private void setupInvokeButton(Player player) {
        ItemBuilder builder = new ItemBuilder(Material.END_PORTAL_FRAME);
        if (ritual == null) {
            builder.setDisplayName("§5Initiate Invocation")
                .addLore("§7Select an ancient ritual to start", "§7the invocation process.");
        } else {
            builder.setDisplayName("§5Invoke " + ritual.getSummonedCreature().getName())
                .addLore("", " §7Cost: ");
            for (CraftingObject ingredient : ritual.getOfferings())
                builder.addLore(" §8‣ " + Utilities.extractIngredientName(ingredient) + " §8x" + ingredient.getAmount());

            if (ritual.getGemstoneCost() > 0)
                builder.addLore(" §8‣ §d" + Formatter.formatDecimalWithCommas(ritual.getGemstoneCost()) + " Gems");
            if (ritual.getExperienceLevelCost() > 0)
                builder.addLore(" §8‣ §3" + Formatter.formatDecimalWithCommas(ritual.getExperienceLevelCost()) + " XP Levels");

            builder.addLore("", " §8§lBEWARE!", " §8Such ritual demand sacrifice,", " §8and the Ancients do not return", " §8unchanged.", "")
                .addLore(canInvoke ? "§eClick to confirm!" : "§cYou don't have the required ingredients!")
                .setEnchantmentGlint(canInvoke);
        }

        setItem(32, builder.toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
            if (ritual != null && canInvoke) invokeMob(p);
            else p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
        });
    }

    private void setupNavigationButtons() {
        ItemStack recipeBook = new ItemBuilder(Material.WRITABLE_BOOK, "§5Ancient Rituals")
            .addLore("§8Voidling Grimoire", "",
                " §7Browse ancient rituals written eons ", " §7ago by the §5Voidling Mages§7, devised ",
                " §7to invoke forgotten creatures", " §7back into existence.", "",
                " §8§lBEWARE!", " §8Such rituals demand sacrifice,", " §8and the Ancients do not return",
                " §8unchanged.", "", "§eClick to browse!").toItemStack();

        setItem(49, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
        setItem(50, recipeBook, MenuItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(new RitualsDirectoryMenu(this), p));
    }

    private void consumeIngredients(Player player) {
        ritual.getOfferings().forEach(ingredient -> Utilities.consumeItemsFromInventory(player, ingredient));
        BlightedPlayer.getBlightedPlayer(player).removeGems(ritual.getGemstoneCost());
        player.setLevel(player.getLevel() - ritual.getExperienceLevelCost());
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

        final Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(3));
        spawnLoc.setY(player.getLocation().getY());

        SoundSequence.ANCIENT_MOB_SPAWN.play(spawnLoc);
        RitualAnimations.playRiteAnimation(BlightedMC.getInstance(), spawnLoc, () -> handleFinalImpact(spawnLoc));
    }

    private void handleFinalImpact(Location loc) {
        if (ritual.getSummonedCreature() != null) {
            Bukkit.broadcastMessage("§5 ☤ §dThe §c" + ritual.getSummonedCreature().getName() + " §dhas been summoned by §f" + getPlayer().getName() + "§d.");
            ritual.getSummonedCreature().clone().spawn(loc);
        }
    }
}

