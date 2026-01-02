package fr.moussax.blightedMC.smp.features.fishing;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.LootCondition;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.providers.AmountProvider;
import fr.moussax.blightedMC.smp.core.shared.loot.results.EntityResult;
import fr.moussax.blightedMC.smp.core.shared.loot.results.ItemResult;
import fr.moussax.blightedMC.smp.core.shared.loot.results.gems.GemsResult;
import fr.moussax.blightedMC.smp.features.entities.spawnable.blighted.BlightedHusk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class OverworldLavaFishing implements FishingLootProvider {


    @Override
    public void register() {
        addLava(World.Environment.NORMAL);
    }

    @Override
    public FishingLootTable provide() {
        Consumer<LivingEntity> applyFireResistance = entity ->
            entity.addPotionEffect(
                new PotionEffect(
                    PotionEffectType.FIRE_RESISTANCE,
                    PotionEffect.INFINITE_DURATION,
                    1,
                    false,
                    false
                )
            );

        return FishingLootTable.builder()
            .setEntityRollChance(0.15)
            .addEntities(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.MAGMA_CUBE),
                        GREAT_CATCH
                    ),
                    50.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.SILVERFISH, applyFireResistance),
                        GREAT_CATCH
                    ),
                    40.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.HUSK, applyFireResistance),
                        GREAT_CATCH
                    ),
                    30.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.PARCHED, applyFireResistance),
                        GREAT_CATCH
                    ),
                    30.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.WITCH, applyFireResistance),
                        GREAT_CATCH
                    ),
                    20.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.ILLUSIONER, applyFireResistance),
                        GREAT_CATCH
                    ),
                    10.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.blighted(new BlightedHusk()),
                        GREAT_CATCH
                    ),
                    10.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .addItems(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.BONE),
                        COMMON
                    ),
                    120,
                    AmountProvider.range(2, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.DRIED_KELP),
                        COMMON
                    ),
                    120,
                    AmountProvider.range(5, 10),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.FLINT),
                        COMMON
                    ),
                    110,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GUNPOWDER),
                        COMMON
                    ),
                    110,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.COPPER_NUGGET),
                        COMMON
                    ),
                    100,
                    AmountProvider.range(5, 15),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.IRON_NUGGET),
                        COMMON
                    ),
                    100,
                    AmountProvider.range(5, 15),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.COAL),
                        COMMON
                    ),
                    100,
                    AmountProvider.range(3, 8),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.STONE),
                        COMMON
                    ),
                    100,
                    AmountProvider.range(5, 10),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.STONE_BUTTON, b -> b.setDisplayName("§rPebble")),
                        COMMON
                    ),
                    100,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.TUFF),
                        GOOD_CATCH
                    ),
                    60,
                    AmountProvider.range(5, 10),
                    LootCondition.atMostY(0)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        new GemsResult(),
                        GOOD_CATCH
                    ),
                    60,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.EXPERIENCE_BOTTLE),
                        GOOD_CATCH
                    ),
                    55,
                    AmountProvider.range(3, 8),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.COOKED_BEEF),
                        GOOD_CATCH
                    ),
                    55,
                    AmountProvider.range(4, 8),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.LAPIS_LAZULI),
                        GOOD_CATCH
                    ),
                    50,
                    AmountProvider.range(8, 16),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.REDSTONE),
                        GOOD_CATCH
                    ),
                    50,
                    AmountProvider.range(8, 16),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MAGMA_BLOCK),
                        GOOD_CATCH
                    ),
                    50,
                    AmountProvider.range(5, 10),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.OBSIDIAN),
                        GOOD_CATCH
                    ),
                    55,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.POINTED_DRIPSTONE),
                        GOOD_CATCH
                    ),
                    45,
                    AmountProvider.range(2, 8),
                    LootCondition.biome(Biome.DRIPSTONE_CAVES)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("CREME_BRULEE"),
                        GOOD_CATCH
                    ),
                    40,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GOLDEN_APPLE),
                        GOOD_CATCH
                    ),
                    35,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.LAVA_BUCKET),
                        GOOD_CATCH
                    ),
                    35,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        new GemsResult(),
                        GREAT_CATCH
                    ),
                    30,
                    AmountProvider.range(6, 10),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.AMETHYST_CLUSTER),
                        GREAT_CATCH
                    ),
                    20,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.EMERALD),
                        GREAT_CATCH
                    ),
                    20,
                    AmountProvider.range(3, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.RAW_GOLD_BLOCK),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.fixed(2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.DIAMOND),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        new GemsResult(),
                        OUTSTANDING_CATCH
                    ),
                    10,
                    AmountProvider.range(12, 16),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.TOTEM_OF_UNDYING),
                        OUTSTANDING_CATCH
                    ),
                    5,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
                        OUTSTANDING_CATCH
                    ),
                    3,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.OMINOUS_BOTTLE, b -> b.setItemMeta(
                            meta -> ((OminousBottleMeta) meta).setAmplifier(2)
                        )),
                        OUTSTANDING_CATCH
                    ),
                    3,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.DIAMOND_BLOCK),
                        OUTSTANDING_CATCH
                    ),
                    2,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE),
                        OUTSTANDING_CATCH
                    ),
                    1,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.DEEP_DARK)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.BUDDING_AMETHYST, b -> b.setRarity(ItemRarity.RARE)),
                        OUTSTANDING_CATCH
                    ),
                    1,
                    AmountProvider.fixed(1),
                    LootCondition.atMostY(-30)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ENCHANTED_GOLDEN_APPLE),
                        OUTSTANDING_CATCH
                    ),
                    1,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                )
            )
            .build();
    }
}
