package fr.moussax.blightedMC.engine.entities.rituals.registry;

import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import java.util.function.Consumer;

@FunctionalInterface
public interface RitualRegistryHandler extends Consumer<AncientRitual> {
    default void register(AncientRitual ritual) {
        accept(ritual);
    }
}
