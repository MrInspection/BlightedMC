package fr.moussax.blightedMC.core.fishing.loot;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Represents a single entry in a loot pool.
 * <p>
 * A loot entry can represent an item, a Bukkit entity, or a custom BlightedEntity.
 * Each entry has a weight for random selection, optional conditions for eligibility,
 * and an optional catch message displayed to the player when obtained.
 */
public class LootEntry {
  private final double weight;
  private final LootCondition condition;
  private final ItemStack item;
  private final EntityType entityType;
  private final BlightedEntity blightedEntity;
  private String catchMessage;

  private LootEntry(double weight, LootCondition condition, ItemStack item, EntityType entityType, BlightedEntity blightedEntity) {
    this.weight = weight;
    this.condition = condition != null ? condition : LootCondition.alwaysTrue();
    this.item = item;
    this.entityType = entityType;
    this.blightedEntity = blightedEntity;
  }

  /**
   * Creates a loot entry representing an item with no specific conditions.
   *
   * @param item   the item to give
   * @param weight the weight for random selection
   * @return a new LootEntry
   */
  public static LootEntry item(ItemStack item, double weight) {
    return new LootEntry(weight, null, item, null, null);
  }

  /**
   * Creates a loot entry representing an item with a condition.
   *
   * @param item      the item to give
   * @param weight    the weight for random selection
   * @param condition the condition for this loot entry
   * @return a new LootEntry
   */
  public static LootEntry item(ItemStack item, double weight, LootCondition condition) {
    return new LootEntry(weight, condition, item, null, null);
  }

  /**
   * Creates a loot entry representing a Bukkit entity with no specific condition.
   *
   * @param entityType the type of entity to spawn
   * @param weight     the weight for random selection
   * @return a new LootEntry
   */
  public static LootEntry entity(EntityType entityType, double weight) {
    return new LootEntry(weight, null, null, entityType, null);
  }

  /**
   * Creates a loot entry representing a Bukkit entity with a condition.
   *
   * @param entityType the type of entity to spawn
   * @param weight     the weight for random selection
   * @param condition  the condition for this loot entry
   * @return a new LootEntry
   */
  public static LootEntry entity(EntityType entityType, double weight, LootCondition condition) {
    return new LootEntry(weight, condition, null, entityType, null);
  }

  /**
   * Creates a loot entry representing a custom BlightedEntity with no specific condition.
   *
   * @param blightedEntity the BlightedEntity to spawn
   * @param weight         the weight for random selection
   * @return a new LootEntry
   */
  public static LootEntry blightedEntity(BlightedEntity blightedEntity, double weight) {
    return new LootEntry(weight, null, null, null, blightedEntity);
  }

  /**
   * Creates a loot entry representing a custom BlightedEntity with a condition.
   *
   * @param blightedEntity the BlightedEntity to spawn
   * @param weight         the weight for random selection
   * @param condition      the condition for this loot entry
   * @return a new LootEntry
   */
  public static LootEntry blightedEntity(BlightedEntity blightedEntity, double weight, LootCondition condition) {
    return new LootEntry(weight, condition, null, null, blightedEntity);
  }

  /**
   * Sets a custom message to display to the player when this loot is obtained.
   *
   * @param message the message to display
   * @return this LootEntry for chaining
   */
  public LootEntry withCatchMessage(String message) {
    this.catchMessage = message;
    return this;
  }

  /**
   * Creates the item for the player and sends any catch message.
   *
   * @param player the player receiving the item
   * @return the created ItemStack
   */
  public ItemStack createItem(BlightedPlayer player) {
    if (catchMessage != null) {
      player.getPlayer().sendMessage(catchMessage);
    }
    return item;
  }

  /**
   * Spawns the entity at the given location with the given velocity and sends any catch message.
   *
   * @param player   the player triggering the spawn
   * @param location the location to spawn the entity
   * @param velocity the velocity to apply
   * @return the spawned entity, or null if none
   */
  public LivingEntity spawnEntity(BlightedPlayer player, Location location, Vector velocity) {
    LivingEntity spawned = null;

    if (entityType != null) {
      spawned = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location.add(velocity), entityType);
      spawned.setVelocity(velocity);
    } else if (blightedEntity != null) {
      spawned = blightedEntity.clone().spawn(location.add(velocity));
      spawned.setVelocity(velocity);
    }

    if (spawned != null && catchMessage != null) {
      player.getPlayer().sendMessage(catchMessage);
    }

    return spawned;
  }

  /**
   * Checks whether this loot entry meets the given context conditions.
   *
   * @param context the loot context to test
   * @return true if the entry is eligible
   */
  public boolean meetsCondition(LootContext context) {
    return condition.test(context);
  }

  /** @return true if this entry represents an item */
  public boolean isItem() {
    return item != null;
  }

  /** @return true if this entry represents an entity (Bukkit or Blighted) */
  public boolean isEntity() {
    return entityType != null || blightedEntity != null;
  }

  /** @return the weight of this entry for random selection */
  public double getWeight() {
    return weight;
  }

  /** @return the item associated with this entry, if any */
  public ItemStack getItem() {
    return item;
  }

  /** @return the Bukkit entity type associated with this entry, if any */
  public EntityType getEntityType() {
    return entityType;
  }

  /** @return the BlightedEntity associated with this entry, if any */
  public BlightedEntity getBlightedEntity() {
    return blightedEntity;
  }

  /** @return the catch message associated with this entry, if any */
  public String getCatchMessage() {
    return catchMessage;
  }
}
