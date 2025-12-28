package fr.moussax.blightedMC.smp.core.menus;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handles the navigation and history of opened menus for each player.
 *
 * <p>This class maintains a stack of menus per player to allow
 * going back to the previous menu and clearing menu history.</p>
 */
public class MenuRouter {
    private static final Map<UUID, Deque<Menu>> history = new HashMap<>();

    /**
     * Registers the currently opened menu for a player.
     *
     * <p>If the player already has a menu stack, the new menu is pushed
     * onto it. Otherwise, a new stack is created.</p>
     *
     * @param player the player opening the menu
     * @param menu   the menu to register as current
     */
    public static void setCurrentMenu(Player player, Menu menu) {
        history.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>()).push(menu);
    }

    /**
     * Navigates back to the previous menu for the player.
     *
     * <p>If the player has no previous menu, their inventory is closed.
     * Otherwise, the previous menu in the stack is reopened.</p>
     *
     * @param player the player navigating back
     */
    public static void goBack(Player player) {
        Deque<Menu> stack = history.get(player.getUniqueId());
        if (stack == null || stack.isEmpty()) {
            player.closeInventory();
            return;
        }

        stack.pop();

        if (stack.isEmpty()) {
            player.closeInventory();
            history.remove(player.getUniqueId());
        } else {
            stack.peek().open(player);
        }
    }

    /**
     * Clears the menu history for a specific player.
     *
     * <p>After calling this, {@link #goBack(Player)} will immediately close the inventory
     * if called for the same player.</p>
     *
     * @param player the player whose menu history should be cleared
     */
    public static void clearHistory(Player player) {
        history.remove(player.getUniqueId());
    }
}
