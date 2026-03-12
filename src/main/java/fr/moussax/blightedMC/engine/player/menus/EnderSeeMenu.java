package fr.moussax.blightedMC.engine.player.menus;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class EnderSeeMenu extends Menu {
    private final Player target;
    private BukkitTask refreshTask;
    private Menu previousMenu;

    public EnderSeeMenu(Player target) {
        super(target.getName() + "'s Ender Chest", 36);
        this.target = target;
        this.previousMenu = null;
    }

    public EnderSeeMenu(Player target, Menu previousMenu) {
        super(target.getName() + "'s Ender Chest", 36);
        this.target = target;
        this.previousMenu = previousMenu;
    }

    @Override
    public void build(Player player) {
        setItem(
            0,
            MenuElementPreset.CLOSE_BUTTON,
            MenuItemInteraction.ANY_CLICK,
            (_, _) -> close()
        );
        fillSlots(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, MenuElementPreset.EMPTY_SLOT_FILLER);
        if (previousMenu != null) {
            setItem(
                1,
                MenuElementPreset.BACK_BUTTON,
                MenuItemInteraction.ANY_CLICK,
                (p, _) -> BlightedMC.menuManager().openMenu(previousMenu, p)
            );
        }
        updateContents();
    }

    @Override
    public void open(@NonNull Player player) {
        super.open(player);
        this.refreshTask = Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(), () -> {
            if (!player.isOnline() || player.getOpenInventory().getTopInventory() != getInventory()) {
                if (refreshTask != null && !refreshTask.isCancelled()) {
                    refreshTask.cancel();
                }
                return;
            }
            updateContents();
        }, 1L, 1L);
    }

    private void updateContents() {
        Inventory enderChest = target.getEnderChest();
        for (int slot = 0; slot < 27; slot++) {
            ItemStack realItem = enderChest.getItem(slot);
            int menuSlot = slot + 9;

            ItemStack currentItem = getInventory().getItem(menuSlot);
            if (!Objects.equals(realItem, currentItem)) {
                getInventory().setItem(menuSlot, realItem);
            }
            if (realItem != null) {
                setItem(menuSlot, realItem, MenuItemInteraction.ANY_CLICK, (_, _) -> {
                });
            }
        }
    }
}
