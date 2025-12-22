package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.core.entities.immunity.FireImmunity;
import fr.moussax.blightedMC.core.entities.immunity.MeleeImmunity;
import fr.moussax.blightedMC.core.entities.immunity.ProjectileImmunity;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Supplier;

/**
 * Base abstraction for plugin-managed entities.
 * <p>
 * Encapsulates configuration and runtime behavior for custom entities:
 * identity, attributes, equipment, display (name tag / boss bar), loot,
 * immunities, lifecycle tasks, and attachments.
 *
 * <p>Typical usage:
 * <pre>{@code
 * public class BlightedZombie extends AbstractBlightedEntity {
 *     public BlightedZombie() {
 *         super("Blighted Zombie", 40, EntityType.ZOMBIE);
 *         setDamage(6);
 *         setDefense(2);
 *         setLootTable(new ZombieLootTable());
 *     }
 * }
 *
 * BlightedEntity zombie = new BlightedZombie();
 * zombie.spawn(location);
 * }</pre>
 */
public abstract class AbstractBlightedEntity implements Cloneable {
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
    private final List<EntityImmunity> immunities = new ArrayList<>();
    private final LifecycleTaskManager lifecycleTasks = new LifecycleTaskManager();

    /**
     * Marks whether runtime scheduling / initialization has been executed.
     */
    private boolean runtimeInitialized = false;

    /**
     * Constructs a basic BlightedEntity with name, health, and type.
     *
     * @param name       the display name
     * @param maxHealth  maximum health value
     * @param entityType underlying Bukkit entity type
     */
    public AbstractBlightedEntity(@NonNull String name, int maxHealth, EntityType entityType) {
        this(name, maxHealth, 1, 0, entityType);
    }

    /**
     * Constructs a BlightedEntity with specified damage.
     *
     * @param name       the display name
     * @param maxHealth  maximum health value
     * @param damage     base damage value
     * @param entityType underlying Bukkit entity type
     */
    public AbstractBlightedEntity(String name, int maxHealth, int damage, EntityType entityType) {
        this(name, maxHealth, damage, 0, entityType);
    }

    /**
     * Full constructor for BlightedEntity.
     *
     * @param name       the display name
     * @param maxHealth  maximum health
     * @param damage     base damage
     * @param defense    base defense/armor
     * @param entityType underlying Bukkit entity type
     */
    public AbstractBlightedEntity(String name, int maxHealth, int damage, int defense, EntityType entityType) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.damage = damage;
        this.defense = defense;
        this.entityType = entityType;
    }

    /**
     * Spawns the configured entity at the specified location.
     * <p>
     * The method initializes immunity rules, creates the entity, persists the identifier,
     * applies attributes/equipment/display, and registers listeners and runtime tasks.
     *
     * @param location the spawn location
     * @return the spawned {@link LivingEntity}
     */
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

    /**
     * Attaches this BlightedEntity wrapper to an existing {@link LivingEntity}.
     * <p>
     * Useful when converting pre-existing Minecraft entities into managed BlightedEntities.
     *
     * @param existing the existing living entity
     */
    public void attachToExisting(LivingEntity existing) {
        this.entity = existing;
        initImmunityRules();
        configureDisplay();
        BlightedEntitiesListener.registerEntity(existing, this);
        initRuntime();
    }

    /**
     * Persists the configured entity id into the entity's {@link PersistentDataContainer}.
     * Uses plugin NamespacedKey({@code "entityId"}).
     */
    private void persistEntityId() {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(BlightedMC.getInstance(), ENTITY_ID_KEY);
        data.set(key, PersistentDataType.STRING, getEntityId());
    }

    /**
     * Applies configured attributes to the spawned entity and sets initial health.
     * <p>
     * Sets MAX_HEALTH, ATTACK_DAMAGE, and ARMOR attributes and marks entity persistent.
     */
    private void configureAttributes() {
        setAttribute(Attribute.MAX_HEALTH, maxHealth);
        setAttribute(Attribute.ATTACK_DAMAGE, damage);
        setAttribute(Attribute.ARMOR, defense);
        applyAttributes();
        entity.setHealth(maxHealth);

        entity.setPersistent(true);
        entity.setRemoveWhenFarAway(false);
    }

    /**
     * Applies equipment configuration to the spawned entity.
     * Delegates to {@link #applyEquipment()}.
     */
    private void configureEquipment() {
        applyEquipment();
    }

    /**
     * Configures visual display: name tag visibility and boss bar creation when required.
     */
    private void configureDisplay() {
        updateNameTag();
        if (nameTagType != EntityNameTag.HIDDEN) {
            entity.setCustomNameVisible(true);
        }
        if (nameTagType == EntityNameTag.BOSS) {
            createBossBar();
        }
    }

    /**
     * Initializes runtime scheduling tasks if not already initialized.
     * Safe to call multiple times; scheduling occurs once.
     */
    protected final void initRuntime() {
        if (runtimeInitialized) return;
        runtimeInitialized = true;
        lifecycleTasks.scheduleAll();
    }

    /**
     * Immediately kills the underlying entity and cancels lifecycle tasks.
     * <p>
     * Removes boss bar and attachments before setting health to zero.
     */
    public void kill() {
        if (isNotAlive()) return;
        removeBossBar();
        removeAllAttachments();
        lifecycleTasks.cancelAll();
        entity.setHealth(0);
    }

    /**
     * Called when this entity dies.
     * <p>
     * Intended for subclass-specific death behavior.
     * The default implementation does nothing.
     *
     * @param location death location
     */
    public void onDeath(Location location) {
        // Default implementation does nothing
    }

    /**
     * Applies damage to the underlying entity and updates the display.
     *
     * @param amount amount of damage to apply
     */
    public void damage(double amount) {
        if (isNotAlive()) return;
        entity.damage(amount);
        updateNameTag();
    }

    /**
     * Adds or updates an attribute value to be applied on spawn or when reconfigured.
     *
     * @param attribute the attribute to set
     * @param value     base value for the attribute
     */
    public void addAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }

    /**
     * Applies all configured attributes to the entity.
     * Iterates {@link #attributes} and sets each attribute using {@link #setAttribute(Attribute, double)}.
     */
    protected void applyAttributes() {
        for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
            setAttribute(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Safely sets a single attribute on the runtime entity if available.
     *
     * @param attribute the attribute to set
     * @param value     the base value to assign
     */
    private void setAttribute(Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    /**
     * Applies equipment (armor/main/offhand) to the spawned entity and disables drop chances.
     * If no equipment is configured, this method returns immediately.
     */
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

    /**
     * Disables item drop chances for all equipment slots on the provided equipment instance.
     *
     * @param equipment the entity equipment to modify
     */
    private void disableEquipmentDrops(EntityEquipment equipment) {
        equipment.setHelmetDropChance(0);
        equipment.setChestplateDropChance(0);
        equipment.setLeggingsDropChance(0);
        equipment.setBootsDropChance(0);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHandDropChance(0);
    }

    /**
     * Initializes immunity rules for this entity based on the {@link EntityImmunities} annotation.
     * <p>
     * Converts declared immunity types into concrete {@link EntityImmunity} instances.
     */
    protected void initImmunityRules() {
        EntityImmunities attribute = getClass().getAnnotation(EntityImmunities.class);
        if (attribute == null) return;

        for (EntityImmunities.ImmunityType type : attribute.value()) {
            switch (type) {
                case MELEE -> immunities.add(new MeleeImmunity());
                case FIRE -> immunities.add(new FireImmunity());
                case PROJECTILE -> immunities.add(new ProjectileImmunity());
            }
        }
    }

    /**
     * Evaluates whether an incoming damage event should be ignored according to configured immunity rules.
     *
     * @param entity the attacking entity
     * @param event  the damage event
     * @return {@code true} if the damage should be ignored (entity is immune), {@code false} otherwise
     */
    public boolean isImmuneTo(LivingEntity entity, EntityDamageEvent event) {
        for (EntityImmunity rule : immunities) {
            if (rule.isImmune(entity, event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the custom name tag of the entity and its boss bar progress/title.
     * Safe to call when an entity is null (no-op).
     */
    public void updateNameTag() {
        if (entity != null) {
            entity.setCustomName(createNameTag());
        }
        updateBossBar();
    }

    /**
     * Updates boss bar title and progress based on current entity health and configured max health.
     */
    private void updateBossBar() {
        if (bossBar == null || nameTagType != EntityNameTag.BOSS) return;

        bossBar.setTitle(getBossBarTitle());
        double progress = Math.max(0, Math.min(1, entity.getHealth() / (double) maxHealth));
        bossBar.setProgress(progress);
    }

    /**
     * Builds the formatted name tag string for this entity.
     *
     * @return formatted name string (may include health info)
     */
    protected String createNameTag() {
        if (entity == null) return name;
        return nameTagType.format(name, entity.getHealth(), maxHealth);
    }

    /**
     * Creates and registers a {@link BossBar} for this entity if not already created.
     * Adds all online players to the bar.
     */
    protected void createBossBar() {
        if (bossBar != null) return;

        bossBar = Bukkit.createBossBar(getBossBarTitle(), bossBarColor, bossBarStyle);
        bossBar.setProgress(1.0);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
    }

    /**
     * Returns the default boss bar title used when creating the boss bar.
     *
     * @return the boss bar title string
     */
    protected String getBossBarTitle() {
        return "§5" + getName();
    }

    /**
     * Updates boss bar appearance and applies the new color/style immediately if a bar exists.
     *
     * @param color the new bar color
     * @param style the new bar style
     */
    public void setBossBarAppearance(BarColor color, BarStyle style) {
        this.bossBarColor = color;
        this.bossBarStyle = style;
        if (bossBar != null) {
            bossBar.setColor(color);
            bossBar.setStyle(style);
        }
    }

    /**
     * Convenience setter for the boss bar color while preserving style.
     *
     * @param color new bar color
     */
    public void setBossBarColor(BarColor color) {
        setBossBarAppearance(color, this.bossBarStyle);
    }

    /**
     * Convenience setter for the boss bar style while preserving color.
     *
     * @param style new bar style
     */
    public void setBossBarStyle(BarStyle style) {
        setBossBarAppearance(this.bossBarColor, style);
    }

    /**
     * Removes and clears the boss bar if present.
     */
    public void removeBossBar() {
        if (bossBar == null) return;

        bossBar.removeAll();
        bossBar = null;
    }

    /**
     * Invokes the entity's configured {@link LootTable} to drop loot at the location for the player.
     * No-op if no loot table is configured.
     *
     * @param location drop location
     * @param player   player triggering the drop
     */
    public void dropLoot(Location location, BlightedPlayer player) {
        if (lootTable == null) return;
        lootTable.dropLoot(location, player);
    }

    /**
     * Adds a repeating lifecycle task to this entity's task manager and schedules it immediately if possible.
     *
     * @param factory     supplier producing a {@link BukkitRunnable}
     * @param delayTicks  initial delay in ticks
     * @param periodTicks repeating period in ticks
     */
    protected final void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
        lifecycleTasks.addRepeatingTask(factory, delayTicks, periodTicks);
        if (canScheduleTask()) {
            lifecycleTasks.scheduleLast();
        }
    }

    /**
     * Adds a delayed lifecycle task to this entity's task manager and schedules it immediately if possible.
     *
     * @param factory    supplier producing a {@link BukkitRunnable}
     * @param delayTicks delay in ticks before execution
     */
    protected final void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
        lifecycleTasks.addDelayedTask(factory, delayTicks);
        if (canScheduleTask()) {
            lifecycleTasks.scheduleLast();
        }
    }

    /**
     * Returns whether tasks can be scheduled for this entity at the current runtime state.
     *
     * @return {@code true} if entity exists, is alive, and runtime initialization completed
     */
    private boolean canScheduleTask() {
        return entity != null && !entity.isDead() && runtimeInitialized;
    }

    /**
     * Registers an attachment and associates it with its owner.
     * <p>
     * Disables drops for attachment equipment and stores the attachment in the global map.
     *
     * @param attachment the attachment to register
     */
    public static void addAttachment(EntityAttachment attachment) {
        if (attachment == null || attachment.entity() == null || attachment.owner() == null) return;

        ENTITY_ATTACHMENTS.put(attachment.entity(), attachment);
        attachment.owner().attachments.add(attachment);

        if (attachment.entity() instanceof LivingEntity living) {
            disableAttachmentEquipmentDrops(living);
        }
    }

    /**
     * Disables equipment drop chances on a living attachment entity.
     *
     * @param living attachment living entity
     */
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

    /**
     * Unregisters an attachment and removes associations.
     *
     * @param attachment the attachment to unregister
     */
    public static void unregisterAttachment(EntityAttachment attachment) {
        if (attachment == null) return;

        ENTITY_ATTACHMENTS.remove(attachment.entity());
        if (attachment.owner() != null) {
            attachment.owner().attachments.remove(attachment);
        }
    }

    /**
     * Removes and deletes all attachments owned by this entity.
     * Ensures attachments are removed from world and global map.
     */
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

    /**
     * Immediately kills all attachment entities (sets health to 0) and clears associations.
     */
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

    /**
     * Retrieves the {@link EntityAttachment} associated with a given Bukkit entity, or null if none.
     *
     * @param entity the entity to query
     * @return the associated {@link EntityAttachment} or null
     */
    public static EntityAttachment getAttachment(Entity entity) {
        return ENTITY_ATTACHMENTS.get(entity);
    }

    /**
     * Checks whether the given entity is an attachment.
     *
     * @param entity the entity to query
     * @return {@code true} if the entity is registered as an attachment
     */
    public static boolean isAttachment(Entity entity) {
        return ENTITY_ATTACHMENTS.containsKey(entity);
    }

    /**
     * Returns whether the runtime entity is null or dead.
     *
     * @return {@code true} if not alive
     */
    protected boolean isNotAlive() {
        return entity == null || entity.isDead();
    }

    /**
     * Returns the runtime {@link LivingEntity} instance or null if not spawned/attached.
     *
     * @return the underlying entity
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Returns the configured entity id.
     *
     * @return entity id string
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Returns the configured display name.
     *
     * @return entity name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the loot table used for drops when this entity dies.
     *
     * @param lootTable the loot table to assign
     */
    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    /**
     * Sets the name tag display type used for formatting and boss behavior.
     *
     * @param nameTagType the name tag type to use
     */
    public void setNameTagType(EntityNameTag nameTagType) {
        this.nameTagType = nameTagType;
    }

    /**
     * Returns the configured amount of experience to drop.
     *
     * @return dropped experience points
     */
    public int getDroppedExp() {
        return droppedExp;
    }

    /**
     * Sets the amount of experience to drop when the entity dies.
     *
     * @param droppedExp experience points to drop
     */
    public void setDroppedExp(int droppedExp) {
        this.droppedExp = droppedExp;
    }

    /**
     * Sets the configured damage value.
     *
     * @param damage damage to set
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Sets the configured defense (armor) value.
     *
     * @param defense defense value to set
     */
    public void setDefense(int defense) {
        this.defense = defense;
    }

    /**
     * Returns the configured maximum health.
     *
     * @return max health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Produces a deep-ish clone of this {@link AbstractBlightedEntity} instance.
     * <p>
     * The returned clone has entity/bossbar/runtime state reset and copies of
     * attributes, attachments list, armor, and hand items.
     *
     * @return cloned entity configuration
     */
    @Override
    public AbstractBlightedEntity clone() {
        try {
            AbstractBlightedEntity clone = (AbstractBlightedEntity) super.clone();

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
            throw new RuntimeException("Failed to clone AbstractBlightedEntity", e);
        }
    }

    /**
     * Helper to clone the armor array deeply.
     *
     * @return cloned armor array or null
     */
    private ItemStack[] cloneArmor() {
        if (this.armor == null) return null;

        ItemStack[] clonedArmor = new ItemStack[this.armor.length];
        for (int i = 0; i < this.armor.length; i++) {
            clonedArmor[i] = cloneItem(this.armor[i]);
        }
        return clonedArmor;
    }

    /**
     * Helper to clone a single ItemStack safely.
     *
     * @param item the item to clone
     * @return a cloned ItemStack or null if input was null
     */
    private ItemStack cloneItem(ItemStack item) {
        return item != null ? item.clone() : null;
    }
}
