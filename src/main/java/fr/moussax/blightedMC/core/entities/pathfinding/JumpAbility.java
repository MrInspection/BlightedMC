package fr.moussax.blightedMC.core.entities.pathfinding;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.TreeMap;

public class JumpAbility extends BukkitRunnable {
  private int cooldown = 0;
  private final LivingEntity entity;

  public JumpAbility(LivingEntity livingEntity) {
    this.entity = livingEntity;
    this.runTaskTimer(BlightedMC.getInstance(), 0, 1);
  }

  @Override
  public void run() {
    if(entity.isOnGround()) {
      cooldown--;
      boolean isPlayerNearby = false;
      TreeMap<Double, Player> playerLocations = new TreeMap<>();

      for(Player p : Bukkit.getOnlinePlayers()) {
        if(entity.getLocation().distance(p.getLocation()) < 8) {
          isPlayerNearby = true;
        } else {
          playerLocations.put(entity.getLocation().distance(p.getLocation()), p);
        }
      }

      if(cooldown <= 0) {
        if(!isPlayerNearby) {
          Location l = playerLocations.firstEntry().getValue().getLocation();
          Vector dir = l.toVector().subtract(entity.getLocation().toVector()).normalize();
          entity.setVelocity(entity.getVelocity().add(new Vector(0, 0.42, 0).add(dir.multiply(0.6))));
          cooldown = 5;
        }
      }
    }
  }
}
