package fr.moussax.blightedMC.engine.entities.rituals.registry;

import fr.moussax.blightedMC.engine.entities.rituals.AncientCreature;
import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;

import java.util.List;

@FunctionalInterface
public interface RitualProvider {

    void register();

    default void add(AncientRitual ritual) {
        RitualRegistry.register(ritual);
    }

    default void add(AncientRitual... rituals) {
        for (AncientRitual ritual : rituals) {
            RitualRegistry.register(ritual);
        }
    }

    default void add(List<AncientRitual> rituals) {
        rituals.forEach(RitualRegistry::register);
    }

    default AncientRitual.Builder ritual(AncientCreature creature) {
        return AncientRitual.Builder.of(creature);
    }
}
