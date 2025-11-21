package fr.moussax.blightedMC.core.menus;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof Menu menu)) return;
        event.setCancelled(true);
        Menu.MenuSlot slot = menu.slots.get(event.getSlot());
        if (slot != null) {
            slot.handle(player, event.getClick());
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.0f);
        }
    }
}
