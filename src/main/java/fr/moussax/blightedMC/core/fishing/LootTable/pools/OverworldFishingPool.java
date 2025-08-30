package fr.moussax.blightedMC.core.fishing.LootTable.pools;

import fr.moussax.blightedMC.core.fishing.LootTable.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class OverworldFishingPool implements FishingLootTable {
  private static final Random RANDOM = new Random();
  private final LootPool regularPool;

  public OverworldFishingPool() {
    regularPool = new LootPool();
    regularPool.addWithCondition(new LootEntry(50, LootCondition.alwaysTrue(), new ItemStack(Material.DIAMOND)));
    regularPool.addWithCondition(new LootEntry(50, LootCondition.alwaysTrue(), new ItemStack(Material.SALMON)));
  }

  @Override
  public LivingEntity summonSeaCreature(BlightedPlayer player, Location spawnLocation, Vector vector) {
    return null;
  }

  @Override
  public ItemStack getItemDrop(BlightedPlayer player) {
    var playerBiome = player.getPlayer().getLocation().getBlock().getBiome();

    return regularPool.roll(RANDOM, new LootContext(player, playerBiome, player.getPlayer().getWorld().getEnvironment()))
      .map(e -> e.createItem(player)).orElse(null);
  }
}
