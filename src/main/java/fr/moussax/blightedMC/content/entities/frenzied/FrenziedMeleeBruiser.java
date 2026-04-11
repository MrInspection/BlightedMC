package fr.moussax.blightedMC.content.entities.frenzied;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public sealed abstract class FrenziedMeleeBruiser extends FrenziedCreature
    permits FrenziedZombie, FrenziedHusk, FrenziedZombifiedPiglin {

    private static final double SLAM_RADIUS = 3.5;
    private static final double SLAM_DAMAGE = 4.0;
    private static final double SLAM_KNOCKBACK_STRENGTH = 0.6;
    private static final long SLAM_DELAY_TICKS = 60L;
    private static final long SLAM_PERIOD_TICKS = 60L;
    private static final long SLAM_ENRAGED_PERIOD_TICKS = 30L;

    protected FrenziedMeleeBruiser(String entityId, String name, EntityType entityType) {
        super(entityId, name, entityType);
    }

    @Override
    protected final void onDefineCombatBehavior() {
        addAbility(SLAM_DELAY_TICKS, SLAM_PERIOD_TICKS, this::performGroundSlam);
    }

    @Override
    protected final void onEnrageBehavior() {
        addAbility(0L, SLAM_ENRAGED_PERIOD_TICKS, this::performGroundSlam);
    }

    private void performGroundSlam() {
        Player target = getNearestPlayer(SLAM_RADIUS);
        if (target == null) return;

        Location slamLocation = entity.getLocation();

        entity.getWorld().spawnParticle(
            Particle.EXPLOSION,
            slamLocation,
            6, 0.4, 0.1, 0.4, 0.05
        );
        entity.getWorld().playSound(
            slamLocation,
            Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
            1.0f, 0.6f
        );

        for (Entity nearby : entity.getNearbyEntities(SLAM_RADIUS, SLAM_RADIUS, SLAM_RADIUS)) {
            if (!(nearby instanceof Player player)) continue;
            player.damage(SLAM_DAMAGE, entity);
            applyKnockbackAwayFrom(player, slamLocation);
        }
    }

    private void applyKnockbackAwayFrom(Player player, Location origin) {
        Vector knockback = player.getLocation().toVector()
            .subtract(origin.toVector())
            .normalize()
            .multiply(SLAM_KNOCKBACK_STRENGTH)
            .setY(0.3);
        player.setVelocity(player.getVelocity().add(knockback));
    }
}