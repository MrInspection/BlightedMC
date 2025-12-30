package fr.moussax.blightedMC.smp.core.items.blocks.registry;

import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.smp.features.blocks.BlightedForge;
import fr.moussax.blightedMC.smp.features.blocks.BlightedWorkbench;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

/**
 * Central registry for all {@link BlightedBlock} instances in BlightedMC.
 */
public final class BlockRegistry {

    public static final HashMap<String, BlightedBlock> REGISTRY = new HashMap<>();

    private BlockRegistry() {
    }

    /**
     * Initializes the registry by clearing existing blocks and registering new ones.
     * <p>
     * Should be called during plugin startup.
     */
    public static void initialize() {
        clear();

        register(new BlightedWorkbench());
        register(new BlightedForge());

        Log.success("BlockRegistry", "Registered " + REGISTRY.size() + " custom blocks.");
    }

    /**
     * Registers a single block in the registry.
     *
     * @param block the block to register
     */
    static void register(@NonNull BlightedBlock block) {
        if (block.getBlightedItem() == null) {
            Log.warn("BlockRegistry", "Attempted to register block with null item. Skipping.");
            return;
        }
        REGISTRY.put(block.getId(), block);
    }

    /**
     * Retrieves a registered block by its ID.
     *
     * @param id the block's unique item ID
     * @return the corresponding {@link BlightedBlock}, or {@code null} if not found
     */
    @Nullable
    public static BlightedBlock get(String id) {
        return REGISTRY.get(id);
    }

    /**
     * Clears all blocks from the registry.
     */
    public static void clear() {
        REGISTRY.clear();
    }
}
