package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightedHusk extends BlightedFaction {
    public BlightedHusk() {
        super("BLIGHTED_HUSK", "Blighted Husk", 30, EntityType.HUSK);
    }

    @Override
    protected void onEnrage(LivingEntity entity) {

    }

    @Override
    protected void setupSpawnConditions() {

    }
}
