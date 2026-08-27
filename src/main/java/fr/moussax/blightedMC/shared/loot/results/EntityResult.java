package fr.moussax.blightedMC.shared.loot.results;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.shared.loot.LootContext;
import fr.moussax.blightedMC.shared.loot.LootResult;
import fr.moussax.blightedMC.shared.text.Formatter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link LootResult} that spawns a vanilla or custom entity at the loot origin.
 */
public final class EntityResult implements LootResult {
    private final EntityType entityType;
    private final BlightedEntity blightedEntity;
    private final Consumer<LivingEntity> entityModifier;

    private EntityResult(EntityType entityType, BlightedEntity blightedEntity, Consumer<LivingEntity> entityModifier) {
        this.entityType = entityType;
        this.blightedEntity = blightedEntity;
        this.entityModifier = entityModifier;
    }

    /**
     * Creates an entity result for a vanilla entity type.
     *
     * @param entityType entity type to spawn
     * @return new entity result
     */
    public static EntityResult vanilla(EntityType entityType) {
        return new EntityResult(Objects.requireNonNull(entityType), null, null);
    }

    /**
     * Creates an entity result for a vanilla entity type with a modifier function.
     *
     * @param entityType     entity type to spawn
     * @param entityModifier modifier applied to the spawned entity
     * @return new entity result
     */
    public static EntityResult vanilla(EntityType entityType, Consumer<LivingEntity> entityModifier) {
        return new EntityResult(Objects.requireNonNull(entityType), null, entityModifier);
    }

    /**
     * Creates an entity result for a custom Blighted entity.
     *
     * @param blightedEntity Blighted entity to spawn
     * @return new entity result
     */
    public static EntityResult blighted(BlightedEntity blightedEntity) {
        return new EntityResult(null, Objects.requireNonNull(blightedEntity), null);
    }

    /**
     * Spawns the entity at the loot origin and applies initial velocity and modifiers.
     *
     * @param context loot context
     * @param amount  quantity parameter (unused for single entity spawn)
     */
    @Override
    public void execute(LootContext context, int amount) {
        LivingEntity spawned = null;

        if (entityType != null) {
            Object rawEntity = Objects.requireNonNull(context.origin().getWorld())
                    .spawnEntity(context.origin(), entityType);

            if (rawEntity instanceof LivingEntity living) {
                spawned = living;
            } else {
                return;
            }
        } else if (blightedEntity != null) {
            spawned = blightedEntity.clone().spawn(context.origin());
        }

        if (spawned != null) {
            if (entityModifier != null) {
                entityModifier.accept(spawned);
            }
            if (context.velocity() != null) {
                spawned.setVelocity(context.velocity());
            }
        }
    }

    /**
     * Returns the formatted display name of the spawned entity type or generic description.
     *
     * @param amount drop quantity
     * @return formatted display name
     */
    @Override
    public String displayName(int amount) {
        if (entityType != null) {
            return Formatter.formatEnumName(entityType.name());
        }
        return "Blighted Entity";
    }
}
