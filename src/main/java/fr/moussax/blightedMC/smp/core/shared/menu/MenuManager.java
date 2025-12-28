package fr.moussax.blightedMC.smp.core.shared.menu;

import org.bukkit.entity.Player;

/**
 * Utility class for opening menus for players.
 *
 * <p>Provides overloaded methods to handle both standard
 * {@link Menu} instances and {@link PaginatedMenu} instances.</p>
 */
public final class MenuManager {

    /**
     * Opens a standard menu for the specified player.
     *
     * @param menu   menu to open
     * @param player player who will see the menu
     */
    public static void openMenu(Menu menu, Player player) {
        menu.open(player);
    }

    /**
     * Opens a paginated menu for the specified player.
     *
     * @param menu   paginated menu to open
     * @param player player who will see the menu
     */
    public static void openMenu(PaginatedMenu menu, Player player) {
        menu.open(player);
    }
}
