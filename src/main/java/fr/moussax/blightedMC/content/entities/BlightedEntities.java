package fr.moussax.blightedMC.content.entities;

import fr.moussax.blightedMC.content.entities.bosses.CorruptedChampion;
import fr.moussax.blightedMC.content.entities.factions.blightsworn.*;
import fr.moussax.blightedMC.content.entities.powerful.Endersent;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.registry.RegistryModule;

import java.util.function.Consumer;

public class BlightedEntities implements RegistryModule<Consumer<BlightedEntity>> {

    @Override
    public void register(Consumer<BlightedEntity> registry) {
        registry.accept(new BlightswornBogged());
        registry.accept(new BlightswornDrowned());
        registry.accept(new BlightswornHusk());
        registry.accept(new BlightswornParched());
        registry.accept(new BlightswornPiglin());
        registry.accept(new BlightswornSkeleton());
        registry.accept(new BlightswornStray());
        registry.accept(new BlightswornWitherSkeleton());
        registry.accept(new BlightswornZombie());
        registry.accept(new BlightswornZombifiedPiglin());

        registry.accept(new CorruptedChampion());
        registry.accept(new Endersent());
        registry.accept(new Watchling());
        registry.accept(new Illusioner());
    }
}
