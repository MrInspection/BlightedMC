package fr.moussax.blightedMC.game.entities.spawnable.powerful;

import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
import org.bukkit.entity.EntityType;

public class DreamDefender extends SpawnableEntity {
    /**
     * Creates a spawn definition for a blighted entity type.
     *
     * @param entityId    unique identifier for the entity type
     * @param name        display name
     * @param maxHealth   maximum health value
     * @param entityType  underlying Bukkit {@link EntityType}
     * @param probability spawn probability in the range {@code [0.0, 1.0]}
     */
    protected DreamDefender(String entityId, String name, int maxHealth, EntityType entityType, double probability) {
        super(entityId, name, maxHealth, entityType, probability);
    }

    @Override
    protected void defineSpawnConditions() {

    }
}
