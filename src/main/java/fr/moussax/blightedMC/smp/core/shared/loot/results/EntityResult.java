package fr.moussax.blightedMC.smp.core.shared.loot.results;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * A {@link LootResult} that spawns an entity at the loot origin.
 * Supports both vanilla {@link EntityType} and custom {@link AbstractBlightedEntity}.
 * An optional velocity can be applied to the spawned entity.
 */
public final class EntityResult implements LootResult {
    private final EntityType entityType;
    private final AbstractBlightedEntity blightedEntity;
    private final Vector velocity;

    private EntityResult(EntityType entityType, AbstractBlightedEntity blightedEntity, Vector velocity) {
        this.entityType = entityType;
        this.blightedEntity = blightedEntity;
        this.velocity = velocity;
    }

    /**
     * Creates an EntityResult for a vanilla Minecraft entity.
     *
     * @param entityType the type of vanilla entity to spawn
     * @param velocity the velocity to apply to the spawned entity
     * @return a new EntityResult
     */
    public static EntityResult vanilla(EntityType entityType, Vector velocity) {
        return new EntityResult(Objects.requireNonNull(entityType), null, velocity);
    }

    /**
     * Creates an EntityResult for a custom Blighted entity.
     *
     * @param blightedEntity the Blighted entity to spawn
     * @param velocity the velocity to apply to the spawned entity
     * @return a new EntityResult
     */
    public static EntityResult blighted(AbstractBlightedEntity blightedEntity, Vector velocity) {
        return new EntityResult(null, Objects.requireNonNull(blightedEntity), velocity);
    }

    /**
     * Spawns the entity at the loot origin with the configured velocity.
     *
     * @param context the loot context
     * @param amount ignored for entity spawns
     */
    @Override
    public void execute(LootContext context, int amount) {
        LivingEntity spawned = null;

        if (entityType != null) {
            spawned = (LivingEntity) Objects.requireNonNull(context.origin().getWorld())
                .spawnEntity(context.origin(), entityType);
        } else if (blightedEntity != null) {
            spawned = blightedEntity.clone().spawn(context.origin());
        }

        if (spawned != null && velocity != null) {
            spawned.setVelocity(velocity);
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
            String name = entityType.name().toLowerCase().replace('_', ' ');
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        return "Blighted Entity";
    }
}
