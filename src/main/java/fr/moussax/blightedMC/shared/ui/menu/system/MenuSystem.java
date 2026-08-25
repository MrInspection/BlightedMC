package fr.moussax.blightedMC.shared.ui.menu.system;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.TickableMenu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages menu lifecycle, navigation, and active menu state.
 *
 * <p>Provides menu history, tracks active menus, and handles periodic updates
 * for {@link TickableMenu} implementations.</p>
 */
public final class MenuSystem {
    private final JavaPlugin plugin;
    private final Map<UUID, Deque<Menu>> menuHistory = new ConcurrentHashMap<>();
    private final Map<UUID, Menu> activeMenus = new ConcurrentHashMap<>();
    private volatile boolean shutdownInitiated = false;
    private final BukkitTask tickTask;
    private long tickCounter = 0L;

    /**
     * Creates a menu system and starts its shared update task.
     *
     * @param plugin plugin instance used to schedule menu updates
     */
    public MenuSystem(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.tickTask = plugin.getServer().getScheduler()
                .runTaskTimer(plugin, this::tickActiveMenus, 1L, 1L);
    }

    private void tickActiveMenus() {
        tickCounter++;
        for (Map.Entry<UUID, Menu> entry : activeMenus.entrySet()) {
            if (!(entry.getValue() instanceof TickableMenu tickable)) continue;
            if (tickCounter % Math.max(1L, tickable.tickPeriodTicks()) != 0) continue;

            Player player = plugin.getServer().getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                tickable.onTick(player);
            }
        }
    }

    /**
     * Opens a menu for a player.
     *
     * @param menu   menu to open
     * @param player player viewing the menu
     */
    public void openMenu(@NonNull Menu menu, @NonNull Player player) {
        if (shutdownInitiated) return;
        menu.setMenuSystem(this);
        menu.open(player);
    }

    /**
     * Registers a menu as active for a player.
     *
     * @param player player opening the menu
     * @param menu   menu being opened
     */
    public void registerMenu(@NonNull Player player, @NonNull Menu menu) {
        if (shutdownInitiated) return;

        UUID playerId = player.getUniqueId();
        Deque<Menu> stack = menuHistory.computeIfAbsent(playerId, k -> new ArrayDeque<>());
        if (stack.peek() != menu) {
            stack.push(menu);
        }
        activeMenus.put(playerId, menu);
    }

    /**
     * Returns the menu currently open for a player.
     *
     * @param player player to check
     * @return active menu, or {@code null} if none is open
     */
    public Menu getActiveMenu(@NonNull Player player) {
        return activeMenus.get(player.getUniqueId());
    }

    /**
     * Returns to the previous menu in the player's navigation history.
     *
     * <p>Closes the inventory when no previous menu remains.</p>
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
                previousMenu.setMenuSystem(this);
                previousMenu.open(player);
            }
        }
    }

    /**
     * Removes all menu state associated with a player.
     *
     * @param player player whose menu state should be removed
     */
    public void cleanup(@NonNull Player player) {
        UUID playerId = player.getUniqueId();
        menuHistory.remove(playerId);
        activeMenus.remove(playerId);
    }

    /**
     * Shuts down the menu system and releases all active resources.
     *
     * <p>Cancels the shared update task, closes active menus, and clears all
     * menu state.</p>
     */
    public void shutdown() {
        shutdownInitiated = true;
        if (tickTask != null) {
            tickTask.cancel();
        }

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
     * Checks whether shutdown has been initiated.
     *
     * @return {@code true} if the system is shutting down
     */
    public boolean isShuttingDown() {
        return shutdownInitiated;
    }
}
