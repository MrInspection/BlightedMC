package fr.moussax.blightedMC.smp.core.items.forging;

import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides utility methods for managing forge fuels.
 *
 * <p>Maintains a mapping of specific items (vanilla or custom) to their fuel values
 * used in forging. Supports querying the fuel provided by a single item or an entire
 * stack, and checking whether an item is valid fuel.</p>
 *
 * <p>All methods are static and the class is non-instantiable.</p>
 */
public final class ForgeFuel {
    private static final Map<String, Integer> FORGE_FUELS = new HashMap<>();

    static {
        FORGE_FUELS.put("vanilla:COAL", 10);
        FORGE_FUELS.put("vanilla:MAGMA_BLOCK", 40);
        FORGE_FUELS.put("vanilla:BLAZE_ROD", 200);
        FORGE_FUELS.put("vanilla:LAVA_BUCKET", 1000);
        FORGE_FUELS.put("ENCHANTED_COAL", 3000);
        FORGE_FUELS.put("ENCHANTED_LAVA_BUCKET", 10000);
        FORGE_FUELS.put("MAGMA_BUCKET", 20000);
        FORGE_FUELS.put("PLASMA_BUCKET", 50000);
    }

    private ForgeFuel() {}

    /**
     * Returns the total fuel provided by the given item stack.
     *
     * @param item the item stack to evaluate
     * @return total fuel amount (fuel per item * item quantity)
     */
    public static int getFuelAmount(@NonNull ItemStack item) {
        if (item.getType() == Material.AIR) return 0;

        String id = Utilities.resolveItemId(item, "");

        return FORGE_FUELS.getOrDefault(id, 0) * item.getAmount();
    }

    /**
     * Returns the fuel provided by a single item.
     *
     * @param item the item stack to evaluate
     * @return fuel amount for a single item
     */
    public static int getFuelPerItem(@NonNull ItemStack item) {
        if (item.getType() == Material.AIR) return 0;

        String id = Utilities.resolveItemId(item, "");
        return FORGE_FUELS.getOrDefault(id, 0);
    }

    /**
     * Checks if the item is a valid forge fuel.
     *
     * @param item the item stack to check
     * @return {@code true} if it can be used as fuel, {@code false} otherwise
     */
    public static boolean isFuel(ItemStack item) {
        return getFuelPerItem(item) > 0;
    }
}
