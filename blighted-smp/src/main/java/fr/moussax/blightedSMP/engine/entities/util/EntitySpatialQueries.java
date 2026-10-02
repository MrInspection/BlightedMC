package fr.moussax.blightedSMP.engine.entities.util;

import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Utility methods for spatial searches, targeting queries, and area-of-effect combat mechanics.
 */
public final class EntitySpatialQueries {

    private EntitySpatialQueries() {
    }

    /**
     * Finds survival-mode players within a spherical radius around a center location.
     *
     * @param center search center
     * @param radius search radius
     * @return matching survival players, or empty list if center is invalid
     */
    public static List<Player> getNearbyPlayers(@Nullable Location center, double radius) {
        if (center == null || center.getWorld() == null || radius <= 0) {
            return Collections.emptyList();
        }
        return center.getWorld()
                .getNearbyEntities(
                        center,
                        radius,
                        radius,
                        radius,
                        entity -> entity instanceof Player player && player.getGameMode() == GameMode.SURVIVAL
                )
                .stream()
                .map(entity -> (Player) entity)
                .toList();
    }

    /**
     * Finds the nearest survival-mode player to a center location within the given radius.
     *
     * @param center search center
     * @param radius search radius
     * @return nearest player, or {@code null} if none found
     */
    @Nullable
    public static Player getNearestPlayer(@Nullable Location center, double radius) {
        if (center == null) {
            return null;
        }
        return getNearbyPlayers(center, radius).stream()
                .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(center)))
                .orElse(null);
    }

    /**
     * Finds nearby {@link BlightedPlayer} instances around a center location.
     *
     * @param center search center
     * @param radius search radius
     * @return list of nearby BlightedPlayer instances
     */
    public static List<BlightedPlayer> getNearbyBlightedPlayers(@Nullable Location center, double radius) {
        return getNearbyPlayers(center, radius).stream()
                .map(BlightedPlayer::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Finds the nearest {@link BlightedPlayer} to a center location.
     *
     * @param center search center
     * @param radius search radius
     * @return nearest BlightedPlayer, or {@code null}
     */
    @Nullable
    public static BlightedPlayer getNearestBlightedPlayer(@Nullable Location center, double radius) {
        Player target = getNearestPlayer(center, radius);
        return target != null ? BlightedPlayer.get(target) : null;
    }

    /**
     * Damages all nearby survival-mode players around a center location.
     *
     * @param center       damage center
     * @param radius       damage radius
     * @param damageAmount damage dealt
     * @param source       attacking entity responsible for the damage
     */
    public static void damageNearbyPlayers(
            @Nullable Location center,
            double radius,
            double damageAmount,
            @Nullable Entity source
    ) {
        if (center == null) return;
        List<Player> hitPlayers = getNearbyPlayers(center, radius);
        for (Player player : hitPlayers) {
            player.damage(damageAmount, source);
        }
    }

    /**
     * Damages and knocks back all nearby survival-mode players away from the center.
     *
     * @param center            effect center
     * @param radius            effect radius
     * @param damageAmount      damage dealt
     * @param knockbackStrength horizontal knockback strength
     * @param verticalKnockback vertical velocity
     * @param source            attacking entity responsible for the effect
     */
    public static void damageAndKnockbackNearbyPlayers(
            @Nullable Location center,
            double radius,
            double damageAmount,
            double knockbackStrength,
            double verticalKnockback,
            @Nullable Entity source
    ) {
        if (center == null) return;
        List<Player> hitPlayers = getNearbyPlayers(center, radius);
        for (Player player : hitPlayers) {
            player.damage(damageAmount, source);
            Vector knockbackVector = player.getLocation().toVector().subtract(center.toVector()).setY(0);
            if (knockbackVector.lengthSquared() > 0.001) {
                knockbackVector.normalize().multiply(knockbackStrength).setY(verticalKnockback);
            } else {
                knockbackVector = new Vector(0, verticalKnockback, 0);
            }
            player.setVelocity(knockbackVector);
        }
    }

    /**
     * Disables a target player's shield if they are currently blocking.
     *
     * @param target        target player
     * @param cooldownTicks duration in ticks to put shield on cooldown
     */
    public static void disableShieldIfBlocking(@Nullable Player target, int cooldownTicks) {
        if (target != null && target.isBlocking()) {
            target.setCooldown(Material.SHIELD, cooldownTicks);
            target.getWorld().playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.8f);
        }
    }

    /**
     * Rotates an entity on the horizontal plane to face a target location.
     *
     * @param entity entity to rotate
     * @param target target location
     */
    public static void faceLocation(@Nullable LivingEntity entity, @Nullable Location target) {
        if (entity == null || !entity.isValid() || entity.isDead() || target == null) {
            return;
        }
        Vector direction = target.toVector().subtract(entity.getLocation().toVector()).setY(0);
        if (direction.lengthSquared() > 0.001) {
            Location location = entity.getLocation();
            location.setYaw((float) Math.toDegrees(-Math.atan2(direction.getX(), direction.getZ())));
            entity.teleport(location);
        }
    }
}
