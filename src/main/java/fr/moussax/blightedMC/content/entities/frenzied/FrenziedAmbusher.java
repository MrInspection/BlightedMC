package fr.moussax.blightedMC.content.entities.frenzied;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public sealed abstract class FrenziedAmbusher extends FrenziedCreature
    permits FrenziedWitherSkeleton, FrenziedPiglin {

    private static final double CHARGE_TRIGGER_RADIUS = 8.0;
    private static final double CHARGE_SPEED = 1.4;
    private static final double CHARGE_DAMAGE_BONUS = 4.0;
    private static final long CHARGE_PERIOD_TICKS = 80L;
    private static final long CHARGE_ENRAGED_PERIOD_TICKS = 40L;

    private boolean isCharging = false;

    protected FrenziedAmbusher(String entityId, String name, EntityType entityType) {
        super(entityId, name, entityType);
    }

    @Override
    protected final void onDefineCombatBehavior() {
        addAbility(20L, CHARGE_PERIOD_TICKS, this::attemptCharge);
    }

    @Override
    protected final void onEnrageBehavior() {
        addAbility(0L, CHARGE_ENRAGED_PERIOD_TICKS, this::attemptCharge);
    }

    private void attemptCharge() {
        if (isCharging) return;

        Player target = getNearestPlayer(CHARGE_TRIGGER_RADIUS);
        if (target == null) return;

        isCharging = true;

        Location origin = entity.getLocation();
        Vector chargeDirection = target.getLocation().toVector()
            .subtract(origin.toVector())
            .normalize()
            .multiply(CHARGE_SPEED)
            .setY(0.15);

        entity.setVelocity(chargeDirection);
        entity.getWorld().playSound(origin, Sound.ENTITY_RAVAGER_ROAR, 0.8f, 1.4f);
        entity.getWorld().spawnParticle(
            Particle.SWEEP_ATTACK, origin, 5, 0.3, 0.1, 0.3, 0.05
        );

        // Damage window — check for nearby players one tick after the lunge lands.
        fr.moussax.blightedMC.utils.Utilities.delay(() -> {
            if (isNotAlive()) {
                isCharging = false;
                return;
            }
            Player hit = getNearestPlayer(2.0);
            if (hit != null) {
                hit.damage(getDamage() + CHARGE_DAMAGE_BONUS, entity);
                entity.getWorld().playSound(
                    entity.getLocation(),
                    Sound.ENTITY_PLAYER_ATTACK_STRONG,
                    1.0f, 0.8f
                );
            }
            isCharging = false;
        }, 8L);
    }

    @Override
    public FrenziedAmbusher clone() {
        FrenziedAmbusher clone = (FrenziedAmbusher) super.clone();
        clone.isCharging = false;
        return clone;
    }
}
