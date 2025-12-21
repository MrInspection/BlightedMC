package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedParched extends BlightedFaction {
    public BlightedParched() {
        super("BLIGHTED_PARCHED", "Blighted Parched", 30, EntityType.PARCHED);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
