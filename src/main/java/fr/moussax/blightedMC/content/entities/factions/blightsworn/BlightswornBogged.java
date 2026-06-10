package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class BlightswornBogged extends BlightswornArcherArchetype {
    BlightswornBogged() {
        super("BLIGHTSWORN_BOGGED", "Blighsworn Bogged", EntityType.BOGGED);
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
