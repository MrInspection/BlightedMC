package fr.moussax.blightedMC.smp.core.shared.loot;

/**
 * Represents a single loot outcome that can be executed.
 *
 * <p>Implementations define how to apply the loot (e.g., give an item,
 * spawn an entity, or grant currency) and provide a display name for it.</p>
 */
public interface LootResult {

    /**
     * Executes the loot according to the given context and amount.
     *
     * @param context the context of the loot roll
     * @param amount the quantity or magnitude of the loot
     */
    void execute(LootContext context, int amount);

    /**
     * Returns a display name for this loot, optionally including the amount.
     *
     * @param amount the quantity or magnitude of the loot
     * @return formatted loot name
     */
    String displayName(int amount);
}
