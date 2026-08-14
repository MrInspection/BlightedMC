package fr.moussax.blightedMC.engine.fishing.modifiers;

import fr.moussax.blightedMC.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Calculates Fishing Speed bonuses and applies them to fishing wait times.
 *
 * <p>Fishing Speed is derived from the rod's Lure enchantment and any active
 * {@link FishingSpeedModifier} set bonuses. The resulting stat is converted
 * into a reduced wait time using a diminishing-returns curve.</p>
 */
public final class FishingSpeedCalculator {

    private static final double LURE_SPEED_PER_LEVEL = 10.0;

    private FishingSpeedCalculator() {
    }

    /**
     * Calculates the player's total Fishing Speed.
     *
     * <p>Each Lure level contributes {@value #LURE_SPEED_PER_LEVEL}% Fishing Speed.
     * Active full-set bonuses implementing {@link FishingSpeedModifier} contribute
     * their respective bonuses.</p>
     *
     * @param player the player whose active set bonuses are evaluated
     * @param rod    the fishing rod whose Lure enchantment is evaluated
     * @return the total Fishing Speed percentage
     */
    public static double calculate(BlightedPlayer player, ItemStack rod) {
        double speed = 0.0;

        if (rod != null) {
            speed += rod.getEnchantmentLevel(Enchantment.LURE) * LURE_SPEED_PER_LEVEL;
        }

        if (player != null) {
            for (FullSetBonus bonus : player.getActiveFullSetBonuses()) {
                if (bonus instanceof FishingSpeedModifier modifier) {
                    speed += modifier.getFishingSpeedBonus();
                }
            }
        }

        return speed;
    }

    /**
     * Applies the Fishing Speed stat to a base fishing wait time.
     *
     * <p>Uses a diminishing-returns curve where increasing Fishing Speed
     * progressively reduces the wait time while never reducing it below
     * one tick.</p>
     *
     * @param baseWaitTicks    the base fishing wait time in ticks
     * @param fishingSpeedStat the Fishing Speed percentage
     * @return the adjusted wait time in ticks
     */
    public static int applyToWaitTicks(int baseWaitTicks, double fishingSpeedStat) {
        double factor = 100.0 / (100.0 + Math.max(0, fishingSpeedStat));
        return (int) Math.max(1, Math.round(baseWaitTicks * factor));
    }
}
