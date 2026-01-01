package fr.moussax.blightedMC.smp.core.shared.ui.menu.system;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central system managing menu lifecycle, navigation, and cleanup.
 *
 * <p>Handles menu history, active menu tracking, and proper shutdown cleanup
 * to prevent resource leaks and class loading issues.</p>
 */
public final class MenuSystem {
    private final BlightedMC plugin;
    private final Map<UUID, Deque<Menu>> menuHistory = new ConcurrentHashMap<>();
    private final Map<UUID, Menu> activeMenus = new ConcurrentHashMap<>();
    private volatile boolean shutdownInitiated = false;

    public MenuSystem(@NonNull BlightedMC plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a menu as currently open for a player.
     *
     * @param player player opening the menu
     * @param menu   menu being opened
     */
    public void registerMenu(@NonNull Player player, @NonNull Menu menu) {
        if (shutdownInitiated) return;

        UUID playerId = player.getUniqueId();
        menuHistory.computeIfAbsent(playerId, k -> new ArrayDeque<>()).push(menu);
        activeMenus.put(playerId, menu);
    }

    /**
     * Retrieves the currently active menu for a player.
     *
     * @param player player to check
     * @return active menu, or null if none
     */
    public Menu getActiveMenu(@NonNull Player player) {
        return activeMenus.get(player.getUniqueId());
    }

    /**
     * Navigates back to the previous menu.
     *
     * @param player player navigating back
     */
    public void goBack(@NonNull Player player) {
        if (shutdownInitiated) return;

        UUID playerId = player.getUniqueId();
        Deque<Menu> stack = menuHistory.get(playerId);

        if (stack == null || stack.isEmpty()) {
            player.closeInventory();
            cleanup(player);
            return;
        }

        stack.pop();

        if (stack.isEmpty()) {
            player.closeInventory();
            cleanup(player);
        } else {
            Menu previousMenu = stack.peek();
            if (previousMenu != null) {
                previousMenu.open(player);
            }
        }
    }

    /**
     * Cleans up all menu data for a player.
     *
     * @param player player to clean up
     */
    public void cleanup(@NonNull Player player) {
        UUID playerId = player.getUniqueId();
        menuHistory.remove(playerId);
        activeMenus.remove(playerId);
    }

    /**
     * Initiates system shutdown and cleans up all resources.
     *
     * <p>Should be called during plugin disabled to prevent class loading issues.</p>
     */
    public void shutdown() {
        shutdownInitiated = true;

        // Close all active menus
        new ArrayList<>(activeMenus.keySet()).forEach(playerId -> {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        });

        menuHistory.clear();
        activeMenus.clear();
    }

    /**
     * Checks if the system is shutting down.
     *
     * @return true if shutdown has been initiated
     */
    public boolean isShuttingDown() {
        return shutdownInitiated;
    }
}
