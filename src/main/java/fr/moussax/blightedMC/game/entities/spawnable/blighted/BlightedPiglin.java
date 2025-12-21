package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedPiglin extends BlightedFaction {
    public BlightedPiglin() {
        super("BLIGHTED_PIGLIN", "Blighted Piglin", 30, EntityType.PIGLIN);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
