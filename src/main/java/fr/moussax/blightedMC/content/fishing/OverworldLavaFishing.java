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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

import static fr.moussax.blightedMC.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class OverworldLavaFishing implements RegistryModule<FishingRegistryHandler> {

    @Override
    public void register(FishingRegistryHandler registry) {
        registry.register(World.Environment.NORMAL, FishingMethod.LAVA, provide());
    }

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
            .addVanillaEntity(EntityType.MAGMA_CUBE, 50.0, GREAT_CATCH)
            .addVanillaEntity(EntityType.SILVERFISH, 40.0, GREAT_CATCH, applyFireResistance)
            .addVanillaEntity(EntityType.HUSK, 30.0, GREAT_CATCH, applyFireResistance)
            .addVanillaEntity(EntityType.PARCHED, 30.0, GREAT_CATCH, applyFireResistance)
            .addVanillaEntity(EntityType.WITCH, 20.0, GREAT_CATCH, applyFireResistance)
            .addVanillaEntity(EntityType.ILLUSIONER, 10.0, GREAT_CATCH, applyFireResistance)
            .addItem(Material.BONE, 2, 6, 120.0, COMMON)
            .addItem(Material.DRIED_KELP, 5, 10, 120.0, COMMON)
            .addItem(Material.FLINT, 2, 5, 110.0, COMMON)
            .addItem(Material.GUNPOWDER, 2, 5, 110.0, COMMON)
            .addItem(Material.COPPER_NUGGET, 5, 15, 100.0, COMMON)
            .addItem(Material.IRON_NUGGET, 5, 15, 100.0, COMMON)
            .addItem(Material.COAL, 3, 8, 100.0, COMMON)
            .addItem(Material.STONE, 5, 10, 100.0, COMMON)
            .addItem(ItemResult.of(Material.STONE_BUTTON, b -> b.setDisplayName("§rPebble")), 2, 5, 100.0, COMMON)
            .addItem(Material.TUFF, 5, 10, 60.0, GOOD_CATCH, LootCondition.atMostY(0))
            .addItem(new GemsResult(), 2, 5, 60.0, GOOD_CATCH)
            .addItem(Material.EXPERIENCE_BOTTLE, 3, 8, 55.0, GOOD_CATCH)
            .addItem(Material.COOKED_BEEF, 4, 8, 55.0, GOOD_CATCH)
            .addItem(Material.LAPIS_LAZULI, 8, 16, 50.0, GOOD_CATCH)
            .addItem(Material.REDSTONE, 8, 16, 50.0, GOOD_CATCH)
            .addItem(Material.MAGMA_BLOCK, 5, 10, 50.0, GOOD_CATCH)
            .addItem(Material.OBSIDIAN, 2, 4, 55.0, GOOD_CATCH)
            .addItem(Material.POINTED_DRIPSTONE, 2, 8, 45.0, GOOD_CATCH, LootCondition.biome(Biome.DRIPSTONE_CAVES))
            .addItem("CREME_BRULEE", 1, 2, 40.0, GOOD_CATCH)
            .addItem(Material.GOLDEN_APPLE, 1, 35.0, GOOD_CATCH)
            .addItem(Material.LAVA_BUCKET, 1, 35.0, GOOD_CATCH)
            .addItem(new GemsResult(), 6, 10, 30.0, GREAT_CATCH)
            .addItem(Material.AMETHYST_CLUSTER, 2, 4, 20.0, GREAT_CATCH)
            .addItem(Material.EMERALD, 3, 6, 20.0, GREAT_CATCH)
            .addItem(Material.RAW_GOLD_BLOCK, 2, 15.0, GREAT_CATCH)
            .addItem(Material.DIAMOND, 1, 3, 15.0, GREAT_CATCH)
            .addItem(new GemsResult(), 12, 16, 10.0, OUTSTANDING_CATCH)
            .addItem(Material.TOTEM_OF_UNDYING, 1, 5.0, OUTSTANDING_CATCH)
            .addItem(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1, 3.0, OUTSTANDING_CATCH)
            .addItem(ItemResult.of(Material.OMINOUS_BOTTLE, b -> b.setItemMeta(
                meta -> ((OminousBottleMeta) meta).setAmplifier(2)
            )), 1, 3.0, OUTSTANDING_CATCH)
            .addItem(Material.DIAMOND_BLOCK, 1, 2.0, OUTSTANDING_CATCH)
            .addItem(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 1, 1.0, OUTSTANDING_CATCH, LootCondition.biome(Biome.DEEP_DARK))
            .addItem(ItemResult.of(Material.BUDDING_AMETHYST, b -> b.setRarity(ItemRarity.RARE)), 1, 1.0, OUTSTANDING_CATCH, LootCondition.atMostY(-30))
            .addItem(Material.ENCHANTED_GOLDEN_APPLE, 1, 1.0, OUTSTANDING_CATCH)
            .build();
    }
}
