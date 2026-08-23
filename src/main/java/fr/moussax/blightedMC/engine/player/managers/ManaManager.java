package fr.moussax.blightedMC.engine.player.managers;

import lombok.Getter;
import lombok.Setter;

/**
 * Manages a player's mana pool, including its current and maximum capacity
 * and passive regeneration rate.
 *
 * <p>Mana is clamped between {@code 0} and the configured maximum whenever
 * its current or maximum value is changed.</p>
 */
public final class ManaManager {

    @Getter
    private double currentMana;

    @Getter
    private double maxMana;

    @Setter
    private double manaRegenerationRate;

    /**
     * Creates a mana manager with a full mana pool.
     *
     * @param maxMana the maximum amount of mana available
     * @param manaRegenRate the amount of mana regenerated per regeneration cycle
     */
    public ManaManager(double maxMana, double manaRegenRate) {
        this.maxMana = maxMana;
        this.manaRegenerationRate = manaRegenRate;
        this.currentMana = maxMana;
    }

    /**
     * Sets the current mana, clamping the value to the range
     * {@code [0, maxMana]}.
     *
     * @param currentMana the new current mana value
     */
    public void setCurrentMana(double currentMana) {
        if (currentMana < 0) currentMana = 0;
        if (currentMana > maxMana) currentMana = maxMana;
        this.currentMana = currentMana;
    }

    /**
     * Sets the maximum mana and reduces the current mana if it exceeds the new maximum.
     *
     * @param maxMana the new maximum mana value
     */
    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
        if (this.currentMana > maxMana) this.currentMana = maxMana;
    }

    /**
     * Consumes the specified amount of mana if sufficient mana is available.
     *
     * <p>If the current mana is insufficient, no mana is consumed.</p>
     *
     * @param amount the amount of mana to consume
     */
    public void consumeMana(double amount) {
        if (currentMana < amount) return;
        currentMana -= amount;
    }

    /**
     * Regenerates mana by the configured regeneration rate without exceeding the maximum mana.
     */
    public void regenerateMana() {
        currentMana += manaRegenerationRate;
        if (currentMana > maxMana) currentMana = maxMana;
    }
}
