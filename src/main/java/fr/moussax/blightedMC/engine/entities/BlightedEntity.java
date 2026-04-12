package fr.moussax.blightedMC.engine.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedMC.engine.entities.attachment.EntityAttachment;
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
 * Base abstraction for all custom entities.
 *
 * <p>Manages the full lifecycle: spawning, attributes, equipment, boss bar,
 * immunity rules, phase transitions, attachments, scheduled abilities, loot, and cleanup.</p>
 *
 * <h3>Phases</h3>
 * <p>Bosses with multiple phases override {@link #onPhaseTransition(int)} and call
 * {@link #transitionToPhase(int)} to cancel all current abilities and re-register
 * phase-specific ones cleanly. Phase transitions are typically triggered from
 * {@link #onDamageTaken(EntityDamageEvent)} by checking health thresholds.</p>
 *
 * <pre>{@code
 * @Override
 * public void onDefineBehavior() {
 *     transitionToPhase(1); // start at phase 1
 * }
 *
 * @Override
 * public void onDamageTaken(EntityDamageEvent event) {
 *     double remaining = entity.getHealth() - event.getFinalDamage();
 *     if (remaining <= maxHealth * 0.5 && getCurrentPhase() < 2) {
 *         transitionToPhase(2);
 *     }
 * }
 *
 * @Override
 * protected void onPhaseTransition(int phase) {
 *     if (phase == 1) {
 *         addAbility(100L, 200L, this::basicSlam);
 *     } else if (phase == 2) {
 *         addAbility(60L, 120L, this::basicSlam);
 *         addAbility(40L, 300L, this::enrageBeam);
 *     }
 * }
 * }</pre>
 *
 * <h3>Attachment roles</h3>
 * <ul>
 *   <li>{@link AttachmentRole#BODY} — structural hitbox. Damage is redirected to the owner.
 *       Body death kills the owner.</li>
 *   <li>{@link AttachmentRole#DEPENDENT} — subordinate. Dies when the owner dies,
 *       does not affect the owner otherwise.</li>
 * </ul>
 */
public abstract class BlightedEntity implements Cloneable {

    public static final NamespacedKey ENTITY_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_entity_id");
    public static final String FAST_PASS_TAG = "blighted_opt";
    private static final double BOSS_BAR_RANGE = 60.0;

    public Set<EntityAttachment> attachments = new CopyOnWriteArraySet<>();
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
    private LifecycleTaskManager lifecycleTasks = new LifecycleTaskManager();
    private List<EntityImmunity> immunities = Collections.emptyList();
    private boolean runtimeInitialized = false;

    @Getter
    private int currentPhase = 0;

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

    public LivingEntity spawn(Location location) {
        if (entityType == null) throw new IllegalStateException("EntityType cannot be null");

        initImmunityRules();
        entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);

        entity.addScoreboardTag(FAST_PASS_TAG);
        entity.getPersistentDataContainer().set(ENTITY_ID_KEY, PersistentDataType.STRING, getEntityId());

        configureAttributes();
        configureEquipment();
        onConfigureAI(entity);
        if (blightedType == BlightedType.BOSS) createBossBar();

        BlightedEntitiesListener.registerEntity(entity, this);
        initRuntime();

        return entity;
    }

    /**
     * Attaches this wrapper to an already-existing entity (e.g. after chunk reload).
     * Attributes and equipment are not re-applied.
     */
    public void attachToExisting(LivingEntity existing) {
        this.entity = existing;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) {
            entity.addScoreboardTag(FAST_PASS_TAG);
        }

        initImmunityRules();
        configureAttributes();
        onConfigureAI(existing);

        if (blightedType == BlightedType.BOSS) createBossBar();
        BlightedEntitiesListener.registerEntity(existing, this);
        initRuntime();
    }

    private void configureAttributes() {
        setAttribute(Attribute.MAX_HEALTH, maxHealth);
        setAttribute(Attribute.ATTACK_DAMAGE, damage);
        if (defense > 0) setAttribute(Attribute.ARMOR, defense);
        attributes.forEach(this::setAttribute);
        entity.setHealth(maxHealth);
        entity.setRemoveWhenFarAway(false);
        entity.setPersistent(true);
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

    private void setAttribute(Attribute attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
            instance.removeModifier(modifier);
        }
        instance.setBaseValue(value);
    }

    private void initRuntime() {
        if (runtimeInitialized) return;
        runtimeInitialized = true;
        onDefineBehavior();
        if (bossBar != null) startBossBarTask();
        lifecycleTasks.scheduleAll();
    }

    /**
     * Called after spawn and chunk reload.
     * Override to modify or replace NMS AI goals.
     * The entity is fully initialized.
     *
     * <pre>{@code
     * @Override
     * protected void onConfigureAI(LivingEntity spawned) {
     *     if (!(spawned instanceof CraftMob craftMob)) return;
     *     EnderMan nms = (EnderMan) craftMob.getHandle();
     *     nms.goalSelector.removeAllGoals(goal -> true);
     *     nms.goalSelector.addGoal(1, new MeleeAttackGoal(nms, 1.0D, false));
     * }
     * }</pre>
     */
    protected void onConfigureAI(LivingEntity spawned) {
    }

    /**
     * Called once during runtime initialization.
     *
     * <p>For phase-based bosses, call {@code transitionToPhase(1)} here
     * instead of registering abilities directly.</p>
     */
    protected void onDefineBehavior() {
    }

    /**
     * Cancels all current abilities and transitions to the given phase,
     * invoking {@link #onPhaseTransition(int)} to re-register phase-specific abilities.
     * Calling this with the current phase is a no-op.
     */
    protected final void transitionToPhase(int phase) {
        if (phase == currentPhase) return;
        currentPhase = phase;
        lifecycleTasks.cancelAll();
        lifecycleTasks = new LifecycleTaskManager();
        if (bossBar != null) startBossBarTask();
        onPhaseTransition(phase);
        lifecycleTasks.scheduleAll();
    }

    /**
     * Called when entering a new phase. Override to register phase-specific abilities.
     *
     * @param phase the phase being entered
     */
    protected void onPhaseTransition(int phase) {
    }

    /**
     * Registers a repeating ability bound to this entity's lifecycle.
     *
     * <pre>{@code
     * addAbility(100L, 200L, this::castFireball);
     * }</pre>
     *
     * @param delayTicks  ticks before first execution
     * @param periodTicks ticks between executions
     * @param action      ability logic
     */
    protected final void addAbility(long delayTicks, long periodTicks, Runnable action) {
        lifecycleTasks.addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (isNotAlive()) {
                    cancel();
                    return;
                }
                action.run();
            }
        }, delayTicks, periodTicks);

        if (canScheduleTask()) lifecycleTasks.scheduleLast();
    }

    /**
     * Registers a one-shot delayed action bound to this entity's lifecycle.
     *
     * <pre>{@code
     * addDelayedAction(60L, this::enrageRoar);
     * }</pre>
     *
     * @param delayTicks ticks before execution
     * @param action     action logic
     */
    protected final void addDelayedAction(long delayTicks, Runnable action) {
        lifecycleTasks.addDelayedTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (isNotAlive()) return;
                action.run();
            }
        }, delayTicks);

        if (canScheduleTask()) lifecycleTasks.scheduleLast();
    }

    private boolean canScheduleTask() {
        return entity != null && !entity.isDead() && runtimeInitialized;
    }

    public void kill() {
        if (isNotAlive()) return;
        cleanup();
        entity.setHealth(0);
    }

    public void cleanup() {
        removeBossBar();
        killAllAttachments();
        lifecycleTasks.cancelAll();
        BlightedEntitiesListener.unregisterEntity(entity);
    }

    /**
     * Hook invoked after the entity dies naturally. Cleanup is already handled by the listener.
     */
    public void onDeath(Location location) {
    }

    /**
     * Hook invoked when this entity takes damage.
     * Typical use: check health thresholds and call {@link #transitionToPhase(int)}.
     */
    public void onDamageTaken(EntityDamageEvent event) {
    }

    public void damage(double amount) {
        if (isNotAlive()) return;
        entity.damage(amount);
    }

    /**
     * Performs a server-calculated melee attack on the target, applying attribute-based
     * damage and knockback, and plays the swing animation.
     */
    public void meleeAttack(Entity target) {
        if (isNotAlive()) return;
        entity.attack(target);
        entity.swingMainHand();
    }

    /**
     * Sets the AI target of this entity.
     * Only effective if the underlying entity is a {@link Mob}.
     */
    public void setAITarget(LivingEntity target) {
        if (entity instanceof Mob mob) mob.setTarget(target);
    }

    /**
     * Returns {@code true} if this entity has an unobstructed line of sight to the target.
     */
    public boolean hasLineOfSight(Entity target) {
        if (isNotAlive()) return false;
        return entity.hasLineOfSight(target);
    }

    /**
     * Returns all survival-mode players within the given radius.
     *
     * @param radius search radius (same value used for x, y, z)
     */
    public List<Player> getNearbyPlayers(double radius) {
        if (isNotAlive()) return Collections.emptyList();
        return entity.getNearbyEntities(radius, radius, radius).stream()
            .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
            .map(e -> (Player) e)
            .toList();
    }

    /**
     * Returns the nearest survival-mode player within the given radius, or {@code null}.
     */
    public Player getNearestPlayer(double radius) {
        Location origin = entity.getLocation();
        return getNearbyPlayers(radius).stream()
            .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(origin)))
            .orElse(null);
    }

    /**
     * Registers an attachment with {@link AttachmentRole#DEPENDENT} role.
     */
    public void addAttachment(Entity attachmentEntity) {
        addAttachment(attachmentEntity, AttachmentRole.DEPENDENT);
    }

    /**
     * Registers an attachment with an explicit role.
     *
     * @param attachmentEntity the Bukkit entity to attach
     * @param role             {@link AttachmentRole#BODY} or {@link AttachmentRole#DEPENDENT}
     */
    public void addAttachment(Entity attachmentEntity, AttachmentRole role) {
        if (attachmentEntity == null) return;

        attachments.add(new EntityAttachment(attachmentEntity, role));
        BlightedEntitiesListener.registerAttachment(attachmentEntity, this);

        if (attachmentEntity instanceof LivingEntity living) {
            EntityEquipment equipment = living.getEquipment();
            if (equipment != null) zeroEquipmentDropChances(equipment);
            living.addScoreboardTag(FAST_PASS_TAG);
        }
    }

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
     * Returns {@code true} if any {@link AttachmentRole#BODY} attachment is still alive.
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

    public EntityImmunity getTriggeredImmunity(LivingEntity target, EntityDamageEvent event) {
        if (immunities.isEmpty()) return null;
        for (EntityImmunity rule : immunities) {
            if (rule.isImmune(target, event)) return rule;
        }
        return null;
    }

    private void createBossBar() {
        if (bossBar != null) return;
        if (entityType == EntityType.WITHER || entityType == EntityType.ENDER_DRAGON) return;

        bossBar = Bukkit.createBossBar("§f§l" + name, bossBarColor, bossBarStyle);
        bossBar.setProgress(1.0);
    }

    private void startBossBarTask() {
        addAbility(10L, 20L, this::manageBossBarViewers);
    }

    private void manageBossBarViewers() {
        if (bossBar == null) return;

        World world = entity.getWorld();
        Location loc = entity.getLocation();
        double rangeSquared = BOSS_BAR_RANGE * BOSS_BAR_RANGE;

        for (Player player : new ArrayList<>(bossBar.getPlayers())) {
            if (!player.isOnline()
                || player.getWorld() != world
                || player.getLocation().distanceSquared(loc) > rangeSquared) {
                bossBar.removePlayer(player);
            }
        }

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(loc) <= rangeSquared
                && !bossBar.getPlayers().contains(player)) {
                bossBar.addPlayer(player);
            }
        }
    }

    public void updateBossBar() {
        if (bossBar == null || entity == null || entity.isDead()) return;
        double progress = entity.getHealth() / Math.max(1, maxHealth);
        bossBar.setProgress(Math.clamp(progress, 0.0, 1.0));
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

    private void zeroEquipmentDropChances(EntityEquipment equipment) {
        equipment.setHelmetDropChance(0f);
        equipment.setChestplateDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setItemInMainHandDropChance(0f);
        equipment.setItemInOffHandDropChance(0f);
    }

    protected boolean isNotAlive() {
        return entity == null || entity.isDead();
    }

    @Override
    public BlightedEntity clone() {
        try {
            BlightedEntity clone = (BlightedEntity) super.clone();
            clone.entity = null;
            clone.bossBar = null;
            clone.runtimeInitialized = false;
            clone.currentPhase = 0;
            clone.attributes = new HashMap<>(this.attributes);
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
        for (int i = 0; i < this.armor.length; i++) cloned[i] = cloneItem(this.armor[i]);
        return cloned;
    }

    private ItemStack cloneItem(ItemStack item) {
        return item != null ? item.clone() : null;
    }
}
