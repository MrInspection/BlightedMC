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

public class NetherFishingPool implements FishingLootTable {
  private static final Random RANDOM = new Random();

  private final LootPool seaCreaturesPool = new LootPool();
  private final LootPool regularPool;

  public NetherFishingPool() {

    regularPool = new LootPool();
    regularPool.addWithCondition(new LootEntry(25, LootCondition.alwaysTrue(), new ItemStack(Material.MAGMA_CREAM)));
    regularPool.addWithCondition(new LootEntry(20, LootCondition.alwaysTrue(), new ItemStack(Material.NETHERRACK)));
    regularPool.addWithCondition(new LootEntry(15, LootCondition.alwaysTrue(), new ItemStack(Material.BLAZE_POWDER)));
    regularPool.addWithCondition(new LootEntry(5, LootCondition.alwaysTrue(), new ItemStack(Material.COAL)));

    seaCreaturesPool.addWithCondition(
      new LootEntry(0.0001, LootCondition.alwaysTrue(), new Dummy().clone())
        .withMessage("§cA Plhlegblast bursts from the lava!")
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
