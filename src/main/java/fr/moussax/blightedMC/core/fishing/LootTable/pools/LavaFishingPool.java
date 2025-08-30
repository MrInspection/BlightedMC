package fr.moussax.blightedMC.core.fishing.LootTable.pools;

import fr.moussax.blightedMC.core.fishing.LootTable.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.core.registry.entities.Dummy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class LavaFishingPool implements FishingLootTable {
  private static final Random RANDOM = new Random();

  private final LootPool seaCreaturesPool = new LootPool();
  private final LootPool regularPool;

  public LavaFishingPool() {
    regularPool = new LootPool();
    regularPool.addWithCondition(new LootEntry(25, LootCondition.alwaysTrue(), new ItemStack(Material.DIRT)));
    regularPool.addWithCondition(new LootEntry(20, LootCondition.alwaysTrue(), new ItemStack(Material.COBBLESTONE)));
    regularPool.addWithCondition(new LootEntry(15, LootCondition.alwaysTrue(), new ItemStack(Material.STICK)));
    regularPool.addWithCondition(new LootEntry(5, LootCondition.alwaysTrue(), new ItemStack(Material.STRING)));

    seaCreaturesPool.addWithCondition(
      new LootEntry(0.0001, LootCondition.alwaysTrue(), new Dummy().clone())
        .withMessage("§cA Dummy Lava Creature appears!")
    );
  }

  @Override
  public LivingEntity summonSeaCreature(BlightedPlayer player, Location spawnLocation, Vector vector) {
    return seaCreaturesPool.roll(RANDOM, new LootContext(player, spawnLocation.getBlock().getBiome(), spawnLocation.getWorld().getEnvironment()))
      .map(e -> e.spawnCreature(player, spawnLocation, vector)).orElse(null);
  }

  @Override
  public ItemStack getItemDrop(BlightedPlayer player) {
    return regularPool.roll(RANDOM, new LootContext(player, player.getPlayer().getLocation().getBlock().getBiome(), player.getPlayer().getWorld().getEnvironment()))
      .map(e -> e.createItem(player)).orElse(null);
  }
}
