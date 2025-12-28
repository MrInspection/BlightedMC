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
import org.bukkit.util.Vector;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class NetherFishing implements FishingLootProvider {

    @Override
    public void register() {
        addWater(World.Environment.NETHER);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.15)
            .addEntities(
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.ZOMBIFIED_PIGLIN, new Vector(0, 0.3, 0)),
                            GOOD_CATCH
                        ),
                        "§6§lOINK! §7You caught a §cZombified Piglin§7!"
                    ),
                    5.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.MAGMA_CUBE, new Vector(0, 0.3, 0)),
                            GREAT_CATCH
                        ),
                        "§c§lHOT! §7You caught a §6Magma Cube§7!"
                    ),
                    2.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.BLAZE, new Vector(0, 0.3, 0)),
                            OUTSTANDING_CATCH
                        ),
                        "§6§lBLAZING! §7You caught a §eBlaze§7!"
                    ),
                    0.5,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .addItems(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.NETHERRACK),
                        COMMON
                    ),
                    60.0,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SOUL_SAND),
                        COMMON
                    ),
                    40.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.NETHER_WART),
                        GOOD_CATCH
                    ),
                    20.0,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GLOWSTONE_DUST),
                        GOOD_CATCH
                    ),
                    15.0,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MAGMA_CREAM),
                        GREAT_CATCH
                    ),
                    8.0,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.QUARTZ),
                        GREAT_CATCH
                    ),
                    5.0,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GOLD_NUGGET),
                        OUTSTANDING_CATCH
                    ),
                    2.0,
                    AmountProvider.range(3, 8),
                    LootCondition.alwaysTrue()
                )
            )
            .build();
    }
}
