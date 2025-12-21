package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedStray extends BlightedFaction {
    public BlightedStray() {
        super("BLIGHTED_STRAY", "Blighted Stray", 30, EntityType.STRAY);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
