package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Represents an action executed when a menu item is clicked.
 *
 * <p>This is a functional interface and can be implemented with a lambda expression.</p>
 */
@FunctionalInterface
public interface MenuAction {

  /**
   * Executes the menu action for the given player and click type.
   *
   * @param player    the player who clicked
   * @param clickType the type of click
   */
  void execute(Player player, ClickType clickType);

  /**
   * Creates a new {@code MenuAction} that executes only for left-clicks.
   *
   * @param action the action to execute on left-click
   * @return a new {@code MenuAction} that triggers on left-click
   */
  static MenuAction left(MenuAction action) {
    return (player, clickType) -> {
      if (clickType == ClickType.LEFT) action.execute(player, clickType);
    };
  }

  /**
   * Creates a new {@code MenuAction} that executes only for right-clicks.
   *
   * @param action the action to execute on right-click
   * @return a new {@code MenuAction} that triggers on right-click
   */
  static MenuAction right(MenuAction action) {
    return (player, clickType) -> {
      if (clickType == ClickType.RIGHT) action.execute(player, clickType);
    };
  }
}
