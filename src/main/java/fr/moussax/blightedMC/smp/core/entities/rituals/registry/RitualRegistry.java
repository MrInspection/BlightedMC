package fr.moussax.blightedMC.smp.core.entities.rituals.registry;

import fr.moussax.blightedMC.smp.core.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.smp.features.rituals.AncientRituals;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Central registry for managing all {@link AncientRitual} instances.
 * <p>
 * Rituals are registered through {@link RitualProvider} implementations,
 * and can be accessed or cleared via the static API provided by this class.
 * <p>
 * Use {@link #initialize()} to load all rituals from registered providers.
 */
public class RitualRegistry {

    /** Set of all registered ancient rituals */
    public static final Set<AncientRitual> REGISTRY = new HashSet<>();

    /** Registered providers that supply rituals */
    private static final List<RitualProvider> PROVIDERS = List.of(
        new AncientRituals()
    );

    private RitualRegistry() {
    }

    /**
     * Initializes the registry by clearing existing rituals and loading all
     * rituals from providers. Logs the total count of registered rituals.
     */
    public static void initialize() {
        clear();
        PROVIDERS.forEach(RitualProvider::register);
        Log.success("RitualRegistry", "Registered " + REGISTRY.size() + " ancient rituals.");
    }

    /**
     * Registers a single ritual in the registry.
     *
     * @param ritual the ritual to register
     */
    static void register(@NonNull AncientRitual ritual) {
        REGISTRY.add(ritual);
    }

    /**
     * Returns an unmodifiable view of all registered rituals.
     *
     * @return set of all registered rituals
     */
    public static Set<AncientRitual> getAll() {
        return Collections.unmodifiableSet(REGISTRY);
    }

    /**
     * Clears all registered rituals from the registry.
     * <p>
     * Typically used during plugin reloads or shutdown.
     */
    public static void clear() {
        REGISTRY.clear();
    }
}
