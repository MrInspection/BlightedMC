package fr.moussax.blightedMC.smp.core.entities.immunity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class ProjectileImmunity implements EntityImmunity {
    @Override
    public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
        return event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE;
    }
}
