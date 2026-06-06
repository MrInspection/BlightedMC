package fr.moussax.blightedMC.engine.entities.rituals.registry;

import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.content.rituals.AncientRituals;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RitualRegistry {

    public static final Set<AncientRitual> REGISTRY = new HashSet<>();

    private static final List<RitualProvider> PROVIDERS = List.of(
        new AncientRituals()
    );

    private RitualRegistry() {
    }

    public static void initialize() {
        clear();
        PROVIDERS.forEach(RitualProvider::register);
        Log.success("RitualRegistry", "Registered " + REGISTRY.size() + " ancient rituals.");
    }

    static void register(@NonNull AncientRitual ritual) {
        REGISTRY.add(ritual);
    }

    public static Set<AncientRitual> getAll() {
        return Collections.unmodifiableSet(REGISTRY);
    }

    public static void clear() {
        REGISTRY.clear();
    }
}
