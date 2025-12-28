package fr.moussax.blightedMC.smp.core.entities.rituals.menu;

import fr.moussax.blightedMC.smp.core.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.smp.core.entities.rituals.registry.RitualRegistry;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.shared.menu.*;
import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RitualsDirectoryMenu extends PaginatedMenu {

    private static final int[] RECIPE_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 40, 41, 42, 43
    };

    private static final int[] FILLER_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
        17, 18, 26, 27, 35, 36, 44,
        45, 46, 47, 51, 52, 53
    };

    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int NEXT_BUTTON_SLOT = 50;

    private final Menu previousMenu;
    private final List<AncientRitual> cachedRituals;

    public RitualsDirectoryMenu(Menu previousMenu) {
        super("Ancient Rituals", 54);
        this.previousMenu = previousMenu;
        this.cachedRituals = new ArrayList<>(RitualRegistry.REGISTRY);
        this.cachedRituals.sort((r1, r2) -> {
            String name1 = r1.getSummonedCreature() != null ? r1.getSummonedCreature().getName() : "Unknown Ritual";
            String name2 = r2.getSummonedCreature() != null ? r2.getSummonedCreature().getName() : "Unknown Ritual";
            return name1.compareTo(name2);
        });
    }

    @Override
    protected int getTotalItems(Player player) {
        return cachedRituals.size();
    }

    @Override
    protected int getItemsPerPage() {
        return RECIPE_SLOTS.length;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
        if (index >= cachedRituals.size()) {
            return new ItemStack(Material.AIR);
        }

        AncientRitual ritual = cachedRituals.get(index);
        return buildRiteDisplayItem(ritual);
    }

    @Override
    public void build(Player player) {
        totalItems = getTotalItems(player);
        int start = currentPage * getItemsPerPage();
        int end = Math.min(start + getItemsPerPage(), totalItems);

        populateRiteSlots(player, start, end);
        fillSlots(FILLER_SLOTS, MenuElementPreset.EMPTY_SLOT_FILLER);
        setupNavigationButtons(player, end);

        fillEmptyWith(new ItemStack(Material.AIR));
    }

    @Override
    protected void onItemClick(Player player, int index, org.bukkit.event.inventory.ClickType clickType) {
        if (index >= cachedRituals.size()) {
            return;
        }

        AncientRitual rite = cachedRituals.get(index);
        MenuManager.openMenu(new RitualAltarMenu(rite, this), player);
    }

    private ItemStack buildRiteDisplayItem(AncientRitual ritual) {
        ItemStack summoningItem = ritual.getSummoningItem().clone();

        // If the result item doesn't have a display name, use the entity name if possible
        if (!summoningItem.hasItemMeta() || !Objects.requireNonNull(summoningItem.getItemMeta()).hasDisplayName()) {
            ItemBuilder builder = new ItemBuilder(summoningItem);
            if (ritual.getSummonedCreature() != null) {
                builder.setDisplayName("§5" + ritual.getSummonedCreature().getName());
            } else {
                builder.setDisplayName("§5Unknown Ritual");
            }
            summoningItem = builder.toItemStack();
        }

        ItemMeta meta = summoningItem.getItemMeta();
        if (meta == null) {
            return summoningItem;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        appendRiteLore(lore, ritual);
        meta.setLore(lore);
        summoningItem.setItemMeta(meta);

        return summoningItem;
    }

    private void appendRiteLore(List<String> lore, AncientRitual ritual) {
        lore.add("");
        lore.add(" §7Offerings required: ");

        for (CraftingObject offering : ritual.getOfferings()) {
            String offeringName = Utilities.extractIngredientName(offering);
            lore.add(" §8‣ " + offeringName + " §8x" + offering.getAmount());
        }

        if (ritual.getGemstoneCost() > 0) {
            lore.add(" §8‣ §d" + Formatter.formatDecimalWithCommas(ritual.getGemstoneCost()) + " Gems");
        }
        if (ritual.getExperienceLevelCost() > 0) {
            lore.add(" §8‣ §3" + Formatter.formatDecimalWithCommas(ritual.getExperienceLevelCost()) + " XP Level");
        }

        lore.add("");
        lore.add("§eClick to select!");
    }

    private void populateRiteSlots(Player player, int start, int end) {
        int riteIndex = 0;

        for (int i = start; i < end && riteIndex < RECIPE_SLOTS.length; i++) {
            if (i >= cachedRituals.size()) {
                break;
            }

            final int itemIndex = i;
            setItem(RECIPE_SLOTS[riteIndex], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK,
                (p, t) -> onItemClick(p, itemIndex, t));
            riteIndex++;
        }
    }

    private void setupNavigationButtons(Player player, int end) {
        setupBackButton();
        setupNextButton(end);
        setupCloseButton();
    }

    private void setupBackButton() {
        if (currentPage > 0) {
            setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                currentPage--;
                MenuManager.openMenu(this, p);
            });
            return;
        }

        if (previousMenu != null) {
            setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                close();
                MenuManager.openMenu(previousMenu, p);
            });
        }
    }

    private void setupNextButton(int end) {
        if (end < totalItems) {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.NEXT_BUTTON, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                currentPage++;
                MenuManager.openMenu(this, p);
            });
        } else {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (_, _) -> {
            });
        }
    }

    private void setupCloseButton() {
        setItem(CLOSE_BUTTON_SLOT, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (_, _) -> close());
    }
}
