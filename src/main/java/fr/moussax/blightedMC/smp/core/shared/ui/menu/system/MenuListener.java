package fr.moussax.blightedMC.smp.core.shared.ui.menu.system;

import fr.moussax.blightedMC.smp.core.shared.ui.menu.Menu;
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

        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        Menu.MenuSlot slot = menu.getSlots().get(event.getSlot());
        if (slot != null) {
            slot.handle(player, event.getClick());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu)) return;

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
