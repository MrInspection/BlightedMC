package fr.moussax.blightedMC.engine.entities.rituals.registry;

import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.registry.EngineRegistry;
import fr.moussax.blightedMC.registry.RegistryModule;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Central registry for all {@link AncientRitual} definitions.
 */
public final class RitualRegistry {

    private static final EngineRegistry<AncientRitual> REGISTRY =
            new EngineRegistry<>("RitualRegistry", ritual -> ritual.getSummonedCreature() != null ? ritual.getSummonedCreature().getEntityId() : String.valueOf(ritual.hashCode()));

    private RitualRegistry() {
    }

    public static void initialize(List<RegistryModule<Consumer<AncientRitual>>> modules) {
        REGISTRY.initialize(modules);
    }

    public static void register(@NonNull AncientRitual ritual) {
        REGISTRY.register(ritual);
    }

    public static Collection<AncientRitual> getAll() {
        return REGISTRY.getAll();
    }

    public static void clear() {
        REGISTRY.clear();
    }
}
