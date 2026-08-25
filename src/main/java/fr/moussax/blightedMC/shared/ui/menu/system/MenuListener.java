package fr.moussax.blightedMC.shared.ui.menu.system;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.types.InteractiveMenu;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
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

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }

        boolean isTopInventory = event.getClickedInventory() == event.getView().getTopInventory();
        int slotIndex = event.getRawSlot();

        if (menu instanceof InteractiveMenu || menu.isInteractable(slotIndex)) {
            if (event.isShiftClick() && !isTopInventory) {
                event.setCancelled(true);
                return;
            }
            if (isTopInventory) {
                boolean interactable = menu.isInteractable(slotIndex);
                event.setCancelled(!interactable);
                if (interactable) {
                    Utilities.delay(() -> menu.onUpdate(player), 1L);
                }
                return;
            }
            event.setCancelled(false);
            Utilities.delay(() -> menu.onUpdate(player), 1L);
            return;
        }

        event.setCancelled(true);
        if (!isTopInventory) {
            return;
        }

        Menu.MenuSlot slot = menu.getSlots().get(event.getSlot());
        if (slot != null) {
            slot.handle(player, event.getClick());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;

        if (menu instanceof InteractiveMenu) {
            for (int slot : event.getRawSlots()) {
                if (slot < event.getView().getTopInventory().getSize() && !menu.isInteractable(slot)) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(false);
            Utilities.delay(() -> menu.onUpdate((Player) event.getWhoClicked()), 1L);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;

        menu.onClose(player);

        Utilities.delay(() -> {
            if (!player.isOnline()) {
                menuSystem.cleanup(player);
                return;
            }
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (!(topInventory.getHolder() instanceof Menu)) {
                menuSystem.cleanup(player);
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        menuSystem.cleanup(event.getPlayer());
    }
}
