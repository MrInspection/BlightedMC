package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightswornPiglin extends BlightswornEliteArchetype {
    BlightswornPiglin() {
        super("BLIGHTSWORN_PIGLIN", "Blightsworn Piglin", EntityType.PIGLIN);
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
