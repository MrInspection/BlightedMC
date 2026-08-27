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

    /** Immunity to fire, lava, fire tick, and campfire damage. */
    FIRE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                    event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                    event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                    event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE;
        }
    },

    /** Immunity to direct melee and sweep attacks. */
    MELEE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                    event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
        }
    },

    /** Immunity to ranged projectile damage. */
    PROJECTILE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE;
        }
    },

    /** Immunity to fall damage. */
    FALL {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            return event.getCause() == EntityDamageEvent.DamageCause.FALL;
        }
    },

    /** Immunity to heavy mace weapon attacks. */
    MACE {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            if (!(event instanceof EntityDamageByEntityEvent damageByEntity)) return false;
            if (!(damageByEntity.getDamager() instanceof Player damager)) return false;
            return damager.getInventory().getItemInMainHand().getType() == Material.MACE;
        }

        @Override
        public String getImmunityMessage() {
            return "§c A mystical force prevented your mace from dealing damage.";
        }
    },

    /** Immunity to magic, poison, wither, dragon breath, and sonic boom damage. */
    MAGIC {
        @Override
        public boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event) {
            EntityDamageEvent.DamageCause cause = event.getCause();
            return cause == EntityDamageEvent.DamageCause.MAGIC ||
                    cause == EntityDamageEvent.DamageCause.POISON ||
                    cause == EntityDamageEvent.DamageCause.WITHER ||
                    cause == EntityDamageEvent.DamageCause.DRAGON_BREATH ||
                    cause == EntityDamageEvent.DamageCause.SONIC_BOOM;
        }
    }
}
