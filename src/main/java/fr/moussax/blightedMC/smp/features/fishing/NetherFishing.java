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
import fr.moussax.blightedMC.smp.core.shared.loot.results.gems.GemsResult;
import fr.moussax.blightedMC.smp.features.entities.spawnable.blighted.BlightedZombifiedPiglin;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

import static fr.moussax.blightedMC.smp.core.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class NetherFishing implements FishingLootProvider {

    @Override
    public void register() {
        addLava(World.Environment.NETHER);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.15)
            .addEntities(
                LootEntry.weighted(
                    new MessageDecorator(
                        new FishingLootSoundDecorator(
                            EntityResult.blighted(new BlightedZombifiedPiglin()),
                            FishingLootSoundDecorator.FishingCatchQuality.COMMON
                        ),
                        " §6§lOINK! §7You caught a §dBlighted Zombified Piglin§7!"
                    ),
                    15.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.MAGMA_CUBE),
                        GREAT_CATCH
                    ),
                    2.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.GHAST),
                        GREAT_CATCH
                    ),
                    20,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.HOGLIN),
                        GREAT_CATCH
                    ),
                    30,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.CRIMSON_FOREST)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.ENDERMAN),
                        GREAT_CATCH
                    ),
                    30,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.WARPED_FOREST)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        EntityResult.vanilla(EntityType.SKELETON),
                        GREAT_CATCH
                    ),
                    30,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                )
            )
            .addItems(
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ROTTEN_FLESH),
                        COMMON
                    ),
                    120.0,
                    AmountProvider.range(2, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.QUARTZ),
                        COMMON
                    ),
                    110.0,
                    AmountProvider.range(4, 12),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MUSHROOM_STEW),
                        COMMON
                    ),
                    110.0,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GLOWSTONE_DUST),
                        COMMON
                    ),
                    110.0,
                    AmountProvider.range(4, 12),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.COAL),
                        COMMON
                    ),
                    100.0,
                    AmountProvider.range(3, 8),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GOLD_NUGGET),
                        COMMON
                    ),
                    100.0,
                    AmountProvider.range(5, 15),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.IRON_NUGGET),
                        COMMON
                    ),
                    100.0,
                    AmountProvider.range(3, 9),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.COOKED_CHICKEN),
                        COMMON
                    ),
                    100.0,
                    AmountProvider.range(2, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.COOKED_PORKCHOP),
                        COMMON
                    ),
                    100.0,
                    AmountProvider.range(2, 6),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.BAKED_POTATO),
                        COMMON
                    ),
                    90.0,
                    AmountProvider.range(3, 8),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.STRING),
                        COMMON
                    ),
                    80.0,
                    AmountProvider.range(1, 4),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.LEATHER),
                        COMMON
                    ),
                    80.0,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("SULFUR"),
                        COMMON
                    ),
                    60.0,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
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
                        ItemResult.of(Material.GOLD_INGOT),
                        GOOD_CATCH
                    ),
                    55,
                    AmountProvider.range(2, 5),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("ENCHANTED_QUARTZ"),
                        GOOD_CATCH
                    ),
                    50,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("ENCHANTED_GLOWSTONE_DUST"),
                        GOOD_CATCH
                    ),
                    50,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GOLDEN_CARROT),
                        GOOD_CATCH
                    ),
                    50,
                    AmountProvider.range(3, 8),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.OBSIDIAN),
                        GOOD_CATCH
                    ),
                    45,
                    AmountProvider.range(2, 4),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.BLAZE_ROD),
                        GOOD_CATCH
                    ),
                    45,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SPECTRAL_ARROW),
                        GOOD_CATCH
                    ),
                    40,
                    AmountProvider.range(8, 16),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SPECTRAL_ARROW),
                        GOOD_CATCH
                    ),
                    40,
                    AmountProvider.range(8, 16),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GILDED_BLACKSTONE),
                        GOOD_CATCH
                    ),
                    40,
                    AmountProvider.range(1, 4),
                    LootCondition.biome(Biome.BASALT_DELTAS)
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
                        ItemResult.randomDurability(Material.CROSSBOW, 0.10, 0.80),
                        GOOD_CATCH
                    ),
                    35,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.randomDurability(Material.GOLDEN_HELMET, 0.20, 0.75),
                        GOOD_CATCH
                    ),
                    35,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("FLAMES"),
                        GOOD_CATCH
                    ),
                    35,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SUSPICIOUS_STEW, b ->
                            b.setItemMeta(meta -> ((SuspiciousStewMeta) meta).addCustomEffect(
                                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0), false)
                            )
                        ),
                        GOOD_CATCH
                    ),
                    35,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.WARPED_FUNGUS_ON_A_STICK),
                        GOOD_CATCH
                    ),
                    30,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.WARPED_FOREST)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SOUL_SAND),
                        GOOD_CATCH
                    ),
                    30,
                    AmountProvider.range(1, 2),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ENDER_PEARL),
                        GOOD_CATCH
                    ),
                    30,
                    AmountProvider.range(1, 2),
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
                        ItemResult.of(Material.GOLDEN_APPLE),
                        GREAT_CATCH
                    ),
                    25,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.CRYING_OBSIDIAN),
                        GREAT_CATCH
                    ),
                    25,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.GHAST_TEAR),
                        GREAT_CATCH
                    ),
                    25,
                    AmountProvider.range(1, 3),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.POTION, b -> b.setItemMeta(meta ->
                            ((PotionMeta) meta).setBasePotionType(PotionType.LONG_FIRE_RESISTANCE)
                        )),
                        GREAT_CATCH
                    ),
                    20,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.randomEnchantedBook(
                            List.of(
                                Enchantment.SMITE,
                                Enchantment.PROTECTION,
                                Enchantment.FIRE_PROTECTION,
                                Enchantment.SHARPNESS,
                                Enchantment.FEATHER_FALLING
                            ),
                            3,
                            5
                        ),
                        GREAT_CATCH
                    ),
                    20,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SKELETON_SKULL),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.DRIED_GHAST),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("SUSPICIOUS_FUNGUS"),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.RESPAWN_ANCHOR),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.PIGLIN_HEAD),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.NETHERITE_SCRAP),
                        GREAT_CATCH
                    ),
                    15,
                    AmountProvider.range(1, 2),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ANCIENT_DEBRIS),
                        GREAT_CATCH
                    ),
                    10,
                    AmountProvider.fixed(1),
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
                        ItemResult.of("FLAMES"),
                        OUTSTANDING_CATCH
                    ),
                    8,
                    AmountProvider.range(8, 16),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MUSIC_DISC_PIGSTEP),
                        OUTSTANDING_CATCH
                    ),
                    5,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.CRIMSON_FOREST)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.MUSIC_DISC_TEARS),
                        OUTSTANDING_CATCH
                    ),
                    5,
                    AmountProvider.fixed(1),
                    LootCondition.biome(Biome.SOUL_SAND_VALLEY)
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE),
                        OUTSTANDING_CATCH
                    ),
                    3,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.WITHER_SKELETON_SKULL),
                        OUTSTANDING_CATCH
                    ),
                    3,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.OMINOUS_BOTTLE, b -> b.setItemMeta(
                            meta -> ((OminousBottleMeta) meta).setAmplifier(4)
                        )),
                        OUTSTANDING_CATCH
                    ),
                    3,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of("VENGEFUL_EYE"),
                        OUTSTANDING_CATCH
                    ),
                    2,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.ENCHANTED_GOLDEN_APPLE),
                        OUTSTANDING_CATCH
                    ),
                    1,
                    AmountProvider.fixed(1),
                    LootCondition.alwaysTrue()
                ),
                LootEntry.weighted(
                    new FishingLootFeedbackDecorator(
                        ItemResult.of(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
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
