package fr.moussax.blightedMC.core.registry.entities.spawnable;

import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class RedstoneEngineer extends SpawnableEntity {
  private static final double LASER_RANGE = 16.0;
  private static final double LASER_DAMAGE = 5.0;
  private static final long TICK_DELAY = 10L;

  public RedstoneEngineer() {
    super(
      "REDSTONE_ENGINEER",
      "Redstone Engineer",
      28,
      EntityType.ZOMBIE,
      0.10
    );

    addAttribute(Attribute.SCALE, 6.5);

    // Lifecycle laser ability; targets nearest valid player periodically
    addRepeatingTask(() -> new BukkitRunnable() {
      @Override
      public void run() {
        LivingEntity self = getEntity();
        if (self == null || !self.isValid()) {
          cancel();
          return;
        }

        List<Player> nearbyPlayers = self.getNearbyEntities(LASER_RANGE, LASER_RANGE, LASER_RANGE)
          .stream().filter(e -> e instanceof Player).map(e -> (Player) e).toList();
        if (nearbyPlayers.isEmpty()) return;

        Player targetPlayer;
        Mob mob = (self instanceof Mob) ? (Mob) self : null;

        if (mob != null) {
          LivingEntity current = mob.getTarget();
          if (current instanceof Player && nearbyPlayers.contains(current)) {
            targetPlayer = (Player) current;
          } else {
            targetPlayer = nearbyPlayers.get(ThreadLocalRandom.current().nextInt(nearbyPlayers.size()));
            mob.setTarget(targetPlayer);
          }
        } else {
          targetPlayer = nearbyPlayers.get(ThreadLocalRandom.current().nextInt(nearbyPlayers.size()));
        }

        laser(self, BlightedPlayer.getBlightedPlayer(targetPlayer));
      }
    }, TICK_DELAY, TICK_DELAY);
  }

  @Override
  public LivingEntity spawn(Location location) {
    return super.spawn(location);
  }

  // no-op: ability logic migrated to lifecycle task registration in constructor

  private void laser(LivingEntity entity, BlightedPlayer target) {
    Player player = target.getPlayer();
    if(player == null || !player.isOnline()) return;

    Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255,0,0),2);
    player.damage(LASER_DAMAGE, entity);

    // Face target
    Vector direction = player.getLocation().toVector().subtract(entity.getEyeLocation().toVector()).normalize();
    Location mobLocation = entity.getLocation();
    mobLocation.setDirection(direction.clone());
    entity.teleport(mobLocation);

    // Draw laser
    Vector step = direction.multiply(0.2);
    Location current = entity.getEyeLocation().clone();
    int safety = 0;
    while (player.getLocation().distance(current) > 0.5) {
      current.add(step);
      Objects.requireNonNull(current.getWorld()).spawnParticle(Particle.DUST, current, 1, dust);
      if(safety++ > 200) break;
    }
  }

  @Override
  protected void setupSpawnConditions() {

  }
}
