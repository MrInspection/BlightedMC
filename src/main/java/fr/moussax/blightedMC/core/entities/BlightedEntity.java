package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
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
  private static final String ENTITY_ID_KEY = "entityId";
  private static final Map<Entity, EntityAttachment> ENTITY_ATTACHMENTS =
    Collections.synchronizedMap(new WeakHashMap<>());

  protected String entityId;
  protected String name;
  protected EntityType entityType;
  protected LivingEntity entity;

  protected int maxHealth;
  protected int damage;
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

  public final Set<EntityAttachment> attachments = new HashSet<>();

  private final List<EntityImmunityRule> immunityRules = new ArrayList<>();
  private final LifecycleTaskManager lifecycleTasks = new LifecycleTaskManager();
  private boolean runtimeInitialized = false;

  public BlightedEntity(String name, int maxHealth, EntityType entityType) {
    this(name, maxHealth, 1, 0, entityType);
  }

  public BlightedEntity(String name, int maxHealth, int damage, EntityType entityType) {
    this(name, maxHealth, damage, 0, entityType);
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

    persistEntityId();
    configureAttributes();
    configureEquipment();
    configureDisplay();

    BlightedEntitiesListener.registerEntity(entity, this);
    initRuntime();

    return entity;
  }

  public void attachToExisting(LivingEntity existing) {
    this.entity = existing;
    initImmunityRules();
    configureDisplay();
    BlightedEntitiesListener.registerEntity(existing, this);
    initRuntime();
  }

  private void persistEntityId() {
    PersistentDataContainer data = entity.getPersistentDataContainer();
    NamespacedKey key = new NamespacedKey(BlightedMC.getInstance(), ENTITY_ID_KEY);
    data.set(key, PersistentDataType.STRING, getEntityId());
  }

  private void configureAttributes() {
    setAttribute(Attribute.MAX_HEALTH, maxHealth);
    setAttribute(Attribute.ATTACK_DAMAGE, damage);
    setAttribute(Attribute.ARMOR, defense);
    applyAttributes();
    entity.setHealth(maxHealth);
    entity.setPersistent(true);
  }

  private void configureEquipment() {
    applyEquipment();
  }

  private void configureDisplay() {
    updateNameTag();
    if (nameTagType != EntityNameTag.HIDDEN) {
      entity.setCustomNameVisible(true);
    }
    if (nameTagType == EntityNameTag.BOSS) {
      createBossBar();
    }
  }

  protected final void initRuntime() {
    if (runtimeInitialized) return;
    runtimeInitialized = true;
    lifecycleTasks.scheduleAll();
  }

  public void kill() {
    if (isNotAlive()) return;
    removeBossBar();
    removeAllAttachments();
    lifecycleTasks.cancelAll();
    entity.setHealth(0);
  }

  public void damage(double amount) {
    if (isNotAlive()) return;
    entity.damage(amount);
    updateNameTag();
  }

  public void addAttribute(Attribute attribute, double value) {
    attributes.put(attribute, value);
  }

  protected void applyAttributes() {
    for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
      setAttribute(entry.getKey(), entry.getValue());
    }
  }

  private void setAttribute(Attribute attribute, double value) {
    AttributeInstance instance = entity.getAttribute(attribute);
    if (instance != null) {
      instance.setBaseValue(value);
    }
  }

  protected void applyEquipment() {
    if (armor == null && itemInMainHand == null && itemInOffHand == null) return;

    EntityEquipment equipment = entity.getEquipment();
    if (equipment == null) return;

    if (armor != null) {
      equipment.setArmorContents(armor);
    }
    equipment.setItemInMainHand(itemInMainHand);
    equipment.setItemInOffHand(itemInOffHand);

    disableEquipmentDrops(equipment);
  }

  private void disableEquipmentDrops(EntityEquipment equipment) {
    equipment.setHelmetDropChance(0);
    equipment.setChestplateDropChance(0);
    equipment.setLeggingsDropChance(0);
    equipment.setBootsDropChance(0);
    equipment.setItemInMainHandDropChance(0);
    equipment.setItemInOffHandDropChance(0);
  }

  protected void initImmunityRules() {
    EntityImmunities attribute = getClass().getAnnotation(EntityImmunities.class);
    if (attribute == null) return;

    for (EntityImmunities.ImmunityType type : attribute.value()) {
      switch (type) {
        case MELEE -> immunityRules.add(new MeleeImmunityRule());
        case FIRE -> immunityRules.add(new FireImmunityRule());
        case PROJECTILE -> immunityRules.add(new ProjectileImmunity());
      }
    }
  }

  public boolean isImmuneTo(LivingEntity entity, EntityDamageEvent event) {
    for (EntityImmunityRule rule : immunityRules) {
      if (rule.isImmune(entity, event)) {
        return true;
      }
    }
    return false;
  }

  public void updateNameTag() {
    if (entity != null) {
      entity.setCustomName(createNameTag());
    }
    updateBossBar();
  }

  private void updateBossBar() {
    if (bossBar == null || nameTagType != EntityNameTag.BOSS) return;

    bossBar.setTitle(getBossBarTitle());
    double progress = Math.max(0, Math.min(1, entity.getHealth() / (double) maxHealth));
    bossBar.setProgress(progress);
  }

  protected String createNameTag() {
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
    if (bossBar == null) return;

    bossBar.removeAll();
    bossBar = null;
  }

  public void dropLoot(Location location, BlightedPlayer player) {
    if (lootTable == null) return;
    lootTable.dropLoot(location, player);
  }

  protected final void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
    lifecycleTasks.addRepeatingTask(factory, delayTicks, periodTicks);
    if (canScheduleTask()) {
      lifecycleTasks.scheduleLast();
    }
  }

  protected final void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
    lifecycleTasks.addDelayedTask(factory, delayTicks);
    if (canScheduleTask()) {
      lifecycleTasks.scheduleLast();
    }
  }

  private boolean canScheduleTask() {
    return entity != null && !entity.isDead() && runtimeInitialized;
  }

  public static void addAttachment(EntityAttachment attachment) {
    if (attachment == null || attachment.entity() == null || attachment.owner() == null) return;

    ENTITY_ATTACHMENTS.put(attachment.entity(), attachment);
    attachment.owner().attachments.add(attachment);

    if (attachment.entity() instanceof LivingEntity living) {
      disableAttachmentEquipmentDrops(living);
    }
  }

  private static void disableAttachmentEquipmentDrops(LivingEntity living) {
    EntityEquipment equipment = living.getEquipment();
    if (equipment == null) return;

    equipment.setHelmetDropChance(0);
    equipment.setChestplateDropChance(0);
    equipment.setLeggingsDropChance(0);
    equipment.setBootsDropChance(0);
    equipment.setItemInMainHandDropChance(0);
    equipment.setItemInOffHandDropChance(0);
  }

  public static void unregisterAttachment(EntityAttachment attachment) {
    if (attachment == null) return;

    ENTITY_ATTACHMENTS.remove(attachment.entity());
    if (attachment.owner() != null) {
      attachment.owner().attachments.remove(attachment);
    }
  }

  private void removeAllAttachments() {
    for (EntityAttachment attachment : new ArrayList<>(attachments)) {
      Entity attachmentEntity = attachment.entity();
      if (attachmentEntity != null && !attachmentEntity.isDead()) {
        attachmentEntity.remove();
      }
      ENTITY_ATTACHMENTS.remove(attachmentEntity);
    }
    attachments.clear();
  }

  public void killAllAttachments() {
    for (EntityAttachment attachment : new ArrayList<>(attachments)) {
      Entity attachmentEntity = attachment.entity();
      if (attachmentEntity instanceof LivingEntity living && !living.isDead()) {
        living.setHealth(0);
      }
      ENTITY_ATTACHMENTS.remove(attachmentEntity);
    }
    attachments.clear();
  }

  public static EntityAttachment getAttachment(Entity entity) {
    return ENTITY_ATTACHMENTS.get(entity);
  }

  public static boolean isAttachment(Entity entity) {
    return ENTITY_ATTACHMENTS.containsKey(entity);
  }

  protected boolean isNotAlive() {
    return entity == null || entity.isDead();
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
      clone.attachments.clear();

      clone.armor = cloneArmor();
      clone.itemInMainHand = cloneItem(this.itemInMainHand);
      clone.itemInOffHand = cloneItem(this.itemInOffHand);

      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Failed to clone BlightedEntity", e);
    }
  }

  private ItemStack[] cloneArmor() {
    if (this.armor == null) return null;

    ItemStack[] clonedArmor = new ItemStack[this.armor.length];
    for (int i = 0; i < this.armor.length; i++) {
      clonedArmor[i] = cloneItem(this.armor[i]);
    }
    return clonedArmor;
  }

  private ItemStack cloneItem(ItemStack item) {
    return item != null ? item.clone() : null;
  }
}
