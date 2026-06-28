package fr.moussax.blightedMC.content.fishing;

import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.fishing.registry.FishingRegistryHandler;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import static fr.moussax.blightedMC.shared.loot.decorators.FishingLootFeedbackDecorator.FishingCatchQuality.*;

public class EndFishing implements RegistryModule<FishingRegistryHandler> {

    @Override
    public void register(FishingRegistryHandler registry) {
        registry.register(World.Environment.THE_END, FishingMethod.WATER, provide());
    }

    public FishingLootTable provide() {
        return FishingLootTable.builder()
                .setEntityRollChance(0.20)
                .addVanillaEntity(EntityType.ENDERMITE, 3.0, GOOD_CATCH, "§b§lYUCK! §7You caught an §5Endermite§7!")
                .addVanillaEntity(EntityType.SHULKER, 0.8, OUTSTANDING_CATCH, "§d§lRARE CATCH! §7You caught a §5Shulker§7!")
                .addItem(Material.END_STONE, 3, 50.0, COMMON)
                .addItem(Material.CHORUS_FRUIT, 2, 40.0, GOOD_CATCH)
                .addItem(Material.ENDER_PEARL, 1, 10.0, GREAT_CATCH)
                .build();
    }
}
