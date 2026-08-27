package fr.moussax.blightedMC.shared.loot;

/**
 * Outcome executable upon selection from a {@link LootTable}.
 */
public interface LootResult {

    /**
     * Executes the loot action in the world for the specified quantity.
     *
     * @param context loot context
     * @param amount  drop quantity
     */
    void execute(LootContext context, int amount);

    /**
     * Returns a formatted display name for this loot result.
     *
     * @param amount drop quantity
     * @return formatted display name
     */
    String displayName(int amount);
}
