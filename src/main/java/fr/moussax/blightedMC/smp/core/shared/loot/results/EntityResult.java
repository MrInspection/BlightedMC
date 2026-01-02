package fr.moussax.blightedMC.smp.core.shared.loot.results;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link LootResult} that spawns an entity at the loot origin.
 * Supports both vanilla {@link EntityType} and custom {@link AbstractBlightedEntity}.
 */
public final class EntityResult implements LootResult {
    private final EntityType entityType;
    private final AbstractBlightedEntity blightedEntity;
    private final Consumer<LivingEntity> entityModifier;

    private EntityResult(EntityType entityType, AbstractBlightedEntity blightedEntity, Consumer<LivingEntity> entityModifier) {
        this.entityType = entityType;
        this.blightedEntity = blightedEntity;
        this.entityModifier = entityModifier;
    }

    /**
     * Creates an EntityResult for a vanilla Minecraft entity.
     *
     * @param entityType the type of vanilla entity to spawn
     * @return a new EntityResult
     */
    public static EntityResult vanilla(EntityType entityType) {
        return new EntityResult(Objects.requireNonNull(entityType), null, null);
    }

    /**
     * Creates an EntityResult for a vanilla entity with a modifier to adjust the mob.
     *
     * @param entityType     the type of vanilla entity to spawn
     * @param entityModifier a function to modify the spawned entity
     * @return a new EntityResult
     */
    public static EntityResult vanilla(EntityType entityType, Consumer<LivingEntity> entityModifier) {
        return new EntityResult(Objects.requireNonNull(entityType), null, entityModifier);
    }

    /**
     * Creates an EntityResult for a custom Blighted entity.
     *
     * @param blightedEntity the Blighted entity to spawn
     * @return a new EntityResult
     */
    public static EntityResult blighted(AbstractBlightedEntity blightedEntity) {
        return new EntityResult(null, Objects.requireNonNull(blightedEntity), null);
    }

    /**
     * Spawns the entity at the loot origin with the configured velocity.
     *
     * @param context the loot context
     * @param amount  ignored for entity spawns
     */
    @Override
    public void execute(LootContext context, int amount) {
        LivingEntity spawned = null;

        if (entityType != null) {
            var entity = Objects.requireNonNull(context.origin().getWorld())
                .spawnEntity(context.origin(), entityType);

            if (entity instanceof LivingEntity living) {
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
     * Returns a display name for the spawned entity.
     *
     * @param amount ignored for entity spawns
     * @return the entity name
     */
    @Override
    public String displayName(int amount) {
        if (entityType != null) {
            return Formatter.formatEnumName(entityType.name());
        }
        return "Blighted Entity";
    }
}
