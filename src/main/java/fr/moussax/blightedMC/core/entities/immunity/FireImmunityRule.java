package fr.moussax.blightedMC.core.entities.immunity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class FireImmunityRule implements EntityImmunityRule{
  @Override
  public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
    return event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
      event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
      event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
      event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE;
  }
}
