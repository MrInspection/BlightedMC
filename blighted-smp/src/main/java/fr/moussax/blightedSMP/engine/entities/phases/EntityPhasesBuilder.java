package fr.moussax.blightedSMP.engine.entities.phases;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Fluent builder for configuring mob health phase transitions.
 */
public final class EntityPhasesBuilder {

    private final BlightedEntity entity;

    public EntityPhasesBuilder(@NonNull BlightedEntity entity) {
        this.entity = Objects.requireNonNull(entity, "entity cannot be null");
    }

    /**
     * Registers an action executed when the entity's health percentage drops to or below the threshold.
     *
     * @param healthThreshold health percentage in range [0.0, 1.0] (e.g. 0.50 for 50% HP)
     * @param action          action to execute
     * @return this builder
     */
    public EntityPhasesBuilder at(double healthThreshold, @NonNull Runnable action) {
        entity.getPhaseManager().registerPhase(healthThreshold, action);
        return this;
    }
}
