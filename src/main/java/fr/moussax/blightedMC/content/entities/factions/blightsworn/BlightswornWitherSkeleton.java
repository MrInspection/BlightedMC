package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightswornWitherSkeleton extends BlightswornEliteArchetype {
    BlightswornWitherSkeleton() {
        super("BLIGHTSWORN_WITHER_SKELETON", "Blightsworn Wither Skeleton", EntityType.WITHER_SKELETON);
    }

    @Override
    protected void onNormalBehavior() {

    }

    @Override
    protected void onEnrageBehavior() {

    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void defineSpawnConditions() {

    }
}
