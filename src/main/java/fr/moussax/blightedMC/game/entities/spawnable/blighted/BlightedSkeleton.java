package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedSkeleton extends BlightedFaction {
    public BlightedSkeleton() {
        super("BLIGHTED_SKELETON", "Blighted Skeleton", 30, EntityType.SKELETON);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
