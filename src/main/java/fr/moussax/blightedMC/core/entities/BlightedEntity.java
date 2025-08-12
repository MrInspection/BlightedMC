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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

public abstract class BlightedEntity implements Cloneable {
  protected String entityId;
  protected String name;
  protected EntityType entityType;
  protected LivingEntity entity;

  protected int maxHealth;
  protected int damage;
  protected int trueDamage = 0;
  protected int defense;
  protected int droppedExp = 0;
  protected Map<Attribute, Double> attributes = new HashMap<>();

  protected ItemStack itemInMainHand;
  protected ItemStack itemInOffHand;
  protected ItemStack[] armor;

  protected LootTable lootTable;
  protected EntityNameTag nameTagType = EntityNameTag.DEFAULT;

  protected BossBar bossBar;
  protected BarColor bossBarColor = BarColor.PURPLE;
  protected BarStyle bossBarStyle = BarStyle.SOLID;

  private static final Map<Entity, EntityAttachment> ENTITY_ATTACHMENTS =
    Collections.synchronizedMap(new WeakHashMap<>());

  public final Set<EntityAttachment> attachments = new HashSet<>();

  private List<EntityImmunityRule> immunityRules = new ArrayList<>();
  private LifecycleTaskManager lifecycleTasks = new LifecycleTaskManager();
  private boolean runtimeInitialized = false;

  public BlightedEntity(String name, int maxHealth, EntityType entityType) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.entityType = entityType;
  }

  public BlightedEntity(String name, int maxHealth, int damage, EntityType entityType) {
    this.name = name;
    this.damage = damage;
    this.maxHealth = maxHealth;
    this.entityType = entityType;
  }

  public BlightedEntity(String name, int maxHealth, int damage, int defense, EntityType entityType) {
    this.name = name;
    this.maxHealth = maxHealth;
    this.damage = damage;
    this.defense = defense;
    this.entityType = entityType;
  }

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
    setAttribute(Attribute.ATTACK_DAMAGE, damage);
    setAttribute(Attribute.ARMOR, defense);
    applyAttributes();

    entity.setHealth(maxHealth);

    applyEquipment();
    updateNameTag();

    if (nameTagType != EntityNameTag.HIDDEN) {
      entity.setCustomNameVisible(true);
    }

    if (nameTagType == EntityNameTag.BOSS) {
      createBossBar();
    }

    BlightedEntitiesListener.registerEntity(entity, this);

    initRuntime();
    return entity;
  }

  public void attachToExisting(LivingEntity existing) {
    this.entity = existing;
    initImmunityRules();

    updateNameTag();
    if (nameTagType == EntityNameTag.BOSS) {
      createBossBar();
    }

    BlightedEntitiesListener.registerEntity(existing, this);

    initRuntime();
  }

  protected final void initRuntime() {
    if (runtimeInitialized) return;
    runtimeInitialized = true;
    lifecycleTasks.scheduleAll();
  }

  public void kill() {
    if (entity == null || entity.isDead()) return;
    removeBossBar();
    removeAttachment();
    lifecycleTasks.cancelAll();
    entity.setHealth(0);
  }

  public void damage(double amount) {
    if (entity == null || entity.isDead()) return;
    entity.damage(amount);
    updateNameTag();
  }

  public void addAttribute(Attribute attribute, double value) {
    attributes.put(attribute, value);
  }

  protected void applyAttributes() {
    for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
      AttributeInstance instance = entity.getAttribute(entry.getKey());
      if (instance != null) instance.setBaseValue(entry.getValue());
    }
  }

  private void setAttribute(Attribute attribute, double value) {
    AttributeInstance instance = entity.getAttribute(attribute);
    if (instance != null) instance.setBaseValue(value);
  }

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

  public boolean isImmuneTo(LivingEntity entity, EntityDamageEvent event) {
    for (EntityImmunityRule rule : immunityRules) {
      if (rule.isImmune(entity, event)) return true;
    }
    return false;
  }

  public void updateNameTag() {
    if (entity != null) entity.setCustomName(generateNameTag());
    if (bossBar != null && nameTagType == EntityNameTag.BOSS) {
      bossBar.setTitle(getBossBarTitle());
      bossBar.setProgress(Math.max(0, Math.min(1, entity.getHealth() / (double) maxHealth)));
    }
  }

  protected String generateNameTag() {
    if (entity == null) return name;
    return nameTagType.format(name, entity.getHealth(), maxHealth);
  }

  protected void createBossBar() {
    if (bossBar != null) return;
    bossBar = Bukkit.createBossBar(getBossBarTitle(), bossBarColor, bossBarStyle);
    bossBar.setProgress(1.0);
    Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
  }

  protected String getBossBarTitle() {
    return "§5" + getName();
  }

  public void setBossBarAppearance(BarColor color, BarStyle style) {
    this.bossBarColor = color;
    this.bossBarStyle = style;
    if (bossBar != null) {
      bossBar.setColor(color);
      bossBar.setStyle(style);
    }
  }

  public void setBossBarColor(BarColor color) {
    setBossBarAppearance(color, this.bossBarStyle);
  }

  public void setBossBarStyle(BarStyle style) {
    setBossBarAppearance(this.bossBarColor, style);
  }

  public void removeBossBar() {
    if (bossBar != null) {
      bossBar.removeAll();
      bossBar = null;
    }
  }

  public void dropLoot(Location location, BlightedPlayer player) {
    if (lootTable == null) return;
    lootTable.dropLoot(location, player);
  }

  protected final void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
    lifecycleTasks.addRepeatingTask(factory, delayTicks, periodTicks);
    if (entity != null && !entity.isDead() && runtimeInitialized) lifecycleTasks.scheduleLast();
  }

  protected final void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
    lifecycleTasks.addDelayedTask(factory, delayTicks);
    if (entity != null && !entity.isDead() && runtimeInitialized) lifecycleTasks.scheduleLast();
  }

  public static void addAttachment(EntityAttachment attachment) {
    if (attachment == null || attachment.entity() == null || attachment.owner() == null) return;

    ENTITY_ATTACHMENTS.put(attachment.entity(), attachment);
    attachment.owner().attachments.add(attachment);

    try {
      Entity e = attachment.entity();
      if (e instanceof LivingEntity living) {
        EntityEquipment eq = living.getEquipment();
        if (eq != null) {
          eq.setHelmetDropChance(0);
          eq.setChestplateDropChance(0);
          eq.setLeggingsDropChance(0);
          eq.setBootsDropChance(0);
          eq.setItemInMainHandDropChance(0);
          eq.setItemInOffHandDropChance(0);
        }
      }
    } catch (Throwable ignored) {
    }
  }

  public static void unregisterAttachment(EntityAttachment attachment) {
    if (attachment == null) return;
    try {
      ENTITY_ATTACHMENTS.remove(attachment.entity());
    } catch (Throwable ignored) {
    }
    try {
      if (attachment.owner() != null) attachment.owner().attachments.remove(attachment);
    } catch (Throwable ignored) {
    }
  }

  private void removeAttachment() {
    List<EntityAttachment> copy = new ArrayList<>(attachments);
    for (EntityAttachment attachment : copy) {
      try {
        Entity entity = attachment.entity();
        if (entity != null && !entity.isDead()) entity.remove();
      } catch (Throwable ignored) {
      }
      try {
        ENTITY_ATTACHMENTS.remove(attachment.entity());
      } catch (Throwable ignored) {
      }
      attachments.remove(attachment);
    }
    attachments.clear();
  }

  public static EntityAttachment getAttachment(Entity entity) {
    return ENTITY_ATTACHMENTS.get(entity);
  }

  public static boolean isAttachment(Entity entity) {
    return ENTITY_ATTACHMENTS.containsKey(entity);
  }

  public void killAllAttachments() {
    for (EntityAttachment attachment : new ArrayList<>(attachments)) {
      Entity entity = attachment.entity();
      if (entity instanceof LivingEntity living && !living.isDead()) {
        try {
          living.setHealth(0);
        } catch (Throwable ignored) {
        }
      }
      ENTITY_ATTACHMENTS.remove(entity);
    }
    attachments.clear();
  }

  public LivingEntity getEntity() {
    return entity;
  }

  public String getEntityId() {
    return entityId;
  }

  public String getName() {
    return name;
  }

  public void setLootTable(LootTable lootTable) {
    this.lootTable = lootTable;
  }

  public void setNameTagType(EntityNameTag nameTagType) {
    this.nameTagType = nameTagType;
  }

  public int getDroppedExp() {
    return droppedExp;
  }

  public void setDroppedExp(int droppedExp) {
    this.droppedExp = droppedExp;
  }

  public int getTrueDamage() {
    return trueDamage;
  }

  public void setDamage(int damage) {
    this.damage = damage;
  }

  public void setDefense(int defense) {
    this.defense = defense;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  @Override
  public BlightedEntity clone() {
    try {
      BlightedEntity clone = (BlightedEntity) super.clone();

      clone.entity = null;
      clone.bossBar = null;
      clone.runtimeInitialized = false;

      clone.attributes = new HashMap<>(this.attributes);
      clone.immunityRules = new ArrayList<>(this.immunityRules);
      clone.lifecycleTasks = new LifecycleTaskManager();

      clone.attachments.clear();

      if (this.armor != null) {
        clone.armor = new ItemStack[this.armor.length];
        for (int i = 0; i < this.armor.length; i++) {
          clone.armor[i] = this.armor[i] != null ? this.armor[i].clone() : null;
        }
      } else {
        clone.armor = null;
      }

      clone.itemInMainHand = this.itemInMainHand != null ? this.itemInMainHand.clone() : null;
      clone.itemInOffHand = this.itemInOffHand != null ? this.itemInOffHand.clone() : null;

      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Failed to clone BlightedEntity", e);
    }
  }
}
