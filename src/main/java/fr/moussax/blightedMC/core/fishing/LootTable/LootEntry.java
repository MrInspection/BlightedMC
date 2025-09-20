package fr.moussax.blightedMC.core.fishing.LootTable;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public class LootEntry {

  private final double weight;
  private final LootCondition condition;
  private String message;
  private final ItemStack item;
  private final EntityType entityType;
  private final BlightedEntity blightedEntity;

  // Item constructor
  public LootEntry(double weight, LootCondition condition, ItemStack item) {
    this.weight = weight;
    this.condition = condition != null ? condition : LootCondition.alwaysTrue();
    this.item = item;
    this.entityType = null;
    this.blightedEntity = null;
  }

  // Vanilla entity constructor
  public LootEntry(double weight, LootCondition condition, EntityType entityType) {
    this.weight = weight;
    this.condition = condition != null ? condition : LootCondition.alwaysTrue();
    this.item = null;
    this.entityType = entityType;
    this.blightedEntity = null;
  }

  // BlightedEntity constructor
  public LootEntry(double weight, LootCondition condition, BlightedEntity blightedEntity) {
    this.weight = weight;
    this.condition = condition != null ? condition : LootCondition.alwaysTrue();
    this.item = null;
    this.entityType = null;
    this.blightedEntity = blightedEntity;
  }

  public LootEntry withMessage(String message) {
    this.message = message;
    return this;
  }

  public ItemStack createItem(BlightedPlayer player) {
    if (message != null) player.getPlayer().sendMessage(message);
    return item;
  }

  public LivingEntity spawnCreature(BlightedPlayer player, Location location, Vector vector) {
    LivingEntity spawned = null;
    if (entityType != null) {
      spawned = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location.add(vector), entityType);
      spawned.setVelocity(vector);
    } else if (blightedEntity != null) {
      spawned = blightedEntity.clone().spawn(location.add(vector));
      spawned.setVelocity(vector);
    }
    if (spawned != null && message != null) player.getPlayer().sendMessage(message);
    return spawned;
  }

  public boolean isValid(LootContext ctx) {
    return condition.test(ctx);
  }

  public String getMessage() {
    return message;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public BlightedEntity getBlightedEntity() {
    return blightedEntity;
  }

  public ItemStack getItem() {
    return item;
  }

  public double getWeight() {
    return weight;
  }
}
