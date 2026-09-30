package fr.moussax.bedrock.ui.title;

import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Encapsulates display timing parameters for Minecraft titles and subtitles.
 *
 * <p>All timing values are specified in Minecraft server ticks (20 ticks = 1 second).</p>
 *
 * @param fadeIn  number of ticks to transition from transparent to full opacity
 * @param stay    number of ticks the title remains at full opacity
 * @param fadeOut number of ticks to transition from full opacity to transparent
 */
public record TimeableTitle(int fadeIn, int stay, int fadeOut) {

    /**
     * Standard cinematic display: 0.5s fade-in, 3s stay, 1s fade-out (10, 60, 20).
     */
    public static final TimeableTitle DEFAULT = new TimeableTitle(10, 60, 20);

    /**
     * Urgent alert display: instant appearance, 1.5s stay, 0.5s fade-out (0, 30, 10).
     */
    public static final TimeableTitle ALERT = new TimeableTitle(0, 30, 10);

    /**
     * Instant appearance without fade: 0 fade-in, 1.5s stay, 0.5s fade-out (0, 30, 10).
     */
    public static final TimeableTitle INSTANT = new TimeableTitle(0, 30, 10);

    /**
     * Subtle banner display: 0.25s fade-in, 1.75s stay, 0.5s fade-out (5, 35, 10).
     */
    public static final TimeableTitle SUBTITLE_ONLY = new TimeableTitle(5, 35, 10);

    /**
     * Epic or boss encounter display: 1.0s fade-in, 4.5s stay, 1.5s fade-out (20, 90, 30).
     */
    public static final TimeableTitle PROLONGED = new TimeableTitle(20, 90, 30);

    /**
     * Instant flash pulse: 0 fade-in, 0.5s stay, 0.25s fade-out (0, 10, 5).
     */
    public static final TimeableTitle FLASH = new TimeableTitle(0, 10, 5);

    /**
     * Permanent / ambient display refreshed continuously: 0 fade-in, 5s stay, 0.5s fade-out (0, 100, 10).
     */
    public static final TimeableTitle PERMANENT = new TimeableTitle(0, 100, 10);

    /**
     * In-place animation frame: 0 fade-in, 2s stay, 0 fade-out (0, 40, 0).
     */
    public static final TimeableTitle ANIMATION_FRAME = new TimeableTitle(0, 40, 0);

    public TimeableTitle {
        if (fadeIn < 0) fadeIn = 0;
        if (stay < 0) stay = 0;
        if (fadeOut < 0) fadeOut = 0;
    }

    /**
     * Returns the total lifecycle duration of the title in ticks.
     *
     * @return sum of fade-in, stay, and fade-out ticks
     */
    public int totalTicks() {
        return fadeIn + stay + fadeOut;
    }

    /**
     * Creates a timing specification using tick values.
     *
     * @param fadeIn  fade-in ticks
     * @param stay    stay ticks
     * @param fadeOut fade-out ticks
     * @return the timing configuration
     */
    public static TimeableTitle of(int fadeIn, int stay, int fadeOut) {
        return new TimeableTitle(fadeIn, stay, fadeOut);
    }

    /**
     * Creates a timing specification with a custom stay duration, using default 10-tick fade-in and 20-tick fade-out.
     *
     * @param stayTicks stay ticks
     * @return the timing configuration
     */
    public static TimeableTitle stay(int stayTicks) {
        return new TimeableTitle(10, stayTicks, 20);
    }

    /**
     * Creates a timing specification using Java {@link Duration} values.
     *
     * @param fadeIn  fade-in duration
     * @param stay    stay duration
     * @param fadeOut fade-out duration
     * @return the timing configuration
     */
    public static TimeableTitle of(@NonNull Duration fadeIn, @NonNull Duration stay, @NonNull Duration fadeOut) {
        return new TimeableTitle(toTicks(fadeIn), toTicks(stay), toTicks(fadeOut));
    }

    /**
     * Creates a timing specification with a custom stay {@link Duration} and default fades.
     *
     * @param stay stay duration
     * @return the timing configuration
     */
    public static TimeableTitle stay(@NonNull Duration stay) {
        return new TimeableTitle(10, toTicks(stay), 20);
    }

    private static int toTicks(@NonNull Duration duration) {
        return Math.max(0, (int) Math.round(duration.toMillis() / 50.0));
    }
}
