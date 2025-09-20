package fr.moussax.blightedMC.registry.abilities;

import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class InstantTransmissionAbility implements AbilityManager<PlayerInteractEvent> {
  private static final int MAX_DISTANCE = 40;

  @Override
  public boolean triggerAbility(PlayerInteractEvent event) {
    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(event.getPlayer());
    Location targetLocation = getTargetedEyeLocation(blightedPlayer);
    if (targetLocation == null) return false;

    Location currentLook = blightedPlayer.getPlayer().getLocation();
    targetLocation.setYaw(currentLook.getYaw());
    targetLocation.setPitch(currentLook.getPitch());

    blightedPlayer.getPlayer().teleport(targetLocation);

    var world = blightedPlayer.getPlayer().getWorld();
    world.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
    world.spawnParticle(Particle.PORTAL, targetLocation, 40, 0.6, 1.0, 0.6, 0.15f);
    world.spawnParticle(Particle.WITCH, targetLocation, 20, 0.5, 0.5, 0.5, 0.05f);

    var isInSurvival = blightedPlayer.getPlayer().getGameMode() == GameMode.SURVIVAL;

    // Consume Item
    ItemStack usedItem = event.getItem();
    if ((usedItem != null && usedItem.getAmount() > 0) && isInSurvival) {
      usedItem.setAmount(usedItem.getAmount() - 1);
    }

    return true;
  }

  @Override public int getCooldownSeconds() { return 0; }
  @Override public int getManaCost() { return 5; }
  @Override public boolean canTrigger(BlightedPlayer player) { return true; }
  @Override public void start(BlightedPlayer player) {}
  @Override public void stop(BlightedPlayer player) {}

  private Location getTargetedEyeLocation(BlightedPlayer blightedPlayer) {
    var player = blightedPlayer.getPlayer();
    var eyeLocation = player.getEyeLocation();
    var direction = eyeLocation.getDirection();

    RayTraceResult traceResult = player.getWorld().rayTraceBlocks(
      eyeLocation, direction, InstantTransmissionAbility.MAX_DISTANCE, FluidCollisionMode.NEVER, true
    );

    if (traceResult != null && traceResult.getHitBlock() != null) {
      Block hitBlock = traceResult.getHitBlock();

      // always land on top of the block
      Location topOfBlock = new Location(
        player.getWorld(),
        hitBlock.getX() + 0.5,
        hitBlock.getY() + 1.0,
        hitBlock.getZ() + 0.5
      );

      if (isSafeTeleportationLocation(topOfBlock)) return topOfBlock;
      Location upOne = topOfBlock.clone().add(0, 1, 0);
      if (isSafeTeleportationLocation(upOne)) return upOne;
      return null; // if blocked, cancel like a failed pearl
    }

    // fallback: max distance point in the air
    Vector maxVec = direction.normalize().multiply(InstantTransmissionAbility.MAX_DISTANCE);
    Location inAir = eyeLocation.add(maxVec);

    // allow teleport if the space is clear
    if (isSafeTeleportationLocation(inAir)) return inAir;
    return null;
  }

  private boolean isSafeTeleportationLocation(Location location) {
    Block feet = location.getBlock();
    Block head = feet.getRelative(BlockFace.UP);
    Block below = feet.getRelative(BlockFace.DOWN);

    if (!feet.getType().isAir()) return false;
    if (!head.getType().isAir()) return false;
    return below.getType().isSolid();
  }
}
