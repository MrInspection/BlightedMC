package fr.moussax.blightedMC.engine.entities.rituals.menu;

import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.engine.entities.rituals.registry.RitualRegistry;
import fr.moussax.blightedMC.engine.items.crafting.CraftingObject;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.PaginatedMenu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.Formatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class RitualsDirectoryMenu extends PaginatedMenu {

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
        this.cachedRituals.sort(Comparator.comparing(
                ritual -> ritual.getSummonedCreature() != null
                        ? ritual.getSummonedCreature().getName()
                        : "Unknown Ritual"
        ));
    }

    @Override
    protected int getTotalItems(@NonNull Player player) {
        return cachedRituals.size();
    }

    @Override
    protected int getItemsPerPage() {
        return RECIPE_SLOTS.length;
    }

    @Override
    protected ItemStack getItem(@NonNull Player player, int index) {
        if (index >= cachedRituals.size()) {
            return new ItemStack(Material.AIR);
        }

        AncientRitual ritual = cachedRituals.get(index);
        return buildRiteDisplayItem(ritual);
    }

    @Override
    public void build(@NonNull Player player) {
        totalItems = Math.max(0, getTotalItems(player));
        int itemsPerPage = getItemsPerPage();
        int maxPage = Math.max(0, (totalItems - 1) / itemsPerPage);
        currentPage = Math.min(currentPage, maxPage);

        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, totalItems);

        populateRiteSlots(player, start, end);
        fillSlots(FILLER_SLOTS, MenuElementPreset.EMPTY_SLOT_FILLER);
        setupNavigationButtons(end);
    }

    @Override
    protected void onItemClick(@NonNull Player player, int index, @NonNull ClickType clickType) {
        if (index >= cachedRituals.size()) {
            return;
        }

        AncientRitual rite = cachedRituals.get(index);
        openSubMenu(new RitualAltarMenu(rite, this));
    }

    private ItemStack buildRiteDisplayItem(AncientRitual ritual) {
        ItemStack summoningItem = ritual.getDisplayedItem().clone();
        ItemBuilder builder = new ItemBuilder(summoningItem);

        if (!summoningItem.hasItemMeta() || !Objects.requireNonNull(summoningItem.getItemMeta()).hasDisplayName()) {
            if (ritual.getSummonedCreature() != null) {
                builder.setDisplayName("§5" + ritual.getSummonedCreature().getName());
            } else {
                builder.setDisplayName("§5Unknown Ritual");
            }
        }

        if (ritual.getSummonedCreature() != null) {
            String timeFormatted = Formatter.formatTime(ritual.getSummonedCreature().getTimeAllowance());
            builder.addLore("", " §7Time limit: §c" + timeFormatted, "");
        }

        builder.addLore(" §7Offerings required: ");
        for (CraftingObject offering : ritual.getOfferings()) {
            builder.addLore(" §8‣ " + Utilities.extractIngredientName(offering) + " §8x" + offering.getAmount());
        }

        if (ritual.getGemsCost() > 0) {
            builder.addLore(" §8‣ §d" + Formatter.formatDecimalWithCommas(ritual.getGemsCost()) + "✵ Gems");
        }
        if (ritual.getLevelCost() > 0) {
            builder.addLore(" §8‣ §3" + Formatter.formatDecimalWithCommas(ritual.getLevelCost()) + "◎ EXP Levels");
        }

        builder.addLore("", "§eClick to select!");
        return builder.toItemStack();
    }

    private void populateRiteSlots(Player player, int start, int end) {
        int riteIndex = 0;
        for (int i = start; i < end && riteIndex < RECIPE_SLOTS.length; i++) {
            final int itemIndex = i;
            setItem(RECIPE_SLOTS[riteIndex], getItem(player, itemIndex), (p, click) -> onItemClick(p, itemIndex, click));
            riteIndex++;
        }
    }

    private void setupNavigationButtons(int end) {
        if (currentPage > 0) {
            setBackButton(BACK_BUTTON_SLOT, (p, _) -> {
                currentPage--;
                refresh(p);
            });
        } else if (previousMenu != null) {
            setBackButton(BACK_BUTTON_SLOT, previousMenu);
        }

        if (end < totalItems) {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.NEXT_BUTTON, (p, _) -> {
                currentPage++;
                refresh(p);
            });
        } else {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), (_, _) -> {
            });
        }

        setCloseButton(CLOSE_BUTTON_SLOT);
    }
}
