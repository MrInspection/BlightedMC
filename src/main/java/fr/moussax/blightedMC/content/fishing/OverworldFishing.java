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

import java.util.List;

import static fr.moussax.blightedMC.shared.loot.decorators.FishingCatchQuality.*;

public class OverworldFishing implements RegistryModule<FishingRegistryHandler> {

    @Override
    public void register(FishingRegistryHandler registry) {
        registry.register(World.Environment.NORMAL, FishingMethod.WATER, provide());
    }

    public FishingLootTable provide() {
        LootCondition inFrozenWater = context ->
                context.biome() == Biome.FROZEN_OCEAN ||
                        context.biome() == Biome.DEEP_FROZEN_OCEAN ||
                        context.biome() == Biome.FROZEN_RIVER;

        LootCondition inSwamp = context ->
                context.biome() == Biome.SWAMP ||
                        context.biome() == Biome.MANGROVE_SWAMP;

        LootCondition inJungle = context ->
                context.biome() == Biome.JUNGLE ||
                        context.biome() == Biome.SPARSE_JUNGLE ||
                        context.biome() == Biome.BAMBOO_JUNGLE;

        LootCondition inDeepOcean = context ->
                context.biome() == Biome.DEEP_OCEAN ||
                        context.biome() == Biome.DEEP_LUKEWARM_OCEAN ||
                        context.biome() == Biome.DEEP_COLD_OCEAN ||
                        context.biome() == Biome.DEEP_FROZEN_OCEAN;

        return FishingLootTable.builder()
                .setEntityRollChance(0.15)
                // TODO : Finish the fishing mods
                .addVanillaEntity(EntityType.SQUID, 8.0, COMMON, "§b§lSPLASH! §7You caught a §9Squid§7!")
                .addVanillaEntity(EntityType.SALMON, 5.0, GOOD_CATCH, "§e§lNICE! §7You caught a §6Salmon§7!")
                .addVanillaEntityWithSound(EntityType.TROPICAL_FISH, 4.0, GOOD_CATCH, "§d§lCOLORFUL! §7You caught a §bTropical Fish§7!", LootCondition.biome(Biome.WARM_OCEAN))
                .addVanillaEntity(EntityType.PUFFERFISH, 2.0, GREAT_CATCH, "§e§lCAREFUL! §7You caught a §ePufferfish§7!")
                .addVanillaEntity(EntityType.DOLPHIN, 0.5, OUTSTANDING_CATCH, "§b§lAMAZING! §7You caught a §9Dolphin§7!")

                .addItem(ItemResult.of(Material.STONE_BUTTON, builder -> builder.setItemName("Pebble")), 2, 8, 120.0, COMMON)
                .addItem(Material.STICK, 2, 6, 110.0, COMMON)
                .addItem(Material.LILY_PAD, 2, 6, 105.0, COMMON)
                .addItem(Material.KELP, 2, 8, 100.0, COMMON)
                .addItem(Material.STRING, 1, 4, 90.0, COMMON)
                .addItem(Material.HANGING_ROOTS, 1, 3, 75.0, COMMON)
                .addItem(Material.CLAY_BALL, 2, 8, 70.0, COMMON)
                .addItem(Material.FLINT, 1, 3, 60.0, COMMON)
                .addItem(Material.LEATHER, 1, 3, 55.0, COMMON)
                .addItem(Material.ROTTEN_FLESH, 1, 3, 45.0, COMMON)
                .addItem(Material.BOWL, 1, 2, 35.0, COMMON)

                .addItem("ENCHANTED_COD", 1, 2, 65.0, GOOD_CATCH)
                .addItem("ENCHANTED_SALMON", 1, 2, 60.0, GOOD_CATCH)
                .addItem(new GemsResult(), 2, 5, 60.0, GOOD_CATCH)
                .addItem("BLIGHTED_ALGAE", 1, 3, 45.0, GOOD_CATCH)
                .addItem("SMOKED_SALMON_PLATE", 1, 2, 45.0, GOOD_CATCH)
                .addItem("SALTED_COD", 1, 2, 45.0, GOOD_CATCH)
                .addItem(Material.NAUTILUS_SHELL, 1, 40.0, GOOD_CATCH)
                .addItem(Material.INK_SAC, 1, 3, 35.0, GOOD_CATCH)
                .addItem(Material.GLOW_INK_SAC, 1, 2, 30.0, GOOD_CATCH)
                .addItem(Material.PRISMARINE_SHARD, 1, 4, 30.0, GOOD_CATCH)
                .addItem("FISHERMANS_BAIT", 1, 2, 20.0, GOOD_CATCH)
                .addItem("FISHERMANS_STEW", 1, 20.0, GOOD_CATCH)

                .addItem(new GemsResult(), 6, 10, 30.0, GREAT_CATCH)
                .addItem("BARNACLE_CLUSTER", 1, 3, 35.0, GREAT_CATCH)
                .addItem("CORAL_FRAGMENT", 1, 4, 30.0, GREAT_CATCH)
                .addItem(Material.SPONGE, 1, 2, 20.0, GREAT_CATCH)
                .addItem(Material.HEART_OF_THE_SEA, 1, 15.0, GREAT_CATCH)
                .addItem("MESSAGE_IN_A_BOTTLE", 1, 15.0, GREAT_CATCH)
                .addItem(Material.FILLED_MAP, 1, 12.0, GREAT_CATCH)
                .addItem(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 1, 8.0, GREAT_CATCH)
                .addItem(ItemResult.randomEnchantedBook(
                        List.of(
                                Enchantment.UNBREAKING,
                                Enchantment.EFFICIENCY,
                                Enchantment.DEPTH_STRIDER,
                                Enchantment.RESPIRATION,
                                Enchantment.AQUA_AFFINITY
                        ),
                        1,
                        3
                ), 1, 10.0, GREAT_CATCH)
                .addItem("BLIGHTED_SUSHI", 1, 8.0, GREAT_CATCH)

                .addItem(new GemsResult(), 12, 16, 10.0, OUTSTANDING_CATCH)
                .addItem("ABYSSAL_PEARL", 1, 5.0, OUTSTANDING_CATCH)
                .addItem(ItemResult.randomEnchantedBook(
                        List.of(
                                Enchantment.LUCK_OF_THE_SEA,
                                Enchantment.LURE
                        ),
                        4,
                        5
                ), 1, 6.0, OUTSTANDING_CATCH)
                .addItem("DROWNED_RESEARCH_CODEX", 1, 3.0, OUTSTANDING_CATCH)
                .addItem(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 1, 3.0, OUTSTANDING_CATCH)
                .addItem(Material.TRIDENT, 1, 2.0, OUTSTANDING_CATCH)
                .addItem(Material.MUSIC_DISC_13, 1, 2.0, OUTSTANDING_CATCH)
                .addItem(Material.ENCHANTED_GOLDEN_APPLE, 1, 1.0, OUTSTANDING_CATCH)


                .addItem(Material.ICE, 3, 10, 90.0, COMMON, inFrozenWater)
                .addItem(Material.SNOWBALL, 4, 16, 60.0, COMMON, inFrozenWater)
                .addItem(Material.PACKED_ICE, 1, 3, 25.0, GOOD_CATCH, inFrozenWater)
                .addItem(Material.BLUE_ICE, 1, 5.0, GREAT_CATCH, inFrozenWater)

                .addItem(Material.VINE, 2, 8, 70.0, COMMON, inSwamp)
                .addItem(Material.SLIME_BALL, 1, 3, 45.0, GOOD_CATCH, inSwamp)
                .addItem(Material.POISONOUS_POTATO, 1, 10.0, GREAT_CATCH, inSwamp)

                .addItem(Material.BAMBOO, 2, 8, 70.0, COMMON, inJungle)
                .addItem(Material.COCOA_BEANS, 2, 6, 50.0, COMMON, inJungle)
                .addItem(Material.TROPICAL_FISH_BUCKET, 1, 3.0, GREAT_CATCH, inJungle)

                .addItem(Material.PRISMARINE_CRYSTALS, 2, 6, 30.0, GREAT_CATCH, inDeepOcean)
                .addItem(Material.SPONGE, 1, 2, 15.0, GREAT_CATCH, inDeepOcean)
                .build();
    }
}
