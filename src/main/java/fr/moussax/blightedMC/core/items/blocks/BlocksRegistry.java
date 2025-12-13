package fr.moussax.blightedMC.core.items.blocks;

import fr.moussax.blightedMC.game.blocks.BlightedWorkbench;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.HashMap;
import java.util.List;

public final class BlocksRegistry {
    public static final HashMap<String, BlightedBlock> CUSTOM_BLOCKS = new HashMap<>();

    private static List<BlightedBlock> createBlocks() {
        return List.of(new BlightedWorkbench());
    }

    public static void clearBlocks() {
        CUSTOM_BLOCKS.clear();
    }

    public static void addBlock(BlightedBlock block) {
        if (block == null || block.itemTemplate == null) {
            Log.warn("BlocksRegistry", "Attempted to register a block with a null ItemTemplate. Skipping.");
            return;
        }
        CUSTOM_BLOCKS.put(block.itemTemplate.getItemId(), block);
    }

    public static void initializeBlocks() {
        clearBlocks();
        createBlocks().forEach(BlocksRegistry::addBlock);
        Log.success("BlocksRegistry", "Registered " + CUSTOM_BLOCKS.size() + " custom blocks.");
    }
}
