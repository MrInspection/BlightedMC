package fr.moussax.blightedMC.smp.features.fishing;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootProvider;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class EndFishing implements FishingLootProvider {

    @Override
    public void register() {
        addWater(World.Environment.THE_END);
    }

    @Override
    public FishingLootTable provide() {
        return FishingLootTable.builder()
            .setEntityRollChance(0.20)
            .addEntities(
                LootEntry.entity(EntityType.ENDERMITE, 3.0)
                    .withCatchMessage("§b§lYUCK! §7You caught an §5Endermite§7!"),

                LootEntry.entity(EntityType.SHULKER, 0.8)
                    .withCatchMessage("§d§lRARE CATCH! §7You caught a §5Shulker§7!")
            )
            .addItems(
                LootEntry.item(new ItemStack(Material.END_STONE, 3), 50.0),
                LootEntry.item(new ItemStack(Material.CHORUS_FRUIT, 2), 40.0),
                LootEntry.item(new ItemStack(Material.ENDER_PEARL), 10.0)
            )
            .build();
    }
}
