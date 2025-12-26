package fr.moussax.blightedMC.smp.core.entities.loot;

public enum LootDropRarity {
    INSANE(0.5),              // < 0.03% Chance
    MIRACULOUS(0.5),          // < 0.6% Chance
    EXTRAORDINARY(0.5),       // < 3% Chance
    RARE(0.75),               // < 11% Chance
    UNCOMMON(0.75),           // < 31% Chance
    COMMON(1.0);              // <= 100%

    private final double lootingMultiplier;

    LootDropRarity(double lootingMultiplier) {
        this.lootingMultiplier = lootingMultiplier;
    }

    /**
     * Calculates the adjusted drop chance based on the looting level.
     * Formula: baseChance * (1 + (lootingLevel * multiplier))
     *
     * @param baseChance   The original drop chance (0.0 - 1.0)
     * @param lootingLevel The level of the Looting enchantment
     * @return The new drop chance
     */
    public double applyLooting(double baseChance, int lootingLevel) {
        if (lootingLevel <= 0) return baseChance;
        return baseChance * (1.0 + (lootingLevel * lootingMultiplier));
    }
}
