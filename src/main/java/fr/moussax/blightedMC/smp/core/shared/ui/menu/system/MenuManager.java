package fr.moussax.blightedMC.smp.core.shared.ui.menu.system;

import fr.moussax.blightedMC.smp.core.shared.ui.menu.Menu;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Utility class for opening menus for players.
 *
 * <p>Provides overloaded methods to handle both standard
 * {@link Menu} instances and {@link PaginatedMenu} instances.</p>
 */
public final class MenuManager {
    private final MenuSystem menuSystem;

    public MenuManager(@NonNull MenuSystem menuSystem) {
        this.menuSystem = menuSystem;
    }

    /**
     * Opens a standard menu for the specified player.
     *
     * @param menu   menu to open
     * @param player player who will see the menu
     */
    public void openMenu(@NonNull Menu menu, @NonNull Player player) {
        menu.setMenuSystem(menuSystem);
        menu.open(player);
    }

    /**
     * Opens a paginated menu for the specified player.
     *
     * @param menu   paginated menu to open
     * @param player player who will see the menu
     */
    public void openMenu(@NonNull PaginatedMenu menu, @NonNull Player player) {
        menu.setMenuSystem(menuSystem);
        menu.open(player);
    }
}
