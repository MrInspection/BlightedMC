package fr.moussax.blightedMC.core.fishing.loot.pools;

import fr.moussax.blightedMC.core.fishing.loot.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class EndFishingPool implements FishingLootTable {
  private static final Random RANDOM = new Random();
  private final LootPool seaCreaturePool;
  private final LootPool itemPool;

  public EndFishingPool() {
    seaCreaturePool = new LootPool();
    seaCreaturePool.addWithCondition(
      new LootEntry(3.0, LootCondition.alwaysTrue(), EntityType.ENDERMITE)
        .withMessage("§b§lYUCK! §7You caught an §5Endermite§7!")
    );
    seaCreaturePool.addWithCondition(
      new LootEntry(0.8, LootCondition.alwaysTrue(), EntityType.SHULKER)
        .withMessage("§d§lRARE CATCH! §7You caught a §5Shulker§7!")
    );

    itemPool = new LootPool();
    itemPool.addWithCondition(
      new LootEntry(50.0, LootCondition.alwaysTrue(), new ItemStack(Material.END_STONE, 3))
    );
    itemPool.addWithCondition(
      new LootEntry(40.0, LootCondition.alwaysTrue(), new ItemStack(Material.CHORUS_FRUIT, 2))
    );
    itemPool.addWithCondition(
      new LootEntry(10.0, LootCondition.alwaysTrue(), new ItemStack(Material.ENDER_PEARL))
    );
  }

  @Override
  public LivingEntity summonSeaCreature(BlightedPlayer player, Location spawnLocation, Vector velocity) {
    LootContext context = new LootContext(
      player,
      player.getPlayer().getLocation().getBlock().getBiome(),
      player.getPlayer().getWorld().getEnvironment()
    );

    return seaCreaturePool.roll(RANDOM, context)
      .map(entry -> entry.spawnCreature(player, spawnLocation, velocity))
      .orElse(null);
  }

  @Override
  public ItemStack getItemDrop(BlightedPlayer player) {
    LootContext context = new LootContext(
      player,
      player.getPlayer().getLocation().getBlock().getBiome(),
      player.getPlayer().getWorld().getEnvironment()
    );

    return itemPool.roll(RANDOM, context)
      .map(entry -> entry.createItem(player))
      .orElse(null);
  }
}