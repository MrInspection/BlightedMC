package fr.moussax.blightedMC.shared.ui.menu.system;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Provides menu operations for players.
 */
public final class MenuManager {

    private final MenuSystem menuSystem;

    /**
     * Creates a menu manager backed by the specified menu system.
     *
     * @param menuSystem system responsible for menu lifecycle
     */
    public MenuManager(@NonNull MenuSystem menuSystem) {
        this.menuSystem = menuSystem;
    }

    /**
     * Opens a menu for the specified player.
     *
     * @param menu   menu to open
     * @param player player viewing the menu
     */
    public void openMenu(@NonNull Menu menu, @NonNull Player player) {
        menuSystem.openMenu(menu, player);
    }
}
