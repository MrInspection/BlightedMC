package fr.moussax.blightedMC.content.fishing;

import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.fishing.registry.FishingRegistryHandler;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import fr.moussax.blightedMC.shared.loot.LootCondition;
import fr.moussax.blightedMC.shared.loot.decorators.FishingLootSoundDecorator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import static fr.moussax.blightedMC.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class OverworldFishing implements RegistryModule<FishingRegistryHandler> {

    @Override
    public void register(FishingRegistryHandler registry) {
        registry.register(World.Environment.NORMAL, FishingMethod.WATER, provide());
    }

    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.10)
            .addVanillaEntity(EntityType.SQUID, 8.0, COMMON, "§b§lSPLASH! §7You caught a §9Squid§7!")
            .addVanillaEntity(EntityType.SALMON, 5.0, GOOD_CATCH, "§e§lNICE! §7You caught a §6Salmon§7!")
            .addVanillaEntityWithSound(EntityType.TROPICAL_FISH, 4.0, FishingLootSoundDecorator.FishingCatchQuality.GOOD_CATCH, "§d§lCOLORFUL! §7You caught a §bTropical Fish§7!", LootCondition.biome(Biome.WARM_OCEAN))
            .addVanillaEntity(EntityType.PUFFERFISH, 2.0, GREAT_CATCH, "§e§lCAREFUL! §7You caught a §ePufferfish§7!")
            .addVanillaEntity(EntityType.DOLPHIN, 0.5, OUTSTANDING_CATCH, "§b§lAMAZING! §7You caught a §9Dolphin§7!")
            .addItem(Material.ACACIA_BOAT, 1, 2, 50.0, COMMON)
            .addItem(Material.ACACIA_BUTTON, 1, 2, 35.0, COMMON)
            .addItem(Material.ACACIA_PLANKS, 2, 5, 30.0, COMMON)
            .addItem(Material.ACACIA_FENCE, 3, 6, 25.0, COMMON)
            .addItem(Material.ACACIA_FENCE_GATE, 1, 15.0, GOOD_CATCH)
            .addItem(Material.ACACIA_LEAVES, 1, 12.0, GOOD_CATCH)
            .addItem(Material.PRISMARINE_SHARD, 1, 3, 8.0, GREAT_CATCH)
            .addItem(Material.PRISMARINE_CRYSTALS, 1, 2, 5.0, GREAT_CATCH)
            .addItem(Material.NAUTILUS_SHELL, 1, 2.0, OUTSTANDING_CATCH)
            .addItem(Material.HEART_OF_THE_SEA, 1, 0.5, OUTSTANDING_CATCH)
            .build();
    }
}