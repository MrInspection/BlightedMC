package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedDrowned extends BlightedFaction {
    public BlightedDrowned() {
        super("BLIGHTED_DROWNED", "Blighted Drowned", 30, EntityType.DROWNED);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
