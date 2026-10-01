package fr.moussax.bedrock.ui.title;

import fr.moussax.bedrock.scheduling.PluginContext;
import fr.moussax.bedrock.sound.SoundCue;
import fr.moussax.bedrock.ui.animation.TextAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Static entry point for displaying and composing on-screen Minecraft titles and subtitles.
 *
 * <p>Supports one-shot displays, persistent ambient HUDs (for minigames and lobbies),
 * prioritized modal alerts with automatic persistent restoration, automated countdowns,
 * and high-performance, flicker-free title animations.</p>
 */
public final class Title {

    private Title() {
    }

    /**
     * Initializes or retrieves the shared title service for the given plugin.
     *
     * @param plugin      owning plugin
     * @param periodTicks interval in ticks between periodic HUD evaluation passes
     * @return the active title service
     */
    @NonNull
    public static TitleService initialize(@NonNull Plugin plugin, long periodTicks) {
        TitleService service = TitleService.getOrCreate(plugin);
        service.start(periodTicks);
        return service;
    }

    /**
     * Creates a new fluent {@link TitleBuilder}.
     *
     * @return a new title builder instance
     */
    public static TitleBuilder builder() {
        return new TitleBuilder();
    }

    /**
     * Sends a main title to a player with standard default timings (0.5s in, 3s stay, 1s out).
     *
     * @param player target player
     * @param title  title text
     */
    public static void send(@NonNull Player player, @NonNull String title) {
        send(player, title, null, TimeableTitle.DEFAULT, (SoundCue) null);
    }

    /**
     * Sends a title and subtitle to a player with standard default timings.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     */
    public static void send(@NonNull Player player, @NonNull String title, @Nullable String subtitle) {
        send(player, title, subtitle, TimeableTitle.DEFAULT, (SoundCue) null);
    }

    /**
     * Sends a title and subtitle to a player with custom timing parameters.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param times    timing parameters
     */
    public static void send(@NonNull Player player, @NonNull String title, @Nullable String subtitle, @NonNull TimeableTitle times) {
        send(player, title, subtitle, times, (SoundCue) null);
    }

    /**
     * Sends a title and subtitle accompanied by a Bukkit sound effect.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param times    timing parameters
     * @param sound    sound to play
     */
    public static void send(
            @NonNull Player player,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull TimeableTitle times,
            @Nullable Sound sound
    ) {
        builder().title(title).subtitle(subtitle).times(times).sound(sound).send(player);
    }

    /**
     * Sends a title and subtitle accompanied by a {@link SoundCue}.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param times    timing parameters
     * @param soundCue sound cue to play
     */
    public static void send(
            @NonNull Player player,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull TimeableTitle times,
            @Nullable SoundCue soundCue
    ) {
        builder().title(title).subtitle(subtitle).times(times).sound(soundCue).send(player);
    }

    /**
     * Sends a title and subtitle to a player with a custom stay duration.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param duration stay duration
     */
    public static void send(@NonNull Player player, @NonNull String title, @Nullable String subtitle, @NonNull Duration duration) {
        send(player, title, subtitle, TimeableTitle.stay(duration), (SoundCue) null);
    }

    /**
     * Sends a title and subtitle accompanied by a Bukkit sound effect and stay duration.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param duration stay duration
     * @param sound    sound to play
     */
    public static void send(
            @NonNull Player player,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull Duration duration,
            @Nullable Sound sound
    ) {
        send(player, title, subtitle, TimeableTitle.stay(duration), sound);
    }

    /**
     * Sends a title and subtitle accompanied by a {@link SoundCue} and stay duration.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param duration stay duration
     * @param soundCue sound cue to play
     */
    public static void send(
            @NonNull Player player,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull Duration duration,
            @Nullable SoundCue soundCue
    ) {
        send(player, title, subtitle, TimeableTitle.stay(duration), soundCue);
    }

    /**
     * Sends a main title to a collection of players with standard default timings.
     *
     * @param players target players
     * @param title   title text
     */
    public static void send(@NonNull Collection<? extends Player> players, @NonNull String title) {
        send(players, title, null, TimeableTitle.DEFAULT);
    }

    /**
     * Sends a title and subtitle to a collection of players with standard default timings.
     *
     * @param players  target players
     * @param title    title text
     * @param subtitle subtitle text
     */
    public static void send(@NonNull Collection<? extends Player> players, @NonNull String title, @Nullable String subtitle) {
        send(players, title, subtitle, TimeableTitle.DEFAULT);
    }

    /**
     * Sends a title and subtitle to a collection of players with custom timing parameters.
     *
     * @param players  target players
     * @param title    title text
     * @param subtitle subtitle text
     * @param times    timing parameters
     */
    public static void send(
            @NonNull Collection<? extends Player> players,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull TimeableTitle times
    ) {
        Objects.requireNonNull(players, "players cannot be null");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                send(player, title, subtitle, times);
            }
        }
    }

    /**
     * Sends a title and subtitle to a collection of players with custom stay duration.
     *
     * @param players  target players
     * @param title    title text
     * @param subtitle subtitle text
     * @param duration stay duration
     */
    public static void send(
            @NonNull Collection<? extends Player> players,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull Duration duration
    ) {
        send(players, title, subtitle, TimeableTitle.stay(duration));
    }

    /**
     * Sends a subtitle-only banner to a player using subtle timings.
     *
     * @param player   target player
     * @param subtitle subtitle text
     */
    public static void sendSubtitle(@NonNull Player player, @NonNull String subtitle) {
        sendSubtitle(player, subtitle, TimeableTitle.SUBTITLE_ONLY);
    }

    /**
     * Sends a subtitle-only banner to a player with custom timings.
     *
     * @param player   target player
     * @param subtitle subtitle text
     * @param times    timing parameters
     */
    public static void sendSubtitle(@NonNull Player player, @NonNull String subtitle, @NonNull TimeableTitle times) {
        builder().title(" ").subtitle(subtitle).times(times).send(player);
    }

    /**
     * Sends a subtitle-only banner to a player with a custom stay duration.
     *
     * @param player   target player
     * @param subtitle subtitle text
     * @param duration stay duration
     */
    public static void sendSubtitle(@NonNull Player player, @NonNull String subtitle, @NonNull Duration duration) {
        sendSubtitle(player, subtitle, TimeableTitle.stay(duration));
    }

    /**
     * Broadcasts a title to all online players with standard default timings.
     *
     * @param title title text
     */
    public static void broadcast(@NonNull String title) {
        broadcast(title, null, TimeableTitle.DEFAULT);
    }

    /**
     * Broadcasts a title and subtitle to all online players with standard default timings.
     *
     * @param title    title text
     * @param subtitle subtitle text
     */
    public static void broadcast(@NonNull String title, @Nullable String subtitle) {
        broadcast(title, subtitle, TimeableTitle.DEFAULT);
    }

    /**
     * Broadcasts a title and subtitle to all online players with custom timings.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param times    timing parameters
     */
    public static void broadcast(@NonNull String title, @Nullable String subtitle, @NonNull TimeableTitle times) {
        builder().title(title).subtitle(subtitle).times(times).broadcast();
    }

    /**
     * Broadcasts a title and subtitle to all online players with custom stay duration.
     *
     * @param title    title text
     * @param subtitle subtitle text
     * @param duration stay duration
     */
    public static void broadcast(@NonNull String title, @Nullable String subtitle, @NonNull Duration duration) {
        broadcast(title, subtitle, TimeableTitle.stay(duration));
    }

    /**
     * Sets a persistent ambient title on a player with static text strings.
     *
     * @param player   target player
     * @param title    persistent title text
     * @param subtitle persistent subtitle text
     */
    public static void setPersistent(@NonNull Player player, @Nullable String title, @Nullable String subtitle) {
        setPersistent(player, PersistentTitle.of(title, subtitle));
    }

    /**
     * Sets a persistent ambient title on a player with dynamic text suppliers.
     *
     * @param player           target player
     * @param titleSupplier    dynamic title supplier
     * @param subtitleSupplier dynamic subtitle supplier
     */
    public static void setPersistent(
            @NonNull Player player,
            @NonNull Supplier<@Nullable String> titleSupplier,
            @NonNull Supplier<@Nullable String> subtitleSupplier
    ) {
        setPersistent(player, PersistentTitle.of(titleSupplier, subtitleSupplier));
    }

    /**
     * Sets a persistent ambient title on a player with dynamic per-player functions.
     *
     * @param player           target player
     * @param titleSupplier    dynamic per-player title function
     * @param subtitleSupplier dynamic per-player subtitle function
     */
    public static void setPersistent(
            @NonNull Player player,
            @NonNull Function<Player, @Nullable String> titleSupplier,
            @NonNull Function<Player, @Nullable String> subtitleSupplier
    ) {
        setPersistent(player, PersistentTitle.of(titleSupplier, subtitleSupplier));
    }

    /**
     * Sets a persistent ambient title on a player with a custom display refresh duration.
     *
     * @param player   target player
     * @param title    persistent title text
     * @param subtitle persistent subtitle text
     * @param duration display stay duration
     */
    public static void setPersistent(@NonNull Player player, @Nullable String title, @Nullable String subtitle, @NonNull Duration duration) {
        setPersistent(player, PersistentTitle.of(title, subtitle, duration));
    }

    /**
     * Sets a persistent ambient title on a player with dynamic text suppliers and custom stay duration.
     *
     * @param player           target player
     * @param titleSupplier    dynamic title supplier
     * @param subtitleSupplier dynamic subtitle supplier
     * @param duration         display stay duration
     */
    public static void setPersistent(
            @NonNull Player player,
            @NonNull Supplier<@Nullable String> titleSupplier,
            @NonNull Supplier<@Nullable String> subtitleSupplier,
            @NonNull Duration duration
    ) {
        setPersistent(player, PersistentTitle.of(titleSupplier, subtitleSupplier, TimeableTitle.stay(duration)));
    }

    /**
     * Sets a persistent ambient title on a player using a {@link PersistentTitle} configuration.
     *
     * @param player          target player
     * @param persistentTitle persistent title configuration
     */
    public static void setPersistent(@NonNull Player player, @NonNull PersistentTitle persistentTitle) {
        TitleService service = getServiceOrLazy();
        if (service != null) {
            service.setPersistent(player, persistentTitle);
        } else {
            // Standalone fallback
            String title = persistentTitle.titleSupplier().apply(player);
            String subtitle = persistentTitle.subtitleSupplier().apply(player);
            player.sendTitle(title != null ? title : " ", subtitle != null ? subtitle : "",
                    persistentTitle.times().fadeIn(), persistentTitle.times().stay(), persistentTitle.times().fadeOut());
        }
    }

    /**
     * Sets a persistent ambient title on a collection of players.
     *
     * @param players         target players
     * @param persistentTitle persistent title configuration
     */
    public static void setPersistent(@NonNull Collection<? extends Player> players, @NonNull PersistentTitle persistentTitle) {
        Objects.requireNonNull(players, "players cannot be null");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                setPersistent(player, persistentTitle);
            }
        }
    }

    /**
     * Removes any active persistent ambient title from a player.
     *
     * @param player target player
     */
    public static void clearPersistent(@NonNull Player player) {
        TitleService service = getServiceOrLazy();
        if (service != null) {
            service.clearPersistent(player);
        }
    }

    /**
     * Removes active persistent ambient titles from a collection of players.
     *
     * @param players target players
     */
    public static void clearPersistent(@NonNull Collection<? extends Player> players) {
        Objects.requireNonNull(players, "players cannot be null");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                clearPersistent(player);
            }
        }
    }

    /**
     * Checks if a player has an active persistent ambient title.
     *
     * @param player target player
     * @return true if a persistent title is active
     */
    public static boolean hasPersistent(@NonNull Player player) {
        TitleService service = getServiceOrLazy();
        return service != null && service.hasPersistent(player);
    }

    /**
     * Displays a prioritized modal alert to a player for a given duration.
     *
     * <p>Overriding any active persistent title while active. Upon expiration, the persistent title
     * is automatically restored without a flicker.</p>
     *
     * @param player   target player
     * @param title    alert title text
     * @param subtitle alert subtitle text
     * @param duration display duration
     */
    public static void alert(@NonNull Player player, @NonNull String title, @Nullable String subtitle, @NonNull Duration duration) {
        alert(player, title, subtitle, 0, duration, TimeableTitle.ALERT);
    }

    /**
     * Displays an urgent alert title to a player with instant appearance and a warning bass sound cue.
     *
     * @param player   target player
     * @param title    alert title text
     * @param subtitle alert subtitle text
     */
    public static void alert(@NonNull Player player, @NonNull String title, @Nullable String subtitle) {
        builder()
                .title(title)
                .subtitle(subtitle)
                .times(TimeableTitle.ALERT)
                .sound(Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.8f)
                .send(player);
    }

    /**
     * Displays a prioritized modal alert to a player with custom priority and duration.
     *
     * @param player   target player
     * @param title    alert title text
     * @param subtitle alert subtitle text
     * @param priority alert priority (higher values take precedence)
     * @param duration display duration
     */
    public static void alert(
            @NonNull Player player,
            @NonNull String title,
            @Nullable String subtitle,
            int priority,
            @NonNull Duration duration
    ) {
        alert(player, title, subtitle, priority, duration, TimeableTitle.ALERT);
    }

    /**
     * Displays a prioritized modal alert to a player with custom priority, duration, and timings.
     *
     * @param player   target player
     * @param title    alert title text
     * @param subtitle alert subtitle text
     * @param priority alert priority
     * @param duration display duration
     * @param times    timing parameters
     */
    public static void alert(
            @NonNull Player player,
            @NonNull String title,
            @Nullable String subtitle,
            int priority,
            @NonNull Duration duration,
            @NonNull TimeableTitle times
    ) {
        alert(player, TitleAlert.of(title, subtitle, priority, duration, times));
    }

    /**
     * Queues a prepared {@link TitleAlert} modal alert on a player.
     *
     * @param player target player
     * @param alert  title alert
     */
    public static void alert(@NonNull Player player, @NonNull TitleAlert alert) {
        TitleService service = getServiceOrLazy();
        if (service != null) {
            service.sendAlert(player, alert);
        } else {
            String title = alert.title(player);
            String subtitle = alert.subtitle(player);
            builder().title(title != null ? title : " ").subtitle(subtitle).times(alert.times()).send(player);
        }
    }

    /**
     * Displays a prioritized modal alert to a collection of players.
     *
     * @param players target players
     * @param alert   modal alert
     */
    public static void alert(@NonNull Collection<? extends Player> players, @NonNull TitleAlert alert) {
        Objects.requireNonNull(players, "players cannot be null");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                alert(player, alert);
            }
        }
    }

    /**
     * Displays a prioritized modal alert to a collection of players for a given duration.
     *
     * @param players  target players
     * @param title    alert title text
     * @param subtitle alert subtitle text
     * @param duration display duration
     */
    public static void alert(
            @NonNull Collection<? extends Player> players,
            @NonNull String title,
            @Nullable String subtitle,
            @NonNull Duration duration
    ) {
        alert(players, TitleAlert.of(title, subtitle, 0, duration));
    }

    /**
     * Broadcasts a modal alert to all online players.
     *
     * @param alert modal alert
     */
    public static void broadcastAlert(@NonNull TitleAlert alert) {
        alert(Bukkit.getOnlinePlayers(), alert);
    }

    /**
     * Broadcasts a modal alert to all online players for a given duration.
     *
     * @param title    alert title text
     * @param subtitle alert subtitle text
     * @param duration display duration
     */
    public static void broadcastAlert(@NonNull String title, @Nullable String subtitle, @NonNull Duration duration) {
        broadcastAlert(TitleAlert.of(title, subtitle, 0, duration));
    }

    /**
     * Clears all active modal alerts on a player and immediately restores underlying persistent titles.
     *
     * @param player target player
     */
    public static void clearAlerts(@NonNull Player player) {
        TitleService service = getServiceOrLazy();
        if (service != null) {
            service.clearAlerts(player);
        }
    }

    /**
     * Clears all active modal alerts on a collection of players and restores persistent titles.
     *
     * @param players target players
     */
    public static void clearAlerts(@NonNull Collection<? extends Player> players) {
        Objects.requireNonNull(players, "players cannot be null");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                clearAlerts(player);
            }
        }
    }

    /**
     * Clears and resets the active title displayed to a player.
     *
     * @param player target player
     */
    public static void clear(@NonNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        TitleService service = TitleService.getInstance();
        if (service != null) {
            service.clearAll(player);
        } else {
            player.resetTitle();
        }
    }

    /**
     * Clears and resets the active title displayed to a collection of players.
     *
     * @param players target players
     */
    public static void clear(@NonNull Collection<? extends Player> players) {
        Objects.requireNonNull(players, "players cannot be null");
        for (Player player : players) {
            if (player != null && player.isOnline()) {
                clear(player);
            }
        }
    }

    /**
     * Clears and resets the active title displayed to a player. Alias for {@link #clear(Player)}.
     *
     * @param player target player
     */
    public static void reset(@NonNull Player player) {
        clear(player);
    }

    /**
     * Clears and resets the active title for all online players.
     */
    public static void clearAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            clear(player);
        }
    }

    /**
     * Displays a prolonged announcement title to a player with a celebratory completion sound.
     *
     * @param player   target player
     * @param title    announcement title text
     * @param subtitle announcement subtitle text
     */
    public static void announcement(@NonNull Player player, @NonNull String title, @Nullable String subtitle) {
        builder()
                .title(title)
                .subtitle(subtitle)
                .times(TimeableTitle.PROLONGED)
                .sound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f)
                .send(player);
    }

    /**
     * Plays a high-performance, flicker-free title animation on a player.
     *
     * @param player    target player
     * @param animation text animation to execute
     * @return the running Bukkit task handle
     */
    public static BukkitTask animate(@NonNull Player player, @NonNull TextAnimation animation) {
        return animate(player, animation, null);
    }

    /**
     * Plays a high-performance title animation on a player with a completion callback.
     *
     * @param player     target player
     * @param animation  text animation to execute
     * @param onComplete optional action executed upon animation completion
     * @return the running Bukkit task handle
     */
    public static BukkitTask animate(@NonNull Player player, @NonNull TextAnimation animation, @Nullable Runnable onComplete) {
        return animation.playTitle(player, onComplete);
    }

    /**
     * Plays a title animation across a collection of players.
     *
     * @param players   target players
     * @param animation text animation to execute
     */
    public static void animate(@NonNull Collection<? extends Player> players, @NonNull TextAnimation animation) {
        animate(players, animation, null);
    }

    /**
     * Plays a title animation across a collection of players with a completion callback.
     *
     * @param players    target players
     * @param animation  text animation to execute
     * @param onComplete optional action executed when all animations complete
     */
    public static void animate(@NonNull Collection<? extends Player> players, @NonNull TextAnimation animation, @Nullable Runnable onComplete) {
        animation.playTitle(players, onComplete);
    }

    /**
     * Executes an automated animated countdown title sequence on a player's screen (e.g. 3.. 2.. 1.. GO!).
     *
     * <p>Decrements each second with an audible pling sound of ascending pitch, culminating
     * in the final completion title, subtitle, and callback.</p>
     *
     * @param player             target player
     * @param seconds            countdown duration in seconds
     * @param completionTitle    final title displayed when countdown reaches 0
     * @param completionSubtitle final subtitle displayed when countdown reaches 0
     * @param onComplete         optional action to execute when countdown completes
     * @return the active BukkitTask managing the countdown
     */
    public static BukkitTask countdown(
            @NonNull Player player,
            int seconds,
            @NonNull String completionTitle,
            @Nullable String completionSubtitle,
            @Nullable Runnable onComplete
    ) {
        if (seconds <= 0) {
            builder().title(completionTitle).subtitle(completionSubtitle).times(TimeableTitle.of(0, 30, 10)).send(player);
            if (onComplete != null) {
                onComplete.run();
            }
            return null;
        }
        return countdown(player, seconds, remaining -> switch (remaining) {
            case 1 -> "§a§l" + remaining;
            case 2 -> "§e§l" + remaining;
            default -> "§c§l" + remaining;
        }, completionTitle, completionSubtitle, Sound.BLOCK_NOTE_BLOCK_PLING, Sound.BLOCK_NOTE_BLOCK_PLING, onComplete);
    }

    /**
     * Executes an automated animated countdown title sequence on a player's screen with custom formatting and sound.
     *
     * @param player             target player
     * @param seconds            countdown duration in seconds
     * @param secondFormatter    function formatting the title text for each second (receives remaining seconds)
     * @param completionTitle    final title displayed when countdown reaches 0
     * @param completionSubtitle final subtitle displayed when countdown reaches 0
     * @param tickSound          sound played each second, or null for silent
     * @param completionSound    sound played at countdown completion, or null for silent
     * @param onComplete         optional action to execute when countdown completes
     * @return the active BukkitTask managing the countdown
     */
    public static BukkitTask countdown(
            @NonNull Player player,
            int seconds,
            @NonNull IntFunction<String> secondFormatter,
            @NonNull String completionTitle,
            @Nullable String completionSubtitle,
            @Nullable Sound tickSound,
            @Nullable Sound completionSound,
            @Nullable Runnable onComplete
    ) {
        return countdown(List.of(player), seconds, secondFormatter, completionTitle, completionSubtitle, tickSound, completionSound, onComplete);
    }

    /**
     * Executes an automated animated countdown title sequence across multiple players simultaneously.
     *
     * @param players            target players
     * @param seconds            countdown duration in seconds
     * @param completionTitle    final title displayed when countdown reaches 0
     * @param completionSubtitle final subtitle displayed when countdown reaches 0
     * @param onComplete         optional action to execute when countdown completes
     * @return the active BukkitTask managing the countdown
     */
    public static BukkitTask countdown(
            @NonNull Collection<? extends Player> players,
            int seconds,
            @NonNull String completionTitle,
            @Nullable String completionSubtitle,
            @Nullable Runnable onComplete
    ) {
        if (seconds <= 0) {
            for (Player player : players) {
                if (player.isOnline()) {
                    builder().title(completionTitle).subtitle(completionSubtitle).times(TimeableTitle.of(0, 30, 10)).send(player);
                }
            }
            if (onComplete != null) {
                onComplete.run();
            }
            return null;
        }
        return countdown(players, seconds, remaining -> switch (remaining) {
            case 1 -> "§a§l" + remaining;
            case 2 -> "§e§l" + remaining;
            default -> "§c§l" + remaining;
        }, completionTitle, completionSubtitle, Sound.BLOCK_NOTE_BLOCK_PLING, Sound.BLOCK_NOTE_BLOCK_PLING, onComplete);
    }

    /**
     * Executes an automated animated countdown title sequence across multiple players with custom formatting and sounds.
     *
     * @param players            target players
     * @param seconds            countdown duration in seconds
     * @param secondFormatter    function formatting the title text for each second (receives remaining seconds)
     * @param completionTitle    final title displayed when countdown reaches 0
     * @param completionSubtitle final subtitle displayed when countdown reaches 0
     * @param tickSound          sound played each second, or null for silent
     * @param completionSound    sound played at countdown completion, or null for silent
     * @param onComplete         optional action to execute when countdown completes
     * @return the active BukkitTask managing the countdown
     */
    public static BukkitTask countdown(
            @NonNull Collection<? extends Player> players,
            int seconds,
            @NonNull IntFunction<String> secondFormatter,
            @NonNull String completionTitle,
            @Nullable String completionSubtitle,
            @Nullable Sound tickSound,
            @Nullable Sound completionSound,
            @Nullable Runnable onComplete
    ) {
        Objects.requireNonNull(players, "players cannot be null");
        Objects.requireNonNull(secondFormatter, "secondFormatter cannot be null");
        Objects.requireNonNull(completionTitle, "completionTitle cannot be null");
        List<Player> countdownPlayers = List.copyOf(players);

        if (seconds <= 0) {
            for (Player player : countdownPlayers) {
                if (player.isOnline()) {
                    builder()
                            .title(completionTitle)
                            .subtitle(completionSubtitle)
                            .times(TimeableTitle.of(0, 30, 10))
                            .send(player);
                }
            }
            if (onComplete != null) {
                onComplete.run();
            }
            return null;
        }

        Plugin plugin;
        try {
            plugin = resolvePlugin();
        } catch (IllegalStateException _) {
            return null;
        }

        return new BukkitRunnable() {
            private int remaining = seconds;

            @Override
            public void run() {
                boolean anyOnline = false;
                for (Player player : countdownPlayers) {
                    if (player.isOnline()) {
                        anyOnline = true;
                        break;
                    }
                }
                if (!anyOnline) {
                    cancel();
                    return;
                }

                if (remaining > 0) {
                    String titleText = secondFormatter.apply(remaining);
                    float pitch = Math.min(2.0f, 1.0f + (seconds - remaining) * 0.25f);

                    for (Player player : countdownPlayers) {
                        if (player.isOnline()) {
                            TitleBuilder builder = builder()
                                    .title(titleText)
                                    .subtitle("")
                                    .times(TimeableTitle.of(0, 22, 5));
                            if (tickSound != null) {
                                builder.sound(tickSound, 1.0f, pitch);
                            }
                            builder.send(player);
                        }
                    }

                    remaining--;
                } else {
                    for (Player player : countdownPlayers) {
                        if (player.isOnline()) {
                            TitleBuilder builder = builder()
                                    .title(completionTitle)
                                    .subtitle(completionSubtitle)
                                    .times(TimeableTitle.of(0, 30, 10));
                            if (completionSound != null) {
                                builder.sound(completionSound, 1.0f, 2.0f);
                            }
                            builder.send(player);
                        }
                    }

                    cancel();

                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private static Plugin resolvePlugin() {
        TitleService service = TitleService.getInstance();
        if (service != null && service.getPlugin() != null) {
            return service.getPlugin();
        }
        return PluginContext.get();
    }

    private static TitleService getServiceOrLazy() {
        TitleService service = TitleService.getInstance();
        if (service == null) {
            try {
                Plugin plugin = resolvePlugin();
                if (plugin != null) {
                    service = TitleService.getOrCreate(plugin);
                    service.start(10L);
                }
            } catch (Exception ignored) {
            }
        }
        return service;
    }
}
