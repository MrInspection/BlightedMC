package fr.moussax.blightedMC.smp.core.entities.loot;

public enum LootDropRarity {
    INSANE(0.01),      // < 1% base chance | +1% per looting level
    CRAZY(0.01),       // 1-3% base chance | +1% per looting level
    VERY_RARE(0.02),   // 3-8% base chance | +2% per looting level
    RARE(0.03),        // 8-20% base chance | +3% per looting level
    UNCOMMON(0.05),    // 20-50% base chance | +5% per looting level
    COMMON(0.10);      // 50-100% base chance | +10% per looting level

    private final double lootingBonus;

    LootDropRarity(double lootingBonus) {
        this.lootingBonus = lootingBonus;
    }

    /**
     * Calculates the adjusted drop chance based on looting level.
     * Formula: baseChance + (lootingLevel * lootingBonus)
     *
     * @param baseChance   original drop chance (0.0 - 1.0)
     * @param lootingLevel looting enchantment level
     * @return adjusted drop chance, capped at 1.0
     */
    public double applyLooting(double baseChance, int lootingLevel) {
        if (lootingLevel <= 0) return baseChance;
        return Math.min(1.0, baseChance + (lootingLevel * lootingBonus));
    }
}
