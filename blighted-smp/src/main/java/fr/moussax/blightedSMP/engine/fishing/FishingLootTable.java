package fr.moussax.blightedSMP.engine.fishing;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import fr.moussax.blightedSMP.engine.loot.*;
import fr.moussax.blightedSMP.engine.loot.decorators.FeedbackSpecification;
import fr.moussax.blightedSMP.engine.loot.decorators.FishingCatchQuality;
import fr.moussax.blightedSMP.engine.loot.decorators.GenericFeedbackDecorator;
import fr.moussax.blightedSMP.engine.loot.providers.AmountProvider;
import fr.moussax.blightedSMP.engine.loot.results.EntityResult;
import fr.moussax.blightedSMP.engine.loot.results.ItemResult;
import fr.moussax.blightedSMP.engine.loot.strategies.WeightedSelectionStrategy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Loot table for fishing rolls containing separate entity and item drop pools.
 *
 * <p>The entity pool is evaluated first based on the base roll chance, player Luck of the Sea level,
 * and fishing combo. If no entity is selected, the item pool is evaluated using weighted selection.</p>
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
     * Rolls this fishing loot table without Luck of the Sea or combo bonuses.
     *
     * @param player   player performing the roll
     * @param location location where catch occurs
     * @param velocity velocity of fishing hook
     * @return {@code true} if a loot entry was selected and executed
     */
    public boolean roll(BlightedPlayer player, Location location, Vector velocity) {
        return roll(player, location, velocity, 0, 0);
    }

    /**
     * Rolls this fishing loot table using a Luck of the Sea level bonus.
     *
     * @param player         player performing the roll
     * @param location       location where catch occurs
     * @param velocity       velocity of fishing hook
     * @param luckOfSeaLevel player Luck of the Sea level
     * @return {@code true} if a loot entry was selected and executed
     */
    public boolean roll(BlightedPlayer player, Location location, Vector velocity, int luckOfSeaLevel) {
        return roll(player, location, velocity, luckOfSeaLevel, 0);
    }

    /**
     * Rolls this fishing loot table using Luck of the Sea and fishing combo bonuses.
     *
     * @param player         player performing the roll
     * @param location       location where catch occurs
     * @param velocity       velocity of fishing hook
     * @param luckOfSeaLevel player Luck of the Sea level
     * @param combo          current fishing combo count
     * @return {@code true} if a loot entry was selected and executed
     */
    public boolean roll(BlightedPlayer player, Location location, Vector velocity, int luckOfSeaLevel, int combo) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        World world = Objects.requireNonNull(location.getWorld());
        Biome biome = world.getBiome(location);
        LootContext context = new LootContext(player, world, biome, location, random, velocity);

        double seaCreatureChanceBonus = Math.min(0.10, combo * 0.005);
        double luckBonus = luckOfSeaLevel * LUCK_ENTITY_CHANCE_PER_LEVEL;
        double finalEntityChance = Math.min(1.0, entityRollChance + luckBonus + seaCreatureChanceBonus);

        if (!entityTable.isEmpty() && random.nextDouble() <= finalEntityChance) {
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
     * Creates a new builder for configuring a fishing loot table.
     *
     * @return fishing loot table builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for constructing {@link FishingLootTable} instances.
     */
    public static final class Builder {
        private static final Function<FishingCatchQuality, FeedbackSpecification> FISHING_FEEDBACK_MAPPER = quality -> switch (quality) {
            case GOOD_CATCH ->
                    FeedbackSpecification.full(" §5§lGOOD CATCH! §f| §7You found §f", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f);
            case GREAT_CATCH ->
                    FeedbackSpecification.full(" §6§lGREAT CATCH! §f| §7You found §f", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f);
            case OUTSTANDING_CATCH ->
                    FeedbackSpecification.full(" §d§lOUTSTANDING CATCH! §f| §7You found §f", Sound.ENTITY_PLAYER_LEVELUP, 1.5f);
            default -> null;
        };

        private static final Function<FishingCatchQuality, FeedbackSpecification> FISHING_SOUND_MAPPER = quality -> switch (quality) {
            case GOOD_CATCH -> FeedbackSpecification.soundOnly(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f);
            case GREAT_CATCH -> FeedbackSpecification.soundOnly(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f);
            case OUTSTANDING_CATCH -> FeedbackSpecification.soundOnly(Sound.ENTITY_PLAYER_LEVELUP, 1.5f);
            default -> null;
        };

        private final LootTable.Builder entityTableBuilder = LootTable.builder();
        private final LootTable.Builder itemTableBuilder = LootTable.builder();
        private double entityRollChance = 0.15;

        /**
         * Sets the base probability of selecting from the entity pool instead of items.
         *
         * @param chance entity selection probability (clamped between 0.0 and 1.0)
         * @return this builder
         */
        public Builder setEntityRollChance(double chance) {
            this.entityRollChance = Math.clamp(chance, 0.0, 1.0);
            return this;
        }

        /**
         * Adds an entry to the entity loot pool.
         *
         * @param entry entity loot entry
         * @return this builder
         */
        public Builder addEntity(LootEntry entry) {
            entityTableBuilder.addEntry(entry);
            return this;
        }

        /**
         * Adds multiple entries to the entity loot pool.
         *
         * @param entries entity loot entries
         * @return this builder
         */
        public Builder addEntities(LootEntry... entries) {
            entityTableBuilder.addEntries(entries);
            return this;
        }

        /**
         * Adds an entry to the item loot pool.
         *
         * @param entry item loot entry
         * @return this builder
         */
        public Builder addItem(LootEntry entry) {
            itemTableBuilder.addEntry(entry);
            return this;
        }

        /**
         * Adds multiple entries to the item loot pool.
         *
         * @param entries item loot entries
         * @return this builder
         */
        public Builder addItems(LootEntry... entries) {
            itemTableBuilder.addEntries(entries);
            return this;
        }

        /**
         * Adds a weighted item result with a variable quantity range and catch feedback quality.
         *
         * @param result        loot result
         * @param minimumAmount minimum drop quantity
         * @param maximumAmount maximum drop quantity
         * @param weight        selection weight
         * @param quality       catch quality tier for feedback
         * @return this builder
         */
        public Builder addItem(
                LootResult result,
                int minimumAmount,
                int maximumAmount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(result, minimumAmount, maximumAmount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a weighted item result with a variable quantity range, catch feedback quality, and selection condition.
         *
         * @param result        loot result
         * @param minimumAmount minimum drop quantity
         * @param maximumAmount maximum drop quantity
         * @param weight        selection weight
         * @param quality       catch quality tier for feedback
         * @param condition     condition required for eligibility
         * @return this builder
         */
        public Builder addItem(
                LootResult result,
                int minimumAmount,
                int maximumAmount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(LootEntry.weighted(
                    new GenericFeedbackDecorator<>(result, quality, FISHING_FEEDBACK_MAPPER),
                    weight,
                    AmountProvider.range(minimumAmount, maximumAmount),
                    condition
            ));
        }

        /**
         * Adds a weighted item result with a fixed quantity and catch feedback quality.
         *
         * @param result  loot result
         * @param amount  drop quantity
         * @param weight  selection weight
         * @param quality catch quality tier for feedback
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
         * Adds a weighted item result with a fixed quantity, catch feedback quality, and selection condition.
         *
         * @param result    loot result
         * @param amount    drop quantity
         * @param weight    selection weight
         * @param quality   catch quality tier for feedback
         * @param condition condition required for eligibility
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
         * Adds a vanilla Minecraft entity catch.
         *
         * @param type    entity type to spawn
         * @param weight  selection weight
         * @param quality catch quality tier for feedback
         * @return this builder
         */
        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality) {
            return addVanillaEntity(type, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a vanilla Minecraft entity catch with a selection condition.
         *
         * @param type      entity type to spawn
         * @param weight    selection weight
         * @param quality   catch quality tier for feedback
         * @param condition condition required for eligibility
         * @return this builder
         */
        public Builder addVanillaEntity(
                EntityType type,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new GenericFeedbackDecorator<>(EntityResult.vanilla(type), quality, FISHING_FEEDBACK_MAPPER),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla Minecraft entity catch with an entity modifier.
         *
         * @param type     entity type to spawn
         * @param weight   selection weight
         * @param quality  catch quality tier for feedback
         * @param modifier function to modify the spawned entity
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
         * Adds a vanilla Minecraft entity catch with an entity modifier and selection condition.
         *
         * @param type      entity type to spawn
         * @param weight    selection weight
         * @param quality   catch quality tier for feedback
         * @param modifier  function to modify the spawned entity
         * @param condition condition required for eligibility
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
                    new GenericFeedbackDecorator<>(EntityResult.vanilla(type, modifier), quality, FISHING_FEEDBACK_MAPPER),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla Minecraft entity catch with a custom catch message.
         *
         * @param type         entity type to spawn
         * @param weight       selection weight
         * @param quality      catch quality tier for feedback
         * @param catchMessage custom message sent on catch
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
         * Adds a vanilla Minecraft entity catch with a custom catch message and selection condition.
         *
         * @param type         entity type to spawn
         * @param weight       selection weight
         * @param quality      catch quality tier for feedback
         * @param catchMessage custom message sent on catch
         * @param condition    condition required for eligibility
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
                    new GenericFeedbackDecorator<>(
                            EntityResult.vanilla(type),
                            quality,
                            catchQuality -> {
                                FeedbackSpecification baseSpecification = FISHING_FEEDBACK_MAPPER.apply(catchQuality);
                                return new FeedbackSpecification(
                                        catchMessage,
                                        baseSpecification != null ? baseSpecification.messagePrefix() : null,
                                        baseSpecification != null ? baseSpecification.sound() : null,
                                        baseSpecification != null ? baseSpecification.pitch() : 1.0f
                                );
                            }
                    ),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla Minecraft entity catch with sound-only feedback and a custom catch message.
         *
         * @param type         entity type to spawn
         * @param weight       selection weight
         * @param soundQuality catch quality tier for sound feedback
         * @param catchMessage custom message sent on catch
         * @return this builder
         */
        public Builder addVanillaEntityWithSound(
                EntityType type,
                double weight,
                FishingCatchQuality soundQuality,
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
         * Adds a vanilla Minecraft entity catch with sound-only feedback, a custom catch message, and selection condition.
         *
         * @param type         entity type to spawn
         * @param weight       selection weight
         * @param soundQuality catch quality tier for sound feedback
         * @param catchMessage custom message sent on catch
         * @param condition    condition required for eligibility
         * @return this builder
         */
        public Builder addVanillaEntityWithSound(
                EntityType type,
                double weight,
                FishingCatchQuality soundQuality,
                String catchMessage,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new GenericFeedbackDecorator<>(
                            EntityResult.vanilla(type),
                            soundQuality,
                            catchQuality -> {
                                FeedbackSpecification baseSpecification = FISHING_SOUND_MAPPER.apply(catchQuality);
                                return new FeedbackSpecification(
                                        catchMessage,
                                        null,
                                        baseSpecification != null ? baseSpecification.sound() : null,
                                        baseSpecification != null ? baseSpecification.pitch() : 1.0f
                                );
                            }
                    ),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a custom Blighted entity catch.
         *
         * @param blightedEntity Blighted entity to spawn
         * @param weight         selection weight
         * @param quality        catch quality tier for feedback
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
         * Adds a custom Blighted entity catch with a selection condition.
         *
         * @param blightedEntity Blighted entity to spawn
         * @param weight         selection weight
         * @param quality        catch quality tier for feedback
         * @param condition      condition required for eligibility
         * @return this builder
         */
        public Builder addBlightedEntity(
                BlightedEntity blightedEntity,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addEntity(LootEntry.weighted(
                    new GenericFeedbackDecorator<>(EntityResult.blighted(blightedEntity), quality, FISHING_FEEDBACK_MAPPER),
                    weight,
                    AmountProvider.fixed(1),
                    condition
            ));
        }

        /**
         * Adds a vanilla material item catch with a variable quantity range.
         *
         * @param material      material to drop
         * @param minimumAmount minimum drop quantity
         * @param maximumAmount maximum drop quantity
         * @param weight        selection weight
         * @param quality       catch quality tier for feedback
         * @return this builder
         */
        public Builder addItem(
                Material material,
                int minimumAmount,
                int maximumAmount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(material, minimumAmount, maximumAmount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a vanilla material item catch with a variable quantity range and selection condition.
         *
         * @param material      material to drop
         * @param minimumAmount minimum drop quantity
         * @param maximumAmount maximum drop quantity
         * @param weight        selection weight
         * @param quality       catch quality tier for feedback
         * @param condition     condition required for eligibility
         * @return this builder
         */
        public Builder addItem(
                Material material,
                int minimumAmount,
                int maximumAmount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(LootEntry.weighted(
                    new GenericFeedbackDecorator<>(ItemResult.of(material), quality, FISHING_FEEDBACK_MAPPER),
                    weight,
                    AmountProvider.range(minimumAmount, maximumAmount),
                    condition
            ));
        }

        /**
         * Adds a registered item catch with a variable quantity range.
         *
         * @param itemId        registry ID of item
         * @param minimumAmount minimum drop quantity
         * @param maximumAmount maximum drop quantity
         * @param weight        selection weight
         * @param quality       catch quality tier for feedback
         * @return this builder
         */
        public Builder addItem(
                String itemId,
                int minimumAmount,
                int maximumAmount,
                double weight,
                FishingCatchQuality quality
        ) {
            return addItem(itemId, minimumAmount, maximumAmount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a registered item catch with a variable quantity range and selection condition.
         *
         * @param itemId        registry ID of item
         * @param minimumAmount minimum drop quantity
         * @param maximumAmount maximum drop quantity
         * @param weight        selection weight
         * @param quality       catch quality tier for feedback
         * @param condition     condition required for eligibility
         * @return this builder
         */
        public Builder addItem(
                String itemId,
                int minimumAmount,
                int maximumAmount,
                double weight,
                FishingCatchQuality quality,
                LootCondition condition
        ) {
            return addItem(LootEntry.weighted(
                    new GenericFeedbackDecorator<>(ItemResult.of(itemId), quality, FISHING_FEEDBACK_MAPPER),
                    weight,
                    AmountProvider.range(minimumAmount, maximumAmount),
                    condition
            ));
        }

        /**
         * Adds a vanilla material item catch with a fixed quantity.
         *
         * @param material material to drop
         * @param amount   drop quantity
         * @param weight   selection weight
         * @param quality  catch quality tier for feedback
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
         * Adds a vanilla material item catch with a fixed quantity and selection condition.
         *
         * @param material  material to drop
         * @param amount    drop quantity
         * @param weight    selection weight
         * @param quality   catch quality tier for feedback
         * @param condition condition required for eligibility
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
         * Adds a registered item catch with a fixed quantity.
         *
         * @param itemId  registry ID of item
         * @param amount  drop quantity
         * @param weight  selection weight
         * @param quality catch quality tier for feedback
         * @return this builder
         */
        public Builder addItem(String itemId, int amount, double weight, FishingCatchQuality quality) {
            return addItem(itemId, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        /**
         * Adds a registered item catch with a fixed quantity and selection condition.
         *
         * @param itemId    registry ID of item
         * @param amount    drop quantity
         * @param weight    selection weight
         * @param quality   catch quality tier for feedback
         * @param condition condition required for eligibility
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
         * Constructs the configured {@link FishingLootTable}.
         *
         * @return new fishing loot table
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
