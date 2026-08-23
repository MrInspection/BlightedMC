package fr.moussax.blightedMC.engine.fishing;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.loot.LootCondition;
import fr.moussax.blightedMC.shared.loot.LootContext;
import fr.moussax.blightedMC.shared.loot.LootEntry;
import fr.moussax.blightedMC.shared.loot.LootResult;
import fr.moussax.blightedMC.shared.loot.LootTable;
import fr.moussax.blightedMC.shared.loot.decorators.FishingLootFeedbackDecorator;
import fr.moussax.blightedMC.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality;
import fr.moussax.blightedMC.shared.loot.decorators.FishingLootSoundDecorator;
import fr.moussax.blightedMC.shared.loot.decorators.MessageDecorator;
import fr.moussax.blightedMC.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.shared.loot.results.EntityResult;
import fr.moussax.blightedMC.shared.loot.results.ItemResult;
import fr.moussax.blightedMC.shared.loot.strategies.WeightedSelectionStrategy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Defines the loot available from a fishing method.
 *
 * <p>A fishing loot table contains separate pools for entities and items. On each
 * successful roll, the entity pool is evaluated first according to its configured
 * roll chance. If no entity is selected, or the entity pool is empty, the item
 * pool is evaluated instead.</p>
 *
 * <p>Loot entries use weighted selection and may be restricted by
 * {@link LootCondition conditions}. Entity roll chance is additionally affected
 * by the player's Luck of the Sea enchantment.</p>
 */
public final class FishingLootTable {

    private static final double LUCK_ENTITY_CHANCE_PER_LEVEL = 0.03;

    private final LootTable entityTable;
    private final LootTable itemTable;
    private final double entityRollChance;

    private FishingLootTable(LootTable entityTable, LootTable itemTable, double entityRollChance) {
        this.entityTable = entityTable;
        this.itemTable = itemTable;
        this.entityRollChance = entityRollChance;
    }

    /**
     * Rolls this fishing loot table without a Luck of the Sea bonus.
     *
     * @param player   the player performing the fishing roll
     * @param location the location at which the catch occurs
     * @param velocity the velocity of the fishing hook
     * @return {@code true} if a loot entry was selected and executed
     */
    public boolean roll(BlightedPlayer player, Location location, Vector velocity) {
        return roll(player, location, velocity, 0);
    }

    /**
     * Rolls this fishing loot table using the specified Luck of the Sea level.
     *
     * <p>Luck of the Sea increases the probability of selecting an entity
     * from the entity pool. If no entity is selected, the item pool is used.</p>
     *
     * @param player         the player performing the fishing roll
     * @param location       the location at which the catch occurs
     * @param velocity       the velocity of the fishing hook
     * @param luckOfSeaLevel the player's Luck of the Sea enchantment level
     * @return {@code true} if a loot entry was selected and executed
     */
    public boolean roll(BlightedPlayer player, Location location, Vector velocity, int luckOfSeaLevel) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        World world = Objects.requireNonNull(location.getWorld());
        Biome biome = world.getBiome(location);
        LootContext context = new LootContext(player, world, biome, location, random, velocity);

        double adjustedEntityChance = Math.min(
                1.0,
                entityRollChance + (luckOfSeaLevel * LUCK_ENTITY_CHANCE_PER_LEVEL)
        );

        if (!entityTable.isEmpty() && random.nextDouble() <= adjustedEntityChance) {
            entityTable.execute(context);
            return true;
        }

        if (!itemTable.isEmpty()) {
            itemTable.execute(context);
            return true;
        }

        return false;
    }

    /**
     * Creates a builder for configuring a fishing loot table.
     *
     * @return a new fishing loot table builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for constructing {@link FishingLootTable} instances.
     *
     * <p>The builder provides both generic loot-entry methods and convenience
     * methods for common fishing results such as items, vanilla entities, and
     * Blighted entities.</p>
     */
    public static final class Builder {
        private final LootTable.Builder entityTableBuilder = LootTable.builder();
        private final LootTable.Builder itemTableBuilder = LootTable.builder();
        private double entityRollChance = 0.15;

        /**
         * Sets the base probability of selecting an entity instead of an item.
         *
         * <p>The value is clamped to the inclusive range {@code [0.0, 1.0]}.
         * Luck of the Sea is applied separately when the table is rolled.</p>
         *
         * @param chance the entity selection probability
         * @return this builder
         */
        public Builder setEntityRollChance(double chance) {
            this.entityRollChance = Math.clamp(chance, 0.0, 1.0);
            return this;
        }

        /**
         * Adds an entity loot entry to the table.
         *
         * @param entry the entity loot entry
         * @return this builder
         */
        public Builder addEntity(LootEntry entry) {
            entityTableBuilder.addEntry(entry);
            return this;
        }

        /**
         * Adds multiple entity loot entries to the table.
         *
         * @param entries the entity loot entries
         * @return this builder
         */
        public Builder addEntities(LootEntry... entries) {
            entityTableBuilder.addEntries(entries);
            return this;
        }

        /**
         * Adds an item loot entry to the table.
         *
         * @param entry the item loot entry
         * @return this builder
         */
        public Builder addItem(LootEntry entry) {
            itemTableBuilder.addEntry(entry);
            return this;
        }

        /**
         * Adds multiple item loot entries to the table.
         *
         * @param entries the item loot entries
         * @return this builder
         */
        public Builder addItems(LootEntry... entries) {
            itemTableBuilder.addEntries(entries);
            return this;
        }

        /**
         * Adds a weighted item result with a variable amount and catch quality.
         *
         * @param result    the loot result to produce
         * @param minAmount the minimum amount
         * @param maxAmount the maximum amount
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addItem(
                LootResult result,
                int minAmount,
                int maxAmount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(result, minAmount, maxAmount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted item result with a variable amount, catch quality,
         * and selection condition.
         *
         * @param result    the loot result to produce
         * @param minAmount the minimum amount
         * @param maxAmount the maximum amount
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addItem(
                LootResult result,
                int minAmount,
                int maxAmount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(LootEntry.weighted(
                    new FishingLootFeedbackDecorator(result, quality),
                    weight,
                    AmountProvider.range(minAmount, maxAmount),
                    condition
            ));
        }

        /**
         * Adds a weighted item result with a fixed amount and catch quality.
         *
         * @param result  the loot result to produce
         * @param amount  the amount to produce
         * @param weight  the selection weight
         * @param quality the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addItem(
                LootResult result,
                int amount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(result, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted item result with a fixed amount, catch quality,
         * and selection condition.
         *
         * @param result    the loot result to produce
         * @param amount    the amount to produce
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addItem(
                LootResult result,
                int amount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(result, amount, amount, weight, quality, condition);
        }

        /**
         * Adds a vanilla Minecraft entity as a fishing catch.
         *
         * @param type    the entity type to spawn
         * @param weight  the selection weight
         * @param quality the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality) {
            return addVanillaEntity(type, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a vanilla Minecraft entity with a selection condition.
         *
         * @param type      the entity type to spawn
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addVanillaEntity(
                EntityType type,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new FishingLootFeedbackDecorator(EntityResult.vanilla(type), quality),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla Minecraft entity with a custom entity modifier.
         *
         * @param type     the entity type to spawn
         * @param weight   the selection weight
         * @param quality  the feedback quality associated with the catch
         * @param modifier the modifier applied to the spawned entity
         * @return this builder
         */
        public Builder addVanillaEntity(
                EntityType type,
                double weight,
                FishingCatchQuality quality,
                Consumer<LivingEntity> modifier
        ) {
            return addVanillaEntity(type, weight, quality, modifier, LootCondition.alwaysTrue());
        }

        /**
         * Adds a vanilla Minecraft entity with a custom entity modifier
         * and selection condition.
         *
         * @param type      the entity type to spawn
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param modifier  the modifier applied to the spawned entity
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addVanillaEntity(
                EntityType type,
                double weight,
                FishingCatchQuality quality,
                Consumer<LivingEntity> modifier,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new FishingLootFeedbackDecorator(EntityResult.vanilla(type, modifier), quality),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla Minecraft entity with a custom catch message.
         *
         * @param type         the entity type to spawn
         * @param weight       the selection weight
         * @param quality      the feedback quality associated with the catch
         * @param catchMessage the message displayed when the entity is caught
         * @return this builder
         */
        public Builder addVanillaEntity(
                EntityType type,
                double weight,
                FishingCatchQuality quality,
                String catchMessage
        ) {
            return addVanillaEntity(type, weight, quality, catchMessage, LootCondition.alwaysTrue());
        }

        /**
         * Adds a vanilla Minecraft entity with a custom catch message
         * and selection condition.
         *
         * @param type         the entity type to spawn
         * @param weight       the selection weight
         * @param quality      the feedback quality associated with the catch
         * @param catchMessage the message displayed when the entity is caught
         * @param condition    the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addVanillaEntity(
                EntityType type,
                double weight,
                FishingCatchQuality quality,
                String catchMessage,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new MessageDecorator(
                            new FishingLootFeedbackDecorator(EntityResult.vanilla(type), quality),
                            catchMessage
                    ),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla Minecraft entity with catch-specific sound feedback
         * and a custom catch message.
         *
         * @param type         the entity type to spawn
         * @param weight       the selection weight
         * @param soundQuality the sound feedback quality
         * @param catchMessage the message displayed when the entity is caught
         * @return this builder
         */
        public Builder addVanillaEntityWithSound(
                EntityType type,
                double weight,
                FishingLootSoundDecorator.FishingCatchQuality soundQuality,
                String catchMessage
        ) {
            return addVanillaEntityWithSound(
                    type,
                    weight,
                    soundQuality,
                    catchMessage,
                    LootCondition.alwaysTrue()
            );
        }

        /**
         * Adds a vanilla Minecraft entity with catch-specific sound feedback,
         * a custom catch message, and selection condition.
         *
         * @param type         the entity type to spawn
         * @param weight       the selection weight
         * @param soundQuality the sound feedback quality
         * @param catchMessage the message displayed when the entity is caught
         * @param condition    the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addVanillaEntityWithSound(
                EntityType type,
                double weight,
                FishingLootSoundDecorator.FishingCatchQuality soundQuality,
                String catchMessage,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new MessageDecorator(
                            new FishingLootSoundDecorator(EntityResult.vanilla(type), soundQuality),
                            catchMessage
                    ),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a Blighted entity as a fishing catch.
         *
         * @param blightedEntity the Blighted entity to spawn
         * @param weight         the selection weight
         * @param quality        the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addBlightedEntity(
                BlightedEntity blightedEntity,
                double weight,
                FishingCatchQuality quality
        ) {
            return addBlightedEntity(blightedEntity, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a Blighted entity with a selection condition.
         *
         * @param blightedEntity the Blighted entity to spawn
         * @param weight         the selection weight
         * @param quality        the feedback quality associated with the catch
         * @param condition      the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addBlightedEntity(
                BlightedEntity blightedEntity,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new FishingLootFeedbackDecorator(EntityResult.blighted(blightedEntity), quality),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a weighted vanilla material item with a variable amount.
         *
         * @param material  the material to produce
         * @param minAmount the minimum amount
         * @param maxAmount the maximum amount
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addItem(
                Material material,
                int minAmount,
                int maxAmount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(material, minAmount, maxAmount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted vanilla material item with a variable amount
         * and selection condition.
         *
         * @param material  the material to produce
         * @param minAmount the minimum amount
         * @param maxAmount the maximum amount
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addItem(
                Material material,
                int minAmount,
                int maxAmount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(LootEntry.weighted(
                    new FishingLootFeedbackDecorator(ItemResult.of(material), quality),
                    weight,
                    AmountProvider.range(minAmount, maxAmount),
                    condition
            ));
        }

        /**
         * Adds a weighted registered item with a variable amount.
         *
         * @param itemId    the registered item identifier
         * @param minAmount the minimum amount
         * @param maxAmount the maximum amount
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addItem(
                String itemId,
                int minAmount,
                int maxAmount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(itemId, minAmount, maxAmount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted registered item with a variable amount
         * and selection condition.
         *
         * @param itemId    the registered item identifier
         * @param minAmount the minimum amount
         * @param maxAmount the maximum amount
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addItem(
                String itemId,
                int minAmount,
                int maxAmount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(LootEntry.weighted(
                    new FishingLootFeedbackDecorator(ItemResult.of(itemId), quality),
                    weight,
                    AmountProvider.range(minAmount, maxAmount),
                    condition
            ));
        }

        /**
         * Adds a weighted vanilla material item with a fixed amount.
         *
         * @param material the material to produce
         * @param amount   the amount to produce
         * @param weight   the selection weight
         * @param quality  the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addItem(
                Material material,
                int amount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(material, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted vanilla material item with a fixed amount
         * and selection condition.
         *
         * @param material  the material to produce
         * @param amount    the amount to produce
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addItem(
                Material material,
                int amount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(material, amount, amount, weight, quality, condition);
        }

        /**
         * Adds a weighted registered item with a fixed amount.
         *
         * @param itemId  the registered item identifier
         * @param amount  the amount to produce
         * @param weight  the selection weight
         * @param quality the feedback quality associated with the catch
         * @return this builder
         */
        public Builder addItem(String itemId, int amount, double weight, FishingCatchQuality quality) {
            return addItem(itemId, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted registered item with a fixed amount
         * and selection condition.
         *
         * @param itemId    the registered item identifier
         * @param amount    the amount to produce
         * @param weight    the selection weight
         * @param quality   the feedback quality associated with the catch
         * @param condition the condition required for the entry to be selected
         * @return this builder
         */
        public Builder addItem(
                String itemId,
                int amount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(itemId, amount, amount, weight, quality, condition);
        }

        /**
         * Builds the configured fishing loot table.
         *
         * @return a fishing loot table containing the configured entity and item pools
         */
        public FishingLootTable build() {
            entityTableBuilder
                    .selectionStrategy(new WeightedSelectionStrategy())
                    .rollChance(1.0);

            itemTableBuilder
                    .selectionStrategy(new WeightedSelectionStrategy())
                    .rollChance(1.0);

            return new FishingLootTable(
                    entityTableBuilder.build(),
                    itemTableBuilder.build(),
                    entityRollChance
            );
        }
    }
}
