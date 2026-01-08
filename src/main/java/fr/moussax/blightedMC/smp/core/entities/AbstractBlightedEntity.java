package fr.moussax.blightedMC.smp.core.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Base abstraction for all custom entities.
 *
 * <p>This class encapsulates the full lifecycle of a custom entity, including
 * spawning or attaching to an existing entity, attribute/equipment configuration,
 * boss bar handling, immunity rules, attachments, scheduled tasks, loot drops,
 * and cleanup.</p>
 *
 * <p>Concrete implementations are expected to define behavior by overriding
 * {@link #onDefineBehavior()}, damage handling hooks, and configuration fields
 * before spawning.</p>
 */
public abstract class AbstractBlightedEntity implements Cloneable {

    /** Persistent data key used to identify a blighted entity instance. */
    public static final NamespacedKey ENTITY_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_entity_id");

    /** Scoreboard tag used to skip expensive runtime checks. */
    public static final String FAST_PASS_TAG = "blighted_opt";

    /** Maximum distance at which players can see the boss bar. */
    private static final double BOSS_BAR_RANGE = 60.0;

    protected String entityId;
    protected String name;
    protected EntityType entityType;
    protected LivingEntity entity;

    protected int maxHealth;
    protected int damage;
    protected int defense;
    protected int droppedExp = 0;
    protected boolean persistent = false;

    protected ItemStack itemInMainHand;
    protected ItemStack itemInOffHand;
    protected ItemStack[] armor;

    protected LootTable lootTable;
    protected EntityNameTag nameTagType = EntityNameTag.DEFAULT;

    protected BossBar bossBar;
    protected BarColor bossBarColor = BarColor.RED;
    protected BarStyle bossBarStyle = BarStyle.SOLID;

    protected Map<Attribute, Double> attributes = new HashMap<>();
    public Set<EntityAttachment> attachments = new CopyOnWriteArraySet<>();

    private LifecycleTaskManager lifecycleTasks = new LifecycleTaskManager();
    private List<EntityImmunity> immunities = Collections.emptyList();

    /** Prevents runtime initialization from executing more than once. */
    private boolean runtimeInitialized = false;

    /**
     * Creates a blighted entity definition.
     *
     * @param name entity display name
     * @param maxHealth maximum health value
     * @param entityType Bukkit entity type
     */
    public AbstractBlightedEntity(@NonNull String name, int maxHealth, EntityType entityType) {
        this(name, maxHealth, 1, 0, entityType);
    }

    /**
     * Creates a blighted entity definition with damage.
     */
    public AbstractBlightedEntity(@NonNull String name, int maxHealth, int damage, EntityType entityType) {
        this(name, maxHealth, damage, 0, entityType);
    }

    /**
     * Creates a fully defined blighted entity.
     */
    public AbstractBlightedEntity(@NonNull String name, int maxHealth, int damage, int defense, EntityType entityType) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.damage = damage;
        this.defense = defense;
        this.entityType = entityType;
    }

    /**
     * Spawns the entity at the given location and initializes its full runtime state.
     *
     * @param location spawn location
     * @return spawned living entity
     * @throws IllegalStateException if the entity type is not defined
     */
    public LivingEntity spawn(Location location) {
        if (entityType == null) throw new IllegalStateException("EntityType cannot be null");

        initImmunityRules();
        entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);

        entity.addScoreboardTag(FAST_PASS_TAG);

        if (persistent) {
            PersistentDataContainer data = entity.getPersistentDataContainer();
            data.set(ENTITY_ID_KEY, PersistentDataType.STRING, getEntityId());
        }

        configureAttributes();
        configureEquipment();
        configureDisplay();

        BlightedEntitiesListener.registerEntity(entity, this);
        initRuntime();

        return entity;
    }

    /**
     * Attaches this blighted entity wrapper to an already existing Bukkit entity.
     *
     * <p>No attributes or equipment are re-applied.</p>
     */
    public void attachToExisting(LivingEntity existing) {
        this.entity = existing;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) {
            entity.addScoreboardTag(FAST_PASS_TAG);
        }
        initImmunityRules();
        configureDisplay();
        BlightedEntitiesListener.registerEntity(existing, this);
        initRuntime();
    }

    private void configureAttributes() {
        setAttribute(Attribute.MAX_HEALTH, maxHealth);
        setAttribute(Attribute.ATTACK_DAMAGE, damage);
        setAttribute(Attribute.ARMOR, defense);

        if (!attributes.isEmpty()) {
            attributes.forEach(this::setAttribute);
        }

        entity.setHealth(maxHealth);
        entity.setRemoveWhenFarAway(!persistent);
    }

    private void configureEquipment() {
        applyEquipment();
    }

    private void applyEquipment() {
        if (armor == null && itemInMainHand == null && itemInOffHand == null) return;

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        if (armor != null) equipment.setArmorContents(armor);
        if (itemInMainHand != null) equipment.setItemInMainHand(itemInMainHand);
        if (itemInOffHand != null) equipment.setItemInOffHand(itemInOffHand);

        disableEquipmentDrops(equipment);
    }

    private void disableEquipmentDrops(EntityEquipment equipment) {
        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
        equipment.setItemInOffHandDropChance(0f);
    }

    private void configureDisplay() {
        if (nameTagType == EntityNameTag.HIDDEN) {
            entity.setCustomNameVisible(false);
            return;
        }
        if (nameTagType == EntityNameTag.BOSS) createBossBar();

        updateNameTag();
        entity.setCustomNameVisible(true);
    }

    private void initRuntime() {
        if (runtimeInitialized) return;
        runtimeInitialized = true;
        onDefineBehavior();

        if (bossBar != null) startBossBarTask();
        lifecycleTasks.scheduleAll();
    }

    /**
     * Called once during runtime initialization.
     *
     * <p>Override to register AI logic, repeating tasks, or event-driven behavior.</p>
     */
    protected void onDefineBehavior() {
        // Override to define behavior
    }

    /**
     * Immediately kills the entity and triggers cleanup.
     */
    public void kill() {
        if (isNotAlive()) return;
        cleanup();
        entity.setHealth(0);
    }

    /**
     * Performs full cleanup without forcing entity death.
     *
     * <p>Cancels tasks, removes boss bars, unregisters listeners, and kills attachments.</p>
     */
    public void cleanup() {
        removeBossBar();
        killAllAttachments();
        lifecycleTasks.cancelAll();
        BlightedEntitiesListener.unregisterEntity(entity);
    }

    /**
     * Called when the entity dies naturally.
     *
     * @param location death location
     */
    public void onDeath(Location location) {
        cleanup();
    }

    /**
     * Hook invoked when this entity takes damage.
     *
     * <p>Override to modify or react to damage events.</p>
     */
    public void onDamageTaken(EntityDamageEvent event) {
        // Override to implement behavior
    }

    /**
     * Inflicts direct damage and refreshes visual state.
     */
    public void damage(double amount) {
        if (isNotAlive()) return;
        entity.damage(amount);
        updateNameTag();
    }

    /**
     * Adds a base attribute override applied during spawn.
     */
    public void addAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }

    private void setAttribute(Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
                instance.removeModifier(modifier);
            }
            instance.setBaseValue(value);
        }
    }

    private void initImmunityRules() {
        EntityImmunities attribute = getClass().getAnnotation(EntityImmunities.class);
        if (attribute == null) return;

        List<EntityImmunity> tempList = new ArrayList<>(4);
        for (EntityImmunities.ImmunityType type : attribute.value()) {
            switch (type) {
                case MELEE -> tempList.add(EntityImmunity.MELEE);
                case FIRE -> tempList.add(EntityImmunity.FIRE);
                case PROJECTILE -> tempList.add(EntityImmunity.PROJECTILE);
                case MACE -> tempList.add(EntityImmunity.MACE);
            }
        }
        this.immunities = tempList;
    }

    /**
     * Returns the immunity rule triggered by the given damage event, if any.
     *
     * @return matching immunity or {@code null}
     */
    public EntityImmunity getTriggeredImmunity(LivingEntity entity, EntityDamageEvent event) {
        if (immunities.isEmpty()) return null;
        for (EntityImmunity rule : immunities) {
            if (rule.isImmune(entity, event)) return rule;
        }
        return null;
    }

    /**
     * Updates the entity name tag and boss bar progress.
     */
    public void updateNameTag() {
        if (entity == null || entity.isDead()) return;
        if (nameTagType != EntityNameTag.HIDDEN) entity.setCustomName(createNameTag());

        if (bossBar != null) {
            double progress = entity.getHealth() / Math.max(1, maxHealth);
            bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
            bossBar.setTitle(getBossBarTitle());
        }
    }

    /**
     * Creates the formatted name tag based on current health.
     */
    private String createNameTag() {
        if (entity == null) return name;
        return nameTagType.format(name, entity.getHealth(), maxHealth);
    }

    private void createBossBar() {
        if (bossBar != null) return;
        if (entityType == EntityType.WITHER || entityType == EntityType.ENDER_DRAGON) return;

        bossBar = Bukkit.createBossBar(getBossBarTitle(), bossBarColor, bossBarStyle);
        bossBar.setProgress(1.0);
    }

    private void startBossBarTask() {
        addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (entity == null || !entity.isValid()) {
                    cleanup();
                    cancel();
                    return;
                }

                if (bossBar == null) {
                    cancel();
                    return;
                }

                manageBossBarViewers();
            }
        }, 10L, 20L); // Run every second (20 ticks)
    }

    private void manageBossBarViewers() {
        World world = entity.getWorld();
        Location loc = entity.getLocation();

        for (Player player : bossBar.getPlayers()) {
            if (!player.isOnline() ||
                player.getWorld() != world ||
                player.getLocation().distanceSquared(loc) > (BOSS_BAR_RANGE * BOSS_BAR_RANGE)) {
                bossBar.removePlayer(player);
            }
        }

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(loc) <= (BOSS_BAR_RANGE * BOSS_BAR_RANGE)) {
                if (!bossBar.getPlayers().contains(player)) {
                    bossBar.addPlayer(player);
                }
            }
        }
    }

    private String getBossBarTitle() {
        return "§f§l" + getName();
    }

    public void setBossBarAppearance(BarColor color, BarStyle style) {
        this.bossBarColor = color;
        this.bossBarStyle = style;
        if (bossBar != null) {
            bossBar.setColor(color);
            bossBar.setStyle(style);
        }
    }

    public void removeBossBar() {
        if (bossBar == null) return;
        bossBar.removeAll();
        bossBar = null;
    }

    /**
     * Drops loot using the configured loot table.
     */
    public void dropLoot(Location location, BlightedPlayer player) {
        if (lootTable == null) return;
        World world = Objects.requireNonNull(location.getWorld());
        Biome biome = world.getBiome(location);
        LootContext context = new LootContext(player, world, biome, location, ThreadLocalRandom.current(), null);
        lootTable.execute(context);
    }

    /**
     * Registers a repeating task bound to this entity lifecycle.
     */
    protected final void addRepeatingTask(Supplier<BukkitRunnable> factory, long delayTicks, long periodTicks) {
        lifecycleTasks.addRepeatingTask(factory, delayTicks, periodTicks);
        if (canScheduleTask()) lifecycleTasks.scheduleLast();
    }

    /**
     * Registers a delayed task bound to this entity lifecycle.
     */
    protected final void addDelayedTask(Supplier<BukkitRunnable> factory, long delayTicks) {
        lifecycleTasks.addDelayedTask(factory, delayTicks);
        if (canScheduleTask()) lifecycleTasks.scheduleLast();
    }

    private boolean canScheduleTask() {
        return entity != null && !entity.isDead() && runtimeInitialized;
    }

    /**
     * Attaches an auxiliary entity owned by this entity.
     *
     * <p>Attachments are automatically cleaned up on death.</p>
     */
    public void addAttachment(EntityAttachment attachment) {
        if (attachment == null || attachment.entity() == null) return;
        this.attachments.add(attachment);
        BlightedEntitiesListener.registerAttachment(attachment.entity(), this);

        if (attachment.entity() instanceof LivingEntity living) {
            EntityEquipment entityEquipment = living.getEquipment();
            if (entityEquipment != null) disableEquipmentDrops(entityEquipment);
            living.addScoreboardTag(FAST_PASS_TAG);
        }
    }

    /**
     * Kills and unregisters all attached entities.
     */
    public void killAllAttachments() {
        if (attachments.isEmpty()) return;
        for (EntityAttachment attachment : attachments) {
            Entity attachmentEntity = attachment.entity();
            if (attachmentEntity == null) return;

            BlightedEntitiesListener.unregisterAttachment(attachmentEntity);
            if (attachmentEntity instanceof LivingEntity living && !living.isDead()) {
                living.setHealth(0);
            } else {
                attachmentEntity.remove();
            }
        }
        attachments.clear();
    }

    /**
     * @return {@code true} if the entity is null or dead
     */
    protected boolean isNotAlive() {
        return entity == null || entity.isDead();
    }

    /**
     * @return the underlying Bukkit entity
     */
    public LivingEntity getEntity() {
        return entity;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getName() {
        return name;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return persistent;
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

    public int getDamage() {
        return damage;
    }

    /**
     * Creates a deep clone of this entity definition.
     *
     * <p>Runtime state, tasks, and live entities are not copied.</p>
     */
    @Override
    public AbstractBlightedEntity clone() {
        try {
            AbstractBlightedEntity clone = (AbstractBlightedEntity) super.clone();
            clone.entity = null;
            clone.bossBar = null;
            clone.runtimeInitialized = false;
            clone.attributes = new HashMap<>();
            clone.attributes.putAll(this.attributes);
            clone.attachments = new CopyOnWriteArraySet<>();
            clone.lifecycleTasks = new LifecycleTaskManager();
            clone.armor = cloneArmor();
            clone.itemInMainHand = cloneItem(this.itemInMainHand);
            clone.itemInOffHand = cloneItem(this.itemInOffHand);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    private ItemStack[] cloneArmor() {
        if (this.armor == null) return null;
        ItemStack[] cloned = new ItemStack[this.armor.length];
        for (int i = 0; i < this.armor.length; i++) {
            cloned[i] = cloneItem(this.armor[i]);
        }
        return cloned;
    }

    private ItemStack cloneItem(ItemStack item) {
        return item != null ? item.clone() : null;
    }
}
