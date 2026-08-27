package fr.moussax.blightedMC.engine.items.blocks.registry;

import fr.moussax.blightedMC.engine.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class BlockRegistry {

    public static final Map<String, BlightedBlock> REGISTRY = new HashMap<>();

    private BlockRegistry() {
    }

    public static void initialize(List<RegistryModule<Consumer<BlightedBlock>>> modules) {
        clear();
        modules.forEach(module -> module.register(BlockRegistry::register));
        Log.success("BlockRegistry", "Registered " + REGISTRY.size() + " custom blocks.");
    }

    public static void register(@NonNull BlightedBlock block) {
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
