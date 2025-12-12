package fr.moussax.blightedMC.core.fishing.dimension;

import fr.moussax.blightedMC.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.core.fishing.loot.LootCondition;
import fr.moussax.blightedMC.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.gameplay.entities.Dummy;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class OverworldFishing {
    public static FishingLootTable create() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.30)
            .addEntities(
                LootEntry.blightedEntity(new Dummy(), 1.0)
                    .withCatchMessage("§b§l NICE CATCH! §7You caught a §3Dummy§7!"),

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
