package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedBogged extends BlightedFaction {
    public BlightedBogged() {
        super("BLIGHTED_BOGGED", "Blighted Bogged", 30, EntityType.BOGGED);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
