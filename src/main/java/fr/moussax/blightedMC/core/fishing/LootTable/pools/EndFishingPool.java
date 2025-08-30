package fr.moussax.blightedMC.core.fishing.LootTable.pools;

import fr.moussax.blightedMC.core.fishing.LootTable.FishingLootTable;
import fr.moussax.blightedMC.core.fishing.LootTable.LootCondition;
import fr.moussax.blightedMC.core.fishing.LootTable.LootEntry;
import fr.moussax.blightedMC.core.fishing.LootTable.LootPool;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class EndFishingPool implements FishingLootTable {
  private static final Random RANDOM = new Random();
  private final LootPool regularPool;

  public EndFishingPool() {
    regularPool = new LootPool();
    regularPool.addWithCondition(new LootEntry(50, LootCondition.alwaysTrue(), new ItemStack(Material.END_STONE)));
    regularPool.addWithCondition(new LootEntry(50, LootCondition.alwaysTrue(), new ItemStack(Material.CHORUS_FRUIT)));
  }

  @Override
  public LivingEntity summonSeaCreature(BlightedPlayer player, Location spawnLocation, Vector vector) {
    return null;
  }

  @Override
  public ItemStack getItemDrop(BlightedPlayer player) {
    return regularPool.roll(RANDOM, new fr.moussax.blightedMC.core.fishing.LootTable.LootContext(player, player.getPlayer().getLocation().getBlock().getBiome(), player.getPlayer().getWorld().getEnvironment()))
      .map(e -> e.createItem(player)).orElse(null);
  }
}
