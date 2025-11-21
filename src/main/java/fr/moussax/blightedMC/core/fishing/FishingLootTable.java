package fr.moussax.blightedMC.core.fishing;

import fr.moussax.blightedMC.core.fishing.loot.LootContext;
import fr.moussax.blightedMC.core.fishing.loot.LootEntry;
import fr.moussax.blightedMC.core.fishing.loot.LootPool;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Random;

/**
 * Represents a fishing loot table containing pools for entities and items.
 * <p>
 * Provides methods to roll for entities or items based on a player's context.
 * Can be constructed using the {@link Builder} pattern.
 */
public class FishingLootTable {
    private final LootPool entityPool;
    private final LootPool itemPool;
    private final Random randomizer = new Random();

    /**
     * Constructs a new FishingLootTable with specified entity and item pools.
     *
     * @param entityPool the pool of entities that can be spawned
     * @param itemPool   the pool of items that can be dropped
     */
    public FishingLootTable(LootPool entityPool, LootPool itemPool) {
        this.entityPool = entityPool;
        this.itemPool = itemPool;
    }

    /**
     * Rolls the entity pool to determine which entity, if any, should spawn.
     *
     * @param player   the player performing the fishing action
     * @param location the location to spawn the entity
     * @param velocity the velocity vector to apply to the spawned entity
     * @return the spawned entity, or {@code null} if no entity is rolled
     */
    public LivingEntity rollEntity(BlightedPlayer player, Location location, Vector velocity) {
        LootContext context = createContext(player);

        Optional<LootEntry> entry = entityPool.roll(randomizer, context);
        return entry.map(lootEntry -> lootEntry.spawnEntity(player, location, velocity)).orElse(null);
    }

    /**
     * Rolls the item pool to determine which item, if any, should be given.
     *
     * @param player the player performing the fishing action
     * @return the rolled item, or {@code null} if no item is rolled
     */
    public ItemStack rollItem(BlightedPlayer player) {
        LootContext context = createContext(player);

        Optional<LootEntry> entry = itemPool.roll(randomizer, context);
        return entry.map(lootEntry -> lootEntry.createItem(player)).orElse(null);
    }

    /**
     * Creates a new loot context for the specified player.
     *
     * @param player the player for which to create the context
     * @return a new LootContext
     */
    private LootContext createContext(BlightedPlayer player) {
        return new LootContext(
                player,
                player.getPlayer().getLocation().getBlock().getBiome(),
                player.getPlayer().getWorld().getEnvironment()
        );
    }

    /**
     * Returns a new {@link Builder} for constructing a FishingLootTable.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating {@link FishingLootTable} instances.
     * <p>
     * Supports adding entity and item entries, setting roll chances, and constructing the table.
     */
    public static class Builder {

        private LootPool entityPool = new LootPool();
        private LootPool itemPool = new LootPool();

        /**
         * Sets the entity pool for the table.
         */
        public Builder entityPool(LootPool pool) {
            this.entityPool = pool;
            return this;
        }

        /**
         * Sets the item pool for the table.
         */
        public Builder itemPool(LootPool pool) {
            this.itemPool = pool;
            return this;
        }

        /**
         * Adds a single entity entry to the entity pool.
         */
        public Builder addEntity(LootEntry entry) {
            this.entityPool.add(entry);
            return this;
        }

        /**
         * Adds multiple entity entries to the entity pool.
         */
        public Builder addEntities(LootEntry... entries) {
            this.entityPool.add(entries);
            return this;
        }

        /**
         * Adds a single item entry to the item pool.
         */
        public Builder addItem(LootEntry entry) {
            this.itemPool.add(entry);
            return this;
        }

        /**
         * Adds multiple item entries to the item pool.
         */
        public Builder addItems(LootEntry... entries) {
            this.itemPool.add(entries);
            return this;
        }

        /**
         * Sets the roll chance for the entity pool.
         */
        public Builder setEntityRollChance(double chance) {
            this.entityPool.setRollChance(chance);
            return this;
        }

        /**
         * Sets the roll chance for the item pool.
         */
        public Builder setItemRollChance(double chance) {
            this.itemPool.setRollChance(chance);
            return this;
        }

        /**
         * Builds and returns the {@link FishingLootTable} instance.
         */
        public FishingLootTable build() {
            return new FishingLootTable(entityPool, itemPool);
        }
    }
}
