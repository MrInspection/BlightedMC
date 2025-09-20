package fr.moussax.blightedMC.core.fishing.LootTable;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface FishingLootTable {
  LivingEntity summonSeaCreature(BlightedPlayer player, Location spawnLocation, Vector vector);
  ItemStack getItemDrop(BlightedPlayer player);
}
