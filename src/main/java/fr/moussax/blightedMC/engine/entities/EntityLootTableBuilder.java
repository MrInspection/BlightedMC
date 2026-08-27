package fr.moussax.blightedMC.engine.entities;

import fr.moussax.blightedMC.shared.loot.LootCondition;
import fr.moussax.blightedMC.shared.loot.LootEntry;
import fr.moussax.blightedMC.shared.loot.LootTable;
import fr.moussax.blightedMC.shared.loot.decorators.EntityLootRarity;
import fr.moussax.blightedMC.shared.loot.decorators.FeedbackSpecification;
import fr.moussax.blightedMC.shared.loot.decorators.GenericFeedbackDecorator;
import fr.moussax.blightedMC.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.shared.loot.results.ItemResult;
import fr.moussax.blightedMC.shared.loot.results.gems.GemsResult;
import fr.moussax.blightedMC.shared.loot.strategies.LootingAwareProbabilisticStrategy;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fluent builder for creating {@link LootTable} instances associated with blighted entities.
 *
 * <p>Entries added through this builder are selected probabilistically using a
 * {@link LootingAwareProbabilisticStrategy} and wrapped with rarity-based feedback.</p>
 */
public final class EntityLootTableBuilder {

    private static final Function<EntityLootRarity, FeedbackSpecification> ENTITY_FEEDBACK_MAPPER = rarity -> switch (rarity) {
        case RARE ->
                FeedbackSpecification.full(" §f§lRARE DROP! §f| §7You found §f", Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.8f);
        case VERY_RARE ->
                FeedbackSpecification.full(" §b§lVERY RARE DROP! §f| §7You found §f", Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f);
        case CRAZY ->
                FeedbackSpecification.full(" §d§lCRAZY DROP! §f| §7You found §f", Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.2f);
        case INSANE ->
                FeedbackSpecification.full(" §c§lINSANE DROP! §f| §7You found §f", Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f);
        default -> null;
    };

    private final LootTable.Builder builder = LootTable.builder();
    private int maxDrops = 3;

    /**
     * Adds a registered item loot entry with a variable quantity range.
     *
     * @param itemId        registry ID of the item
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity) {
        return addLoot(itemId, minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds a registered item loot entry with a variable quantity range and selection condition.
     *
     * @param itemId        registry ID of the item
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     condition required for eligibility
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity, LootCondition condition) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.of(itemId), rarity, ENTITY_FEEDBACK_MAPPER
                        ),
                        dropChance,
                        AmountProvider.range(minimumAmount, maximumAmount),
                        condition
                )
        );
        return this;
    }

    /**
     * Adds a registered item loot entry with a fixed quantity.
     *
     * @param itemId     registry ID of the item
     * @param amount     drop quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, int amount, double dropChance, EntityLootRarity rarity) {
        return addLoot(itemId, amount, amount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds a registered item loot entry with a fixed quantity and selection condition.
     *
     * @param itemId     registry ID of the item
     * @param amount     drop quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @param condition  condition required for eligibility
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, int amount, double dropChance, EntityLootRarity rarity, LootCondition condition) {
        return addLoot(itemId, amount, amount, dropChance, rarity, condition);
    }

    /**
     * Adds a material item loot entry with a variable quantity range.
     *
     * @param material      material to drop
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity) {
        return addLoot(material, minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds a material item loot entry with a variable quantity range and selection condition.
     *
     * @param material      material to drop
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     condition required for eligibility
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity, LootCondition condition) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.of(material), rarity, ENTITY_FEEDBACK_MAPPER
                        ),
                        dropChance,
                        AmountProvider.range(minimumAmount, maximumAmount),
                        condition
                )
        );
        return this;
    }

    /**
     * Adds a material item loot entry with a fixed quantity.
     *
     * @param material   material to drop
     * @param amount     drop quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, int amount, double dropChance, EntityLootRarity rarity) {
        return addLoot(material, amount, amount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds a material item loot entry with a fixed quantity and selection condition.
     *
     * @param material   material to drop
     * @param amount     drop quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @param condition  condition required for eligibility
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, int amount, double dropChance, EntityLootRarity rarity, LootCondition condition) {
        return addLoot(material, amount, amount, dropChance, rarity, condition);
    }

    /**
     * Adds a modified material item loot entry with a variable quantity range.
     *
     * @param material      material to drop
     * @param modifier      function to modify the item builder
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, Consumer<ItemBuilder> modifier, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity) {
        return addLoot(material, modifier, minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds a modified material item loot entry with a variable quantity range and selection condition.
     *
     * @param material      material to drop
     * @param modifier      function to modify the item builder
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     condition required for eligibility
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, Consumer<ItemBuilder> modifier, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity, LootCondition condition) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.of(material, modifier), rarity, ENTITY_FEEDBACK_MAPPER
                        ),
                        dropChance,
                        AmountProvider.range(minimumAmount, maximumAmount),
                        condition
                )
        );
        return this;
    }

    /**
     * Adds a modified material item loot entry with a fixed quantity.
     *
     * @param material   material to drop
     * @param modifier   function to modify the item builder
     * @param amount     drop quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, Consumer<ItemBuilder> modifier, int amount, double dropChance, EntityLootRarity rarity) {
        return addLoot(material, modifier, amount, amount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds a modified material item loot entry with a fixed quantity and selection condition.
     *
     * @param material   material to drop
     * @param modifier   function to modify the item builder
     * @param amount     drop quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @param condition  condition required for eligibility
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, Consumer<ItemBuilder> modifier, int amount, double dropChance, EntityLootRarity rarity, LootCondition condition) {
        return addLoot(material, modifier, amount, amount, dropChance, rarity, condition);
    }

    /**
     * Adds an enchanted book loot entry where the enchantment and level are selected from a pool.
     *
     * @param enchantmentPool map of enchantments to levels
     * @param dropChance      selection probability (0.0 to 1.0)
     * @param rarity          rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addEnchantedBookLoot(Map<Enchantment, Integer> enchantmentPool, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.randomEnchantedBook(enchantmentPool), rarity, ENTITY_FEEDBACK_MAPPER
                        ),
                        dropChance,
                        AmountProvider.fixed(1),
                        LootCondition.alwaysTrue()
                )
        );
        return this;
    }

    /**
     * Adds an enchanted book loot entry with a level range selected from candidate enchantments.
     *
     * @param enchantments list of candidate enchantments
     * @param minimumLevel minimum enchantment level
     * @param maximumLevel maximum enchantment level
     * @param dropChance   selection probability (0.0 to 1.0)
     * @param rarity       rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addEnchantedBookLoot(List<Enchantment> enchantments, int minimumLevel, int maximumLevel, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.randomEnchantedBook(enchantments, minimumLevel, maximumLevel), rarity, ENTITY_FEEDBACK_MAPPER
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
     * @param material          material to drop
     * @param minimumPercentage minimum durability percentage
     * @param maximumPercentage maximum durability percentage
     * @param dropChance        selection probability (0.0 to 1.0)
     * @param rarity            rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addDamagedLoot(Material material, double minimumPercentage, double maximumPercentage, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.randomDurability(material, minimumPercentage, maximumPercentage), rarity, ENTITY_FEEDBACK_MAPPER
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
     * @param gems       quantity of gems granted
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addGemsLoot(int gems, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(new GemsResult(), rarity, ENTITY_FEEDBACK_MAPPER),
                        dropChance,
                        AmountProvider.fixed(gems),
                        LootCondition.alwaysTrue()
                )
        );
        return this;
    }

    /**
     * Sets the maximum number of loot drops allowed per roll.
     *
     * @param maxDrops maximum number of drops
     * @return this builder
     */
    public EntityLootTableBuilder setMaxDrop(int maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    /**
     * Constructs the configured {@link LootTable} instance.
     *
     * @return a new entity loot table
     */
    public LootTable build() {
        return builder
                .selectionStrategy(new LootingAwareProbabilisticStrategy(maxDrops))
                .rollChance(1.0)
                .build();
    }
}
