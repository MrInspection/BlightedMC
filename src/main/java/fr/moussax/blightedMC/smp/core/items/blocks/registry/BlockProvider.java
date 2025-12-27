package fr.moussax.blightedMC.smp.core.items.blocks.registry;

import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlock;

import java.util.List;

/**
 * Defines a module that provides custom blocks to the {@link BlockRegistry}.
 * <p>
 * Implementations should define their blocks in {@link #register()} and
 * can use the provided {@code add} methods to register them individually,
 * in bulk, or from a list.
 */
@FunctionalInterface
public interface BlockProvider {

    /**
     * Called to register all blocks defined by this provider.
     * Implementations should call one of the {@code add} methods here.
     */
    void register();

    /**
     * Registers a single block.
     *
     * @param block the block to register
     */
    default void add(BlightedBlock block) {
        BlockRegistry.register(block);
    }

    /**
     * Registers multiple blocks.
     *
     * @param blocks the blocks to register
     */
    default void add(BlightedBlock... blocks) {
        for (BlightedBlock block : blocks) {
            BlockRegistry.register(block);
        }
    }

    /**
     * Registers a list of blocks.
     *
     * @param blocks the list of blocks to register
     */
    default void add(List<BlightedBlock> blocks) {
        blocks.forEach(BlockRegistry::register);
    }
}
