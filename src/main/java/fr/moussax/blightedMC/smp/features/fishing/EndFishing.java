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

public class EndFishing implements FishingLootProvider {

    @Override
    public void register() {
        addWater(World.Environment.THE_END);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.20)
            .addEntities(
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.ENDERMITE, new Vector(0, 0.3, 0)),
                            FishingLootFeedbackDecorator.FishingCatchQuality.GOOD_CATCH
                        ),
                        "§b§lYUCK! §7You caught an §5Endermite§7!"
                    ),
                    3.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootFeedbackDecorator(
                            EntityResult.vanilla(EntityType.SHULKER, new Vector(0, 0.3, 0)),
                            FishingLootFeedbackDecorator.FishingCatchQuality.OUTSTANDING_CATCH
                        ),
                        "§d§lRARE CATCH! §7You caught a §5Shulker§7!"
                    ),
                    0.8,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .addItems(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.END_STONE),
                        FishingLootFeedbackDecorator.FishingCatchQuality.COMMON
                    ),
                    50.0,
                    AmountProvider.fixed(3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.CHORUS_FRUIT),
                        FishingLootFeedbackDecorator.FishingCatchQuality.GOOD_CATCH
                    ),
                    40.0,
                    AmountProvider.fixed(2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ENDER_PEARL),
                        FishingLootFeedbackDecorator.FishingCatchQuality.GREAT_CATCH
                    ),
                    10.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .build();
    }
}
