package fr.moussax.blightedMC.content.fishing;

import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.fishing.registry.FishingRegistryHandler;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import fr.moussax.blightedMC.shared.loot.LootCondition;
import fr.moussax.blightedMC.shared.loot.results.ItemResult;
import fr.moussax.blightedMC.shared.loot.results.gems.GemsResult;
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

import static fr.moussax.blightedMC.shared.loot.decorators.FishingCatchQuality.*;

public class NetherFishing implements RegistryModule<FishingRegistryHandler> {

    @Override
    public void register(FishingRegistryHandler registry) {
        registry.register(World.Environment.NETHER, FishingMethod.LAVA, provide());
    }

    public FishingLootTable provide() {
        return FishingLootTable.builder()
                .setEntityRollChance(0.15)
                .addVanillaEntity(EntityType.MAGMA_CUBE, 2.0, GREAT_CATCH)
                .addVanillaEntity(EntityType.GHAST, 20.0, GREAT_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addVanillaEntity(EntityType.HOGLIN, 30.0, GREAT_CATCH, LootCondition.biome(Biome.CRIMSON_FOREST))
                .addVanillaEntity(EntityType.ENDERMAN, 30.0, GREAT_CATCH, LootCondition.biome(Biome.WARPED_FOREST))
                .addVanillaEntity(EntityType.SKELETON, 30.0, GREAT_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem(Material.ROTTEN_FLESH, 2, 6, 120.0, COMMON)
                .addItem(Material.QUARTZ, 4, 12, 110.0, COMMON)
                .addItem(Material.MUSHROOM_STEW, 1, 110.0, COMMON)
                .addItem(Material.GLOWSTONE_DUST, 4, 12, 110.0, COMMON)
                .addItem(Material.COAL, 3, 8, 100.0, COMMON)
                .addItem(Material.GOLD_NUGGET, 5, 15, 100.0, COMMON)
                .addItem(Material.IRON_NUGGET, 3, 9, 100.0, COMMON)
                .addItem(Material.COOKED_CHICKEN, 2, 6, 100.0, COMMON)
                .addItem(Material.COOKED_PORKCHOP, 2, 6, 100.0, COMMON)
                .addItem(Material.BAKED_POTATO, 3, 8, 90.0, COMMON)
                .addItem(Material.STRING, 1, 4, 80.0, COMMON)
                .addItem(Material.LEATHER, 1, 3, 80.0, COMMON)
                .addItem("SULFUR", 2, 4, 60.0, COMMON)
                .addItem(new GemsResult(), 2, 5, 60.0, GOOD_CATCH)
                .addItem(Material.EXPERIENCE_BOTTLE, 3, 8, 55.0, GOOD_CATCH)
                .addItem(Material.GOLD_INGOT, 2, 5, 55.0, GOOD_CATCH)
                .addItem("ENCHANTED_QUARTZ", 1, 3, 50.0, GOOD_CATCH)
                .addItem("ENCHANTED_GLOWSTONE_DUST", 1, 3, 50.0, GOOD_CATCH)
                .addItem(Material.GOLDEN_CARROT, 3, 8, 50.0, GOOD_CATCH)
                .addItem(Material.OBSIDIAN, 2, 4, 45.0, GOOD_CATCH)
                .addItem(Material.BLAZE_ROD, 1, 3, 45.0, GOOD_CATCH)
                .addItem(Material.SPECTRAL_ARROW, 8, 16, 40.0, GOOD_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem(Material.GILDED_BLACKSTONE, 1, 4, 40.0, GOOD_CATCH, LootCondition.biome(Biome.BASALT_DELTAS))
                .addItem("CREME_BRULEE", 1, 2, 40.0, GOOD_CATCH)
                .addItem(ItemResult.randomDurability(Material.CROSSBOW, 0.10, 0.80), 1, 35.0, GOOD_CATCH)
                .addItem(ItemResult.randomDurability(Material.GOLDEN_HELMET, 0.20, 0.75), 1, 35.0, GOOD_CATCH)
                .addItem("FLAMES", 1, 35.0, GOOD_CATCH)
                .addItem(ItemResult.of(Material.SUSPICIOUS_STEW, b ->
                        b.setItemMeta(meta -> ((SuspiciousStewMeta) meta).addCustomEffect(
                                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0), false)
                        )
                ), 1, 35.0, GOOD_CATCH)
                .addItem(Material.WARPED_FUNGUS_ON_A_STICK, 1, 30.0, GOOD_CATCH, LootCondition.biome(Biome.WARPED_FOREST))
                .addItem(Material.SOUL_SAND, 1, 2, 30.0, GOOD_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem(Material.ENDER_PEARL, 1, 2, 30.0, GOOD_CATCH)
                .addItem(new GemsResult(), 6, 10, 30.0, GREAT_CATCH)
                .addItem(Material.GOLDEN_APPLE, 1, 2, 25.0, GREAT_CATCH)
                .addItem(Material.CRYING_OBSIDIAN, 1, 3, 25.0, GREAT_CATCH)
                .addItem(Material.GHAST_TEAR, 1, 3, 25.0, GREAT_CATCH)
                .addItem(ItemResult.of(Material.POTION, b -> b.setItemMeta(meta ->
                        ((PotionMeta) meta).setBasePotionType(PotionType.LONG_FIRE_RESISTANCE)
                )), 1, 20.0, GREAT_CATCH)
                .addItem(ItemResult.randomEnchantedBook(
                        List.of(
                                Enchantment.SMITE,
                                Enchantment.PROTECTION,
                                Enchantment.FIRE_PROTECTION,
                                Enchantment.SHARPNESS,
                                Enchantment.FEATHER_FALLING
                        ),
                        3,
                        5
                ), 1, 20.0, GREAT_CATCH)
                .addItem(Material.SKELETON_SKULL, 1, 15.0, GREAT_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem(Material.DRIED_GHAST, 1, 15.0, GREAT_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem("SUSPICIOUS_FUNGUS", 1, 15.0, GREAT_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem(Material.RESPAWN_ANCHOR, 1, 15.0, GREAT_CATCH)
                .addItem(Material.PIGLIN_HEAD, 1, 15.0, GREAT_CATCH)
                .addItem(Material.NETHERITE_SCRAP, 1, 2, 15.0, GREAT_CATCH)
                .addItem(Material.ANCIENT_DEBRIS, 1, 10.0, GREAT_CATCH)
                .addItem(new GemsResult(), 12, 16, 10.0, OUTSTANDING_CATCH)
                .addItem("FLAMES", 8, 16, 8.0, OUTSTANDING_CATCH)
                .addItem(Material.MUSIC_DISC_PIGSTEP, 1, 5.0, OUTSTANDING_CATCH, LootCondition.biome(Biome.CRIMSON_FOREST))
                .addItem(Material.MUSIC_DISC_TEARS, 1, 5.0, OUTSTANDING_CATCH, LootCondition.biome(Biome.SOUL_SAND_VALLEY))
                .addItem(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 1, 3.0, OUTSTANDING_CATCH)
                .addItem(Material.WITHER_SKELETON_SKULL, 1, 3.0, OUTSTANDING_CATCH)
                .addItem(ItemResult.of(Material.OMINOUS_BOTTLE, b -> b.setItemMeta(
                        meta -> ((OminousBottleMeta) meta).setAmplifier(4)
                )), 1, 3.0, OUTSTANDING_CATCH)
                .addItem("VENGEFUL_EYE", 1, 2.0, OUTSTANDING_CATCH)
                .addItem(Material.ENCHANTED_GOLDEN_APPLE, 1, 1.0, OUTSTANDING_CATCH)
                .addItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1, 1.0, OUTSTANDING_CATCH)
                .build();
    }
}
