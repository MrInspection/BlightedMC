package fr.moussax.bedrock.ui.menu;

import org.bukkit.entity.Player;

/**
 * Opt-in for menus that require periodic updates while open.
 *
 * <p>Implementations should update individual slots via
 * {@link Menu#setSlotItem(int, org.bukkit.inventory.ItemStack)} rather than
 * calling {@link Menu#refresh()} to avoid rebuilding the menu on every tick.</p>
 */
@FunctionalInterface
public interface TickableMenu {

    /**
     * Returns the number of ticks between updates.
     *
     * @return tick interval between {@link #onTick(Player)} calls
     */
    default long tickPeriodTicks() {
        return 20L;
    }

    /**
     * Updates the menu for the specified viewer.
     *
     * <p>Called periodically while this menu is the player's active menu.</p>
     *
     * @param player viewer whose menu is being updated
     */
    void onTick(Player player);
}
