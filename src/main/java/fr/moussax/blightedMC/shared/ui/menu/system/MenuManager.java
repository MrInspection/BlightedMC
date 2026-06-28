package fr.moussax.blightedMC.shared.ui.menu.system;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Utility class for opening menus for players.
 */
public final class MenuManager {
    private final MenuSystem menuSystem;

    public MenuManager(@NonNull MenuSystem menuSystem) {
        this.menuSystem = menuSystem;
    }

    /**
     * Opens a menu for the specified player.
     *
     * @param menu   menu to open
     * @param player player who will see the menu
     */
    public void openMenu(@NonNull Menu menu, @NonNull Player player) {
        menu.setMenuSystem(menuSystem);
        menu.open(player);
    }
}
