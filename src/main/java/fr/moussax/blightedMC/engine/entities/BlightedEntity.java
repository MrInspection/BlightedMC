package fr.moussax.blightedMC.engine.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedMC.engine.entities.attachment.EntityAttachment;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import fr.moussax.blightedMC.engine.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.loot.LootContext;
import fr.moussax.blightedMC.shared.loot.LootTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Base abstraction for custom runtime-controlled entities bound to a Bukkit {@link LivingEntity}.
 *
 * <p>Provides a deterministic lifecycle for entity initialization, behavior definition, and teardown.
 * Supports phase-based transitions, modular components, scheduled behaviors, attachments, and optional boss bar integration.
 *
 * <p>Instances are stateful and bound to a single entity instance at runtime.
 * All methods must be invoked on the server main thread unless explicitly documented.
 *
 * <p>Lifecycle:
 * spawn/attach → attributes → AI → components → runtime initialization → phase evaluation
 */
public abstract class BlightedEntity implements Cloneable {

    public static final NamespacedKey ENTITY_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_entity_id");
    public static final NamespacedKey ATTACHMENT_OWNER_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_owner");
    public static final NamespacedKey ATTACHMENT_ROLE_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_role");
    public static final String FAST_PASS_TAG = "blighted_opt";
    private static final double BOSS_BAR_RANGE = 60.0;

    private NavigableMap<Double, Runnable> phaseThresholds = new TreeMap<>(Collections.reverseOrder());

    private LifecycleTaskManager coreTasks = new LifecycleTaskManager();
    private LifecycleTaskManager phaseTasks = new LifecycleTaskManager();
    public Set<EntityAttachment> attachments = new CopyOnWriteArraySet<>();
    private Map<String, EntityComponent> components = new HashMap<>();

    @Getter
    protected String entityId;
    @Getter
    protected String name;
    @Getter
    protected EntityType entityType;
    @Getter
    protected LivingEntity entity;
    @Getter
    protected int maxHealth;
    @Getter
    @Setter
    protected int damage;
    @Setter
    protected int defense;
    @Setter
    @Getter
    protected int droppedExp = 0;

    protected ItemStack itemInMainHand;
    protected ItemStack itemInOffHand;
    protected ItemStack[] armor;

    @Setter
    protected LootTable lootTable;
    @Setter
    protected BlightedType blightedType = BlightedType.DEFAULT;
    protected BossBar bossBar;
    protected BarColor bossBarColor = BarColor.RED;
    protected BarStyle bossBarStyle = BarStyle.SOLID;
    protected Map<Attribute, Double> attributes = new HashMap<>();

    private List<EntityImmunity> immunities = Collections.emptyList();
    private boolean runtimeInitialized = false;
    private boolean componentsInitialized = false;

    public BlightedEntity(@NonNull String name, int maxHealth, EntityType entityType) {
        this(name, maxHealth, 1, 0, entityType);
    }

    public BlightedEntity(@NonNull String name, int maxHealth, int damage, EntityType entityType) {
        this(name, maxHealth, damage, 0, entityType);
    }

    public BlightedEntity(@NonNull String name, int maxHealth, int damage, int defense, EntityType entityType) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.damage = damage;
        this.defense = defense;
        this.entityType = entityType;
    }

    /**
     * Spawns and initializes a new entity instance.
     *
     * @param location spawn location
     * @return initialized entity
     * @throws IllegalStateException if entity type is undefined
     */
    public LivingEntity spawn(Location location) {
        if (entityType == null) throw new IllegalStateException("EntityType cannot be null");

        initImmunityRules();
        entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);

        entity.addScoreboardTag(FAST_PASS_TAG);
        entity.getPersistentDataContainer().set(ENTITY_ID_KEY, PersistentDataType.STRING, getEntityId());

        initializeAttributes();
        configureEquipment();
        onConfigureAI(entity);
        if (blightedType == BlightedType.BOSS) createBossBar();

        BlightedEntitiesListener.registerEntity(entity, this);
        initComponents();
        initRuntime();

        return entity;
    }

    /**
     * Binds this abstraction to an existing entity and restores runtime state.
     *
     * @param existing target entity
     */
    public void attachToExisting(LivingEntity existing) {
        this.entity = existing;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) {
            entity.addScoreboardTag(FAST_PASS_TAG);
        }

        initImmunityRules();
        rehydrateAttributes();
        onConfigureAI(existing);

        if (blightedType == BlightedType.BOSS) createBossBar();
        BlightedEntitiesListener.registerEntity(existing, this);

        initComponents();
        onRehydrate(existing);

        if (!runtimeInitialized) {
            initRuntime();
        } else {
            coreTasks.scheduleAll();
            phaseTasks.scheduleAll();
        }
    }

    /**
     * Forces entity death and performs full cleanup.
     */
    public void kill() {
        if (isNotAlive()) return;
        cleanup();
        entity.setHealth(0);
    }

    /**
     * Stops all runtime systems and detaches framework state from the entity.
     */
    public void cleanup() {
        Location currentLoc = entity != null ? entity.getLocation() : null;
        if (currentLoc != null) {
            for (EntityComponent component : components.values()) {
                component.onDeath(this, currentLoc);
            }
        }

        removeBossBar();
        killAllAttachments();
        destroyComponents();
        coreTasks.cancelAll();
        phaseTasks.cancelAll();
        BlightedEntitiesListener.unregisterEntity(entity);
    }

    /**
     * Lifecycle hook invoked on entity death.
     */
    public void onDeath(Location location) {
    }

    /**
     * Lifecycle hook invoked when entity takes damage.
     */
    public void onDamageTaken(EntityDamageEvent event) {
    }

    /**
     * AI configuration hook executed after spawn or attach.
     */
    protected void onConfigureAI(LivingEntity spawned) {
    }

    /**
     * Hook for restoring behavior on an existing entity.
     */
    protected void onRehydrate(LivingEntity existing) {
    }

    /**
     * Defines scheduled behaviors and runtime abilities.
     */
    protected void onDefineBehavior() {
    }

    /**
     * Registers a phase triggered when health ratio is below or equal to threshold.
     *
     * @param healthPercentage normalized threshold (0.0–1.0)
     * @param onTransition     phase logic
     */
    protected final void registerPhase(double healthPercentage, Runnable onTransition) {
        phaseThresholds.put(healthPercentage, onTransition);
    }

    /**
     * Evaluates and triggers pending phase transitions based on current health.
     *
     * @param currentHealth current entity health
     */
    public final void evaluatePhases(double currentHealth) {
        if (phaseThresholds.isEmpty()) return;
        double healthPercentage = currentHealth / maxHealth;

        Iterator<Map.Entry<Double, Runnable>> iterator = phaseThresholds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Double, Runnable> entry = iterator.next();
            if (healthPercentage > entry.getKey()) break;
            phaseTasks.cancelAll();
            phaseTasks = new LifecycleTaskManager();
            entry.getValue().run();
            phaseTasks.scheduleAll();
            iterator.remove();
        }
    }

    /**
     * Registers a repeating ability in the core/phase lifecycle.
     *
     * @param delayTicks  initial delay
     * @param periodTicks execution interval
     * @param action      task logic
     */
    @SuppressWarnings("SameParameterValue")
    protected final void addCoreAbility(long delayTicks, long periodTicks, Runnable action) {
        scheduleAbility(coreTasks, delayTicks, periodTicks, action);
    }

    /**
     * Registers a repeating ability in the core/phase lifecycle.
     *
     * @param delayTicks  initial delay
     * @param periodTicks execution interval
     * @param action      task logic
     */
    protected final void addPhaseAbility(long delayTicks, long periodTicks, Runnable action) {
        scheduleAbility(phaseTasks, delayTicks, periodTicks, action);
    }

    /**
     * Registers a delayed execution in the core/phase lifecycle.
     *
     * @param delayTicks delay before execution
     * @param action     task logic
     */
    protected final void addCoreDelayedAction(long delayTicks, Runnable action) {
        scheduleDelayedAction(coreTasks, delayTicks, action);
    }

    /**
     * Registers a delayed execution in the core/phase lifecycle.
     *
     * @param delayTicks delay before execution
     * @param action     task logic
     */
    protected final void addPhaseDelayedAction(long delayTicks, Runnable action) {
        scheduleDelayedAction(phaseTasks, delayTicks, action);
    }

    /**
     * Applies damage to the entity.
     *
     * @param amount damage amount
     */
    public void damage(double amount) {
        if (isNotAlive()) return;
        entity.damage(amount);
    }

    /**
     * Performs a melee attack on a target entity.
     *
     * @param target attack target
     */
    public void meleeAttack(Entity target) {
        if (isNotAlive()) return;
        entity.attack(target);
        entity.swingMainHand();
    }

    /**
     * Sets AI target if entity supports mob behavior.
     *
     * @param target target entity
     */
    public void setAITarget(LivingEntity target) {
        if (entity instanceof Mob mob) mob.setTarget(target);
    }

    /**
     * Checks line of sight to target entity.
     *
     * @param target target entity
     * @return true if visible
     */
    public boolean hasLineOfSight(Entity target) {
        if (isNotAlive()) return false;
        return entity.hasLineOfSight(target);
    }

    public List<Player> getNearbyPlayers(double radius) {
        if (isNotAlive()) return Collections.emptyList();
        return entity.getNearbyEntities(radius, radius, radius).stream()
                .filter(nearbyEntity -> nearbyEntity instanceof Player player && player.getGameMode() == GameMode.SURVIVAL)
                .map(nearbyEntity -> (Player) nearbyEntity)
                .toList();
    }

    public Player getNearestPlayer(double radius) {
        Location origin = entity.getLocation();
        return getNearbyPlayers(radius).stream()
                .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(origin)))
                .orElse(null);
    }

    public void addAttachment(Entity attachmentEntity) {
        addAttachment(attachmentEntity, AttachmentRole.DEPENDENT);
    }

    /**
     * Attaches an entity with a defined role.
     *
     * @param attachmentEntity entity to attach
     * @param role             attachment role
     */
    public void addAttachment(Entity attachmentEntity, AttachmentRole role) {
        if (attachmentEntity == null) return;
        attachments.add(new EntityAttachment(attachmentEntity, role));
        BlightedEntitiesListener.registerAttachment(attachmentEntity, this);

        if (entity != null) {
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING, entity.getUniqueId().toString());
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_ROLE_KEY, PersistentDataType.STRING, role.name());
        }

        if (attachmentEntity instanceof LivingEntity living) {
            EntityEquipment equipment = living.getEquipment();
            if (equipment != null) zeroEquipmentDropChances(equipment);
            living.addScoreboardTag(FAST_PASS_TAG);
        }
    }

    /**
     * Removes and destroys all attachments.
     */
    public void killAllAttachments() {
        if (attachments.isEmpty()) return;
        for (EntityAttachment attachment : attachments) {
            Entity attachmentEntity = attachment.entity();
            if (attachmentEntity == null) continue;

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
     * Checks if a living body attachment exists.
     *
     * @return true if present
     */
    public boolean hasLivingBodyAttachment() {
        for (EntityAttachment attachment : attachments) {
            if (attachment.role() == AttachmentRole.BODY
                    && attachment.entity() instanceof LivingEntity living
                    && !living.isDead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers a component. Immediately initialized if entity is active.
     *
     * @param component component instance
     */
    public void addComponent(EntityComponent component) {
        components.put(component.getId(), component);
        if (componentsInitialized && entity != null) component.onInit(entity);
    }

    /**
     * Retrieves a registered component by id.
     *
     * @param id  component identifier
     * @param <T> component type
     * @return component or null
     */
    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> T getComponent(String id) {
        return (T) components.get(id);
    }

    /**
     * Exposes registered components for external generic listeners.
     */
    public Collection<EntityComponent> getComponents() {
        return new ArrayList<>(components.values());
    }

    public void updateBossBar() {
        if (bossBar == null || entity == null || entity.isDead()) return;
        double progress = entity.getHealth() / Math.max(1, maxHealth);
        bossBar.setProgress(Math.clamp(progress, 0.0, 1.0));
    }

    /**
     * Sets boss bar visual configuration.
     *
     * @param color bar color
     * @param style bar style
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
     * Removes boss bar if present.
     */
    public void removeBossBar() {
        if (bossBar == null) return;
        bossBar.removeAll();
        bossBar = null;
    }

    /**
     * Executes loot table at location context.
     *
     * @param location drop location
     * @param player   associated player
     */
    public void dropLoot(Location location, BlightedPlayer player) {
        if (lootTable == null) return;
        World world = Objects.requireNonNull(location.getWorld());
        Biome biome = world.getBiome(location);
        LootContext context = new LootContext(player, world, biome, location, ThreadLocalRandom.current(), null);
        lootTable.execute(context);
    }

    public void addAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }

    /**
     * Evaluates immunity rules against a damage event.
     *
     * @param target entity
     * @param event  damage event
     * @return matched immunity or null
     */
    public EntityImmunity getTriggeredImmunity(LivingEntity target, EntityDamageEvent event) {
        if (immunities.isEmpty()) return null;
        for (EntityImmunity rule : immunities) {
            if (rule.isImmune(target, event)) return rule;
        }
        return null;
    }

    private void initComponents() {
        if (componentsInitialized) return;
        componentsInitialized = true;
        components.values().forEach(component -> component.onInit(entity));
    }

    private void destroyComponents() {
        components.values().forEach(component -> component.onDestroy(entity));
        componentsInitialized = false;
    }

    /**
     * Initializes runtime systems (behavior, tasks, phases). Executed once per instance.
     */
    private void initRuntime() {
        if (runtimeInitialized) return;
        onDefineBehavior();
        if (bossBar != null) startBossBarTask();

        addCoreAbility(5L, 5L, () -> {
            for (EntityComponent component : components.values()) {
                component.onTick(this);
            }
        });

        runtimeInitialized = true;
        coreTasks.scheduleAll();
        evaluatePhases(maxHealth);
    }

    private void initializeAttributes() {
        setAttribute(Attribute.MAX_HEALTH, maxHealth);
        setAttribute(Attribute.ATTACK_DAMAGE, damage);
        if (defense > 0) setAttribute(Attribute.ARMOR, defense);
        attributes.forEach(this::setAttribute);

        AttributeInstance maxHealthAttr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr != null) entity.setHealth(maxHealthAttr.getValue());

        lockEntityProperties();
    }

    private void rehydrateAttributes() {
        setAttributeBaseOnly(Attribute.MAX_HEALTH, maxHealth);
        setAttributeBaseOnly(Attribute.ATTACK_DAMAGE, damage);
        if (defense > 0) setAttributeBaseOnly(Attribute.ARMOR, defense);
        attributes.forEach(this::setAttributeBaseOnly);

        AttributeInstance maxHealthAttr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr != null) entity.setHealth(Math.min(entity.getHealth(), maxHealthAttr.getValue()));

        lockEntityProperties();
    }

    private void lockEntityProperties() {
        entity.setRemoveWhenFarAway(false);
        entity.setPersistent(true);
        entity.setCanPickupItems(false);
    }

    private void setAttribute(Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
            instance.removeModifier(modifier);
        }
        instance.setBaseValue(value);
    }

    private void setAttributeBaseOnly(Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.setBaseValue(value);
    }

    private void configureEquipment() {
        if (armor == null && itemInMainHand == null && itemInOffHand == null) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        if (armor != null) equipment.setArmorContents(armor);
        if (itemInMainHand != null) equipment.setItemInMainHand(itemInMainHand);
        if (itemInOffHand != null) equipment.setItemInOffHand(itemInOffHand);

        zeroEquipmentDropChances(equipment);
    }

    private void zeroEquipmentDropChances(EntityEquipment equipment) {
        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
        equipment.setItemInOffHandDropChance(0f);
    }

    private void initImmunityRules() {
        EntityImmunities annotation = getClass().getAnnotation(EntityImmunities.class);
        if (annotation == null) return;
        List<EntityImmunity> tempList = new ArrayList<>(4);
        for (EntityImmunities.ImmunityType type : annotation.value()) {
            switch (type) {
                case MELEE -> tempList.add(EntityImmunity.MELEE);
                case FIRE -> tempList.add(EntityImmunity.FIRE);
                case PROJECTILE -> tempList.add(EntityImmunity.PROJECTILE);
                case MACE -> tempList.add(EntityImmunity.MACE);
            }
        }
        this.immunities = tempList;
    }

    private void scheduleAbility(LifecycleTaskManager manager, long delayTicks, long periodTicks, Runnable action) {
        manager.addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (isNotAlive()) {
                    cancel();
                    return;
                }
                try {
                    action.run();
                } catch (Exception exception) {
                    BlightedMC.getInstance().getLogger().warning("[BlightedEntity] Ability threw an exception on entity '" + name + "': " + exception.getMessage());
                }
            }
        }, delayTicks, periodTicks);
        if (canScheduleTask()) manager.scheduleLast();
    }

    private void scheduleDelayedAction(LifecycleTaskManager manager, long delayTicks, Runnable action) {
        manager.addDelayedTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (isNotAlive()) return;
                try {
                    action.run();
                } catch (Exception exception) {
                    BlightedMC.getInstance().getLogger().warning("[BlightedEntity] Delayed action threw an exception on entity '" + name + "': " + exception.getMessage());
                }
            }
        }, delayTicks);
        if (canScheduleTask()) manager.scheduleLast();
    }

    private boolean canScheduleTask() {
        return entity != null && !entity.isDead() && runtimeInitialized;
    }

    private void createBossBar() {
        if (bossBar != null) return;
        if (entityType == EntityType.WITHER || entityType == EntityType.ENDER_DRAGON) return;
        bossBar = Bukkit.createBossBar("§f§l" + name, bossBarColor, bossBarStyle);
        bossBar.setProgress(1.0);
    }

    private void startBossBarTask() {
        addCoreAbility(10L, 20L, this::manageBossBarViewers);
    }

    private void manageBossBarViewers() {
        if (bossBar == null) return;
        World world = entity.getWorld();
        Location loc = entity.getLocation();
        double rangeSquared = BOSS_BAR_RANGE * BOSS_BAR_RANGE;

        for (Player player : new ArrayList<>(bossBar.getPlayers())) {
            if (!player.isOnline() || player.getWorld() != world || player.getLocation().distanceSquared(loc) > rangeSquared) {
                bossBar.removePlayer(player);
            }
        }
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(loc) <= rangeSquared && !bossBar.getPlayers().contains(player)) {
                bossBar.addPlayer(player);
            }
        }
    }

    protected boolean isNotAlive() {
        return entity == null || !entity.isValid() || entity.isDead();
    }

    @Override
    public BlightedEntity clone() {
        try {
            BlightedEntity clone = (BlightedEntity) super.clone();
            clone.entity = null;
            clone.bossBar = null;
            clone.runtimeInitialized = false;
            clone.componentsInitialized = false;
            clone.attributes = new HashMap<>(this.attributes);
            clone.attachments = new CopyOnWriteArraySet<>();
            clone.coreTasks = new LifecycleTaskManager();
            clone.phaseTasks = new LifecycleTaskManager();
            clone.armor = cloneArmor();
            clone.itemInMainHand = cloneItem(this.itemInMainHand);
            clone.itemInOffHand = cloneItem(this.itemInOffHand);
            clone.phaseThresholds = new TreeMap<>(this.phaseThresholds);

            clone.components = new HashMap<>();
            for (Map.Entry<String, EntityComponent> entry : this.components.entrySet()) {
                clone.components.put(entry.getKey(), entry.getValue().clone());
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    private ItemStack[] cloneArmor() {
        if (this.armor == null) return null;
        ItemStack[] cloned = new ItemStack[this.armor.length];
        for (int i = 0; i < this.armor.length; i++) cloned[i] = cloneItem(this.armor[i]);
        return cloned;
    }

    private ItemStack cloneItem(ItemStack item) {
        return item != null ? item.clone() : null;
    }
}
