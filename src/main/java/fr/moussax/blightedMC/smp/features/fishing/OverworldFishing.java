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
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

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
                            EntityResult.vanilla(EntityType.SQUID, new Vector(0, 0.3, 0)),
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
                            EntityResult.vanilla(EntityType.SALMON, new Vector(0, 0.3, 0)),
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
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.TROPICAL_FISH, new Vector(0, 0.3, 0)),
                            GOOD_CATCH
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
                            EntityResult.vanilla(EntityType.PUFFERFISH, new Vector(0, 0.3, 0)),
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
                            EntityResult.vanilla(EntityType.DOLPHIN, new Vector(0, 0.3, 0)),
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
                        ItemResult.of(Material.COD),
                        COMMON
                    ),
                    50.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SALMON),
                        COMMON
                    ),
                    35.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.KELP),
                        COMMON
                    ),
                    30.0,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SEAGRASS),
                        COMMON
                    ),
                    25.0,
                    AmountProvider.range(3, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.TROPICAL_FISH),
                        GOOD_CATCH
                    ),
                    15.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.PUFFERFISH),
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