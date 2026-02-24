package fr.moussax.blightedMC.smp.core.items.blocks.registry;

import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.smp.features.items.blocks.BlightedForge;
import fr.moussax.blightedMC.smp.features.items.blocks.BlightedWorkbench;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

public final class BlockRegistry {

    public static final HashMap<String, BlightedBlock> REGISTRY = new HashMap<>();

    private BlockRegistry() {
    }

    public static void initialize() {
        clear();

        register(new BlightedWorkbench());
        register(new BlightedForge());

        Log.success("BlockRegistry", "Registered " + REGISTRY.size() + " custom blocks.");
    }

    static void register(@NonNull BlightedBlock block) {
        if (block.getBlightedItem() == null) {
            Log.warn("BlockRegistry", "Attempted to register block with null item. Skipping.");
            return;
        }
        REGISTRY.put(block.getId(), block);
    }

    @Nullable
    public static BlightedBlock get(String id) {
        return REGISTRY.get(id);
    }

    public static void clear() {
        REGISTRY.clear();
    }
}
