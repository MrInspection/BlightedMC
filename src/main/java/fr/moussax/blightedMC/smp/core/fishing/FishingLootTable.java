package fr.moussax.blightedMC.smp.core.fishing;

import fr.moussax.blightedMC.smp.core.fishing.loot.LootContext;
import fr.moussax.blightedMC.smp.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.fishing.loot.LootPool;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public class FishingLootTable {
    private final LootPool entityPool;
    private final LootPool itemPool;
    private final double entityRollChance;

    private FishingLootTable(LootPool entityPool, LootPool itemPool, double entityRollChance) {
        this.entityPool = entityPool;
        this.itemPool = itemPool;
        this.entityRollChance = entityRollChance;
    }

    public LivingEntity rollEntity(BlightedPlayer player, Location location, Vector velocity) {
        if (entityPool.isEmpty()) return null;

        World world = location.getWorld();
        Biome biome = Objects.requireNonNull(world).getBiome(location);
        LootContext context = new LootContext(player, biome, world.getEnvironment());

        return entityPool.roll(context)
            .filter(LootEntry::isEntity)
            .map(entry -> entry.spawnEntity(player, location, velocity))
            .orElse(null);
    }

    public ItemStack rollItem(BlightedPlayer player) {
        if (itemPool.isEmpty()) return null;

        Location location = player.getPlayer().getLocation();
        World world = location.getWorld();
        Biome biome = Objects.requireNonNull(world).getBiome(location);
        LootContext context = new LootContext(player, biome, world.getEnvironment());

        return itemPool.roll(context)
            .filter(LootEntry::isItem)
            .map(entry -> entry.createItem(player))
            .orElse(null);
    }

    public double getEntityRollChance() {
        return entityRollChance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LootPool entityPool = new LootPool();
        private final LootPool itemPool = new LootPool();
        private double entityRollChance = 0.15;

        public Builder setEntityRollChance(double chance) {
            this.entityRollChance = Math.max(0.0, Math.min(1.0, chance));
            return this;
        }

        public Builder addEntity(LootEntry entry) {
            entityPool.add(entry);
            return this;
        }

        public Builder addEntities(LootEntry... entries) {
            entityPool.add(entries);
            return this;
        }

        public Builder addItem(LootEntry entry) {
            itemPool.add(entry);
            return this;
        }

        public Builder addItems(LootEntry... entries) {
            itemPool.add(entries);
            return this;
        }

        public FishingLootTable build() {
            entityPool.setRollChance(entityRollChance);
            return new FishingLootTable(entityPool, itemPool, entityRollChance);
        }
    }
}
