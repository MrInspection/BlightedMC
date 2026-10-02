package fr.moussax.blightedSMP.engine.entities.boss;

import lombok.Getter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jspecify.annotations.NonNull;

/**
 * Fluent builder for configuring boss bar appearance and view distance.
 */
@Getter
public final class BossBarBuilder {

    private BarColor color = BarColor.RED;
    private BarStyle style = BarStyle.SOLID;
    private double viewRadius = EntityBossBarController.DEFAULT_VIEW_RADIUS;

    /**
     * Sets the bar display color.
     *
     * @param color boss bar color
     * @return this builder
     */
    public BossBarBuilder color(@NonNull BarColor color) {
        this.color = color;
        return this;
    }

    /**
     * Sets the segmentation style of the boss bar.
     *
     * @param style boss bar style
     * @return this builder
     */
    public BossBarBuilder style(@NonNull BarStyle style) {
        this.style = style;
        return this;
    }

    /**
     * Sets the player detection radius in blocks within which the boss bar is visible.
     *
     * @param viewRadius visibility radius in blocks
     * @return this builder
     */
    public BossBarBuilder viewRadius(double viewRadius) {
        this.viewRadius = viewRadius;
        return this;
    }
}
