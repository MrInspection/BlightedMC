package fr.moussax.blightedMC.smp.features.fishing;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.loot.LootCondition;
import fr.moussax.blightedMC.smp.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootProvider;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class OverworldFishing implements FishingLootProvider {

    @Override
    public void register() {
        addWater(World.Environment.NORMAL);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.30)
            .addEntities(
                LootEntry.entity(EntityType.DROWNED, 1.0, LootCondition.biome(Biome.DEEP_OCEAN))
                    .withCatchMessage("§d§l RARE CATCH! §7You caught a §bDrowned§7!"),

                LootEntry.entity(EntityType.GUARDIAN, 0.5, LootCondition.biome(Biome.DEEP_OCEAN))
                    .withCatchMessage("§6§l EPIC CATCH! §7You caught a §2Guardian§7!")
            )
            .addItems(
                LootEntry.item(new ItemStack(Material.IRON_INGOT, 2), 60.0),
                LootEntry.item(new ItemStack(Material.GOLD_INGOT), 30.0),
                LootEntry.item(new ItemStack(Material.SPYGLASS), 15.0),
                LootEntry.item(new ItemStack(Material.SPONGE), 10.0),
                LootEntry.item(new ItemStack(Material.SOUL_CAMPFIRE, 3), 5.0)
            )
            .build();
    }
}
