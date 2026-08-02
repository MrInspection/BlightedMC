package fr.moussax.blightedMC.engine.entities.immunity;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Standard implementation of entity immunities mapping to specific damage types.
 */
public enum DamageType implements EntityImmunity {
    FIRE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                   event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                   event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                   event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE;
        }
    },
    MELEE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                   event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
        }
    },
    PROJECTILE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE;
        }
    },
    MACE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            if (!(event instanceof EntityDamageByEntityEvent damageByEntity)) return false;
            if (!(damageByEntity.getDamager() instanceof Player damager)) return false;
            return damager.getInventory().getItemInMainHand().getType() == Material.MACE;
        }

        @Override
        public String getImmunityMessage() {
            return "§4 ■ §cThe Blight prevents your mace from dealing damage.";
        }
    }
}
