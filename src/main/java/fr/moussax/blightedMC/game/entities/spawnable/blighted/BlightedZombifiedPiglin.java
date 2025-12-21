package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedZombifiedPiglin extends BlightedFaction {
    public BlightedZombifiedPiglin() {
        super("BLIGHTED_ZOMBIFIED_PIGLIN", "Blighted Zombified Piglin", 30, EntityType.ZOMBIFIED_PIGLIN);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
