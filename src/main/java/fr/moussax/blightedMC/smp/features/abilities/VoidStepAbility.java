package fr.moussax.blightedMC.smp.features.abilities;

import fr.moussax.blightedMC.smp.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class VoidStepAbility implements AbilityManager<PlayerInteractEvent> {
    private static final int MAX_DISTANCE = 40;

    @Override
    public String[] getDescription() {
        return new String[]{
            "Teleport through the void to the ",
            "block you're looking at, up to §e" + MAX_DISTANCE,
            "blocks away."
        };
    }

    @Override
    public boolean triggerAbility(PlayerInteractEvent event) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(event.getPlayer());
        Location targetLocation = getTargetedEyeLocation(blightedPlayer);

        if (targetLocation == null) {
            // FIX: Add feedback so you know why it didn't work
            blightedPlayer.getPlayer().sendMessage("§cObstructed destination!");
            return false;
        }

        Location currentLook = blightedPlayer.getPlayer().getLocation();
        targetLocation.setYaw(currentLook.getYaw());
        targetLocation.setPitch(currentLook.getPitch());

        blightedPlayer.getPlayer().teleport(targetLocation);

        var world = blightedPlayer.getPlayer().getWorld();
        world.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        world.spawnParticle(Particle.PORTAL, targetLocation, 40, 0.6, 1.0, 0.6, 0.15f);
        world.spawnParticle(Particle.WITCH, targetLocation, 20, 0.5, 0.5, 0.5, 0.05f);

        var isInSurvival = blightedPlayer.getPlayer().getGameMode() == GameMode.SURVIVAL;

        ItemStack usedItem = event.getItem();
        if ((usedItem != null && usedItem.getAmount() > 0) && isInSurvival) {
            usedItem.setAmount(usedItem.getAmount() - 1);
        }

        return true;
    }

    @Override
    public int getCooldownSeconds() {
        return 0;
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public boolean canTrigger(BlightedPlayer player) {
        return true;
    }

    @Override
    public void start(BlightedPlayer player) {}

    @Override
    public void stop(BlightedPlayer player) {}

    private Location getTargetedEyeLocation(BlightedPlayer blightedPlayer) {
        var player = blightedPlayer.getPlayer();
        var eyeLocation = player.getEyeLocation();
        var direction = eyeLocation.getDirection();

        RayTraceResult traceResult = player.getWorld().rayTraceBlocks(
            eyeLocation, direction, MAX_DISTANCE, FluidCollisionMode.NEVER, true
        );

        // Case 1: We hit a block
        if (traceResult != null && traceResult.getHitBlock() != null) {
            Block hitBlock = traceResult.getHitBlock();

            // Try landing on top
            Location topOfBlock = hitBlock.getLocation().add(0.5, 1.0, 0.5);

            // STRICT check for landing: We need solid ground below
            if (isSafe(topOfBlock, true)) return topOfBlock;

            return null;
        }

        // Case 2: We hit nothing (Air Dash)
        Vector maxVec = direction.normalize().multiply(MAX_DISTANCE);
        Location inAir = eyeLocation.add(maxVec);

        // LENIENT check for air: We DON'T need solid ground below
        if (isSafe(inAir, false)) return inAir;

        return null;
    }

    /**
     * Checks if a location is safe to teleport to.
     * @param location The feet location
     * @param requireGround Whether we strictly require a solid block below (true for landing, false for air dash)
     */
    private boolean isSafe(Location location, boolean requireGround) {
        Block feet = location.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block below = feet.getRelative(BlockFace.DOWN);

        // 1. Must not suffocate
        if (feet.getType().isSolid()) return false;
        if (head.getType().isSolid()) return false;

        // 2. Ground check (Only if required)
        if (requireGround && !below.getType().isSolid()) return false;

        return true;
    }
}
