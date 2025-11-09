package fr.moussax.blightedMC.core.fishing;

import fr.moussax.blightedMC.core.fishing.loot.LootContext;
import fr.moussax.blightedMC.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.core.fishing.loot.LootPool;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Random;

public class FishingSystem {
  private final LootPool seaCreaturePool;
  private final LootPool itemPool;
  private final Random randomizer = new Random();

  public FishingSystem(LootPool seaCreaturePool, LootPool itemPool) {
    this.seaCreaturePool = seaCreaturePool;
    this.itemPool = itemPool;
  }

  public LivingEntity trySummonSeaCreature(BlightedPlayer player, Location location, Vector vector, LootContext ctx) {
    Optional<LootEntry> entry = seaCreaturePool.roll(randomizer, ctx);
    return entry.map(e -> e.spawnCreature(player, location, vector)).orElse(null);
  }

  public ItemStack tryGetItemDrop(BlightedPlayer player, LootContext ctx) {
    Optional<LootEntry> entry = itemPool.roll(randomizer, ctx);
    return entry.map(e -> e.createItem(player)).orElse(null);
  }
}
