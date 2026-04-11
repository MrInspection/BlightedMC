package fr.moussax.blightedMC.content.entities.frenzied;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public sealed abstract class FrenziedSkirmisher extends FrenziedCreature
    permits FrenziedSkeleton, FrenziedStray, FrenziedBogged, FrenziedParched {

    private static final double STRAFE_RADIUS = 10.0;
    private static final double STRAFE_SPEED = 0.45;
    private static final long STRAFE_PERIOD_TICKS = 15L;
    private static final long VOLLEY_PERIOD_TICKS = 40L;
    private static final long VOLLEY_ENRAGED_PERIOD_TICKS = 20L;

    protected FrenziedSkirmisher(String entityId, String name, EntityType entityType) {
        super(entityId, name, entityType);
    }

    @Override
    protected final void onDefineCombatBehavior() {
        addAbility(5L, STRAFE_PERIOD_TICKS, this::performStrafe);
    }

    @Override
    protected final void onEnrageBehavior() {
        addAbility(0L, VOLLEY_ENRAGED_PERIOD_TICKS, this::performDoubleVolley);
    }

    private void performStrafe() {
        Player target = getNearestPlayer(STRAFE_RADIUS);
        if (target == null) return;

        // Perpendicular vector to the direction toward the target.
        Vector toTarget = target.getLocation().toVector()
            .subtract(entity.getLocation().toVector())
            .normalize();

        Vector strafe = new Vector(-toTarget.getZ(), 0, toTarget.getX())
            .multiply(STRAFE_SPEED);

        // Alternate strafe direction each call using entity tick count parity.
        if (entity.getTicksLived() % 30 < 15) strafe.multiply(-1);

        entity.setVelocity(entity.getVelocity().add(strafe));
    }

    private void performDoubleVolley() {
        Player target = getNearestPlayer(STRAFE_RADIUS);
        if (target == null) return;

        fireArrowAt(target, 0L);
        fireArrowAt(target, 5L);
    }

    private void fireArrowAt(Player target, long delayTicks) {
        Runnable fire = () -> {
            if (isNotAlive()) return;

            Location eyeLoc = entity.getEyeLocation();
            Vector direction = target.getEyeLocation().toVector()
                .subtract(eyeLoc.toVector())
                .normalize();

            Arrow arrow = entity.getWorld().spawnArrow(
                eyeLoc, direction, 1.6f, 4.0f
            );
            arrow.setShooter(entity);
            arrow.setDamage(getDamage() * 0.75);

            entity.getWorld().playSound(
                entity.getLocation(),
                org.bukkit.Sound.ENTITY_ARROW_SHOOT,
                0.8f, 1.1f
            );
            entity.getWorld().spawnParticle(
                Particle.CRIT,
                eyeLoc, 3, 0.1, 0.1, 0.1, 0.02
            );
        };

        if (delayTicks == 0) {
            fire.run();
        } else {
            fr.moussax.blightedMC.utils.Utilities.delay(fire, delayTicks);
        }
    }
}
