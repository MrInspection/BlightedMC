package fr.moussax.blightedMC.engine.player.menus;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.TickableMenu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class EnderSeeMenu extends Menu implements TickableMenu {
    private final Player target;
    private final Menu previousMenu;

    public EnderSeeMenu(Player target) {
        this(target, null);
    }

    public EnderSeeMenu(Player target, @Nullable Menu previousMenu) {
        super(target.getName() + "'s Ender Chest", 36);
        this.target = target;
        this.previousMenu = previousMenu;
    }

    @Override
    public long tickPeriodTicks() {
        return 1L;
    }

    @Override
    public void build(Player player) {
        setCloseButton(0);
        fillSlots(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, MenuElementPreset.EMPTY_SLOT_FILLER);
        if (previousMenu != null) {
            setBackButton(1, previousMenu);
        }
        updateContents();
    }

    @Override
    public void onTick(Player player) {
        if (!target.isOnline()) {
            close();
            return;
        }
        updateContents();
    }

    private void updateContents() {
        Inventory enderChest = target.getEnderChest();
        for (int slot = 0; slot < 27; slot++) {
            ItemStack realItem = enderChest.getItem(slot);
            int menuSlot = slot + 9;

            ItemStack displayItem = (realItem != null && realItem.getType() != Material.AIR) ? realItem : null;
            ItemStack currentItem = getInventory().getItem(menuSlot);
            if (!isSameItem(displayItem, currentItem)) {
                setSlotItem(menuSlot, displayItem);
            }
        }
    }

    private boolean isSameItem(ItemStack first, ItemStack second) {
        boolean emptyFirst = first == null || first.getType() == Material.AIR;
        boolean emptySecond = second == null || second.getType() == Material.AIR;
        if (emptyFirst && emptySecond) return true;
        if (emptyFirst || emptySecond) return false;
        return Objects.equals(first, second);
    }
}
