package fr.moussax.blightedMC.content.items.blocks;

import fr.moussax.blightedMC.engine.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.registry.RegistryModule;

import java.util.function.Consumer;

public class BlightedBlocks implements RegistryModule<Consumer<BlightedBlock>> {

    @Override
    public void register(Consumer<BlightedBlock> registry) {
        registry.accept(new BlightedWorkbench());
        registry.accept(new BlightedForge());
    }
}
