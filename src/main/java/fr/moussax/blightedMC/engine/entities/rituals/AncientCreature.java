package fr.moussax.blightedMC.engine.entities.rituals;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedMC.utils.Formatter;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Base class for ancient creatures summoned through an {@link AncientRitual}.
 *
 * <p>Ancient creatures are boss entities with a limited amount of time to be
 * defeated. A hologram attached to the creature displays its remaining time,
 * summoner, name, and optionally its currently active ability.</p>
 *
 * <p>The creature automatically collapses when its time allowance expires and
 * plays the appropriate defeat or collapse effects during its lifecycle.</p>
 */
public abstract class AncientCreature extends BlightedEntity {

    private static final int DEFAULT_TIME_ALLOWANCE_SECONDS = 240;
    private static final double HOLOGRAM_RESCAN_RADIUS = 8.0;
    private static final NamespacedKey HOLOGRAM_KEY =
            new NamespacedKey(BlightedMC.getInstance(), "ancient_creature_hologram");

    @Getter
    @Setter
    protected int timeAllowance;
    @Getter
    protected int remainingSeconds;
    @Getter
    @Setter
    protected String summonerName = "Unknown";

    @Getter
    private String activeAbilityName;

    private TextDisplay hologram;
    private boolean isCollapsing = false;

    /**
     * Creates an ancient creature using the default time allowance.
     *
     * @param name       creature display name
     * @param maxHealth  creature maximum health
     * @param entityType Bukkit entity type
     */
    public AncientCreature(@NonNull String name, int maxHealth, EntityType entityType) {
        this(name, maxHealth, 1, 0, entityType, DEFAULT_TIME_ALLOWANCE_SECONDS);
    }

    /**
     * Creates an ancient creature using the default time allowance.
     *
     * @param name       creature display name
     * @param maxHealth  creature maximum health
     * @param damage     base attack damage
     * @param entityType Bukkit entity type
     */
    public AncientCreature(@NonNull String name, int maxHealth, int damage, EntityType entityType) {
        this(name, maxHealth, damage, 0, entityType, DEFAULT_TIME_ALLOWANCE_SECONDS);
    }

    /**
     * Creates an ancient creature using the default time allowance.
     *
     * @param name       creature display name
     * @param maxHealth  creature maximum health
     * @param damage     base attack damage
     * @param defense    base armor value
     * @param entityType Bukkit entity type
     */
    public AncientCreature(@NonNull String name, int maxHealth, int damage, int defense, EntityType entityType) {
        this(name, maxHealth, damage, defense, entityType, DEFAULT_TIME_ALLOWANCE_SECONDS);
    }

    /**
     * Creates an ancient creature using the specified time allowance.
     *
     * @param name                 creature display name
     * @param maxHealth            creature maximum health
     * @param entityType           Bukkit entity type
     * @param timeAllowanceSeconds time allowed to defeat the creature, in seconds
     */
    public AncientCreature(@NonNull String name, int maxHealth, EntityType entityType, int timeAllowanceSeconds) {
        this(name, maxHealth, 1, 0, entityType, timeAllowanceSeconds);
    }

    /**
     * Creates an ancient creature using the specified damage and time allowance.
     *
     * @param name                 creature display name
     * @param maxHealth            creature maximum health
     * @param damage               base attack damage
     * @param entityType           Bukkit entity type
     * @param timeAllowanceSeconds time allowed to defeat the creature, in seconds
     */
    public AncientCreature(@NonNull String name, int maxHealth, int damage, EntityType entityType, int timeAllowanceSeconds) {
        this(name, maxHealth, damage, 0, entityType, timeAllowanceSeconds);
    }

    /**
     * Creates an ancient creature using the specified combat stats and time allowance.
     *
     * @param name                 creature display name
     * @param maxHealth            creature maximum health
     * @param damage               base attack damage
     * @param defense              base armor value
     * @param entityType           Bukkit entity type
     * @param timeAllowanceSeconds time allowed to defeat the creature, in seconds
     */
    public AncientCreature(@NonNull String name, int maxHealth, int damage, int defense, EntityType entityType, int timeAllowanceSeconds) {
        super(name, maxHealth, damage, defense, entityType);
        this.timeAllowance = timeAllowanceSeconds;
        this.remainingSeconds = timeAllowanceSeconds;
        setBoss(true);
    }

    /**
     * Sets the player who summoned this creature.
     *
     * <p>The player's name is stored for display in the creature's hologram.
     * If no player is supplied, the summoner is set to {@code "Unknown"}.</p>
     *
     * @param player summoning player, or {@code null}
     */
    public void setSummoner(@Nullable Player player) {
        this.summonerName = (player != null) ? player.getName() : "Unknown";
    }

    /**
     * Sets the name of the ability currently active on this creature.
     *
     * <p>The attached hologram is updated immediately after the ability name
     * changes.</p>
     *
     * @param abilityName active ability name, or {@code null} when no ability is active
     */
    public void setActiveAbilityName(@Nullable String abilityName) {
        this.activeAbilityName = abilityName;
        updateHologramText();
    }

    /**
     * Clears the currently active ability and updates the hologram.
     */
    public void clearActiveAbility() {
        this.activeAbilityName = null;
        updateHologramText();
    }

    /**
     * Spawns the creature and creates its attached hologram.
     *
     * @param location location at which the creature is spawned
     * @return spawned creature entity
     */
    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawned = super.spawn(location);
        createHologram(spawned.getLocation());
        return spawned;
    }

    /**
     * Restores the creature's hologram after the entity is rehydrated.
     *
     * <p>The hologram is located among nearby entities using the persistent
     * owner and hologram markers and reattached as a passenger when found.</p>
     *
     * @param existing existing creature entity being rehydrated
     */
    @Override
    protected void onRehydrate(LivingEntity existing) {
        String ownerUuid = existing.getUniqueId().toString();

        for (Entity nearby : existing.getNearbyEntities(HOLOGRAM_RESCAN_RADIUS, HOLOGRAM_RESCAN_RADIUS, HOLOGRAM_RESCAN_RADIUS)) {
            if (!(nearby instanceof TextDisplay display)) continue;

            PersistentDataContainer pdc = display.getPersistentDataContainer();
            String attachedOwner = pdc.get(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING);
            if (!ownerUuid.equals(attachedOwner)) continue;

            if (pdc.has(HOLOGRAM_KEY, PersistentDataType.BYTE)) {
                this.hologram = display;
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDefineBehavior() {
        super.onDefineBehavior();
        addCoreAbility(20L, 20L, this::handleTimeTick);
    }

    private void createHologram(Location spawnLocation) {
        if (spawnLocation.getWorld() == null || entity == null) return;

        TextDisplay display = (TextDisplay) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.TEXT_DISPLAY);

        display.setBillboard(Display.Billboard.CENTER);
        display.setDefaultBackground(false);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setShadowed(true);
        display.setPersistent(false);

        float verticalSeatOffset = (float) (entity.getHeight() * 0.10f + 0.10f);
        Transformation transformation = new Transformation(
                new Vector3f(0f, verticalSeatOffset, 0f),
                new AxisAngle4f(),
                new Vector3f(1f, 1f, 1f),
                new AxisAngle4f()
        );
        display.setTransformation(transformation);

        display.getPersistentDataContainer().set(HOLOGRAM_KEY, PersistentDataType.BYTE, (byte) 1);

        this.hologram = display;
        this.hologram.setText(buildHologramContent());

        addAttachment(this.hologram, AttachmentRole.VISUAL);
        entity.addPassenger(this.hologram);
    }

    private String buildHologramContent() {
        String timeFormatted = Formatter.formatTime(Math.max(0, remainingSeconds));

        String firstLine = (activeAbilityName != null && !activeAbilityName.isEmpty())
                ? activeAbilityName + " §c" + timeFormatted
                : "§c" + timeFormatted;

        return firstLine + "\n"
                + "§bSpawned by: §3" + summonerName + "\n"
                + "§4⚚ §f" + name;
    }

    private void handleTimeTick() {
        if (!isAlive() || isCollapsing) return;

        remainingSeconds--;
        updateHologramText();

        if (remainingSeconds <= 0) {
            handleTimeExpiration();
        }
    }

    private void updateHologramText() {
        if (hologram != null && hologram.isValid()) {
            hologram.setText(buildHologramContent());
        }
    }

    private void handleTimeExpiration() {
        if (isCollapsing || !isAlive()) return;
        this.isCollapsing = true;

        Location location = entity.getLocation().clone();

        Bukkit.broadcastMessage("§5 ☤ §f" + summonerName + " §dfailed to defeat the §4" + name + "§d on time! The Ancient Creature returned to the forbidden realm.");
        SoundSequence.ANCIENT_MOB_COLLAPSE.play(location);

        entity.setAI(false);
        entity.setInvulnerable(true);

        RitualAnimations.playCollapseAnimation(BlightedMC.getInstance(), location, () -> {
            cleanup();
            if (entity != null && entity.isValid()) {
                entity.remove();
            }
        });
    }

    @Override
    public void onDeath(Location location) {
        super.onDeath(location);
        SoundSequence.ANCIENT_MOB_DEFEAT.play(location);
    }
}
