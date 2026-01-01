package fr.moussax.blightedMC.smp.core.shared.ui.menu.system;

import fr.moussax.blightedMC.smp.core.items.crafting.menu.CraftingTableMenu;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.InteractiveMenu;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.Menu;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;

public final class MenuListener implements Listener {
    private final MenuSystem menuSystem;

    public MenuListener(@NonNull MenuSystem menuSystem) {
        this.menuSystem = menuSystem;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;

        boolean isTopInventory = event.getClickedInventory() == event.getView().getTopInventory();
        int slotIndex = event.getRawSlot();

        if (menu instanceof InteractiveMenu interactive) {
            if (!isTopInventory || interactive.isInteractable(slotIndex)) {
                event.setCancelled(false);
                Utilities.delay(() -> interactive.onUpdate(player), 1L);
                return;
            }
        }

        event.setCancelled(true);
        if (!isTopInventory) return;

        Menu.MenuSlot slot = menu.getSlots().get(event.getSlot());
        if (slot != null) {
            slot.handle(player, event.getClick());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;

        if (menu instanceof InteractiveMenu interactive) {
            for (int slot : event.getRawSlots()) {
                if (slot < event.getView().getTopInventory().getSize() && !interactive.isInteractable(slot)) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(false);
            Utilities.delay(() -> interactive.onUpdate((Player) event.getWhoClicked()), 1L);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;

        if (menu instanceof CraftingTableMenu craftingMenu) {
            craftingMenu.returnItems(player);
        }

        Menu activeMenu = menuSystem.getActiveMenu(player);
        if (activeMenu != null && activeMenu.getInventory().equals(event.getInventory())) {
            menuSystem.cleanup(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        menuSystem.cleanup(event.getPlayer());
    }
}
