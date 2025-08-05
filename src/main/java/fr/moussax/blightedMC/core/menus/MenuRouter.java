package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Handles menu navigation and history for players.
 *
 * <p>Each player has a stack of menus representing their navigation history.
 * The top of the stack is the currently opened menu.</p>
 */
public class MenuRouter {
  private static final Map<Player, Stack<Menu>> history = new HashMap<>();

  /**
   * Registers the given menu as the current menu for the player.
   * Adds it to the player's navigation history.
   *
   * @param player the player
   * @param menu   the menu to set as current
   */
  public static void setCurrentMenu(Player player, Menu menu) {
    history.computeIfAbsent(player, k -> new Stack<>()).push(menu);
  }

  /**
   * Goes back to the previous menu in the player's history.
   * <ul>
   *   <li>If there is a previous menu, it is opened.</li>
   *   <li>If no previous menu exists, the inventory is closed
   *       and the history is cleared.</li>
   * </ul>
   *
   * @param player the player
   */
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

  /**
   * Returns the currently opened menu for the player.
   *
   * @param player the player
   * @return the current menu, or {@code null} if none
   */
  public static Menu getCurrent(Player player) {
    Stack<Menu> stack = history.get(player);
    return (stack == null || stack.isEmpty()) ? null : stack.peek();
  }

  /**
   * Clears the player's menu history.
   *
   * @param player the player
   */
  public static void clearHistory(Player player) {
    Stack<Menu> stack = history.get(player);
    if (stack != null) stack.clear();
  }
}
