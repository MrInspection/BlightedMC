package fr.moussax.blightedMC.smp.core.entities.rituals.registry;

import fr.moussax.blightedMC.smp.core.entities.rituals.AncientCreature;
import fr.moussax.blightedMC.smp.core.entities.rituals.AncientRitual;

import java.util.List;

/**
 * Defines a provider for custom ancient rituals in the game.
 * <p>
 * Implementations should register rituals by overriding {@link #register()}
 * and using the provided {@code add} methods to add individual rituals,
 * multiple rituals, or a list of rituals.
 * <p>
 * The {@link #ritual(AncientCreature)} helper can be used to start building
 * new {@link AncientRitual} instances.
 */
@FunctionalInterface
public interface RitualProvider {

    /**
     * Registers all rituals defined by this provider.
     * Implementations should call one of the {@code add} methods here.
     */
    void register();

    /**
     * Registers a single ritual.
     *
     * @param ritual the ritual to register
     */
    default void add(AncientRitual ritual) {
        RitualRegistry.register(ritual);
    }

    /**
     * Registers multiple rituals.
     *
     * @param rituals the rituals to register
     */
    default void add(AncientRitual... rituals) {
        for (AncientRitual ritual : rituals) {
            RitualRegistry.register(ritual);
        }
    }

    /**
     * Registers a list of rituals.
     *
     * @param rituals the list of rituals to register
     */
    default void add(List<AncientRitual> rituals) {
        rituals.forEach(RitualRegistry::register);
    }

    /**
     * Starts building a new ritual for the specified creature.
     *
     * @param creature the creature to be summoned
     * @return a builder for creating a new {@link AncientRitual}
     */
    default AncientRitual.Builder ritual(AncientCreature creature) {
        return AncientRitual.Builder.of(creature);
    }
}
