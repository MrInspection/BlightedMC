package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Menu menu = Menu.getCurrent(player);
        if (menu == null) return;
        event.setCancelled(true);
        menu.handleClick(player, event.getSlot(), event.getClick());
    }
}