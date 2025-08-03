package fr.moussax.blightedMC.core.menus;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListeners implements Listener {

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getClickedInventory() == null) return;
    if (!(event.getClickedInventory().getHolder() instanceof Menu menu)) return;

    ClickableItem item = menu.getItemAt(event.getSlot());

    if (item == null) return;
    event.setCancelled(true);

    BlightedPlayer player = BlightedPlayer.getBlightedPlayer((Player) event.getWhoClicked());
    MenuItemClickEvent clickEvent = new MenuItemClickEvent(item, event.getCursor(), player);
    Bukkit.getPluginManager().callEvent(clickEvent);
  }

  @EventHandler
  public void onMenuItemClick(MenuItemClickEvent event) {
    assert event.getClickedItem().getItem().getItemMeta() != null;
    event.getClickedItem().click((Player) event.getBlightedPlayer());
  }
}
