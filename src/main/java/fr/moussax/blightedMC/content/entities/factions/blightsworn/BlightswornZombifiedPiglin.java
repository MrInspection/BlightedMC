package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightswornZombifiedPiglin extends BlightswornEliteArchetype {

    BlightswornZombifiedPiglin() {
        super("BLIGHTSWORN_ZOMBIFIED_PIGLIN", "Blightsworn Zombified Piglin", EntityType.ZOMBIFIED_PIGLIN);
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
