package fr.moussax.bedrock.ui.animation;

import fr.moussax.bedrock.scheduling.PluginContext;
import fr.moussax.bedrock.ui.actionbar.ActionbarService;
import fr.moussax.bedrock.ui.title.TimeableTitle;
import fr.moussax.bedrock.ui.title.TitlePacketSender;
import fr.moussax.bedrock.ui.title.TitleService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * High-performance, flicker-free text animation engine for Minecraft user interfaces.
 *
 * <p>Supports sequenced frame animations across multiple UI channels including Action Bars,
 * Titles, Subtitles, and custom UI consumers. Eliminates visual stutter and client opacity resets
 * through in-place packet updates and automatic background HUD suspension during playback.</p>
 */
public class TextAnimation {

    private static final Pattern COLOR_PATTERN = Pattern.compile("§[0-9a-fk-orA-FK-OR]");

    /**
     * An individual frame within a text animation sequence.
     *
     * @param text       primary frame text (rendered on action bar or main title)
     * @param subtitle   optional subtitle text (used when rendering as a title)
     * @param sound      optional sound played when this frame renders
     * @param soundPitch pitch for the frame sound
     */
    public record Frame(
            @NonNull String text,
            @Nullable String subtitle,
            @Nullable Sound sound,
            float soundPitch
    ) {
        public Frame(@NonNull String text) {
            this(text, null, null, 1.0f);
        }

        public Frame(@NonNull String text, @Nullable Sound sound, float soundPitch) {
            this(text, null, sound, soundPitch);
        }

        public Frame(@NonNull String text, @Nullable String subtitle) {
            this(text, subtitle, null, 1.0f);
        }

        /**
         * Alias for {@link #text()} suited for title-oriented contexts.
         *
         * @return primary text
         */
        public String title() {
            return text;
        }
    }

    private final List<Frame> frames;
    private final long tickInterval;
    private final TimeableTitle finalTimes;
    private final Duration finalStay;
    private final Consumer<Player> onComplete;
    private final TextAnimation nextAnimation;

    protected TextAnimation(
            List<Frame> frames,
            long tickInterval,
            TimeableTitle finalTimes,
            Duration finalStay,
            Consumer<Player> onComplete,
            TextAnimation nextAnimation
    ) {
        this.frames = List.copyOf(frames);
        this.tickInterval = Math.max(1L, tickInterval);
        this.finalTimes = finalTimes != null ? finalTimes : TimeableTitle.DEFAULT;
        this.finalStay = finalStay != null ? finalStay : Duration.ofMillis(this.finalTimes.stay() * 50L);
        this.onComplete = onComplete;
        this.nextAnimation = nextAnimation;
    }

    /**
     * Returns an unmodifiable list of all frames configured for this animation stage.
     *
     * @return animation frames
     */
    @NonNull
    public List<Frame> frames() {
        return frames;
    }

    /**
     * Returns the tick delay between consecutive frame dispatches.
     *
     * @return tick interval
     */
    public long tickInterval() {
        return tickInterval;
    }

    /**
     * Returns the timing parameters applied to title displays upon final frame completion.
     *
     * @return final title timings
     */
    @NonNull
    public TimeableTitle finalTimes() {
        return finalTimes;
    }

    /**
     * Returns the duration the completed text persists on the action bar before restoring background HUDs.
     *
     * @return final stay duration
     */
    @NonNull
    public Duration finalStay() {
        return finalStay;
    }

    /**
     * Returns the completion callback invoked for the target player upon completing this stage.
     *
     * @return completion callback or null
     */
    @Nullable
    public Consumer<Player> onComplete() {
        return onComplete;
    }

    /**
     * Returns the chained next animation stage, if configured.
     *
     * @return next animation stage or null
     */
    @Nullable
    public TextAnimation nextAnimation() {
        return nextAnimation;
    }

    /**
     * Creates a new fluent {@link Builder} to construct a custom text animation sequence.
     *
     * @return a new animation builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Chains this animation to be followed seamlessly by another animation upon completion.
     *
     * @param next the next animation to play
     * @return chained animation sequence
     */
    public TextAnimation then(@NonNull TextAnimation next) {
        Objects.requireNonNull(next, "next animation cannot be null");
        TextAnimation chainedNext = (this.nextAnimation != null) ? this.nextAnimation.then(next) : next;
        return new TextAnimation(this.frames, this.tickInterval, TimeableTitle.ANIMATION_FRAME, Duration.ZERO, this.onComplete, chainedNext);
    }

    /**
     * Constructs a typewriter text-reveal animation where characters appear incrementally.
     *
     * @param fullText     the complete text to reveal
     * @param ticksPerChar tick delay between revealing each character
     * @param clickSound   sound played when each letter appears, or null for silent
     * @return configured typewriter animation
     */
    public static TextAnimation typewriter(
            @NonNull String fullText,
            long ticksPerChar,
            @Nullable Sound clickSound
    ) {
        return typewriter(fullText, null, ticksPerChar, clickSound, TimeableTitle.DEFAULT);
    }

    /**
     * Constructs a typewriter text-reveal animation with a custom action bar final stay duration.
     *
     * @param fullText     the complete text to reveal
     * @param ticksPerChar tick delay between revealing each character
     * @param clickSound   sound played when each letter appears, or null for silent
     * @param finalStay    duration the completed text persists before restoring normal HUD
     * @return configured typewriter animation
     */
    public static TextAnimation typewriter(
            @NonNull String fullText,
            long ticksPerChar,
            @Nullable Sound clickSound,
            @NonNull Duration finalStay
    ) {
        Builder builder = builder().interval(ticksPerChar).finalStay(finalStay);
        buildTypewriterFrames(builder, fullText, null, clickSound);
        return builder.build();
    }

    /**
     * Constructs a typewriter text-reveal animation with static subtitle and title timings.
     *
     * @param fullTitle    the complete title text to reveal
     * @param subtitle     static subtitle displayed underneath
     * @param ticksPerChar tick delay between revealing each character
     * @param clickSound   sound played when each letter appears, or null for silent
     * @param finalTimes   display timings for the completed title
     * @return configured typewriter animation
     */
    public static TextAnimation typewriter(
            @NonNull String fullTitle,
            @Nullable String subtitle,
            long ticksPerChar,
            @Nullable Sound clickSound,
            @NonNull TimeableTitle finalTimes
    ) {
        Builder builder = builder().interval(ticksPerChar).finalTimes(finalTimes);
        buildTypewriterFrames(builder, fullTitle, subtitle, clickSound);
        return builder.build();
    }

    private static void buildTypewriterFrames(Builder builder, String text, @Nullable String subtitle, @Nullable Sound sound) {
        String stripped = COLOR_PATTERN.matcher(text).replaceAll("");
        int visibleLength = stripped.length();

        if (visibleLength == 0) {
            builder.frame(text, subtitle, sound, 1.2f);
        } else {
            for (int i = 1; i <= visibleLength; i++) {
                String partial = substringWithColors(text, i);
                builder.frame(partial, subtitle, sound, 1.2f);
            }
        }
    }

    /**
     * Constructs a dynamic color-shimmer wave animation sliding across the letters of a text.
     *
     * @param text           raw text
     * @param baseColor      base color code (e.g. "§5")
     * @param highlightColor highlight shine peak color code (e.g. "§f")
     * @param bold           whether the text should be bold
     * @param cycles         number of complete shimmer wave cycles
     * @return configured shimmer animation
     */
    public static TextAnimation shimmer(
            @NonNull String text,
            @NonNull String baseColor,
            @NonNull String highlightColor,
            boolean bold,
            int cycles
    ) {
        return shimmer(text, null, baseColor, "§d", highlightColor, bold, cycles, TimeableTitle.DEFAULT);
    }

    /**
     * Constructs a dynamic color-shimmer wave animation with intermediate mid-tone color.
     *
     * @param text           raw text
     * @param baseColor      base color code (e.g. "§5")
     * @param midColor       intermediate shimmer color code (e.g. "§d")
     * @param highlightColor highlight shine peak color code (e.g. "§f")
     * @param bold           whether the text should be bold
     * @param cycles         number of complete shimmer wave cycles
     * @return configured shimmer animation
     */
    public static TextAnimation shimmer(
            @NonNull String text,
            @NonNull String baseColor,
            @NonNull String midColor,
            @NonNull String highlightColor,
            boolean bold,
            int cycles
    ) {
        return shimmer(text, null, baseColor, midColor, highlightColor, bold, cycles, TimeableTitle.DEFAULT);
    }

    /**
     * Constructs a color-shimmer animation with a custom action bar final stay duration.
     *
     * @param text           raw text
     * @param baseColor      base color code
     * @param highlightColor highlight shine color code
     * @param bold           whether the text should be bold
     * @param cycles         number of complete shimmer wave cycles
     * @param finalStay      duration the completed text persists before restoring normal HUD
     * @return configured shimmer animation
     */
    public static TextAnimation shimmer(
            @NonNull String text,
            @NonNull String baseColor,
            @NonNull String highlightColor,
            boolean bold,
            int cycles,
            @NonNull Duration finalStay
    ) {
        return shimmer(text, null, baseColor, "§d", highlightColor, bold, cycles, TimeableTitle.stay(finalStay));
    }

    /**
     * Constructs a color-shimmer animation with intermediate mid-tone and a custom final stay duration.
     *
     * @param text           raw text
     * @param baseColor      base color code
     * @param midColor       intermediate shimmer color code
     * @param highlightColor highlight shine color code
     * @param bold           whether the text should be bold
     * @param cycles         number of complete shimmer wave cycles
     * @param finalStay      duration the completed text persists before restoring normal HUD
     * @return configured shimmer animation
     */
    public static TextAnimation shimmer(
            @NonNull String text,
            @NonNull String baseColor,
            @NonNull String midColor,
            @NonNull String highlightColor,
            boolean bold,
            int cycles,
            @NonNull Duration finalStay
    ) {
        return shimmer(text, null, baseColor, midColor, highlightColor, bold, cycles, TimeableTitle.stay(finalStay));
    }

    /**
     * Constructs a dynamic color-shimmer wave animation with subtitle and title timings.
     *
     * @param text           raw text
     * @param subtitle       static subtitle displayed underneath
     * @param baseColor      base color code
     * @param highlightColor highlight shine peak color code
     * @param bold           whether the text should be bold
     * @param cycles         number of complete shimmer wave cycles
     * @param finalTimes     display timings for the finale
     * @return configured shimmer animation
     */
    public static TextAnimation shimmer(
            @NonNull String text,
            @Nullable String subtitle,
            @NonNull String baseColor,
            @NonNull String highlightColor,
            boolean bold,
            int cycles,
            @NonNull TimeableTitle finalTimes
    ) {
        return shimmer(text, subtitle, baseColor, "§d", highlightColor, bold, cycles, finalTimes);
    }

    /**
     * Constructs a dynamic color-shimmer wave animation with full color palette, subtitle, and timings.
     *
     * @param text           raw text
     * @param subtitle       static subtitle displayed underneath
     * @param baseColor      base color code
     * @param midColor       intermediate shimmer color code
     * @param highlightColor highlight shine peak color code
     * @param bold           whether the text should be bold
     * @param cycles         number of complete shimmer wave cycles
     * @param finalTimes     display timings for the finale
     * @return configured shimmer animation
     */
    public static TextAnimation shimmer(
            @NonNull String text,
            @Nullable String subtitle,
            @NonNull String baseColor,
            @NonNull String midColor,
            @NonNull String highlightColor,
            boolean bold,
            int cycles,
            @NonNull TimeableTitle finalTimes
    ) {
        Builder builder = builder().interval(1L).finalTimes(finalTimes);
        String prefix = bold ? "§l" : "";
        String highlight = highlightColor + prefix;
        String mid = midColor + prefix;
        String base = baseColor + prefix;
        int length = text.length();

        for (int cycle = 0; cycle < cycles; cycle++) {
            for (int highlightIndex = -2; highlightIndex < length + 2; highlightIndex++) {
                StringBuilder frameBuilder = new StringBuilder(length * 4);
                for (int index = 0; index < length; index++) {
                    char character = text.charAt(index);
                    if (index == highlightIndex) {
                        frameBuilder.append(highlight).append(character);
                    } else if (Math.abs(index - highlightIndex) == 1) {
                        frameBuilder.append(mid).append(character);
                    } else {
                        frameBuilder.append(base).append(character);
                    }
                }
                builder.frame(frameBuilder.toString(), subtitle);
            }
        }

        return builder.build();
    }

    /**
     * Constructs a flashing pulse animation alternating between a primary and highlight color.
     *
     * @param text          message text
     * @param primaryColor  standard color code (e.g. "§c")
     * @param flashColor    bright pulse color code (e.g. "§f")
     * @param ticksPerPulse tick delay per pulse phase
     * @param pulses        number of complete pulses
     * @param sound         sound played on each flash, or null for silent
     * @return configured pulse animation
     */
    public static TextAnimation pulse(
            @NonNull String text,
            @NonNull String primaryColor,
            @NonNull String flashColor,
            long ticksPerPulse,
            int pulses,
            @Nullable Sound sound
    ) {
        Builder builder = builder().interval(ticksPerPulse);
        for (int i = 0; i < pulses; i++) {
            builder.frame(flashColor + text, sound, 1.2f);
            builder.frame(primaryColor + text, null, 1.0f);
        }
        return builder.build();
    }

    /**
     * Plays this text animation on the target player's action bar.
     *
     * @param player target player
     * @return the running Bukkit task handle
     */
    public BukkitTask playActionbar(@NonNull Player player) {
        return playActionbar(player, null);
    }

    /**
     * Plays this text animation on the target player's action bar with a completion callback.
     *
     * @param player          target player
     * @param extraOnComplete additional action executed upon completion
     * @return the running Bukkit task handle
     */
    public BukkitTask playActionbar(@NonNull Player player, @Nullable Runnable extraOnComplete) {
        return playActionbar(resolvePlugin(), player, extraOnComplete);
    }

    /**
     * Plays this text animation on the target player's action bar using the specified plugin for scheduling.
     *
     * @param plugin owning plugin
     * @param player target player
     * @return the running Bukkit task handle
     */
    public BukkitTask playActionbar(@NonNull Plugin plugin, @NonNull Player player) {
        return playActionbar(plugin, player, null);
    }

    /**
     * Plays this text animation on the target player's action bar using the specified plugin and completion callback.
     *
     * @param plugin          owning plugin
     * @param player          target player
     * @param extraOnComplete additional action executed upon completion
     * @return the running Bukkit task handle
     */
    public BukkitTask playActionbar(@NonNull Plugin plugin, @NonNull Player player, @Nullable Runnable extraOnComplete) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(player, "player cannot be null");

        if (frames.isEmpty()) {
            if (nextAnimation != null) {
                return nextAnimation.playActionbar(plugin, player, extraOnComplete);
            }
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        if (!player.isOnline()) {
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.setAnimating(player.getUniqueId(), true);
        }

        Frame firstFrame = frames.getFirst();
        sendRawActionbar(player, firstFrame.text());
        if (firstFrame.sound() != null) {
            player.playSound(player.getLocation(), firstFrame.sound(), 1.0f, firstFrame.soundPitch());
        }

        if (frames.size() == 1) {
            if (nextAnimation != null) {
                if (onComplete != null) onComplete.accept(player);
                return nextAnimation.playActionbar(plugin, player, extraOnComplete);
            }
            if (onComplete != null) onComplete.accept(player);
            long stayTicks = Math.max(0L, finalStay.toMillis() / 50L);
            if (stayTicks > 0) {
                return Bukkit.getScheduler().runTaskLater(plugin, () -> finishActionbar(player, extraOnComplete), stayTicks);
            } else {
                finishActionbar(player, extraOnComplete);
                return null;
            }
        }

        return new BukkitRunnable() {
            private int frameIndex = 1;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cleanupActionbar(player);
                    cancel();
                    if (extraOnComplete != null) {
                        extraOnComplete.run();
                    }
                    return;
                }

                if (frameIndex < frames.size() - 1) {
                    Frame frame = frames.get(frameIndex);
                    sendRawActionbar(player, frame.text());
                    if (frame.sound() != null) {
                        player.playSound(player.getLocation(), frame.sound(), 1.0f, frame.soundPitch());
                    }
                    frameIndex++;
                } else {
                    Frame last = frames.getLast();
                    cancel();

                    sendRawActionbar(player, last.text());
                    if (last.sound() != null) {
                        player.playSound(player.getLocation(), last.sound(), 1.0f, last.soundPitch());
                    }
                    if (onComplete != null) {
                        onComplete.accept(player);
                    }

                    if (nextAnimation != null) {
                        nextAnimation.playActionbar(plugin, player, extraOnComplete);
                    } else {
                        long stayTicks = Math.max(0L, finalStay.toMillis() / 50L);
                        if (stayTicks > 0) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> finishActionbar(player, extraOnComplete), stayTicks);
                        } else {
                            finishActionbar(player, extraOnComplete);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, tickInterval, tickInterval);
    }

    /**
     * Plays this text animation across a collection of players' action bars.
     *
     * @param players target players
     */
    public void playActionbar(@NonNull Collection<? extends Player> players) {
        playActionbar(players, null);
    }

    /**
     * Plays this text animation across a collection of players' action bars with a completion callback.
     *
     * @param players         target players
     * @param extraOnComplete action executed when all players complete the animation
     */
    public void playActionbar(@NonNull Collection<? extends Player> players, @Nullable Runnable extraOnComplete) {
        playActionbar(resolvePlugin(), players, extraOnComplete);
    }

    /**
     * Plays this text animation across a collection of players' action bars using the specified plugin.
     *
     * @param plugin          owning plugin
     * @param players         target players
     * @param extraOnComplete action executed when all players complete the animation
     */
    public void playActionbar(
            @NonNull Plugin plugin,
            @NonNull Collection<? extends Player> players,
            @Nullable Runnable extraOnComplete
    ) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(players, "players cannot be null");
        dispatchCollection(players, extraOnComplete, (player, barrier) -> playActionbar(plugin, player, barrier));
    }

    /**
     * Delegate interface for custom action bar packet dispatching or test assertions.
     */
    @FunctionalInterface
    public interface ActionbarHandler {
        void sendActionbar(@NonNull Player player, @NonNull String text);
    }

    private static volatile ActionbarHandler customActionbarHandler;

    /**
     * Overrides the action bar packet handler for testing or custom packet pipelines.
     *
     * @param handler custom action bar handler, or null to revert to default Spigot dispatch
     */
    public static void setCustomActionbarHandler(@Nullable ActionbarHandler handler) {
        customActionbarHandler = handler;
    }

    private static void sendRawActionbar(@NonNull Player player, @NonNull String text) {
        ActionbarHandler handler = customActionbarHandler;
        if (handler != null) {
            handler.sendActionbar(player, text);
            return;
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    private static void finishActionbar(@NonNull Player player, @Nullable Runnable extraOnComplete) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.setAnimating(player.getUniqueId(), false);
            service.renderPlayer(player);
        }
        if (extraOnComplete != null) {
            extraOnComplete.run();
        }
    }

    private static void cleanupActionbar(@NonNull Player player) {
        ActionbarService service = ActionbarService.getInstance();
        if (service != null) {
            service.setAnimating(player.getUniqueId(), false);
        }
    }

    /**
     * Plays this text animation on the target player as an in-place title sequence.
     *
     * @param player target player
     * @return the running Bukkit task handle
     */
    public BukkitTask playTitle(@NonNull Player player) {
        return playTitle(player, null);
    }

    /**
     * Plays this text animation on the target player as a title sequence with a completion callback.
     *
     * @param player          target player
     * @param extraOnComplete additional action executed upon completion
     * @return the running Bukkit task handle
     */
    public BukkitTask playTitle(@NonNull Player player, @Nullable Runnable extraOnComplete) {
        return playTitle(resolvePlugin(), player, extraOnComplete);
    }

    /**
     * Plays this text animation on the target player as a title sequence using the specified plugin for scheduling.
     *
     * @param plugin owning plugin
     * @param player target player
     * @return the running Bukkit task handle
     */
    public BukkitTask playTitle(@NonNull Plugin plugin, @NonNull Player player) {
        return playTitle(plugin, player, null);
    }

    /**
     * Plays this text animation on the target player as a title sequence using the specified plugin and completion callback.
     *
     * @param plugin          owning plugin
     * @param player          target player
     * @param extraOnComplete additional action executed upon completion
     * @return the running Bukkit task handle
     */
    public BukkitTask playTitle(@NonNull Plugin plugin, @NonNull Player player, @Nullable Runnable extraOnComplete) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(player, "player cannot be null");

        if (frames.isEmpty()) {
            if (nextAnimation != null) {
                return nextAnimation.playTitle(plugin, player, extraOnComplete);
            }
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        if (!player.isOnline()) {
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        TitleService titleService = TitleService.getInstance();
        if (titleService != null) {
            titleService.setAnimating(player.getUniqueId(), true);
        }

        Frame firstFrame = frames.getFirst();
        if (firstFrame.sound() != null) {
            player.playSound(player.getLocation(), firstFrame.sound(), 1.0f, firstFrame.soundPitch());
        }

        if (frames.size() == 1) {
            if (nextAnimation != null) {
                int totalTicks = (int) (frames.size() * tickInterval);
                TitlePacketSender.sendFull(player, firstFrame.title(), firstFrame.subtitle(), 0, totalTicks + 60, 0);
                if (onComplete != null) onComplete.accept(player);
                return nextAnimation.playTitle(plugin, player, extraOnComplete);
            }
            TitlePacketSender.sendFull(player, firstFrame.title(), firstFrame.subtitle(),
                    finalTimes.fadeIn(), finalTimes.stay(), finalTimes.fadeOut());
            if (onComplete != null) onComplete.accept(player);

            long finishDelay = finalTimes.stay() + finalTimes.fadeOut();
            if (finishDelay > 0) {
                return Bukkit.getScheduler().runTaskLater(plugin, () -> finishTitle(player, extraOnComplete), finishDelay);
            } else {
                finishTitle(player, extraOnComplete);
                return null;
            }
        }

        // Initialize display with sufficient stay to cover the entire sequence with zero fade-in
        int totalTicks = (int) (frames.size() * tickInterval);
        TitlePacketSender.sendFull(player, firstFrame.title(), firstFrame.subtitle(), 0, totalTicks + 60, 0);

        return new BukkitRunnable() {
            private int frameIndex = 1;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cleanupTitle(player);
                    cancel();
                    if (extraOnComplete != null) {
                        extraOnComplete.run();
                    }
                    return;
                }

                if (frameIndex < frames.size() - 1) {
                    Frame frame = frames.get(frameIndex);
                    // Pure text update: no ClientboundSetTitlesAnimationPacket is sent, preventing opacity reset
                    TitlePacketSender.sendTextOnly(player, frame.title(), frame.subtitle());
                    if (frame.sound() != null) {
                        player.playSound(player.getLocation(), frame.sound(), 1.0f, frame.soundPitch());
                    }
                    frameIndex++;
                } else {
                    Frame last = frames.getLast();
                    cancel();

                    if (nextAnimation != null) {
                        // In-place transfer to the next animation
                        TitlePacketSender.sendTextOnly(player, last.title(), last.subtitle());
                        if (last.sound() != null) {
                            player.playSound(player.getLocation(), last.sound(), 1.0f, last.soundPitch());
                        }
                        if (onComplete != null) {
                            onComplete.accept(player);
                        }
                        nextAnimation.playTitle(plugin, player, extraOnComplete);
                    } else {
                        // Climax frame: apply final sustain and fade-out timings
                        TitlePacketSender.sendFull(player, last.title(), last.subtitle(),
                                finalTimes.fadeIn(), finalTimes.stay(), finalTimes.fadeOut());
                        if (last.sound() != null) {
                            player.playSound(player.getLocation(), last.sound(), 1.0f, last.soundPitch());
                        }
                        if (onComplete != null) {
                            onComplete.accept(player);
                        }

                        long finishDelay = finalTimes.stay() + finalTimes.fadeOut();
                        if (finishDelay > 0) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> finishTitle(player, extraOnComplete), finishDelay);
                        } else {
                            finishTitle(player, extraOnComplete);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, tickInterval, tickInterval);
    }

    /**
     * Plays this text animation as a title across a collection of players.
     *
     * @param players target players
     */
    public void playTitle(@NonNull Collection<? extends Player> players) {
        playTitle(players, null);
    }

    /**
     * Plays this text animation as a title across a collection of players with a completion callback.
     *
     * @param players         target players
     * @param extraOnComplete action executed when all players complete the animation
     */
    public void playTitle(@NonNull Collection<? extends Player> players, @Nullable Runnable extraOnComplete) {
        playTitle(resolvePlugin(), players, extraOnComplete);
    }

    /**
     * Plays this text animation as a title across a collection of players using the specified plugin.
     *
     * @param plugin          owning plugin
     * @param players         target players
     * @param extraOnComplete action executed when all players complete the animation
     */
    public void playTitle(
            @NonNull Plugin plugin,
            @NonNull Collection<? extends Player> players,
            @Nullable Runnable extraOnComplete
    ) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(players, "players cannot be null");
        dispatchCollection(players, extraOnComplete, (player, barrier) -> playTitle(plugin, player, barrier));
    }

    private static void dispatchCollection(
            @NonNull Collection<? extends Player> players,
            @Nullable Runnable extraOnComplete,
            @NonNull BiConsumer<Player, Runnable> dispatcher
    ) {
        List<? extends Player> targetPlayers = players.stream()
                .filter(p -> p != null && p.isOnline())
                .toList();

        if (targetPlayers.isEmpty()) {
            if (extraOnComplete != null) extraOnComplete.run();
            return;
        }

        AtomicInteger remaining = new AtomicInteger(targetPlayers.size());
        Runnable barrier = (extraOnComplete != null) ? () -> {
            if (remaining.decrementAndGet() == 0) {
                extraOnComplete.run();
            }
        } : null;

        for (Player player : targetPlayers) {
            dispatcher.accept(player, barrier);
        }
    }

    private static void finishTitle(@NonNull Player player, @Nullable Runnable extraOnComplete) {
        TitleService service = TitleService.getInstance();
        if (service != null) {
            service.setAnimating(player.getUniqueId(), false);
            service.renderPlayer(player);
        }
        if (extraOnComplete != null) {
            extraOnComplete.run();
        }
    }

    private static void cleanupTitle(@NonNull Player player) {
        TitleService service = TitleService.getInstance();
        if (service != null) {
            service.setAnimating(player.getUniqueId(), false);
        }
    }

    /**
     * Plays this text animation dispatching each rendered frame to a custom consumer.
     *
     * @param player        target player
     * @param frameConsumer consumer receiving each frame during playback
     * @return the running Bukkit task handle
     */
    public BukkitTask play(@NonNull Player player, @NonNull BiConsumer<Player, Frame> frameConsumer) {
        return play(player, frameConsumer, null);
    }

    /**
     * Plays this text animation dispatching each rendered frame to a custom consumer with completion callback.
     *
     * @param player          target player
     * @param frameConsumer   consumer receiving each frame during playback
     * @param extraOnComplete action executed upon completion
     * @return the running Bukkit task handle
     */
    public BukkitTask play(
            @NonNull Player player,
            @NonNull BiConsumer<Player, Frame> frameConsumer,
            @Nullable Runnable extraOnComplete
    ) {
        return play(resolvePlugin(), player, frameConsumer, extraOnComplete);
    }

    /**
     * Plays this text animation dispatching each rendered frame to a custom consumer using the specified plugin.
     *
     * @param plugin          owning plugin
     * @param player          target player
     * @param frameConsumer   consumer receiving each frame during playback
     * @param extraOnComplete action executed upon completion
     * @return the running Bukkit task handle
     */
    public BukkitTask play(
            @NonNull Plugin plugin,
            @NonNull Player player,
            @NonNull BiConsumer<Player, Frame> frameConsumer,
            @Nullable Runnable extraOnComplete
    ) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(frameConsumer, "frameConsumer cannot be null");

        if (frames.isEmpty()) {
            if (nextAnimation != null) {
                return nextAnimation.play(plugin, player, frameConsumer, extraOnComplete);
            }
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        if (!player.isOnline()) {
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        Frame firstFrame = frames.getFirst();
        frameConsumer.accept(player, firstFrame);
        if (firstFrame.sound() != null) {
            player.playSound(player.getLocation(), firstFrame.sound(), 1.0f, firstFrame.soundPitch());
        }

        if (frames.size() == 1) {
            if (nextAnimation != null) {
                if (onComplete != null) onComplete.accept(player);
                return nextAnimation.play(plugin, player, frameConsumer, extraOnComplete);
            }
            if (onComplete != null) onComplete.accept(player);
            if (extraOnComplete != null) extraOnComplete.run();
            return null;
        }

        return new BukkitRunnable() {
            private int frameIndex = 1;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    if (extraOnComplete != null) {
                        extraOnComplete.run();
                    }
                    return;
                }

                if (frameIndex < frames.size() - 1) {
                    Frame frame = frames.get(frameIndex);
                    frameConsumer.accept(player, frame);
                    if (frame.sound() != null) {
                        player.playSound(player.getLocation(), frame.sound(), 1.0f, frame.soundPitch());
                    }
                    frameIndex++;
                } else {
                    Frame last = frames.getLast();
                    cancel();
                    frameConsumer.accept(player, last);
                    if (last.sound() != null) {
                        player.playSound(player.getLocation(), last.sound(), 1.0f, last.soundPitch());
                    }
                    if (onComplete != null) {
                        onComplete.accept(player);
                    }
                    if (nextAnimation != null) {
                        nextAnimation.play(plugin, player, frameConsumer, extraOnComplete);
                    } else if (extraOnComplete != null) {
                        extraOnComplete.run();
                    }
                }
            }
        }.runTaskTimer(plugin, tickInterval, tickInterval);
    }

    private static Plugin resolvePlugin() {
        ActionbarService actionbarService = ActionbarService.getInstance();
        if (actionbarService != null && actionbarService.getPlugin() != null) {
            return actionbarService.getPlugin();
        }
        TitleService titleService = TitleService.getInstance();
        if (titleService != null && titleService.getPlugin() != null) {
            return titleService.getPlugin();
        }
        return PluginContext.get();
    }

    /**
     * Extracts a substring containing a given number of visible characters while preserving color formatting codes.
     *
     * @param text         formatted string
     * @param visibleChars maximum visible character count
     * @return truncated formatted string
     */
    public static String substringWithColors(@NonNull String text, int visibleChars) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '§' && i + 1 < text.length()) {
                result.append(c).append(text.charAt(i + 1));
                i++;
                continue;
            }
            result.append(c);
            count++;
            if (count >= visibleChars) {
                break;
            }
        }
        return result.toString();
    }

    /**
     * Fluent builder for {@link TextAnimation}.
     */
    public static class Builder {
        protected final List<Frame> frames = new ArrayList<>();
        protected long tickInterval = 1L;
        protected TimeableTitle finalTimes = TimeableTitle.DEFAULT;
        protected Duration finalStay = null;
        protected Consumer<Player> onComplete;

        public Builder frame(@NonNull String text) {
            return frame(text, null);
        }

        public Builder frame(@NonNull String text, @Nullable Sound sound, float pitch) {
            frames.add(new Frame(text, null, sound, pitch));
            return this;
        }

        public Builder frame(@NonNull String text, @Nullable String subtitle) {
            frames.add(new Frame(text, subtitle, null, 1.0f));
            return this;
        }

        public Builder frame(@NonNull String text, @Nullable String subtitle, @Nullable Sound sound, float pitch) {
            frames.add(new Frame(text, subtitle, sound, pitch));
            return this;
        }

        public Builder interval(long tickInterval) {
            this.tickInterval = tickInterval;
            return this;
        }

        public Builder interval(@NonNull Duration interval) {
            Objects.requireNonNull(interval, "interval cannot be null");
            this.tickInterval = Math.max(1L, interval.toMillis() / 50L);
            return this;
        }

        public Builder finalTimes(@NonNull TimeableTitle finalTimes) {
            this.finalTimes = Objects.requireNonNull(finalTimes, "finalTimes cannot be null");
            return this;
        }

        public Builder finalTimes(@NonNull Duration stayDuration) {
            return finalTimes(TimeableTitle.stay(stayDuration));
        }

        public Builder finalStay(@NonNull Duration finalStay) {
            this.finalStay = Objects.requireNonNull(finalStay, "finalStay cannot be null");
            return this;
        }

        public Builder onComplete(@Nullable Consumer<Player> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public TextAnimation build() {
            Duration stay = (finalStay != null) ? finalStay : Duration.ofMillis(finalTimes.stay() * 50L);
            return new TextAnimation(frames, tickInterval, finalTimes, stay, onComplete, null);
        }
    }
}
