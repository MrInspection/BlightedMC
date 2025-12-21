package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedWitherSkeleton extends BlightedFaction {
    public BlightedWitherSkeleton() {
        super("BLIGHTED_WITHER_SKELETON",  "Blighted Wither Skeleton", 40, EntityType.WITHER_SKELETON);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
