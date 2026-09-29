package fr.moussax.blightedSMP.engine.entities;

import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.loot.LootCondition;
import fr.moussax.blightedSMP.engine.loot.LootEntry;
import fr.moussax.blightedSMP.engine.loot.LootTable;
import fr.moussax.blightedSMP.engine.loot.decorators.EntityLootRarity;
import fr.moussax.blightedSMP.engine.loot.decorators.FeedbackSpecification;
import fr.moussax.blightedSMP.engine.loot.decorators.GenericFeedbackDecorator;
import fr.moussax.blightedSMP.engine.loot.providers.AmountProvider;
import fr.moussax.blightedSMP.engine.loot.results.ItemResult;
import fr.moussax.blightedSMP.engine.loot.results.gems.GemsResult;
import fr.moussax.blightedSMP.engine.loot.strategies.LootingAwareProbabilisticStrategy;
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
     * Adds custom blighted item drop with fixed quantity of 1.
     *
     * @param item       custom blighted item
     * @param dropChance selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(BlightedItem item, double dropChance) {
        if (item == null) return this;
        return addLoot(
                item.getItemId(),
                1,
                1,
                dropChance,
                EntityLootRarity.COMMON,
                LootCondition.alwaysTrue()
        );
    }

    /**
     * Adds custom blighted item drop with fixed quantity of 1 and rarity feedback.
     *
     * @param item       custom blighted item
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(BlightedItem item, double dropChance, EntityLootRarity rarity) {
        if (item == null) return this;
        return addLoot(item.getItemId(), 1, 1, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds custom blighted item drop with quantity range.
     *
     * @param item          custom blighted item
     * @param minAmount     minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(BlightedItem item, int minAmount, int maximumAmount, double dropChance) {
        if (item == null) return this;
        return addLoot(item.getItemId(), minAmount, maximumAmount, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds custom blighted item drop with quantity range and rarity feedback.
     *
     * @param item          custom blighted item
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(BlightedItem item, int minimumAmount, int maximumAmount, double dropChance, EntityLootRarity rarity) {
        if (item == null) return this;
        return addLoot(item.getItemId(), minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds custom blighted item drop with quantity range, rarity feedback, and eligibility condition.
     *
     * @param item          custom blighted item
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     eligibility condition
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            BlightedItem item,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity,
            LootCondition condition
    ) {
        if (item == null) return this;
        return addLoot(item.getItemId(), minimumAmount, maximumAmount, dropChance, rarity, condition);
    }

    /**
     * Adds registered item drop with fixed quantity of 1.
     *
     * @param itemId     registered item identifier
     * @param dropChance selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, double dropChance) {
        return addLoot(itemId, 1, 1, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds registered item drop with fixed quantity of 1 and rarity feedback.
     *
     * @param itemId     registered item identifier
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, double dropChance, EntityLootRarity rarity) {
        return addLoot(itemId, 1, 1, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds registered item drop with quantity range.
     *
     * @param itemId        registered item identifier
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(String itemId, int minimumAmount, int maximumAmount, double dropChance) {
        return addLoot(itemId, minimumAmount, maximumAmount, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds registered item drop with quantity range and rarity feedback.
     *
     * @param itemId        registered item identifier
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            String itemId,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity
    ) {
        return addLoot(itemId, minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds registered item drop with quantity range, rarity feedback, and eligibility condition.
     *
     * @param itemId        registered item identifier
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     eligibility condition
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            String itemId,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity,
            LootCondition condition
    ) {
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
     * Adds vanilla material item drop with fixed quantity of 1.
     *
     * @param material   item material
     * @param dropChance selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, double dropChance) {
        return addLoot(material, 1, 1, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds vanilla material item drop with fixed quantity of 1 and rarity feedback.
     *
     * @param material   item material
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, double dropChance, EntityLootRarity rarity) {
        return addLoot(material, 1, 1, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds vanilla material item drop with quantity range.
     *
     * @param material      item material
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            int minimumAmount,
            int maximumAmount,
            double dropChance
    ) {
        return addLoot(material, minimumAmount, maximumAmount, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds vanilla material item drop with quantity range and rarity feedback.
     *
     * @param material      item material
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity
    ) {
        return addLoot(material, minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds vanilla material item drop with quantity range, rarity feedback, and eligibility condition.
     *
     * @param material      item material
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     eligibility condition
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity,
            LootCondition condition
    ) {
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
     * Adds modified material item drop with fixed quantity of 1.
     *
     * @param material   item material
     * @param modifier   item builder modification function
     * @param dropChance selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(Material material, Consumer<ItemBuilder> modifier, double dropChance) {
        return addLoot(material, modifier, 1, 1, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds modified material item drop with fixed quantity of 1 and rarity feedback.
     *
     * @param material   item material
     * @param modifier   item builder modification function
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            Consumer<ItemBuilder> modifier,
            double dropChance,
            EntityLootRarity rarity
    ) {
        return addLoot(material, modifier, 1, 1, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds modified material item drop with quantity range.
     *
     * @param material      item material
     * @param modifier      item builder modification function
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            Consumer<ItemBuilder> modifier,
            int minimumAmount,
            int maximumAmount,
            double dropChance
    ) {
        return addLoot(material, modifier, minimumAmount, maximumAmount, dropChance, EntityLootRarity.COMMON, LootCondition.alwaysTrue());
    }

    /**
     * Adds modified material item drop with quantity range and rarity feedback.
     *
     * @param material      item material
     * @param modifier      item builder modification function
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            Consumer<ItemBuilder> modifier,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity
    ) {
        return addLoot(material, modifier, minimumAmount, maximumAmount, dropChance, rarity, LootCondition.alwaysTrue());
    }

    /**
     * Adds modified material item drop with quantity range, rarity feedback, and eligibility condition.
     *
     * @param material      item material
     * @param modifier      item builder modification function
     * @param minimumAmount minimum drop quantity
     * @param maximumAmount maximum drop quantity
     * @param dropChance    selection probability (0.0 to 1.0)
     * @param rarity        rarity tier for feedback
     * @param condition     eligibility condition
     * @return this builder
     */
    public EntityLootTableBuilder addLoot(
            Material material,
            Consumer<ItemBuilder> modifier,
            int minimumAmount,
            int maximumAmount,
            double dropChance,
            EntityLootRarity rarity,
            LootCondition condition
    ) {
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
     * Adds enchanted book drop selected from an enchantment map pool.
     *
     * @param enchantmentPool map of candidate enchantments to levels
     * @param dropChance      selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addEnchantedBook(Map<Enchantment, Integer> enchantmentPool, double dropChance) {
        return addEnchantedBook(enchantmentPool, dropChance, EntityLootRarity.COMMON);
    }

    /**
     * Adds enchanted book drop selected from an enchantment map pool with rarity feedback.
     *
     * @param enchantmentPool map of candidate enchantments to levels
     * @param dropChance      selection probability (0.0 to 1.0)
     * @param rarity          rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addEnchantedBook(
            Map<Enchantment, Integer> enchantmentPool,
            double dropChance,
            EntityLootRarity rarity
    ) {
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
     * Adds enchanted book drop with level range selected from candidate enchantments.
     *
     * @param enchantments list of candidate enchantments
     * @param minimumLevel minimum enchantment level
     * @param maximumLevel maximum enchantment level
     * @param dropChance   selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addEnchantedBook(
            List<Enchantment> enchantments,
            int minimumLevel,
            int maximumLevel,
            double dropChance
    ) {
        return addEnchantedBook(enchantments, minimumLevel, maximumLevel, dropChance, EntityLootRarity.COMMON);
    }

    /**
     * Adds enchanted book drop with level range selected from candidate enchantments with rarity feedback.
     *
     * @param enchantments list of candidate enchantments
     * @param minimumLevel minimum enchantment level
     * @param maximumLevel maximum enchantment level
     * @param dropChance   selection probability (0.0 to 1.0)
     * @param rarity       rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addEnchantedBook(
            List<Enchantment> enchantments,
            int minimumLevel,
            int maximumLevel,
            double dropChance,
            EntityLootRarity rarity
    ) {
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
     * Adds item drop with durability rolled in a percentage range.
     *
     * @param material          item material
     * @param minimumPercentage minimum durability percentage
     * @param maximumPercentage maximum durability percentage
     * @param dropChance        selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addDamagedItem(
            Material material,
            double minimumPercentage,
            double maximumPercentage,
            double dropChance
    ) {
        return addDamagedItem(material, minimumPercentage, maximumPercentage, dropChance, EntityLootRarity.COMMON);
    }

    /**
     * Adds item drop with durability rolled in a percentage range with rarity feedback.
     *
     * @param material          item material
     * @param minimumPercentage minimum durability percentage
     * @param maximumPercentage maximum durability percentage
     * @param dropChance        selection probability (0.0 to 1.0)
     * @param rarity            rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addDamagedItem(
            Material material,
            double minimumPercentage,
            double maximumPercentage,
            double dropChance,
            EntityLootRarity rarity
    ) {
        builder.addEntry(
                LootEntry.probabilistic(
                        new GenericFeedbackDecorator<>(
                                ItemResult.randomDurability(
                                        material,
                                        minimumPercentage,
                                        maximumPercentage
                                ), rarity, ENTITY_FEEDBACK_MAPPER
                        ),
                        dropChance,
                        AmountProvider.fixed(1),
                        LootCondition.alwaysTrue()
                )
        );
        return this;
    }

    /**
     * Adds gem reward drop.
     *
     * @param gems       gem reward quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @return this builder
     */
    public EntityLootTableBuilder addGems(int gems, double dropChance) {
        return addGems(gems, dropChance, EntityLootRarity.COMMON);
    }

    /**
     * Adds gem reward drop with rarity feedback.
     *
     * @param gems       gem reward quantity
     * @param dropChance selection probability (0.0 to 1.0)
     * @param rarity     rarity tier for feedback
     * @return this builder
     */
    public EntityLootTableBuilder addGems(int gems, double dropChance, EntityLootRarity rarity) {
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
     * Sets maximum number of loot drops allowed per roll.
     *
     * @param maxDrops maximum drop count
     * @return this builder
     */
    public EntityLootTableBuilder maxDrops(int maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    /**
     * Sets maximum number of loot drops allowed per roll.
     *
     * @param maxDrops maximum drop count
     * @return this builder
     */
    public EntityLootTableBuilder setMaxDrops(int maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    /**
     * Constructs configured {@link LootTable} instance.
     *
     * @return new entity loot table
     */
    public LootTable build() {
        return builder
                .selectionStrategy(new LootingAwareProbabilisticStrategy(maxDrops))
                .rollChance(1.0)
                .build();
    }
}
