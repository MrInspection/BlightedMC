package fr.moussax.blightedMC.core.fishing.dimension;

import fr.moussax.blightedMC.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.core.fishing.loot.LootEntry;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class EndFishing {

    public static FishingLootTable create() {
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
