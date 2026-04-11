package fr.moussax.blightedMC.engine.items.forging;

import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class ForgeFuel {
    private static final Map<String, Integer> FORGE_FUELS = new HashMap<>();

    static {
        FORGE_FUELS.put("vanilla:COAL", 10);
        FORGE_FUELS.put("vanilla:MAGMA_BLOCK", 40);
        FORGE_FUELS.put("vanilla:BLAZE_ROD", 200);
        FORGE_FUELS.put("vanilla:LAVA_BUCKET", 1_000);
        FORGE_FUELS.put("ENCHANTED_COAL", 3_000);
        FORGE_FUELS.put("ENCHANTED_LAVA_BUCKET", 10_000);
        FORGE_FUELS.put("MAGMA_BUCKET", 20_000);
        FORGE_FUELS.put("PLASMA_BUCKET", 50_000);
    }

    private ForgeFuel() {
    }

    public static int getFuelAmount(@NonNull ItemStack item) {
        if (item.getType() == Material.AIR) return 0;

        String id = Utilities.resolveItemId(item, "");

        return FORGE_FUELS.getOrDefault(id, 0) * item.getAmount();
    }

    public static int getFuelPerItem(@NonNull ItemStack item) {
        if (item.getType() == Material.AIR) return 0;

        String id = Utilities.resolveItemId(item, "");
        return FORGE_FUELS.getOrDefault(id, 0);
    }

    public static boolean isFuel(ItemStack item) {
        return getFuelPerItem(item) > 0;
    }
}
