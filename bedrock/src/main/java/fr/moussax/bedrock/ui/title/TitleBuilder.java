package fr.moussax.bedrock.ui.title;

import fr.moussax.bedrock.sound.SoundCue;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Fluent builder for constructing and dispatching titles to players.
 */
public final class TitleBuilder {

    private String title = "";
    private String subtitle = "";
    private TimeableTitle times = TimeableTitle.DEFAULT;
    private SoundCue soundCue;

    TitleBuilder() {
    }

    /**
     * Sets the main title text.
     *
     * @param title text to display in the main title
     * @return this builder
     */
    public TitleBuilder title(@Nullable String title) {
        this.title = title != null ? title : "";
        return this;
    }

    /**
     * Sets the subtitle text.
     *
     * @param subtitle text to display below the main title
     * @return this builder
     */
    public TitleBuilder subtitle(@Nullable String subtitle) {
        this.subtitle = subtitle != null ? subtitle : "";
        return this;
    }

    /**
     * Sets the timing parameters for this title.
     *
     * @param times timing configuration
     * @return this builder
     */
    public TitleBuilder times(@NonNull TimeableTitle times) {
        this.times = Objects.requireNonNull(times, "times");
        return this;
    }

    /**
     * Sets the display timing parameters using server tick counts.
     *
     * @param fadeIn  fade-in ticks
     * @param stay    stay ticks
     * @param fadeOut fade-out ticks
     * @return this builder
     */
    public TitleBuilder times(int fadeIn, int stay, int fadeOut) {
        return times(TimeableTitle.of(fadeIn, stay, fadeOut));
    }

    /**
     * Sets the display timing parameters using {@link Duration} values.
     *
     * @param fadeIn  fade-in duration
     * @param stay    stay duration
     * @param fadeOut fade-out duration
     * @return this builder
     */
    public TitleBuilder times(@NonNull Duration fadeIn, @NonNull Duration stay, @NonNull Duration fadeOut) {
        return times(TimeableTitle.of(fadeIn, stay, fadeOut));
    }

    /**
     * Sets a custom stay duration with default fade transitions.
     *
     * @param stayDuration duration the title remains visible
     * @return this builder
     */
    public TitleBuilder stay(@NonNull Duration stayDuration) {
        return times(TimeableTitle.stay(stayDuration));
    }

    /**
     * Sets a custom stay duration in server ticks with default fade transitions.
     *
     * @param stayTicks ticks the title remains visible
     * @return this builder
     */
    public TitleBuilder stay(int stayTicks) {
        return times(TimeableTitle.stay(stayTicks));
    }

    /**
     * Associates a sound to play when this title is displayed.
     *
     * @param soundCue sound cue to play
     * @return this builder
     */
    public TitleBuilder sound(@Nullable SoundCue soundCue) {
        this.soundCue = soundCue;
        return this;
    }

    /**
     * Associates a Bukkit sound with volume 1.0 and pitch 1.0 to play when this title is displayed.
     *
     * @param sound sound to play
     * @return this builder
     */
    public TitleBuilder sound(@Nullable Sound sound) {
        return sound(sound, 1.0f, 1.0f);
    }

    /**
     * Associates a Bukkit sound with custom volume and pitch to play when this title is displayed.
     *
     * @param sound  sound to play
     * @param volume sound volume
     * @param pitch  sound pitch
     * @return this builder
     */
    public TitleBuilder sound(@Nullable Sound sound, float volume, float pitch) {
        if (sound == null) {
            this.soundCue = null;
            return this;
        }
        this.soundCue = new SoundCue(sound, volume, pitch, 0L);
        return this;
    }

    public @NonNull String getTitle() {
        return title;
    }

    public @NonNull String getSubtitle() {
        return subtitle;
    }

    public @NonNull TimeableTitle getTimes() {
        return times;
    }

    public @Nullable SoundCue getSoundCue() {
        return soundCue;
    }

    /**
     * Dispatches the configured title to the specified player.
     *
     * @param player player to receive the title
     */
    public void send(@NonNull Player player) {
        Objects.requireNonNull(player, "player");

        // Minecraft clients skip rendering the subtitle if the main title is empty or null.
        // A single space " " satisfies client constraints while rendering only the subtitle visually.
        String effectiveTitle = title.isEmpty() && !subtitle.isEmpty() ? " " : title;

        player.sendTitle(effectiveTitle, subtitle, times.fadeIn(), times.stay(), times.fadeOut());

        if (soundCue != null) {
            soundCue.play(player);
        }
    }

    /**
     * Dispatches the configured title to multiple players.
     *
     * @param players collection of players to receive the title
     */
    public void send(@NonNull Collection<? extends Player> players) {
        Objects.requireNonNull(players, "players");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                send(player);
            }
        }
    }

    /**
     * Dispatches the configured title to an array of players.
     *
     * @param players array of players to receive the title
     */
    public void send(@NonNull Player... players) {
        send(Arrays.asList(players));
    }

    /**
     * Dispatches the configured title to all online players on the server.
     */
    public void broadcast() {
        send(Bukkit.getOnlinePlayers());
    }
}
