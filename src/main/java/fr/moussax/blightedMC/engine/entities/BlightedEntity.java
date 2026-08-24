package fr.moussax.blightedMC.engine.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedMC.engine.entities.attachment.EntityAttachment;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import fr.moussax.blightedMC.engine.entities.immunity.DamageType;
import fr.moussax.blightedMC.engine.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.loot.LootContext;
import fr.moussax.blightedMC.shared.loot.LootTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;

import java.util.*;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Base class for custom runtime-controlled entities backed by a Bukkit {@link LivingEntity}.
 *
 * <p>Manages entity lifecycle, attributes, phases, components, scheduled tasks,
 * attachments, immunities, and optional boss bar support.</p>
 *
 * <p>Instances are stateful and bound to one entity at runtime. API methods must
 * be called from the server thread unless stated otherwise.</p>
 */
public abstract class BlightedEntity implements Cloneable {

    public static final NamespacedKey ENTITY_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_entity_id");
    public static final NamespacedKey ATTACHMENT_OWNER_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_owner");
    public static final NamespacedKey ATTACHMENT_ROLE_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_role");
    public static final NamespacedKey ATTACHMENT_OFFSET_X_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_offset_x");
    public static final NamespacedKey ATTACHMENT_OFFSET_Y_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_offset_y");
    public static final NamespacedKey ATTACHMENT_OFFSET_Z_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_offset_z");
    public static final NamespacedKey ATTACHMENT_SYNC_YAW_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_sync_yaw");
    public static final NamespacedKey ATTACHMENT_SYNC_PITCH_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_attachment_sync_pitch");
    public static final String FAST_PASS_TAG = "blighted_opt";

    private static final double BOSS_BAR_RANGE = 60.0;

    private NavigableMap<Double, Runnable> phaseThresholds = new TreeMap<>(Collections.reverseOrder());
    private LifecycleTaskManager coreTasks = new LifecycleTaskManager();
    private LifecycleTaskManager phaseTasks = new LifecycleTaskManager();
    private Map<String, EntityComponent> components = new HashMap<>();
    public Set<EntityAttachment> attachments = new CopyOnWriteArraySet<>();

    private long lastDamageTick = -1L;
    private UUID lastDamagerUuid = null;

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
    protected boolean isBoss = false;
    @Getter
    @Setter
    protected boolean isPerformingAbility = false;

    protected BossBar bossBar;
    protected BarColor bossBarColor = BarColor.RED;
    protected BarStyle bossBarStyle = BarStyle.SOLID;
    protected Map<Attribute, Double> attributes = new HashMap<>();

    private final List<EntityImmunity> immunities = new ArrayList<>();
    private final Map<DamageType, Double> resistances = new EnumMap<>(DamageType.class);
    private boolean runtimeInitialized = false;
    private boolean componentsInitialized = false;

    /**
     * Creates an entity with default damage and defense values.
     *
     * @param name       entity display name
     * @param maxHealth  maximum health
     * @param entityType Bukkit entity type
     */
    public BlightedEntity(@NonNull String name, int maxHealth, EntityType entityType) {
        this(name, maxHealth, 1, 0, entityType);
    }

    /**
     * Creates an entity with the given health and damage values.
     *
     * @param name       entity display name
     * @param maxHealth  maximum health
     * @param damage     base attack damage
     * @param entityType Bukkit entity type
     */
    public BlightedEntity(@NonNull String name, int maxHealth, int damage, EntityType entityType) {
        this(name, maxHealth, damage, 0, entityType);
    }

    /**
     * Creates an entity with the given base combat stats.
     *
     * @param name       entity display name
     * @param maxHealth  maximum health
     * @param damage     base attack damage
     * @param defense    base armor value
     * @param entityType Bukkit entity type
     */
    public BlightedEntity(@NonNull String name, int maxHealth, int damage, int defense, EntityType entityType) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.damage = damage;
        this.defense = defense;
        this.entityType = entityType;
    }

    /**
     * Spawns and initializes the entity at the given location.
     *
     * @param location spawn location
     * @return the spawned entity
     * @throws IllegalStateException if {@code entityType} is not set
     */
    public LivingEntity spawn(Location location) {
        if (entityType == null) {
            throw new IllegalStateException("EntityType cannot be null");
        }

        initImmunityRules();
        initResistanceRules();
        entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);
        entity.addScoreboardTag(FAST_PASS_TAG);
        entity.getPersistentDataContainer().set(ENTITY_ID_KEY, PersistentDataType.STRING, getEntityId());

        initializeAttributes();
        configureEquipment();
        onConfigureAI(entity);

        if (isBoss) {
            createBossBar();
        }

        BlightedEntitiesListener.registerEntity(entity, this);
        initComponents();
        initRuntime();

        return entity;
    }

    /**
     * Binds this instance to an existing entity and restores its runtime state.
     *
     * @param existing entity to bind to
     */
    public void attachToExisting(LivingEntity existing) {
        this.entity = existing;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) {
            entity.addScoreboardTag(FAST_PASS_TAG);
        }

        initImmunityRules();
        initResistanceRules();
        rehydrateAttributes();
        onConfigureAI(existing);

        if (isBoss) {
            createBossBar();
        }
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
     * Kills the entity and performs full lifecycle cleanup.
     */
    public void kill() {
        if (!isAlive()) return;
        cleanup();
        entity.setHealth(0);
    }

    /**
     * Stops runtime tasks, removes attachments, destroys components,
     * and unregisters the entity from the framework.
     */
    public void cleanup() {
        Location currentLocation = entity != null ? entity.getLocation() : null;
        if (currentLocation != null) {
            for (EntityComponent component : components.values()) {
                component.onDeath(this, currentLocation);
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
     * Called when the entity takes damage.
     *
     * @param event damage event
     */
    public void onDamageTaken(EntityDamageEvent event) {
    }

    /**
     * Called when this entity deals damage to a target entity.
     *
     * @param event damage by entity event
     */
    public void onDamageDealt(EntityDamageByEntityEvent event) {
    }

    /**
     * Called when the entity dies.
     *
     * @param location death location
     */
    public void onDeath(Location location) {
    }

    /**
     * Defines repeating and delayed runtime behavior.
     */
    protected void onDefineBehavior() {
    }

    /**
     * Configures the entity's AI with NMS after spawn or attachment.
     *
     * @param spawned bound entity
     */
    protected void onConfigureAI(LivingEntity spawned) {
    }

    /**
     * Restores custom state after binding to an existing entity.
     *
     * @param existing bound entity
     */
    protected void onRehydrate(LivingEntity existing) {
    }

    /**
     * Registers a phase triggered at or below the given health ratio.
     *
     * @param healthPercentage threshold in the range {@code 0.0–1.0}
     * @param onTransition     action executed when the phase is triggered
     */
    protected final void registerPhase(double healthPercentage, Runnable onTransition) {
        phaseThresholds.put(healthPercentage, onTransition);
    }

    /**
     * Called when a registered phase is triggered.
     *
     * @param healthThreshold triggered threshold in the range {@code 0.0–1.0}
     * @return transition duration in ticks, or {@code 0} for no delay
     */
    protected long onPhaseTransition(double healthThreshold) {
        return 0L;
    }

    /**
     * Evaluates registered phases against the current health.
     *
     * @param currentHealth current health value
     */
    public final void evaluatePhases(double currentHealth) {
        if (phaseThresholds.isEmpty()) return;
        double healthPercentage = currentHealth / maxHealth;

        Iterator<Map.Entry<Double, Runnable>> iterator = phaseThresholds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Double, Runnable> entry = iterator.next();

            if (healthPercentage > entry.getKey()) {
                break;
            }

            phaseTasks.cancelAll();
            phaseTasks = new LifecycleTaskManager();
            entry.getValue().run();
            long transitionDuration = onPhaseTransition(entry.getKey());

            if (transitionDuration > 0) {
                setPerformingAbility(true);
                addCoreDelayedAction(transitionDuration, () -> {
                    setPerformingAbility(false);
                    phaseTasks.scheduleAll();
                });
            } else {
                phaseTasks.scheduleAll();
            }
            iterator.remove();
        }
    }

    /**
     * Registers a repeating task in the core lifecycle.
     *
     * @param delayTicks  initial delay in server ticks
     * @param periodTicks execution interval in server ticks
     * @param action      task action
     */
    @SuppressWarnings("SameParameterValue")
    protected final void addCoreAbility(long delayTicks, long periodTicks, Runnable action) {
        scheduleAbility(coreTasks, delayTicks, periodTicks, action);
    }

    /**
     * Registers a repeating task in the current phase lifecycle.
     *
     * @param delayTicks  initial delay in server ticks
     * @param periodTicks execution interval in server ticks
     * @param action      task action
     */
    protected final void addPhaseAbility(long delayTicks, long periodTicks, Runnable action) {
        scheduleAbility(phaseTasks, delayTicks, periodTicks, action);
    }

    /**
     * Registers a delayed task in the core lifecycle.
     *
     * @param delayTicks delay in server ticks
     * @param action     task action
     */
    protected final void addCoreDelayedAction(long delayTicks, Runnable action) {
        scheduleDelayedAction(coreTasks, delayTicks, action);
    }

    /**
     * Registers a delayed task in the current phase lifecycle.
     *
     * @param delayTicks delay in server ticks
     * @param action     task action
     */
    protected final void addPhaseDelayedAction(long delayTicks, Runnable action) {
        scheduleDelayedAction(phaseTasks, delayTicks, action);
    }

    /**
     * Deals damage to the entity.
     *
     * @param amount damage amount
     */
    public void damage(double amount) {
        if (!isAlive()) {
            return;
        }
        entity.damage(amount);
    }

    /**
     * Performs a melee attack against the target.
     *
     * @param target attack target
     */
    public void meleeAttack(Entity target) {
        if (!isAlive()) {
            return;
        }
        entity.attack(target);
    }

    /**
     * Sets the entity's AI target when supported by its Bukkit type.
     *
     * @param target new AI target
     */
    public void setAITarget(LivingEntity target) {
        if (entity instanceof Mob mob) {
            mob.setTarget(target);
        }
    }

    /**
     * Checks whether the entity has a direct line of sight to a target.
     *
     * @param target target entity
     * @return {@code true} if the target is visible
     */
    public boolean hasLineOfSight(Entity target) {
        if (!isAlive()) {
            return false;
        }
        return entity.hasLineOfSight(target);
    }

    /**
     * Returns nearby survival-mode players around the entity.
     *
     * @param radius search radius
     * @return nearby players, or an empty list if unavailable
     */
    public List<Player> getNearbyPlayers(double radius) {
        return getNearbyPlayers(entity != null ? entity.getLocation() : null, radius);
    }

    /**
     * Returns nearby survival-mode players around a location.
     *
     * @param center search center
     * @param radius search radius
     * @return nearby players, or an empty list if the center is invalid
     */
    public List<Player> getNearbyPlayers(Location center, double radius) {
        if (!isAlive() || center == null || center.getWorld() == null) return Collections.emptyList();
        return center.getWorld().getNearbyEntities(center, radius, radius, radius,
                        entity -> entity instanceof Player player && player.getGameMode() == GameMode.SURVIVAL
                ).stream()
                .map(entity -> (Player) entity)
                .toList();
    }

    /**
     * Returns the nearest survival-mode player within the given radius.
     *
     * @param radius search radius
     * @return nearest player, or {@code null} if none exists
     */
    public Player getNearestPlayer(double radius) {
        return getNearestPlayer(entity != null ? entity.getLocation() : null, radius);
    }

    /**
     * Returns the nearest survival-mode player around a location.
     *
     * @param center search center
     * @param radius search radius
     * @return nearest player, or {@code null} if none exists
     */
    public Player getNearestPlayer(Location center, double radius) {
        if (center == null) return null;
        return getNearbyPlayers(center, radius)
                .stream()
                .min(Comparator.comparingDouble(player -> player.getLocation().distanceSquared(center)))
                .orElse(null);
    }

    /**
     * Returns nearby {@link BlightedPlayer} instances.
     *
     * @param radius search radius
     * @return nearby BlightedMC players
     */
    public List<BlightedPlayer> getNearbyBlightedPlayers(double radius) {
        return getNearbyBlightedPlayers(entity != null ? entity.getLocation() : null, radius);
    }

    /**
     * Returns nearby {@link BlightedPlayer} instances around a location.
     *
     * @param center search center
     * @param radius search radius
     * @return nearby BlightedMC players
     */
    public List<BlightedPlayer> getNearbyBlightedPlayers(Location center, double radius) {
        return getNearbyPlayers(center, radius).stream()
                .map(BlightedPlayer::getBlightedPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Returns the nearest {@link BlightedPlayer}.
     *
     * @param radius search radius
     * @return nearest BlightedMC player, or {@code null}
     */
    public BlightedPlayer getNearestBlightedPlayer(double radius) {
        Player target = getNearestPlayer(radius);
        return target != null ? BlightedPlayer.getBlightedPlayer(target) : null;
    }

    /**
     * Returns the nearest {@link BlightedPlayer} around a location.
     *
     * @param center search center
     * @param radius search radius
     * @return nearest BlightedMC player, or {@code null}
     */
    public BlightedPlayer getNearestBlightedPlayer(Location center, double radius) {
        Player target = getNearestPlayer(center, radius);
        return target != null ? BlightedPlayer.getBlightedPlayer(target) : null;
    }

    /**
     * Attacks the nearest player unless an ability is currently active.
     *
     * @param radius search radius
     */
    public void meleeAttackNearestPlayer(double radius) {
        if (isPerformingAbility()) {
            return;
        }
        Player target = getNearestPlayer(radius);
        if (target != null) {
            meleeAttack(target);
        }
    }

    /**
     * Damages all nearby survival-mode players.
     *
     * @param center       damage center
     * @param radius       damage radius
     * @param damageAmount damage dealt
     */
    public void damageNearbyPlayers(Location center, double radius, double damageAmount) {
        List<Player> hitPlayers = getNearbyPlayers(center, radius);
        for (Player player : hitPlayers) {
            player.damage(damageAmount, entity);
        }
    }

    /**
     * Damages all nearby survival-mode players around the entity.
     *
     * @param radius       damage radius
     * @param damageAmount damage dealt
     */
    public void damageNearbyPlayers(double radius, double damageAmount) {
        damageNearbyPlayers(entity != null ? entity.getLocation() : null, radius, damageAmount);
    }

    /**
     * Damages and knocks back all nearby survival-mode players.
     *
     * @param center            effect center
     * @param radius            effect radius
     * @param damageAmount      damage dealt
     * @param knockbackStrength horizontal knockback strength
     * @param verticalKnockback vertical knockback velocity
     */
    public void damageAndKnockbackNearbyPlayers(
            Location center,
            double radius,
            double damageAmount,
            double knockbackStrength,
            double verticalKnockback
    ) {
        List<Player> hitPlayers = getNearbyPlayers(center, radius);
        for (Player player : hitPlayers) {
            player.damage(damageAmount, entity);
            Vector knockbackVector = player.getLocation().toVector().subtract(center.toVector()).setY(0);
            if (knockbackVector.lengthSquared() > 0.001) {
                knockbackVector.normalize().multiply(knockbackStrength).setY(verticalKnockback);
            } else {
                knockbackVector = new Vector(0, verticalKnockback, 0);
            }
            player.setVelocity(knockbackVector);
        }
    }

    /**
     * Disables a target player's shield if they are currently blocking.
     *
     * @param target        target player
     * @param cooldownTicks duration in ticks to put the shield on cooldown
     */
    public void disableShieldIfBlocking(Player target, int cooldownTicks) {
        if (target != null && target.isBlocking()) {
            target.setCooldown(Material.SHIELD, cooldownTicks);
            target.getWorld().playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.8f);
        }
    }

    /**
     * Rotates the entity to face a target location on the horizontal plane.
     *
     * @param target target location
     */
    public void faceLocation(Location target) {
        if (!isAlive() || target == null) {
            return;
        }
        Vector direction = target.toVector().subtract(entity.getLocation().toVector()).setY(0);
        if (direction.lengthSquared() > 0.001) {
            Location location = entity.getLocation();
            location.setYaw((float) Math.toDegrees(-Math.atan2(direction.getX(), direction.getZ())));
            entity.teleport(location);
        }
    }

    /**
     * Equips or unequips the configured main-hand item.
     *
     * @param equipped whether the item should be equipped
     */
    public void setMainHandEquipped(boolean equipped) {
        if (!isAlive() || entity.getEquipment() == null) {
            return;
        }
        entity.getEquipment().setItemInMainHand(equipped ? itemInMainHand : null);
    }

    /**
     * Equips or unequips the configured off-hand item.
     *
     * @param equipped whether the item should be equipped
     */
    public void setOffHandEquipped(boolean equipped) {
        if (!isAlive() || entity.getEquipment() == null) {
            return;
        }
        entity.getEquipment().setItemInOffHand(equipped ? itemInOffHand : null);
    }

    /**
     * Attaches a non-interactive {@link ItemDisplay} entity with local 3D translation offset.
     *
     * @param offset       local offset relative to base entity origin and facing yaw
     * @param configurator optional configuration consumer
     * @return the created ItemDisplay attachment
     */
    public ItemDisplay attachItemDisplay(Vector offset, Consumer<ItemDisplay> configurator) {
        if (!isAlive()) {
            return null;
        }
        Vector localOffset = offset != null ? offset : new Vector(0, 0, 0);
        Location spawnLoc = entity.getLocation().clone().add(localOffset);
        ItemDisplay display = entity.getWorld().spawn(spawnLoc, ItemDisplay.class, d -> {
            if (configurator != null) {
                configurator.accept(d);
            }
        });
        addAttachment(display, AttachmentRole.VISUAL, localOffset, true, false);
        return display;
    }

    /**
     * Attaches a non-interactive {@link BlockDisplay} entity with local 3D translation offset.
     *
     * @param offset       local offset relative to base entity origin and facing yaw
     * @param configurator optional configuration consumer
     * @return the created BlockDisplay attachment
     */
    public BlockDisplay attachBlockDisplay(Vector offset, Consumer<BlockDisplay> configurator) {
        if (!isAlive()) {
            return null;
        }
        Vector localOffset = offset != null ? offset : new Vector(0, 0, 0);
        Location spawnLoc = entity.getLocation().clone().add(localOffset);
        BlockDisplay display = entity.getWorld().spawn(spawnLoc, BlockDisplay.class, d -> {
            if (configurator != null) {
                configurator.accept(d);
            }
        });
        addAttachment(display, AttachmentRole.VISUAL, localOffset, true, false);
        return display;
    }

    /**
     * Attaches a multi-part hittable {@link Interaction} hitbox entity.
     *
     * @param offset       local offset relative to base entity origin and facing yaw
     * @param width        hitbox width
     * @param height       hitbox height
     * @param configurator optional configuration consumer
     * @return the created Interaction attachment
     */
    public Interaction attachHitbox(Vector offset, float width, float height, Consumer<Interaction> configurator) {
        if (!isAlive()) {
            return null;
        }
        Vector localOffset = offset != null ? offset : new Vector(0, 0, 0);
        Location spawnLoc = entity.getLocation().clone().add(localOffset);
        Interaction interaction = entity.getWorld().spawn(spawnLoc, Interaction.class, i -> {
            i.setInteractionWidth(width);
            i.setInteractionHeight(height);
            if (configurator != null) {
                configurator.accept(i);
            }
        });
        addAttachment(interaction, AttachmentRole.HITBOX, localOffset, true, false);
        return interaction;
    }

    /**
     * Attaches an entity using {@link AttachmentRole#SUBORDINATE}.
     *
     * @param attachmentEntity entity to attach
     */
    public void addAttachment(Entity attachmentEntity) {
        addAttachment(attachmentEntity, AttachmentRole.SUBORDINATE, new Vector(0, 0, 0), true, false);
    }

    /**
     * Attaches an entity with the given role.
     *
     * @param attachmentEntity entity to attach
     * @param role             attachment role
     */
    public void addAttachment(Entity attachmentEntity, AttachmentRole role) {
        addAttachment(attachmentEntity, role, new Vector(0, 0, 0), true, false);
    }

    /**
     * Attaches an entity with the given role and local offset.
     *
     * @param attachmentEntity entity to attach
     * @param role             attachment role
     * @param offset           local 3D offset
     */
    public void addAttachment(Entity attachmentEntity, AttachmentRole role, Vector offset) {
        addAttachment(attachmentEntity, role, offset, true, false);
    }

    /**
     * Attaches an entity with full offset and rotation synchronization configuration.
     *
     * @param attachmentEntity entity to attach
     * @param role             attachment role
     * @param offset           local 3D offset
     * @param syncYaw          whether horizontal rotation follows base yaw
     * @param syncPitch        whether vertical rotation follows base pitch
     */
    public void addAttachment(Entity attachmentEntity, AttachmentRole role, Vector offset, boolean syncYaw, boolean syncPitch) {
        if (attachmentEntity == null) {
            return;
        }
        Vector vector = offset != null ? offset : new Vector(0, 0, 0);
        attachments.add(new EntityAttachment(attachmentEntity, role, vector, syncYaw, syncPitch));
        BlightedEntitiesListener.registerAttachment(attachmentEntity, this);

        if (entity != null) {
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING, entity.getUniqueId().toString());
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_ROLE_KEY, PersistentDataType.STRING, role.name());
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_OFFSET_X_KEY, PersistentDataType.DOUBLE, vector.getX());
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_OFFSET_Y_KEY, PersistentDataType.DOUBLE, vector.getY());
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_OFFSET_Z_KEY, PersistentDataType.DOUBLE, vector.getZ());
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_SYNC_YAW_KEY, PersistentDataType.BYTE, (byte) (syncYaw ? 1 : 0));
            attachmentEntity.getPersistentDataContainer().set(ATTACHMENT_SYNC_PITCH_KEY, PersistentDataType.BYTE, (byte) (syncPitch ? 1 : 0));
        }

        if (attachmentEntity instanceof Display display) {
            display.setTeleportDuration(1);
            display.setInterpolationDuration(1);
        }

        if (attachmentEntity instanceof LivingEntity living) {
            EntityEquipment equipment = living.getEquipment();
            if (equipment != null) {
                zeroEquipmentDropChances(equipment);
            }
        }
        attachmentEntity.addScoreboardTag(FAST_PASS_TAG);
    }

    /**
     * Synchronizes all registered attachments to their relative world position based on base location and facing yaw.
     */
    public void syncAttachments() {
        if (!isAlive() || attachments.isEmpty()) {
            return;
        }

        Location baseLocation = entity.getLocation();
        double radians = Math.toRadians(baseLocation.getYaw());
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        for (EntityAttachment attachment : attachments) {
            Entity attachedEntity = attachment.entity();
            if (attachedEntity == null || !attachedEntity.isValid() || attachedEntity.isDead()) {
                attachments.remove(attachment);
                if (attachedEntity != null) {
                    BlightedEntitiesListener.unregisterAttachment(attachedEntity);
                }
                continue;
            }

            if (entity != null && entity.getPassengers().contains(attachedEntity)) {
                continue;
            }

            Vector offset = attachment.localOffset();
            double xPrime = baseLocation.getX() + (offset.getX() * cos - offset.getZ() * sin);
            double zPrime = baseLocation.getZ() + (offset.getX() * sin + offset.getZ() * cos);
            double yPrime = baseLocation.getY() + offset.getY();

            // When syncYaw/syncPitch are false, preserve current yaw/pitch
            float targetYaw = attachment.syncYaw() ? baseLocation.getYaw() : 0.0f;
            float targetPitch = attachment.syncPitch() ? baseLocation.getPitch() : 0.0f;

            Location targetLocation = new Location(
                    baseLocation.getWorld(),
                    xPrime,
                    yPrime,
                    zPrime,
                    attachment.syncYaw() ? baseLocation.getYaw() : attachedEntity.getLocation().getYaw(),
                    attachment.syncPitch() ? baseLocation.getPitch() : attachedEntity.getLocation().getPitch()
            );

            attachedEntity.teleport(targetLocation);
        }
    }

    /**
     * Removes and destroys all attached entities.
     */
    public void killAllAttachments() {
        if (attachments.isEmpty()) {
            return;
        }

        for (EntityAttachment attachment : attachments) {
            Entity attachmentEntity = attachment.entity();

            if (attachmentEntity == null) {
                continue;
            }

            BlightedEntitiesListener.unregisterAttachment(attachmentEntity);
            attachmentEntity.remove();
        }
        attachments.clear();
    }

    /**
     * Checks whether a living body attachment is currently present.
     *
     * @return {@code true} if a living body attachment exists
     */
    public boolean hasLivingBodyAttachment() {
        for (EntityAttachment attachment : attachments) {
            if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prevents multi-hit exploits (e.g. Sweeping Edge or AoE hitting multiple hitboxes in the same tick).
     *
     * @param damager attacker entity or projectile shooter
     * @return {@code true} if damage was already processed this tick by the same attacker
     */
    public boolean shouldBlockSameTickDamage(Entity damager) {
        long currentTick = entity != null && entity.getWorld() != null ? entity.getWorld().getGameTime() : System.currentTimeMillis();
        UUID damagerUuid = damager != null ? damager.getUniqueId() : null;

        if (currentTick == lastDamageTick && Objects.equals(damagerUuid, lastDamagerUuid)) {
            return true;
        }

        lastDamageTick = currentTick;
        lastDamagerUuid = damagerUuid;
        return false;
    }

    /**
     * Registers a component with this entity.
     *
     * <p>Components added after initialization are initialized immediately.</p>
     *
     * @param component component to register
     */
    public void addComponent(EntityComponent component) {
        components.put(component.getId(), component);
        if (componentsInitialized && entity != null) {
            component.onInit(entity);
        }
    }

    /**
     * Returns a registered component by identifier.
     *
     * @param id  component identifier
     * @param <T> expected component type
     * @return component, or {@code null} if not registered
     */
    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> T getComponent(String id) {
        return (T) components.get(id);
    }

    /**
     * Returns a snapshot of all registered components.
     *
     * @return registered components
     */
    public Collection<EntityComponent> getComponents() {
        return new ArrayList<>(components.values());
    }

    /**
     * Updates the boss bar progress to match the entity's health.
     */
    public void updateBossBar() {
        if (bossBar == null) {
            return;
        }
        if (!isAlive()) {
            removeBossBar();
            return;
        }
        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = (maxHealthAttribute != null && maxHealthAttribute.getValue() > 0)
                ? maxHealthAttribute.getValue()
                : Math.max(1, this.maxHealth);
        double progress = entity.getHealth() / maxHealth;
        bossBar.setProgress(Math.clamp(progress, 0.0, 1.0));
    }

    /**
     * Sets the boss bar color and style.
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
     * Removes the boss bar and all of its viewers.
     */
    public void removeBossBar() {
        if (bossBar == null) {
            return;
        }
        bossBar.removeAll();
        bossBar = null;
    }

    /**
     * Executes the configured loot table at the given location.
     *
     * @param location drop location and loot context
     * @param player   associated player
     */
    public void dropLoot(Location location, BlightedPlayer player) {
        if (lootTable == null) {
            return;
        }

        World world = Objects.requireNonNull(location.getWorld());
        Biome biome = world.getBiome(location);
        LootContext context = new LootContext(
                player,
                world,
                biome,
                location,
                ThreadLocalRandom.current(),
                null
        );
        lootTable.execute(context);
    }

    /**
     * Sets an additional Bukkit attribute value.
     *
     * @param attribute attribute to configure
     * @param value     base attribute value
     */
    public void addAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }

    /**
     * Returns the first immunity rule matching a damage event.
     *
     * @param target entity receiving the event
     * @param event  damage event
     * @return matching immunity, or {@code null} if none applies
     */
    public EntityImmunity getTriggeredImmunity(LivingEntity target, EntityDamageEvent event) {
        if (immunities.isEmpty()) {
            return null;
        }
        for (EntityImmunity rule : immunities) {
            if (rule.isImmune(target, event)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * Returns the highest resistance percentage matching a damage event.
     *
     * @param target entity receiving the event
     * @param event  damage event
     * @return resistance percentage (0.0 if no resistance applies)
     */
    public double getResistancePercent(LivingEntity target, EntityDamageEvent event) {
        if (resistances.isEmpty()) {
            return 0.0;
        }
        double highestResistance = 0.0;
        for (Map.Entry<DamageType, Double> entry : resistances.entrySet()) {
            DamageType type = entry.getKey();
            if (type.isImmune(target, event)) {
                highestResistance = Math.max(highestResistance, entry.getValue());
            }
        }
        return highestResistance;
    }

    private void initComponents() {
        if (componentsInitialized) {
            return;
        }
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
        if (runtimeInitialized) {
            return;
        }
        onDefineBehavior();
        if (bossBar != null) startBossBarTask();

        addCoreAbility(1L, 1L, this::syncAttachments);
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
        if (defense > 0) {
            setAttribute(Attribute.ARMOR, defense);
        }
        attributes.forEach(this::setAttribute);

        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            entity.setHealth(maxHealthAttribute.getValue());
        }

        lockEntityProperties();
    }

    private void rehydrateAttributes() {
        setAttribute(Attribute.MAX_HEALTH, maxHealth);
        setAttribute(Attribute.ATTACK_DAMAGE, damage);
        if (defense > 0) {
            setAttribute(Attribute.ARMOR, defense);
        }
        attributes.forEach(this::setAttribute);

        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            entity.setHealth(Math.min(entity.getHealth(), maxHealthAttribute.getValue()));
        }

        lockEntityProperties();
    }

    private void lockEntityProperties() {
        entity.setRemoveWhenFarAway(!isBoss);
        entity.setPersistent(isBoss);
        entity.setCanPickupItems(false);
    }

    private void setAttribute(Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
            instance.removeModifier(modifier);
        }
        instance.setBaseValue(value);
    }

    private void configureEquipment() {
        if (armor == null && itemInMainHand == null && itemInOffHand == null) {
            return;
        }
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) {
            return;
        }

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
        if (annotation == null) {
            return;
        }
        Collections.addAll(this.immunities, annotation.value());
    }

    /**
     * Adds an immunity rule to this entity.
     *
     * @param immunity immunity rule
     */
    public void addImmunity(EntityImmunity immunity) {
        this.immunities.add(immunity);
    }

    private void initResistanceRules() {
        EntityResistances container = getClass().getAnnotation(EntityResistances.class);
        if (container != null) {
            for (EntityResistance rule : container.value()) {
                this.resistances.put(rule.type(), rule.percent());
            }
        }
        EntityResistance single = getClass().getAnnotation(EntityResistance.class);
        if (single != null) {
            this.resistances.put(single.type(), single.percent());
        }
    }

    /**
     * Adds a damage resistance percentage rule to this entity.
     *
     * @param type    damage type
     * @param percent percentage resisted (0 - 100)
     */
    public void addResistance(DamageType type, double percent) {
        this.resistances.put(type, percent);
    }

    private void scheduleAbility(LifecycleTaskManager manager, long delayTicks, long periodTicks, Runnable action) {
        manager.addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (!isAlive()) {
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
                if (!isAlive()) {
                    return;
                }
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
        if (bossBar != null) {
            return;
        }
        if (entityType == EntityType.WITHER || entityType == EntityType.ENDER_DRAGON) {
            return;
        }
        bossBar = Bukkit.createBossBar("§f§l" + name, bossBarColor, bossBarStyle);
        bossBar.setProgress(1.0);
    }

    private void startBossBarTask() {
        addCoreAbility(10L, 20L, this::manageBossBarViewers);
    }

    private void manageBossBarViewers() {
        if (bossBar == null) {
            return;
        }
        if (!isAlive()) {
            removeBossBar();
            return;
        }

        updateBossBar();

        World world = entity.getWorld();
        Location entityLocation = entity.getLocation();
        double rangeSquared = BOSS_BAR_RANGE * BOSS_BAR_RANGE;

        for (Player player : new ArrayList<>(bossBar.getPlayers())) {
            if (!player.isOnline() || player.getWorld() != world || player.getLocation().distanceSquared(entityLocation) > rangeSquared) {
                bossBar.removePlayer(player);
            }
        }

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(entityLocation) <= rangeSquared && !bossBar.getPlayers().contains(player)) {
                bossBar.addPlayer(player);
            }
        }
    }

    /**
     * Returns whether the bound entity is currently valid and alive.
     *
     * @return {@code true} if the entity can still be used
     */
    protected boolean isAlive() {
        return entity != null && entity.isValid() && !entity.isDead();
    }

    /**
     * Creates a detached copy of this entity definition.
     *
     * <p>The cloned instance is not bound to a Bukkit entity and has no active
     * runtime tasks or boss bar.</p>
     *
     * @return detached clone
     */
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
        if (this.armor == null) {
            return null;
        }

        ItemStack[] cloned = new ItemStack[this.armor.length];
        for (int i = 0; i < this.armor.length; i++) cloned[i] = cloneItem(this.armor[i]);
        return cloned;
    }

    private ItemStack cloneItem(ItemStack item) {
        return item != null ? item.clone() : null;
    }
}
