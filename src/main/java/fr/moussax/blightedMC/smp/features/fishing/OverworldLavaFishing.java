package fr.moussax.blightedMC.smp.features.fishing;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.LootCondition;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.MessageDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.results.EntityResult;
import fr.moussax.blightedMC.smp.core.shared.loot.results.ItemResult;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class OverworldLavaFishing implements FishingLootProvider {

    @Override
    public void register() {
        addLava(World.Environment.NORMAL);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.12)
            .addEntities(
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.STRIDER),
                            GOOD_CATCH
                        ),
                        "§c§lSIZZLE! §7You caught a §6Strider§7!"
                    ),
                    6.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.MAGMA_CUBE),
                            GREAT_CATCH
                        ),
                        "§6§lBURNING! §7You caught a §cMagma Cube§7!"
                    ),
                    3.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.BLAZE),
                            OUTSTANDING_CATCH
                        ),
                        "§e§lINCREDIBLE! §7You caught a §6Blaze§7!"
                    ),
                    0.8,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .addItems(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.OBSIDIAN),
                        COMMON
                    ),
                    45.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.BASALT),
                        COMMON
                    ),
                    40.0,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MAGMA_BLOCK),
                        GOOD_CATCH
                    ),
                    25.0,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MAGMA_CREAM),
                        GOOD_CATCH
                    ),
                    18.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.FIRE_CHARGE),
                        GREAT_CATCH
                    ),
                    10.0,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
                )
            )
            .build();
    }
}
