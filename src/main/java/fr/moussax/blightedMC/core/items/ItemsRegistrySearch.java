package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemsRegistrySearch implements Listener {
  public static final ConcurrentHashMap<UUID, Menu> awaitingSearch = new ConcurrentHashMap<>();

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    if (awaitingSearch.containsKey(uuid)) {
      event.setCancelled(true);
      String search = event.getMessage().trim();
      Menu previousMenu = awaitingSearch.remove(uuid);
      Bukkit.getScheduler().runTask(fr.moussax.blightedMC.BlightedMC.getInstance(), () -> {
        if (!search.isEmpty()) {
          MenuManager.openMenu(new ItemsRegistryMenu.SearchResultsPaginatedMenu(search, previousMenu), event.getPlayer());
        } else {
          MenuManager.openMenu(previousMenu, event.getPlayer());
        }
      });
    }
  }
}