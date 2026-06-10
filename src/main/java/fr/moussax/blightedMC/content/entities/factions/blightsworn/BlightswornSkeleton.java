package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightswornSkeleton extends BlightswornArcherArchetype {
    BlightswornSkeleton() {
        super("BLIGHTSWORN_SKELETON", "Blightsworn Skeleton", EntityType.SKELETON);
    }

    @Override
    protected void applyArrowEffects(Arrow arrow, boolean isPhaseTwo) {

    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void defineSpawnConditions() {

    }
}
