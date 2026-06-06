package fr.moussax.blightedMC.engine.entities.immunity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public final class MeleeImmunity implements EntityImmunity {
    @Override
    public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
        return event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
            event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
    }
}
