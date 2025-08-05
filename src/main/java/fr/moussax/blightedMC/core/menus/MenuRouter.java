package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MenuRouter {
  private static final Map<Player, Stack<Menu>> history = new HashMap<>();

  public static void setCurrentMenu(Player player, Menu menu) {
    history.computeIfAbsent(player, k -> new Stack<>()).push(menu);
  }

  public static void goBack(Player player) {
    Stack<Menu> stack = history.get(player);
    if (stack == null || stack.size() <= 1) {
      player.closeInventory();
      if (stack != null) stack.clear();
      return;
    }
    stack.pop();
    stack.peek().open(player);
  }

  public static Menu getCurrent(Player player) {
    Stack<Menu> stack = history.get(player);
    return (stack == null || stack.isEmpty()) ? null : stack.peek();
  }

  public static void clearHistory(Player player) {
    Stack<Menu> stack = history.get(player);
    if (stack != null) stack.clear();
  }
}
