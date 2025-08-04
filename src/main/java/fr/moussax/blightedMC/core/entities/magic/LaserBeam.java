package fr.moussax.blightedMC.core.entities.magic;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;

public class LaserBeam {
  private final LaserOrigin origin;
  private final LaserDestination destination;
  private static final Map<LivingEntity, LivingEntity> lasers = new HashMap<>();

  public LaserBeam(Location guardianLocation, Location receiverLocation) {
    guardianLocation.setPitch(0);
    receiverLocation.setPitch(0);
    if (!Objects.equals(guardianLocation.getWorld(), receiverLocation.getWorld())) {
      throw new IllegalArgumentException("The worlds must be identical!");
    }

    origin = new LaserOrigin();
    origin.spawn(guardianLocation);

    destination = new LaserDestination();
    destination.spawn(receiverLocation);

    // Set the laser target
    ((Guardian) origin.getEntity()).setTarget(destination.getEntity());
    lasers.put(origin.getEntity(), destination.getEntity());
  }

  public void stop() {
    lasers.remove(origin.getEntity());
    origin.getEntity().remove();
    destination.getEntity().remove();
  }

  public void rotateAroundStartY(double degree) {
    Vector direction = destination.getEntity().getLocation().toVector().subtract(origin.getEntity().getLocation().toVector());
    direction = direction.rotateAroundY(Math.toRadians(degree));
    Location newEndLoc = origin.getEntity().getLocation().add(direction);
    destination.getEntity().teleport(newEndLoc);
  }

  public List<Entity> getHitEntities() {
    return getHitEntities(entity -> true);
  }

  public List<Entity> getHitEntities(Predicate<Entity> filter) {
    List<Entity> hitEntities = new ArrayList<>();

    double length = origin.getEntity().getLocation().toVector().subtract(destination.getEntity().getLocation().toVector()).length();

    for (Entity entity : origin.getEntity().getNearbyEntities(length, length, length)) {
      if (!filter.test(entity)) continue;
      RayTraceResult result = entity.getBoundingBox().rayTrace(
          origin.getEntity().getLocation().toVector(),
          origin.getEntity().getLocation().getDirection(),
          length
      );

      if (result == null) continue;
      if (entity.equals(destination.getEntity())) continue;
      hitEntities.add(entity);
    }
    return hitEntities;
  }

  public static class LaserBeamListener implements Listener {
    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
      if (!(event.getEntity() instanceof LivingEntity entity)) return;
      if (lasers.containsKey(entity) && lasers.get(entity) != event.getTarget()) {
        event.setCancelled(true);
      }
    }
  }

  public static class LaserOrigin extends BlightedEntity {
    public LaserOrigin() {
      super("LazerOrigin", 100, EntityType.GUARDIAN);
      addAttribute(Attribute.MOVEMENT_SPEED, 0.0);
    }

    @Override
    public LivingEntity spawn(Location location) {
      LivingEntity spawned = super.spawn(location);
      if (spawned instanceof Guardian guardian) {
        guardian.setInvisible(true);
        guardian.setInvulnerable(true);
        guardian.setLaser(true);
        guardian.setSilent(true);
        guardian.setGravity(false);
        guardian.setCollidable(false);
        guardian.getScoreboardTags().clear();
        guardian.addScoreboardTag("invincible");
      }
      return spawned;
    }
  }

  public static class LaserDestination extends BlightedEntity {
    public LaserDestination() {
      super("LazerDestination", 100, EntityType.ARMOR_STAND);
    }

    @Override
    public LivingEntity spawn(Location location) {
      LivingEntity spawned = super.spawn(location);
      if (spawned instanceof ArmorStand armorStand) {
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setAI(false);
        armorStand.setSilent(true);
        armorStand.setGravity(false);
        armorStand.getScoreboardTags().clear();
        armorStand.addScoreboardTag("invincible");
        armorStand.addScoreboardTag("remove");
        armorStand.setBasePlate(false);
        armorStand.setCollidable(false);
      }
      return spawned;
    }
  }
}
