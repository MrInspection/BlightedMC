package fr.moussax.blightedSMP.engine.entities.rituals;

import fr.moussax.bedrock.text.Formatter;
import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.content.sound.BlightedSounds;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedSMP.engine.entities.attachment.EntityAttachmentManager;
import fr.moussax.blightedSMP.engine.entities.registry.EntitiesRegistry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

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
    private static NamespacedKey hologramKey;

    private static NamespacedKey getHologramKey() {
        if (hologramKey == null) {
            hologramKey = new NamespacedKey(BlightedSMP.getInstance(), "ancient_creature_hologram");
        }
        return hologramKey;
    }

    @Getter
    protected int timeAllowance = DEFAULT_TIME_ALLOWANCE_SECONDS;
    @Getter
    protected int remainingSeconds = DEFAULT_TIME_ALLOWANCE_SECONDS;
    @Getter
    @Setter
    protected String summonerName = "Unknown";

    @Getter
    private String activeAbilityName;

    private TextDisplay hologram;
    private boolean isCollapsing = false;

    /**
     * Creates an ancient creature with identity requirements using the default time allowance.
     *
     * @param entityId   unique entity identifier
     * @param name       creature display name
     * @param entityType Bukkit entity type
     */
    public AncientCreature(@NonNull String entityId, @NonNull String name, @NonNull EntityType entityType) {
        super(entityId, name, entityType);
        boss();
    }

    /**
     * Sets the countdown time allowance to defeat this ancient creature.
     *
     * @param duration time allowance
     * @return this creature
     */
    public AncientCreature timeAllowance(@NonNull Duration duration) {
        Objects.requireNonNull(duration, "duration cannot be null");
        if (duration.toSeconds() <= 0) {
            throw new IllegalArgumentException("timeAllowance must be at least 1 second, got: " + duration);
        }
        this.timeAllowance = (int) duration.toSeconds();
        this.remainingSeconds = this.timeAllowance;
        return this;
    }

    /**
     * Sets the countdown time allowance in seconds to defeat this ancient creature.
     *
     * @param seconds time allowance in seconds
     * @return this creature
     */
    public AncientCreature timeAllowance(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("timeAllowance must be positive, got: " + seconds);
        }
        return timeAllowance(Duration.ofSeconds(seconds));
    }

    /**
     * Sets the player who summoned this creature.
     *
     * @param player summoning player, or {@code null} for unknown
     * @return this creature
     */
    public AncientCreature summoner(@Nullable Player player) {
        this.summonerName = (player != null) ? player.getName() : "Unknown";
        updateHologramText();
        return this;
    }

    /**
     * Sets the summoner name directly.
     *
     * @param summonerName summoner name
     * @return this creature
     */
    public AncientCreature summoner(@NonNull String summonerName) {
        this.summonerName = Objects.requireNonNull(summonerName, "summonerName cannot be null");
        updateHologramText();
        return this;
    }

    /**
     * Sets the name of the ability currently active on this creature and updates the hologram.
     *
     * @param abilityName active ability name, or {@code null} when no ability is active
     * @return this creature
     */
    public AncientCreature activeAbility(@Nullable String abilityName) {
        this.activeAbilityName = abilityName;
        updateHologramText();
        return this;
    }

    /**
     * Clears the currently active ability and updates the hologram.
     *
     * @return this creature
     */
    public AncientCreature clearActiveAbility() {
        return activeAbility(null);
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

            PersistentDataContainer persistentDataContainer = display.getPersistentDataContainer();
            String attachedOwner = persistentDataContainer.get(EntityAttachmentManager.ATTACHMENT_OWNER_KEY, PersistentDataType.STRING);
            if (!ownerUuid.equals(attachedOwner)) continue;

            if (persistentDataContainer.has(getHologramKey(), PersistentDataType.BYTE)) {
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

        display.getPersistentDataContainer().set(getHologramKey(), PersistentDataType.BYTE, (byte) 1);

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
        BlightedSounds.ANCIENT_MOB_COLLAPSE.play(location);

        entity.setAI(false);
        entity.setInvulnerable(true);

        RitualAnimations.playCollapseAnimation(BlightedSMP.getInstance(), location, () -> {
            cleanup();
            if (entity != null && entity.isValid()) {
                entity.remove();
            }
        });
    }

    @Override
    public void onDeath(Location location) {
        super.onDeath(location);
        BlightedSounds.ANCIENT_MOB_DEFEAT.play(location);
    }

    @NonNull
    @Override
    public AncientCreature createInstance() {
        BlightedEntity fresh = EntitiesRegistry.create(getEntityId());
        if (fresh instanceof AncientCreature ancient) {
            return ancient;
        }
        return (AncientCreature) super.createInstance();
    }
}
