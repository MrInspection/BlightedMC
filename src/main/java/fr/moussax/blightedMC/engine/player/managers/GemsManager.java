package fr.moussax.blightedMC.engine.player.managers;

import lombok.Getter;

@Getter
public final class GemsManager {
    private int gems;

    public GemsManager(int initialGems) {
        if (initialGems < 0) {
            throw new IllegalArgumentException("Initial gems value cannot be negative");
        }
        this.gems = initialGems;
    }

    public void addGems(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Gems value to add cannot be negative");
        }
        gems += value;
    }

    public void removeGems(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Gems value to remove cannot be negative");
        }
        gems = Math.max(0, gems - value);
    }

    public boolean hasEnoughGems(int value) {
        return gems >= value;
    }
}
