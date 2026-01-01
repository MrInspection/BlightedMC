package fr.moussax.blightedMC.smp.core.entities;

import fr.moussax.blightedMC.smp.core.shared.loot.LootCondition;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootTable;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.results.ItemResult;
import fr.moussax.blightedMC.smp.core.shared.loot.results.gems.GemsResult;
import fr.moussax.blightedMC.smp.core.shared.loot.strategies.LootingAwareProbabilisticStrategy;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity;

/**
 * Fluent builder for creating {@link LootTable} instances associated with blighted entities.
 *
 * <p>This builder provides a high-level API to register loot entries with probabilities,
 * amount ranges, rarity-based feedback, and looting-aware selection logic. All entries
 * added through this builder are unconditional and evaluated using a
 * {@link LootingAwareProbabilisticStrategy} at build time.</p>
 *
 * <p>The resulting loot table is intended for entity drops and supports items, materials,
 * enchanted books, durability-variant items, and gem rewards.</p>
 */
public final class BlightedLootBuilder {
    private final LootTable.Builder builder = LootTable.builder();
    private int maxDrops = 3;

    /**
     * Adds an item-based loot entry identified by a registry item ID.
     *
     * @param itemId     the item identifier registered in {@link fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry}
     * @param min        minimum amount dropped
     * @param max        maximum amount dropped
     * @param dropChance probability for this entry to be selected
     * @param rarity     visual and audio feedback rarity
     * @return this builder instance
     */
    public BlightedLootBuilder addLoot(String itemId, int min, int max, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.of(itemId), rarity
                ),
                dropChance,
                AmountProvider.range(min, max),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    /**
     * Adds a material-based loot entry.
     *
     * @param material   the Bukkit material to drop
     * @param min        minimum amount dropped
     * @param max        maximum amount dropped
     * @param dropChance probability for this entry to be selected
     * @param rarity     visual and audio feedback rarity
     * @return this builder instance
     */
    public BlightedLootBuilder addLoot(Material material, int min, int max, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.of(material), rarity
                ),
                dropChance,
                AmountProvider.range(min, max),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    /**
     * Adds an enchanted book loot entry where the enchantment and level are selected
     * from a predefined pool.
     *
     * @param enchantmentPool map of enchantments to their corresponding levels
     * @param dropChance      probability for this entry to be selected
     * @param rarity          visual and audio feedback rarity
     * @return this builder instance
     */
    public BlightedLootBuilder addEnchantedBookFromPool(Map<Enchantment, Integer> enchantmentPool, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.randomEnchantedBook(enchantmentPool), rarity
                ),
                dropChance,
                AmountProvider.fixed(1),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    /**
     * Adds an enchanted book loot entry where the enchantment is selected from a list
     * and the level is rolled within the provided bounds.
     *
     * @param enchantments list of possible enchantments
     * @param minLevel     minimum enchantment level
     * @param maxLevel     maximum enchantment level
     * @param dropChance   probability for this entry to be selected
     * @param rarity       visual and audio feedback rarity
     * @return this builder instance
     */
    public BlightedLootBuilder addEnchantedBookWithLevelRange(List<Enchantment> enchantments, int minLevel, int maxLevel, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.randomEnchantedBook(enchantments, minLevel, maxLevel), rarity
                ),
                dropChance,
                AmountProvider.fixed(1),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    /**
     * Adds an item loot entry whose durability is rolled within a percentage range.
     *
     * @param material   the Bukkit material to drop
     * @param minPercent minimum durability percentage
     * @param maxPercent maximum durability percentage
     * @param dropChance probability for this entry to be selected
     * @param rarity     visual and audio feedback rarity
     * @return this builder instance
     */
    public BlightedLootBuilder addLootWithDurabilityRange(Material material, double minPercent, double maxPercent, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.randomDurability(material, minPercent, maxPercent), rarity
                ),
                dropChance,
                AmountProvider.fixed(1),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    /**
     * Adds a gem reward loot entry.
     *
     * @param gems       amount of gems granted on drop
     * @param dropChance probability for this entry to be selected
     * @param rarity     visual and audio feedback rarity
     * @return this builder instance
     */
    public BlightedLootBuilder addGemsLoot(int gems, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(new GemsResult(), rarity),
                dropChance,
                AmountProvider.fixed(gems),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    /**
     * Sets the maximum number of loot entries that may be selected per roll.
     *
     * @param maxDrops maximum number of drops
     * @return this builder instance
     */
    public BlightedLootBuilder setMaxDrop(int maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    /**
     * Builds the final {@link LootTable} instance.
     *
     * <p>The table uses a looting-aware probabilistic selection strategy and a guaranteed roll chance.</p>
     *
     * @return the constructed loot table
     */
    public LootTable build() {
        return builder
            .selectionStrategy(new LootingAwareProbabilisticStrategy(maxDrops))
            .rollChance(1.0)
            .build();
    }
}
