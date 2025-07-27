package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class BlightedEntity {
  protected String name;
  protected int maxHealth;
  protected int level = -1;

  protected ItemStack itemInMainHand;
  protected ItemStack itemInOffHand;
  protected ItemStack[] armor;

  protected EntityType entityType;
  protected LivingEntity entity;
  protected LootTable lootTable;
  protected EntityNameTag nameTagType = EntityNameTag.DEFAULT;

  protected final Map<Attribute, Double> attributes = new HashMap<>();

  public BlightedEntity(String name, int maxHealth, EntityType entityType) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.entityType = entityType;
  }

  public BlightedEntity(String name, int maxHealth, double baseDamage, double speed, double defense, double scale, int level, ItemStack itemInMainHand, ItemStack itemInOffHand, ItemStack[] armor, EntityType entityType, LivingEntity entity, LootTable lootTable, EntityNameTag nameTagType) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.level = level;
    this.itemInMainHand = itemInMainHand;
    this.itemInOffHand = itemInOffHand;
    this.armor = armor;
    this.entityType = entityType;
    this.entity = entity;
    this.lootTable = lootTable;
    this.nameTagType = nameTagType;
  }

  public void addAttribute(Attribute attribute, double value) {
    attributes.put(attribute, value);
  }

  public LivingEntity spawn(Location location) {
    entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);

    setAttribute(Attribute.MAX_HEALTH, maxHealth);
    applyAttributes();

    entity.setHealth(maxHealth);

    applyEquipment();

    updateNameTag();
    entity.setCustomNameVisible(true);

    BlightedEntitiesListener.registerEntity(entity, this);
    return entity;
  }

  private void setAttribute(Attribute attribute, double value) {
    AttributeInstance instance = entity.getAttribute(attribute);
    if (instance != null) {
      instance.setBaseValue(value);
    }
  }

  public void kill() {
    if (entity != null && !entity.isDead()) {
      entity.setHealth(0);
    }
  }

  public void damage(double amount) {
    if (entity == null || entity.isDead()) return;
    entity.damage(amount);
    updateNameTag();
  }

  public void dropLoot(Location location) {
    if (lootTable == null) return;
    lootTable.dropLootItem(location, null);
    lootTable.dropFromLootPool(location, null);
  }

  public void updateNameTag() {
    if (entity != null) entity.setCustomName(generateNameTag());
  }

  protected String generateNameTag() {
    double health = entity.getHealth();
    double percentage = (health / maxHealth) * 100;

    String colorPrefix = "§a";

    if (percentage < 10) {
      colorPrefix = "§c";
    } else if (percentage < 50) {
      colorPrefix = "§e";
    }

    return switch (nameTagType) {
      case BOSS -> "§d" + name;
      case BLIGHTED -> "§5" + name + " §d" + (int) health +"§r/§5" + maxHealth + "§c❤";
      case SMALL_NUMBER -> "§c" + name + " " + colorPrefix + toShortNumber(health) + "§c❤";
      case DEFAULT -> "§c" + name + " " + colorPrefix + (int) health + "§8/§a" + maxHealth + "§c❤";
    };
  }

  protected void applyAttributes() {
    for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
      AttributeInstance instance = entity.getAttribute(entry.getKey());
      if (instance != null) {
        instance.setBaseValue(entry.getValue());
      }
    }
  }

  protected void applyEquipment() {
    if (armor == null) return;
    EntityEquipment equipment = entity.getEquipment();
    if (equipment == null) return;

    equipment.setArmorContents(armor);
    equipment.setItemInMainHand(itemInMainHand);
    equipment.setItemInOffHand(itemInOffHand);

    equipment.setHelmetDropChance(0);
    equipment.setChestplateDropChance(0);
    equipment.setLeggingsDropChance(0);
    equipment.setBootsDropChance(0);
    equipment.setItemInMainHandDropChance(0);
    equipment.setItemInOffHandDropChance(0);
  }

  protected String toShortNumber(double value) {
    if (value >= 1_000_000_000) return String.format("%.1fB", value / 1_000_000_000);
    if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
    if (value >= 1_000) return String.format("%.1fK", value / 1_000);
    return String.valueOf((int) value);
  }

  public LivingEntity getEntity() {
    return entity;
  }

  public void setLootTable(LootTable lootTable) {
    this.lootTable = lootTable;
  }

  public void setNameTagType(EntityNameTag nameTagType) {
    this.nameTagType = nameTagType;
  }
}
