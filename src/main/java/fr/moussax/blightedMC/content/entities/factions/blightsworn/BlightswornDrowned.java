package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public final class BlightswornDrowned extends BlightswornCreature {
    private static final int MAX_HEALTH = 28;

    public BlightswornDrowned() {
        super("BLIGHTSWORN_DROWNED", "Blightsworn Drowned", EntityType.DROWNED, MAX_HEALTH);
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
    protected void equipEnragedArmor(EntityEquipment equipment) {

    }

    @Override
    protected void defineSpawnConditions() {

    }
}
