package fr.moussax.blightedMC.smp.features.fishing;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootProvider;
import fr.moussax.blightedMC.smp.features.entities.spawnable.InfernalBlaze;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class NetherFishing implements FishingLootProvider {

    @Override
    public void register() {
        addLava(World.Environment.NETHER);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.30)
            .addEntities(
                LootEntry.blightedEntity(new InfernalBlaze(), 1.0)
                    .withCatchMessage("§b§l NICE CATCH! §f| §7You caught an §cInfernal Blaze§7!"),

                LootEntry.entity(EntityType.BLAZE, 1.0)
                    .withCatchMessage("§d§l RARE CATCH! §f| §7You caught a §fBlaze§7!"),

                LootEntry.entity(EntityType.GHAST, 0.5)
                    .withCatchMessage("§6§l EPIC CATCH! §f| §7You caught a §fGhast§7!")
            )
            .addItems(
                LootEntry.item(new ItemStack(Material.ANCIENT_DEBRIS, 2), 60.0),
                LootEntry.item(new ItemStack(Material.GOLD_INGOT), 30.0),
                LootEntry.item(new ItemStack(Material.OBSIDIAN), 15.0),
                LootEntry.item(new ItemStack(Material.NETHERITE_SCRAP), 10.0),
                LootEntry.item(new ItemStack(Material.QUARTZ, 3), 5.0)
            )
            .build();
    }
}