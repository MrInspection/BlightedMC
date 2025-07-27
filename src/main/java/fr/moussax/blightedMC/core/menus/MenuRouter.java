package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MenuRouter {
  private static final Map<Player, Stack<Menu>> menuHistory = new HashMap<>();

  public static void openMenu(Player player, Menu menu) {
    closeCurrentMenu(player);
    menu.open(player);
    menuHistory.computeIfAbsent(player, k -> new Stack<>()).push(menu);
  }

  public static void closeCurrentMenu(Player player) {
    Menu currentMenu = getCurrentMenu(player);
    if(currentMenu != null) {
      currentMenu.close(player);
    }
  }

  public static Menu getCurrentMenu(Player player) {
    Stack<Menu> history = menuHistory.get(player);
    return history == null || history.isEmpty() ? null : history.peek();
  }

  public static void goBack(Player player) {
    Stack<Menu> history = menuHistory.get(player);
    if (history == null || history.isEmpty()) {
      player.closeInventory();
      return;
    }

    history.pop();
    if (history.isEmpty()) {
      player.closeInventory();
      return;
    }

    Menu previousMenu = history.peek();
    previousMenu.open(player);
  }
}
