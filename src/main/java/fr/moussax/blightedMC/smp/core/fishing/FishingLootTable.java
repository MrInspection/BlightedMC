package fr.moussax.blightedMC.smp.core.fishing;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootTable;
import fr.moussax.blightedMC.smp.core.shared.loot.strategies.WeightedSelectionStrategy;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
            this.entityRollChance = Math.max(0.0, Math.min(1.0, chance));
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
