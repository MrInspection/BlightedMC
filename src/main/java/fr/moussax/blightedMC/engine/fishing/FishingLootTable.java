package fr.moussax.blightedMC.engine.fishing;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.loot.LootCondition;
import fr.moussax.blightedMC.shared.loot.LootContext;
import fr.moussax.blightedMC.shared.loot.LootEntry;
import fr.moussax.blightedMC.shared.loot.LootTable;
import fr.moussax.blightedMC.shared.loot.LootResult;
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

public final class FishingLootTable {
    private final LootTable entityTable;
    private final LootTable itemTable;
    private final double entityRollChance;

    private FishingLootTable(LootTable entityTable, LootTable itemTable, double entityRollChance) {
        this.entityTable = entityTable;
        this.itemTable = itemTable;
        this.entityRollChance = entityRollChance;
    }

    public boolean roll(BlightedPlayer player, Location location, Vector velocity) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        World world = Objects.requireNonNull(location.getWorld());
        Biome biome = world.getBiome(location);
        LootContext context = new LootContext(player, world, biome, location, random, velocity);

        if (!entityTable.isEmpty() && random.nextDouble() <= entityRollChance) {
            entityTable.execute(context);
            return true;
        }

        if (!itemTable.isEmpty()) {
            itemTable.execute(context);
            return true;
        }

        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final LootTable.Builder entityTableBuilder = LootTable.builder();
        private final LootTable.Builder itemTableBuilder = LootTable.builder();
        private double entityRollChance = 0.15;

        public Builder setEntityRollChance(double chance) {
            this.entityRollChance = Math.clamp(chance, 0.0, 1.0);
            return this;
        }

        public Builder addEntity(LootEntry entry) {
            entityTableBuilder.addEntry(entry);
            return this;
        }

        public Builder addEntities(LootEntry... entries) {
            entityTableBuilder.addEntries(entries);
            return this;
        }

        public Builder addItem(LootEntry entry) {
            itemTableBuilder.addEntry(entry);
            return this;
        }

        public Builder addItems(LootEntry... entries) {
            itemTableBuilder.addEntries(entries);
            return this;
        }

        // --- DX Fluent Helper Methods ---

        public Builder addItem(LootResult result, int minAmount, int maxAmount, double weight, FishingCatchQuality quality) {
            return addItem(result, minAmount, maxAmount, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addItem(LootResult result, int minAmount, int maxAmount, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addItem(LootEntry.weighted(
                new FishingLootFeedbackDecorator(result, quality),
                weight,
                AmountProvider.range(minAmount, maxAmount),
                condition
            ));
        }

        public Builder addItem(LootResult result, int amount, double weight, FishingCatchQuality quality) {
            return addItem(result, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addItem(LootResult result, int amount, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addItem(result, amount, amount, weight, quality, condition);
        }

        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality) {
            return addVanillaEntity(type, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addEntity(LootEntry.weighted(
                new FishingLootFeedbackDecorator(EntityResult.vanilla(type), quality),
                weight,
                AmountProvider.fixed(1),
                condition
            ));
        }

        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality, Consumer<LivingEntity> modifier) {
            return addVanillaEntity(type, weight, quality, modifier, LootCondition.alwaysTrue());
        }

        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality, Consumer<LivingEntity> modifier, LootCondition condition) {
            return addEntity(LootEntry.weighted(
                new FishingLootFeedbackDecorator(EntityResult.vanilla(type, modifier), quality),
                weight,
                AmountProvider.fixed(1),
                condition
            ));
        }

        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality, String catchMessage) {
            return addVanillaEntity(type, weight, quality, catchMessage, LootCondition.alwaysTrue());
        }

        public Builder addVanillaEntity(EntityType type, double weight, FishingCatchQuality quality, String catchMessage, LootCondition condition) {
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

        public Builder addVanillaEntityWithSound(EntityType type, double weight, FishingLootSoundDecorator.FishingCatchQuality soundQuality, String catchMessage) {
            return addVanillaEntityWithSound(type, weight, soundQuality, catchMessage, LootCondition.alwaysTrue());
        }

        public Builder addVanillaEntityWithSound(EntityType type, double weight, FishingLootSoundDecorator.FishingCatchQuality soundQuality, String catchMessage, LootCondition condition) {
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

        public Builder addBlightedEntity(BlightedEntity blightedEntity, double weight, FishingCatchQuality quality) {
            return addBlightedEntity(blightedEntity, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addBlightedEntity(BlightedEntity blightedEntity, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addEntity(LootEntry.weighted(
                new FishingLootFeedbackDecorator(EntityResult.blighted(blightedEntity), quality),
                weight,
                AmountProvider.fixed(1),
                condition
            ));
        }

        public Builder addItem(Material material, int minAmount, int maxAmount, double weight, FishingCatchQuality quality) {
            return addItem(material, minAmount, maxAmount, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addItem(Material material, int minAmount, int maxAmount, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addItem(LootEntry.weighted(
                new FishingLootFeedbackDecorator(ItemResult.of(material), quality),
                weight,
                AmountProvider.range(minAmount, maxAmount),
                condition
            ));
        }

        public Builder addItem(String itemId, int minAmount, int maxAmount, double weight, FishingCatchQuality quality) {
            return addItem(itemId, minAmount, maxAmount, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addItem(String itemId, int minAmount, int maxAmount, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addItem(LootEntry.weighted(
                new FishingLootFeedbackDecorator(ItemResult.of(itemId), quality),
                weight,
                AmountProvider.range(minAmount, maxAmount),
                condition
            ));
        }

        public Builder addItem(Material material, int amount, double weight, FishingCatchQuality quality) {
            return addItem(material, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addItem(Material material, int amount, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addItem(material, amount, amount, weight, quality, condition);
        }

        public Builder addItem(String itemId, int amount, double weight, FishingCatchQuality quality) {
            return addItem(itemId, amount, amount, weight, quality, LootCondition.alwaysTrue());
        }

        public Builder addItem(String itemId, int amount, double weight, FishingCatchQuality quality, LootCondition condition) {
            return addItem(itemId, amount, amount, weight, quality, condition);
        }

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
