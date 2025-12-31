package fr.moussax.blightedMC.smp.features.fishing;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.LootCondition;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootSoundDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.MessageDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.results.EntityResult;
import fr.moussax.blightedMC.smp.core.shared.loot.results.ItemResult;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class OverworldFishing implements FishingLootProvider {

    @Override
    public void register() {
        addWater(World.Environment.NORMAL);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.10)
            .addEntities(
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.SQUID),
                            COMMON
                        ),
                        "§b§lSPLASH! §7You caught a §9Squid§7!"
                    ),
                    8.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.SALMON),
                            GOOD_CATCH
                        ),
                        "§e§lNICE! §7You caught a §6Salmon§7!"
                    ),
                    5.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootSoundDecorator(
                            EntityResult.vanilla(EntityType.TROPICAL_FISH),
                            FishingLootSoundDecorator.FishingCatchQuality.GOOD_CATCH
                        ),
                        "§d§lCOLORFUL! §7You caught a §bTropical Fish§7!"
                    ),
                    4.0,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.WARM_OCEAN)
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.PUFFERFISH),
                            GREAT_CATCH
                        ),
                        "§e§lCAREFUL! §7You caught a §ePufferfish§7!"
                    ),
                    2.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.DOLPHIN),
                            OUTSTANDING_CATCH
                        ),
                        "§b§lAMAZING! §7You caught a §9Dolphin§7!"
                    ),
                    0.5,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .addItems(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ACACIA_BOAT),
                        COMMON
                    ),
                    50.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ACACIA_BUTTON),
                        COMMON
                    ),
                    35.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ACACIA_PLANKS),
                        COMMON
                    ),
                    30.0,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ACACIA_FENCE),
                        COMMON
                    ),
                    25.0,
                    AmountProvider.range(3, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ACACIA_FENCE_GATE),
                        GOOD_CATCH
                    ),
                    15.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ACACIA_LEAVES),
                        GOOD_CATCH
                    ),
                    12.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.PRISMARINE_SHARD),
                        GREAT_CATCH
                    ),
                    8.0,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.PRISMARINE_CRYSTALS),
                        GREAT_CATCH
                    ),
                    5.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.NAUTILUS_SHELL),
                        OUTSTANDING_CATCH
                    ),
                    2.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.HEART_OF_THE_SEA),
                        OUTSTANDING_CATCH
                    ),
                    0.5,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .build();
    }
}