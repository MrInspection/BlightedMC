package fr.moussax.blightedMC.core.items.blocks;

import fr.moussax.blightedMC.core.registry.blocks.BlightedCraftingTable;

import java.util.HashMap;

public final class BlocksRegistry {
  public static final HashMap<String, BlightedBlock> CUSTOM_BLOCKS = new HashMap<>();

  public static void clearBlocks() {
    CUSTOM_BLOCKS.clear();
  }

  public static void addBlock(BlightedBlock block) {
    CUSTOM_BLOCKS.put(block.itemManager.getItemId(), block);
  }

  public static void initializeBlocks() {
    clearBlocks();

    // Register custom placeable blocks here
    new BlightedCraftingTable();
  }
}
