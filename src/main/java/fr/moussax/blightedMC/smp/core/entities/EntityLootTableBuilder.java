package fr.moussax.blightedMC.smp.core.entities;

import fr.moussax.blightedMC.smp.core.shared.loot.LootCondition;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootTable;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.results.gems.GemsResult;
import fr.moussax.blightedMC.smp.core.shared.loot.results.ItemResult;
import fr.moussax.blightedMC.smp.core.shared.loot.strategies.LootingAwareProbabilisticStrategy;
import org.bukkit.Material;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.EntityLootFeedbackDecorator.EntityLootRarity;

public final class EntityLootTableBuilder {
    private final LootTable.Builder builder = LootTable.builder();
    private int maxDrops = 3;

    public EntityLootTableBuilder addLoot(String itemId, int min, int max, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.of(itemId),
                    rarity
                ),
                dropChance,
                AmountProvider.range(min, max),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    public EntityLootTableBuilder addLoot(Material material, int min, int max, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    ItemResult.of(material),
                    rarity
                ),
                dropChance,
                AmountProvider.range(min, max),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    public EntityLootTableBuilder addGemsLoot(int gems, double dropChance, EntityLootRarity rarity) {
        builder.addEntry(
            LootEntry.probabilistic(
                new EntityLootFeedbackDecorator(
                    new GemsResult(),
                    rarity
                ),
                dropChance,
                AmountProvider.fixed(gems),
                LootCondition.alwaysTrue()
            )
        );
        return this;
    }

    public EntityLootTableBuilder setMaxDrop(int maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    public LootTable build() {
        return builder
            .selectionStrategy(new LootingAwareProbabilisticStrategy(maxDrops))
            .rollChance(1.0)
            .build();
    }
}
