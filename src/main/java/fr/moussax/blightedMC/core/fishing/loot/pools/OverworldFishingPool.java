package fr.moussax.blightedMC.core.fishing.loot.pools;

import fr.moussax.blightedMC.core.fishing.loot.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.gameplay.entities.Dummy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class OverworldFishingPool implements FishingLootTable {
  private static final Random RANDOM = new Random();
  private final LootPool seaCreaturePool;
  private final LootPool itemPool;

  public OverworldFishingPool() {
    seaCreaturePool = new LootPool();
    seaCreaturePool.addWithCondition(
      new LootEntry(2.0, LootCondition.alwaysTrue(), new Dummy())
        .withMessage("§b§l NICE CATCH! §7You caught a §3Dummy§7!")
    );
    seaCreaturePool.addWithCondition(
      new LootEntry(1.0, LootCondition.biome(Biome.DEEP_OCEAN), EntityType.DROWNED)
        .withMessage("§d§l RARE CATCH! §7You caught a §bDrowned§7!")
    );
    seaCreaturePool.addWithCondition(
      new LootEntry(0.5, LootCondition.biome(Biome.DEEP_OCEAN), EntityType.GUARDIAN)
        .withMessage("§6§l EPIC CATCH! §7You caught a §2Guardian§7!")
    );

    itemPool = new LootPool();
    itemPool.addWithCondition(
      new LootEntry(40.0, LootCondition.alwaysTrue(), new ItemStack(Material.IRON_INGOT))
    );
    itemPool.addWithCondition(
      new LootEntry(30.0, LootCondition.alwaysTrue(), new ItemStack(Material.GOLD_INGOT))
    );
    itemPool.addWithCondition(
      new LootEntry(15.0, LootCondition.alwaysTrue(), new ItemStack(Material.SPYGLASS))
    );
    itemPool.addWithCondition(
      new LootEntry(10.0, LootCondition.alwaysTrue(), new ItemStack(Material.SPONGE))
    );
    itemPool.addWithCondition(
      new LootEntry(5.0, LootCondition.alwaysTrue(), new ItemStack(Material.SOUL_CAMPFIRE, 3))
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
