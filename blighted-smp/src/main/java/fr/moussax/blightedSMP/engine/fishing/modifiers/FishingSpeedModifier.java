package fr.moussax.blightedSMP.engine.fishing.modifiers;

import org.bukkit.entity.Player;

/**
 * Represents a modifier that contributes to a player's Fishing Speed.
 *
 * <p>Modifiers may be provided by armor set bonuses, accessories, or other
 * sources. Fishing Speed bonuses are additive and processed using the
 * diminishing-returns curve defined by {@link FishingSpeedCalculator}.</p>
 */
public interface FishingSpeedModifier {

    /**
     * Returns the Fishing Speed bonus provided by this modifier.
     *
     * @return the Fishing Speed bonus percentage
     */
    double getFishingSpeedBonus();

    /**
     * Called when the player casts a fishing rod while this modifier is active.
     *
     * <p>This method can be overridden to apply additional effects triggered
     * by a fishing cast.</p>
     *
     * @param player the player who cast the fishing rod
     */
    default void onFishingCast(Player player) {
    }
}
