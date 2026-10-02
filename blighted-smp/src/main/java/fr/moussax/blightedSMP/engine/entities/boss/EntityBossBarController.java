package fr.moussax.blightedSMP.engine.entities.boss;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Manages the lifecycle, appearance, periodic progress synchronization, and proximity viewer tracking of an entity's {@link BossBar}.
 */
public final class EntityBossBarController {

    public static final double DEFAULT_VIEW_RADIUS = 60.0;
    private static final long SCAN_PERIOD_TICKS = 20L;

    private final Plugin plugin;
    private final Supplier<LivingEntity> liveEntitySupplier;
    private final Supplier<String> titleSupplier;
    private final Supplier<EntityType> entityTypeSupplier;

    private BossBar bossBar;
    @Getter
    private BarColor color = BarColor.RED;
    @Getter
    private BarStyle style = BarStyle.SOLID;
    @Getter
    private double viewRadius = DEFAULT_VIEW_RADIUS;
    private BukkitTask viewerTask;

    public EntityBossBarController(
            @NonNull Plugin plugin,
            @NonNull Supplier<LivingEntity> liveEntitySupplier,
            @NonNull Supplier<String> titleSupplier,
            @NonNull Supplier<EntityType> entityTypeSupplier
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.liveEntitySupplier = Objects.requireNonNull(liveEntitySupplier, "liveEntitySupplier cannot be null");
        this.titleSupplier = Objects.requireNonNull(titleSupplier, "titleSupplier cannot be null");
        this.entityTypeSupplier = Objects.requireNonNull(entityTypeSupplier, "entityTypeSupplier cannot be null");
    }

    /**
     * Creates and initializes the Bukkit boss bar if not already created.
     * Wither and Ender Dragon types manage their own boss bars natively and are skipped.
     */
    public void create() {
        if (bossBar != null) {
            return;
        }

        EntityType type = entityTypeSupplier.get();
        if (type == EntityType.WITHER || type == EntityType.ENDER_DRAGON) {
            return;
        }

        bossBar = Bukkit.createBossBar("§f§l" + titleSupplier.get(), color, style);
        bossBar.setProgress(1.0);
        startViewerTask();
    }

    /**
     * Updates the boss bar fill progress based on the entity's current health ratio.
     *
     * @param progress value in range [0.0, 1.0]
     */
    public void updateProgress(double progress) {
        if (bossBar == null) {
            return;
        }
        bossBar.setProgress(Math.clamp(progress, 0.0, 1.0));
    }

    /**
     * Configures the boss bar's color and segment style.
     *
     * @param color bar color
     * @param style bar style
     */
    public void setAppearance(@NonNull BarColor color, @NonNull BarStyle style) {
        this.color = Objects.requireNonNull(color, "color cannot be null");
        this.style = Objects.requireNonNull(style, "style cannot be null");
        if (bossBar != null) {
            bossBar.setColor(color);
            bossBar.setStyle(style);
        }
    }

    /**
     * Configures settings from a {@link BossBarBuilder}.
     *
     * @param builder boss bar builder
     */
    public void configure(@NonNull BossBarBuilder builder) {
        Objects.requireNonNull(builder, "builder cannot be null");
        setAppearance(builder.getColor(), builder.getStyle());
        this.viewRadius = builder.getViewRadius();
    }

    /**
     * Removes all viewers, cancels the periodic scanning task, and destroys the boss bar.
     */
    public void remove() {
        if (viewerTask != null) {
            viewerTask.cancel();
            viewerTask = null;
        }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

    /**
     * Checks if the boss bar is currently active.
     *
     * @return {@code true} if active
     */
    public boolean isActive() {
        return bossBar != null;
    }

    @Nullable
    public BossBar getBukkitBar() {
        return bossBar;
    }

    public void setViewRadius(double viewRadius) {
        this.viewRadius = Math.max(1.0, viewRadius);
    }

    private void startViewerTask() {
        if (viewerTask != null) {
            viewerTask.cancel();
        }
        viewerTask = Bukkit.getScheduler().runTaskTimer(plugin, this::manageViewers, 0L, SCAN_PERIOD_TICKS);
    }

    private void manageViewers() {
        if (bossBar == null) {
            if (viewerTask != null) {
                viewerTask.cancel();
                viewerTask = null;
            }
            return;
        }

        LivingEntity entity = liveEntitySupplier.get();
        if (entity == null || !entity.isValid() || entity.isDead()) {
            remove();
            return;
        }

        // Periodic health synchronization
        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = maxHealthAttribute != null ? Math.max(1.0, maxHealthAttribute.getValue()) : 20.0;
        double currentHealth = Math.clamp(entity.getHealth(), 0.0, maxHealth);
        bossBar.setProgress(currentHealth / maxHealth);

        Location entityLocation = entity.getLocation();
        if (entityLocation.getWorld() == null) {
            return;
        }

        double radiusSquared = viewRadius * viewRadius;

        for (Player player : new ArrayList<>(bossBar.getPlayers())) {
            if (!player.isOnline()
                    || !player.getWorld().equals(entityLocation.getWorld())
                    || player.getLocation().distanceSquared(entityLocation) > radiusSquared) {
                bossBar.removePlayer(player);
            }
        }

        for (Player player : entityLocation.getWorld().getPlayers()) {
            if (player.isValid()
                    && player.getLocation().distanceSquared(entityLocation) <= radiusSquared
                    && !bossBar.getPlayers().contains(player)) {
                bossBar.addPlayer(player);
            }
        }
    }
}
