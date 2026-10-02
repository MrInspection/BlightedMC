package fr.moussax.blightedSMP.engine.fishing.hooks;

import fr.moussax.blightedSMP.engine.items.ItemType;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.util.Vector;

/**
 * Common abstraction for custom BlightedMC fishing hooks (such as lava and void fishing).
 */
public interface CustomFishingHook {

    /**
     * Attempts to reel in the custom hook.
     *
     * @return true if a catch was completed and rod durability should be consumed, false otherwise
     */
    boolean reelIn();

    /**
     * Cancels any ongoing tracking tasks and cleans up internal references for this hook.
     */
    void remove();

    /**
     * Gets the custom rod item type required to operate this hook.
     *
     * @return the required {@link ItemType}
     */
    ItemType getRequiredRodType();

    /**
     * Finds any active custom fishing hook associated with the specified Bukkit hook.
     *
     * @param hook the Bukkit fish hook entity
     * @return the associated {@link CustomFishingHook}, or null if none exists
     */
    static CustomFishingHook get(FishHook hook) {
        if (hook == null) return null;
        CustomFishingHook lavaHook = LavaFishingHook.get(hook);
        if (lavaHook != null) return lavaHook;
        return VoidFishingHook.get(hook);
    }

    /**
     * Cleans up all active custom fishing hooks across all custom fishing types.
     */
    static void cleanupAll() {
        LavaFishingHook.cleanupAll();
        VoidFishingHook.cleanupAll();
    }

    /**
     * Calculates the arc launch velocity for caught items or entities traveling from
     * the hook location toward the player.
     *
     * @param origin      the hook origin location
     * @param destination the destination (player) location
     * @return the velocity vector
     */
    static Vector calculateLaunchVelocity(Location origin, Location destination) {
        Vector velocity = destination.toVector().subtract(origin.toVector());
        double distance = velocity.length();
        velocity.multiply(0.08);
        velocity.setY(velocity.getY() + (Math.sqrt(distance) * 0.05) + 0.15);
        return velocity;
    }
}
