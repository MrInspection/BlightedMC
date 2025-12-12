package fr.moussax.blightedMC.core.items.forging;

import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling forge fuels.
 * <p>
 * Maps specific items (vanilla or custom) to fuel values used in forging.
 * Provides methods to query total fuel, per-item fuel, and fuel validity.
 * </p>
 */
public class ForgeFuel {

    /** Mapping of item IDs to fuel amounts. */
    private static final Map<String, Integer> FORGE_FUELS = new HashMap<>();

    static {
        FORGE_FUELS.put("vanilla:COAL", 10);
        FORGE_FUELS.put("vanilla:MAGMA_BLOCK", 40);
        FORGE_FUELS.put("vanilla:BLAZE_ROD", 200);
        FORGE_FUELS.put("vanilla:LAVA_BUCKET", 1000);
        FORGE_FUELS.put("ENCHANTED_COAL", 3000);
        FORGE_FUELS.put("ENCHANTED_LAVA_BUCKET", 10000);
        FORGE_FUELS.put("MAGMA_LAVA_BUCKET", 20000);
        FORGE_FUELS.put("PLASMA_LAVA_BUCKET", 50000);
    }

    /**
     * Returns the total fuel provided by the given item stack.
     *
     * @param item the item stack to evaluate
     * @return total fuel amount (fuel per item * item quantity)
     */
    public static int getFuelAmount(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 0;

        String id = getCustomId(item);
        if (id == null) {
            id = "vanilla:" + item.getType().name();
        }

        return FORGE_FUELS.getOrDefault(id, 0) * item.getAmount();
    }

    /**
     * Returns the fuel provided by a single item.
     *
     * @param item the item stack to evaluate
     * @return fuel amount for a single item
     */
    public static int getFuelPerItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 0;

        String id = getCustomId(item);
        if (id == null) {
            id = "vanilla:" + item.getType().name();
        }

        return FORGE_FUELS.getOrDefault(id, 0);
    }

    /**
     * Retrieves the custom item ID from the item's persistent data.
     *
     * @param item the item stack
     * @return custom ID if present, otherwise {@code null}
     */
    private static String getCustomId(ItemStack item) {
        if (item.getItemMeta() == null) return null;
        return item.getItemMeta().getPersistentDataContainer().get(
            ItemDirectory.ID_KEY, PersistentDataType.STRING);
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
