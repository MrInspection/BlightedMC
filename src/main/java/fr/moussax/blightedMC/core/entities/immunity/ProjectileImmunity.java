package fr.moussax.blightedMC.core.entities.immunity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class ProjectileImmunity implements EntityImmunityRule {
  @Override
  public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
    return event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE;
  }
}
