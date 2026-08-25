package fr.moussax.blightedMC.shared.ui.menu.interaction;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Represents an action executed when a menu item is clicked.
 *
 * <p>Supports filtering actions by click type through the provided factory
 * methods.</p>
 */
@FunctionalInterface
public interface MenuAction {

    /**
     * Executes this action for the specified player and click type.
     *
     * @param player    player who clicked
     * @param clickType click type that triggered the action
     */
    void execute(Player player, ClickType clickType);

    /**
     * Creates an action that executes only for left clicks.
     *
     * @param action action to execute
     * @return action restricted to left clicks
     */
    static MenuAction left(MenuAction action) {
        return (player, clickType) -> {
            if (clickType == ClickType.LEFT) action.execute(player, clickType);
        };
    }

    /**
     * Creates an action that executes only for right clicks.
     *
     * @param action action to execute
     * @return action restricted to right clicks
     */
    static MenuAction right(MenuAction action) {
        return (player, clickType) -> {
            if (clickType == ClickType.RIGHT) action.execute(player, clickType);
        };
    }

    /**
     * Creates an action that executes only for middle clicks.
     *
     * @param action action to execute
     * @return action restricted to middle clicks
     */
    static MenuAction middle(MenuAction action) {
        return (player, clickType) -> {
            if (clickType == ClickType.MIDDLE) action.execute(player, clickType);
        };
    }
}
