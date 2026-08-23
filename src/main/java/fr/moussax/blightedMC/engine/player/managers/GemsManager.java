package fr.moussax.blightedMC.engine.player.managers;

import lombok.Getter;

/**
 * Manages a player's gem balance.
 *
 * <p>Gem balances cannot become negative. Attempts to initialize or modify
 * the balance with negative values are rejected.</p>
 */
@Getter
public final class GemsManager {

    private int gems;

    /**
     * Creates a gem manager with the specified initial balance.
     *
     * @param initialGems the initial number of gems
     * @throws IllegalArgumentException if {@code initialGems} is negative
     */
    public GemsManager(int initialGems) {
        if (initialGems < 0) {
            throw new IllegalArgumentException("Initial gems value cannot be negative");
        }
        this.gems = initialGems;
    }

    /**
     * Adds gems to the current balance.
     *
     * @param value the number of gems to add
     * @throws IllegalArgumentException if {@code value} is negative
     */
    public void addGems(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Gems value to add cannot be negative");
        }
        gems += value;
    }

    /**
     * Removes gems from the current balance.
     *
     * <p>The resulting balance is clamped to zero.</p>
     *
     * @param value the number of gems to remove
     * @throws IllegalArgumentException if {@code value} is negative
     */
    public void removeGems(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Gems value to remove cannot be negative");
        }
        gems = Math.max(0, gems - value);
    }

    /**
     * Checks whether the current balance is sufficient for the specified
     * amount.
     *
     * @param value the number of gems required
     * @return {@code true} if the player has at least the specified number of
     * gems
     */
    public boolean hasEnoughGems(int value) {
        return gems >= value;
    }
}
