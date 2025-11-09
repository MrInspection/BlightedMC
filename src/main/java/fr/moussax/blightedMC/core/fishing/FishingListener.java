package fr.moussax.blightedMC.core.fishing;

import fr.moussax.blightedMC.core.fishing.loot.*;
import fr.moussax.blightedMC.core.fishing.loot.pools.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishingListener implements Listener {

  @EventHandler
  public void onPlayerFishing(PlayerFishEvent event) {
    if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
      return;
    }

    Player player = event.getPlayer();
    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
    FishHook hook = event.getHook();
    Location hookLocation = hook.getLocation();
    Material hookBlock = hookLocation.getBlock().getType();

    if (hookBlock != Material.WATER) {
      return;
    }

    // Cancel vanilla loot
    if (event.getCaught() != null) {
      event.getCaught().remove();
    }

    World.Environment environment = player.getWorld().getEnvironment();
    FishingLootTable lootTable = getLootTableForEnvironment(environment);

    // Calculate pull direction (towards player)
    Vector pullDirection = player.getLocation().toVector()
      .subtract(hookLocation.toVector())
      .normalize()
      .multiply(0.4)
      .setY(0.3);

    // Roll for sea creature first
    LivingEntity seaCreature = lootTable.summonSeaCreature(blightedPlayer, hookLocation, pullDirection);

    if (seaCreature != null) {
      spawnFishingParticles(hookLocation);
      player.playSound(hookLocation, Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1.0f, 0.8f);
      return;
    }

    // If no sea creature, try item drop
    ItemStack itemDrop = lootTable.getItemDrop(blightedPlayer);

    if (itemDrop != null) {
      spawnItemAtHook(hookLocation, itemDrop, pullDirection);
      spawnFishingParticles(hookLocation);
      player.playSound(hookLocation, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.2f);
    }
  }

  private FishingLootTable getLootTableForEnvironment(World.Environment environment) {
    return switch (environment) {
      case THE_END -> new EndFishingPool();
      default -> new OverworldFishingPool();
    };
  }

  private void spawnItemAtHook(Location location, ItemStack item, Vector velocity) {
    Item droppedItem = location.getWorld().dropItem(location, item);
    droppedItem.setVelocity(velocity);
    droppedItem.setPickupDelay(10);
  }

  private void spawnFishingParticles(Location location) {
    World world = location.getWorld();
    world.spawnParticle(Particle.CRIT, location, 20, 0.3, 0.3, 0.3, 0.1);
    world.spawnParticle(Particle.BUBBLE, location, 15, 0.2, 0.2, 0.2, 0.05);
  }
}
