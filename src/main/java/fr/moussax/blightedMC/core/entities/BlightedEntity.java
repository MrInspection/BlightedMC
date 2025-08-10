package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import fr.moussax.blightedMC.core.entities.immunity.EntityImmunityRule;
import fr.moussax.blightedMC.core.entities.immunity.FireImmunityRule;
import fr.moussax.blightedMC.core.entities.immunity.MeleeImmunityRule;
import fr.moussax.blightedMC.core.entities.immunity.ProjectileImmunity;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Supplier;

public abstract class BlightedEntity {
  protected String entityId;
  protected String name;
  protected EntityType entityType;
  protected LivingEntity entity;

  protected int maxHealth;
  protected int damage;
  protected int droppedExp = 0;
  protected int trueDamage = 0;
  protected final Map<Attribute, Double> attributes = new HashMap<>();

  protected ItemStack itemInMainHand;
  protected ItemStack itemInOffHand;
  protected ItemStack[] armor;

  protected LootTable lootTable;
  protected EntityNameTag nameTagType = EntityNameTag.DEFAULT;

  protected BossBar bossBar;
  private final List<EntityImmunityRule> immunityRules = new ArrayList<>();
  private boolean runtimeInitialized = false;
  private final LifecycleTaskManager lifecycleTasks = new LifecycleTaskManager();

  /**
   * Create a new BlightedEntity.
   *
   * @param name       the display name of the entity
   * @param maxHealth  the maximum health of the entity
   * @param entityType the Bukkit entity type
   */
  public BlightedEntity(String name, int maxHealth, EntityType entityType) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.entityType = entityType;
  }

  /**
   * Spawns the entity at a given location with attributes, equipment, and name tags applied.
   *
   * @param location the spawn location
   * @return the spawned LivingEntity
   */
  public LivingEntity spawn(Location location) {
    initImmunityRules();
    entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);

    PersistentDataContainer data = entity.getPersistentDataContainer();
    data.set(
      new NamespacedKey(BlightedMC.getPlugin(BlightedMC.class), "entityId"),
      PersistentDataType.STRING,
      getEntityId()
    );

    setAttribute(Attribute.MAX_HEALTH, maxHealth);
    applyAttributes();

    entity.setHealth(maxHealth);

    applyEquipment();
    updateNameTag();
    entity.setCustomNameVisible(true);
    if (nameTagType == EntityNameTag.BOSS) {
      createBossBar();
    }

    BlightedEntitiesListener.registerEntity(entity, this);
    initRuntime();
    return entity;
  }

  /**
   * Attaches this logical entity to an already existing Bukkit entity that was
   * loaded from disk (e.g., after a server restart) and reinitialized runtime-only
   * systems such as name tags, boss bars, and immunity rules.
   * <p>
   * This does not overwrite current health or equipment to preserve persisted state.
   *
   * @param existing the already spawned/loaded entity
   */
  public void attachToExisting(LivingEntity existing) {
    this.entity = existing;
    initImmunityRules();

    updateNameTag();
    if (nameTagType == EntityNameTag.BOSS) {
      createBossBar();
    }

    // Re-register for damage/loot handling
    BlightedEntitiesListener.registerEntity(existing, this);
    initRuntime();
  }

  /**
   * Initializes runtime-only behaviors once per entity lifecycle instance.
   * Calls subclass hook {@link #onPostAttach()} and, if present, invokes
   * a no-arg method named "startAbility" or "startAbilities" via reflection.
   */
  protected final void initRuntime() {
    if (runtimeInitialized) return;
    runtimeInitialized = true;
    lifecycleTasks.scheduleAll();
  }

  /**
   * Registers a repeating task that is automatically started on spawn/attach
   * and restarted on rehydration. The task will be owned and canceled on kill.
   */
  protected final void addRepeatingTask(
    Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
    lifecycleTasks.addRepeatingTask(factory, delayTicks, periodTicks);
    if (entity != null && !entity.isDead() && runtimeInitialized) lifecycleTasks.scheduleLast();
  }

  /**
   * Registers a one-shot delayed task bound to this entity's lifecycle.
   */
  protected final void addDelayedTask(
    Supplier<BukkitRunnable> factory, long delayTicks) {
    lifecycleTasks.addDelayedTask(factory, delayTicks);
    if (entity != null && !entity.isDead() && runtimeInitialized) lifecycleTasks.scheduleLast();
  }


  /**
   * Instantly kills the entity if it is alive and removes its boss bar.
   */
  public void kill() {
    if (entity == null || entity.isDead()) return;
    removeBossBar();
    lifecycleTasks.cancelAll();
    entity.setHealth(0);
  }

  /**
   * Damages the entity and updates its name tag.
   *
   * @param amount the damage amount
   */
  public void damage(double amount) {
    if (entity == null || entity.isDead()) return;
    entity.damage(amount);
    updateNameTag();
  }

  /**
   * Adds a custom attribute to this entity before spawning.
   *
   * @param attribute the attribute to modify
   * @param value     the base value to set
   */
  public void addAttribute(Attribute attribute, double value) {
    attributes.put(attribute, value);
  }

  /**
   * Drops the assigned loot table at a specific location for a player.
   *
   * @param location the drop location
   * @param player   the player for loot context
   */
  public void dropLoot(Location location, BlightedPlayer player) {
    if (lootTable == null) return;
    lootTable.dropLoot(location, player);
  }

  /**
   * Checks if this entity is immune to a given damage event.
   *
   * @param entity the entity that caused the damage
   * @param event  the damage event
   * @return true if the entity is immune, false otherwise
   */
  public boolean isImmuneTo(LivingEntity entity, EntityDamageEvent event) {
    for (EntityImmunityRule rule : immunityRules) {
      if (rule.isImmune(entity, event)) return true;
    }
    return false;
  }

  /**
   * Updates the entity's custom name and boss bar (if any) to reflect its current health.
   */
  public void updateNameTag() {
    if (entity != null) entity.setCustomName(generateNameTag());
    if (bossBar != null && nameTagType == EntityNameTag.BOSS) {
      bossBar.setTitle(generateNameTag());
      bossBar.setProgress(Math.max(0, Math.min(1, entity.getHealth() / (double) maxHealth)));
    }
  }

  /**
   * Removes the boss bar from all players if present.
   */
  public void removeBossBar() {
    if (bossBar != null) {
      bossBar.removeAll();
      bossBar = null;
    }
  }

  /**
   * Initializes the immunity rules based on the {@link EntityAttributes} annotation.
   */
  protected void initImmunityRules() {
    EntityAttributes attribute = getClass().getAnnotation(EntityAttributes.class);
    if (attribute == null) return;

    for (EntityAttributes.Attributes a : attribute.value()) {
      switch (a) {
        case MELEE_IMMUNITY -> immunityRules.add(new MeleeImmunityRule());
        case FIRE_IMMUNITY -> immunityRules.add(new FireImmunityRule());
        case PROJECTILE_IMMUNITY -> immunityRules.add(new ProjectileImmunity());
        default -> { /* ignore unimplemented attributes for now */ }
      }
    }
  }

  /**
   * Applies all registered attributes to the entity.
   */
  protected void applyAttributes() {
    for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
      AttributeInstance instance = entity.getAttribute(entry.getKey());
      if (instance != null) instance.setBaseValue(entry.getValue());
    }
  }

  /**
   * Equips the entity with the specified armor and hand items.
   * Drop chances are set to 0.
   */
  protected void applyEquipment() {
    if (armor == null && itemInMainHand == null && itemInOffHand == null) return;
    EntityEquipment equipment = entity.getEquipment();
    if (equipment == null) return;

    if (armor != null) equipment.setArmorContents(armor);
    equipment.setItemInMainHand(itemInMainHand);
    equipment.setItemInOffHand(itemInOffHand);

    equipment.setHelmetDropChance(0);
    equipment.setChestplateDropChance(0);
    equipment.setLeggingsDropChance(0);
    equipment.setBootsDropChance(0);
    equipment.setItemInMainHandDropChance(0);
    equipment.setItemInOffHandDropChance(0);
  }

  /**
   * Creates a boss bar for this entity if it does not already exist.
   */
  protected void createBossBar() {
    if (bossBar != null) return;
    bossBar = Bukkit.createBossBar("§d" + getName(), BarColor.PURPLE, BarStyle.SOLID); // Customize color/style here
    bossBar.setProgress(1.0);
    Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
  }

  /**
   * Generates the entity's name tag based on its health and {@link EntityNameTag} type.
   *
   * @return the formatted name tag
   */
  protected String generateNameTag() {
    if (entity == null) return name;
    double health = entity.getHealth();
    double percentage = (health / maxHealth) * 100;

    String colorPrefix = "§a";
    if (percentage < 10) colorPrefix = "§c";
    else if (percentage < 50) colorPrefix = "§e";

    return switch (nameTagType) {
      case HIDDEN -> null;
      case BOSS -> "§5" + name + " " + colorPrefix + toShortNumber(health) + "§5❤";
      case BLIGHTED -> "§5" + name + " §d" + (int) health + "§r/§5" + maxHealth + "§c❤";
      case SMALL_NUMBER -> "§c" + name + " " + colorPrefix + toShortNumber(health) + "§c❤";
      case DEFAULT -> "§c" + name + " " + colorPrefix + (int) health + "§8/§a" + maxHealth + "§c❤";
    };
  }

  /**
   * Converts a large number into a short format (e.g., 1.2K, 3.4M).
   *
   * @param value the number to convert
   * @return the short string representation
   */
  protected String toShortNumber(double value) {
    if (value >= 1_000_000_000) return String.format("%.1fB", value / 1_000_000_000);
    if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
    if (value >= 1_000) return String.format("%.1fK", value / 1_000);
    return String.valueOf((int) value);
  }

  /**
   * @return the underlying Bukkit entity
   */
  public LivingEntity getEntity() {
    return entity;
  }

  /**
   * @return the internal entity ID linked for the {@link EntitiesRegistry} system
   */
  public String getEntityId() {
    return entityId;
  }

  /**
   * @return the display name of the entity
   */
  public String getName() {
    return name;
  }

  /**
   * Sets an attribute on the Bukkit entity directly.
   *
   * @param attribute the attribute
   * @param value     the base value
   */
  private void setAttribute(Attribute attribute, double value) {
    AttributeInstance instance = entity.getAttribute(attribute);
    if (instance != null) instance.setBaseValue(value);
  }

  /**
   * Assigns a loot table to this entity.
   *
   * @param lootTable the loot table
   */
  public void setLootTable(LootTable lootTable) {
    this.lootTable = lootTable;
  }

  /**
   * Sets the name tag display type for this entity.
   *
   * @param nameTagType the name tag type
   */
  public void setNameTagType(EntityNameTag nameTagType) {
    this.nameTagType = nameTagType;
  }

  /**
   * @return the experience dropped on death
   */
  public int getDroppedExp() {
    return droppedExp;
  }

  public int getTrueDamage() {
    return trueDamage;
  }

  /**
   * Sets the experience amount dropped on death.
   *
   * @param droppedExp the experience amount
   */
  public void setDroppedExp(int droppedExp) {
    this.droppedExp = droppedExp;
  }
}
