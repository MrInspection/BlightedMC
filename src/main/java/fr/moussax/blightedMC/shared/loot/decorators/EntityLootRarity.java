package fr.moussax.blightedMC.shared.loot.decorators;

/**
 * Rarity tier for entity loot drops and visual feedback.
 */
public enum EntityLootRarity {
    COMMON(0.0),
    UNCOMMON(0.0),
    RARE(0.05),
    VERY_RARE(0.075),
    CRAZY(0.1),
    INSANE(0.125);

    private final double lootingModifier;

    EntityLootRarity(double lootingModifier) {
        this.lootingModifier = lootingModifier;
    }

    /**
     * Applies looting enchantment effects to a base drop chance.
     *
     * @param baseChance   base drop chance
     * @param lootingLevel looting level on main-hand weapon
     * @return adjusted drop chance capped at 1.0
     */
    public double applyLooting(double baseChance, int lootingLevel) {
        return Math.min(1.0, baseChance + (lootingLevel * lootingModifier));
    }
}
