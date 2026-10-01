package fr.moussax.bedrock.ui.animation;

import fr.moussax.bedrock.scheduling.PluginContext;
import fr.moussax.bedrock.ui.actionbar.Actionbar;
import fr.moussax.bedrock.ui.actionbar.ActionbarService;
import fr.moussax.bedrock.ui.title.TimeableTitle;
import fr.moussax.bedrock.ui.title.Title;
import fr.moussax.bedrock.ui.title.TitlePacketSender;
import fr.moussax.bedrock.ui.title.TitleService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TextAnimationTest {

    @BeforeEach
    void setUp() {
        PluginContext.bind(createMockPlugin());
        TextAnimation.setCustomActionbarHandler((_, _) -> {
        });
    }

    @AfterEach
    void tearDown() {
        TitlePacketSender.setCustomHandler(null);
        TextAnimation.setCustomActionbarHandler(null);
        TextAnimation.clearActivePlaybacks();
        ActionbarService.setInstance(null);
        TitleService.setInstance(null);
        PluginContext.unbind();
    }

    private Player createMockPlayer(UUID uuid, List<String> titleRef) {
        return createMockPlayer(uuid, titleRef, new AtomicInteger());
    }

    private Player createMockPlayer(UUID uuid, List<String> titleRef, AtomicInteger resetCount) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (_, method, args) -> {
                    String name = method.getName();
                    switch (name) {
                        case "getUniqueId" -> {
                            return uuid;
                        }
                        case "isOnline" -> {
                            return true;
                        }
                        case "getName" -> {
                            return "TestPlayer";
                        }
                        case "getLocation" -> {
                            return new Location(null, 0, 0, 0);
                        }
                        case "playSound" -> {
                            return null;
                        }
                        case "resetTitle" -> {
                            resetCount.incrementAndGet();
                            return null;
                        }
                    }
                    if ("sendTitle".equals(name) && args != null && args.length > 0) {
                        titleRef.add((String) args[0]);
                        return null;
                    }
                    return null;
                }
        );
    }

    private Plugin createMockPlugin() {
        return (Plugin) Proxy.newProxyInstance(
                Plugin.class.getClassLoader(),
                new Class<?>[]{Plugin.class},
                (_, _, _) -> null
        );
    }

    @Test
    @DisplayName("Expects typewriter factory to incrementally reveal characters while preserving color codes")
    void testTypewriterFrameGeneration() {
        String input = "§aAB§cCD";
        TextAnimation anim = TextAnimation.typewriter(input, 2L, null, Duration.ofSeconds(1));

        assertEquals(2L, anim.tickInterval());
        assertEquals(Duration.ofSeconds(1), anim.finalStay());
        assertEquals(4, anim.frames().size());

        assertEquals("§aA", anim.frames().get(0).text());
        assertEquals("§aAB", anim.frames().get(1).text());
        assertEquals("§aAB§cC", anim.frames().get(2).text());
        assertEquals("§aAB§cCD", anim.frames().get(3).text());
        assertNull(anim.frames().getFirst().sound());
    }

    @Test
    @DisplayName("Expects shimmer factory to generate dynamic color sweep frames")
    void testShimmerFrameGeneration() {
        TextAnimation anim = TextAnimation.shimmer("WIN", "§5", "§d", "§f", true, 1, TimeableTitle.ALERT);

        assertEquals(1L, anim.tickInterval());
        assertEquals(TimeableTitle.ALERT, anim.finalTimes());
        assertFalse(anim.frames().isEmpty());

        // For 3 characters and highlight from -2 to 4, each cycle has 7 frames
        assertEquals(7, anim.frames().size());

        // Peak highlight at index 0 should give highlightColor on first letter and bold prefix
        TextAnimation.Frame peakFrame = anim.frames().get(2); // highlight == 0
        assertTrue(peakFrame.text().contains("§f§lW"));
    }

    @Test
    @DisplayName("Expects pulse factory to alternate between highlight and primary color frames")
    void testPulseFrameGeneration() {
        TextAnimation pulse = TextAnimation.pulse("ALERT", "§c", "§f", 5L, 3, null);

        assertEquals(5L, pulse.tickInterval());
        assertEquals(6, pulse.frames().size()); // 3 flashes * 2 phases

        assertEquals("§fALERT", pulse.frames().get(0).text());
        assertEquals("§cALERT", pulse.frames().get(1).text());
        assertNull(pulse.frames().get(0).sound());
        assertNull(pulse.frames().get(1).sound());
    }

    @Test
    @DisplayName("Expects chained animations to preserve all intermediate stages via then()")
    void testChainingPreservesAllStages() {
        TextAnimation stage1 = TextAnimation.builder().frame("1").build();
        TextAnimation stage2 = TextAnimation.builder().frame("2").build();
        TextAnimation stage3 = TextAnimation.builder().frame("3").build();

        TextAnimation chained = stage1.then(stage2).then(stage3);

        assertEquals("1", chained.frames().getFirst().text());
        assertNotNull(chained.nextAnimation());
        assertEquals("2", chained.nextAnimation().frames().getFirst().text());
        assertNotNull(chained.nextAnimation().nextAnimation());
        assertEquals("3", chained.nextAnimation().nextAnimation().frames().getFirst().text());
    }

    @Test
    @DisplayName("Expects playActionbar to dispatch text frames via actionbar packet handler")
    void testPlayActionbarSingleFrame() {
        UUID uuid = UUID.randomUUID();
        List<String> actionbarPackets = new ArrayList<>();
        List<String> titlePackets = new ArrayList<>();
        Player player = createMockPlayer(uuid, titlePackets);

        TextAnimation.setCustomActionbarHandler((_, text) -> actionbarPackets.add(text));

        AtomicBoolean completed = new AtomicBoolean(false);
        TextAnimation anim = TextAnimation.builder()
                .frame("§eHello Actionbar")
                .finalStay(Duration.ZERO)
                .build();

        Actionbar.animate(player, anim, () -> completed.set(true));

        assertFalse(actionbarPackets.isEmpty());
        assertTrue(actionbarPackets.getFirst().contains("Hello Actionbar"));
        assertTrue(completed.get());
    }

    @Test
    @DisplayName("Expects playTitle to dispatch text frames to player title sender")
    void testPlayTitleSingleFrame() {
        UUID uuid = UUID.randomUUID();
        List<String> titlePackets = new ArrayList<>();
        Player player = createMockPlayer(uuid, titlePackets);

        List<String> textOnlyTitles = new ArrayList<>();
        TitlePacketSender.setCustomHandler((_, title, _) -> {
            if (title != null) textOnlyTitles.add(title);
        });

        AtomicBoolean completed = new AtomicBoolean(false);
        TextAnimation anim = TextAnimation.builder()
                .frame("§6Victory!", "§7Subtitle")
                .finalTimes(TimeableTitle.of(0, 0, 0))
                .build();

        Title.animate(player, anim, () -> completed.set(true));

        assertTrue(titlePackets.contains("§6Victory!"));
        assertTrue(completed.get());
    }

    @Test
    @DisplayName("Expects generic frame consumer to receive all frames during play")
    void testGenericFrameConsumer() {
        UUID uuid = UUID.randomUUID();
        Player player = createMockPlayer(uuid, new ArrayList<>());
        Plugin plugin = createMockPlugin();

        List<String> consumed = new ArrayList<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        TextAnimation anim = TextAnimation.builder()
                .frame("A")
                .build();

        anim.play(plugin, player, (_, frame) -> consumed.add(frame.text()), () -> completed.set(true));

        assertEquals("A", consumed.getFirst());
        assertTrue(completed.get());
    }

    @Test
    @DisplayName("Expects custom builder configuration to apply intervals, finalStay, and finalTimes")
    void testBuilderConfiguration() {
        TextAnimation anim = TextAnimation.builder()
                .frame("Test 1")
                .frame("Test 2", "Sub 2")
                .interval(Duration.ofMillis(100))
                .finalStay(Duration.ofSeconds(3))
                .finalTimes(TimeableTitle.PROLONGED)
                .build();

        assertEquals(2L, anim.tickInterval());
        assertEquals(Duration.ofSeconds(3), anim.finalStay());
        assertEquals(TimeableTitle.PROLONGED, anim.finalTimes());
        assertEquals(2, anim.frames().size());
        assertEquals("Test 1", anim.frames().get(0).title());
        assertNull(anim.frames().get(0).subtitle());
        assertEquals("Test 2", anim.frames().get(1).title());
        assertEquals("Sub 2", anim.frames().get(1).subtitle());
    }

    @Test
    @DisplayName("Expects collection animation dispatch to filter nulls and handle offline players")
    void testCollectionDispatchNullSafety() {
        UUID uuid = UUID.randomUUID();
        List<String> actionbarPackets = new ArrayList<>();
        List<String> titlePackets = new ArrayList<>();
        Player player = createMockPlayer(uuid, titlePackets);
        TextAnimation.setCustomActionbarHandler((_, text) -> actionbarPackets.add(text));

        List<Player> players = new ArrayList<>();
        players.add(null);
        players.add(player);

        TextAnimation anim = TextAnimation.builder().frame("Test").finalStay(Duration.ZERO).build();
        AtomicBoolean completed = new AtomicBoolean(false);

        assertDoesNotThrow(() -> anim.playActionbar(createMockPlugin(), players, () -> completed.set(true)));
        assertTrue(completed.get());
    }

    @Test
    @DisplayName("Expects collection title dispatch to filter nulls and complete barrier")
    void testCollectionTitleDispatch() {
        UUID uuid = UUID.randomUUID();
        List<String> titlePackets = new ArrayList<>();
        Player player = createMockPlayer(uuid, titlePackets);

        List<Player> players = new ArrayList<>();
        players.add(null);
        players.add(player);

        TextAnimation anim = TextAnimation.builder()
                .frame("Title Test")
                .finalTimes(TimeableTitle.of(0, 0, 0))
                .build();
        AtomicBoolean completed = new AtomicBoolean(false);

        assertDoesNotThrow(() -> anim.playTitle(createMockPlugin(), players, () -> completed.set(true)));
        assertTrue(completed.get());
        assertTrue(titlePackets.contains("Title Test"));
    }

    @Test
    @DisplayName("Expects offline player to immediately invoke completion callback without deadlocking barrier")
    void testOfflinePlayerCompletesBarrier() {
        Player offlinePlayer = createOfflineMockPlayer(UUID.randomUUID());
        AtomicBoolean completed = new AtomicBoolean(false);

        TextAnimation anim = TextAnimation.builder().frame("Test").build();
        anim.play(createMockPlugin(), offlinePlayer, (_, _) -> {
        }, () -> completed.set(true));

        assertTrue(completed.get(), "Offline player must trigger completion callback");
    }

    @Test
    @DisplayName("Expects playActionbar replacement to cancel previous playback and invoke its callback")
    void testPlayActionbarReplacementCancelsPreviousPlayback() {
        Plugin plugin = createMockPlugin();
        UUID uuid = UUID.randomUUID();
        Player player = createMockPlayer(uuid, new ArrayList<>());
        ActionbarService service = new ActionbarService(plugin);

        AtomicBoolean completed1 = new AtomicBoolean(false);
        AtomicBoolean completed2 = new AtomicBoolean(false);

        TextAnimation anim1 = TextAnimation.builder().frame("Frame 1").finalStay(Duration.ofSeconds(10)).build();
        TextAnimation anim2 = TextAnimation.builder().frame("Frame 2").finalStay(Duration.ofSeconds(10)).build();

        TextAnimation.Playback playback1 = anim1.playActionbar(plugin, player, () -> completed1.set(true));
        assertNotNull(playback1);
        assertEquals(playback1, TextAnimation.getActivePlayback(player, TextAnimation.Channel.ACTIONBAR));
        assertFalse(playback1.isCancelled());
        assertTrue(service.isAnimating(uuid));

        TextAnimation.Playback playback2 = anim2.playActionbar(plugin, player, () -> completed2.set(true));
        assertNotNull(playback2);
        assertTrue(playback1.isCancelled(), "Previous playback must be cancelled on replacement");
        assertTrue(completed1.get(), "Previous playback callback must be invoked upon cancellation");
        assertEquals(playback2, TextAnimation.getActivePlayback(player, TextAnimation.Channel.ACTIONBAR));
    }

    @Test
    @DisplayName("Expects playTitle replacement to cancel previous playback and invoke its callback")
    void testPlayTitleReplacementCancelsPreviousPlayback() {
        Plugin plugin = createMockPlugin();
        UUID uuid = UUID.randomUUID();
        Player player = createMockPlayer(uuid, new ArrayList<>());
        TitleService service = new TitleService(plugin);

        AtomicBoolean completed1 = new AtomicBoolean(false);
        AtomicBoolean completed2 = new AtomicBoolean(false);

        TextAnimation anim1 = TextAnimation.builder().frame("Title 1").finalTimes(TimeableTitle.of(0, 100, 0)).build();
        TextAnimation anim2 = TextAnimation.builder().frame("Title 2").finalTimes(TimeableTitle.of(0, 100, 0)).build();

        TextAnimation.Playback playback1 = anim1.playTitle(plugin, player, () -> completed1.set(true));
        assertNotNull(playback1);
        assertEquals(playback1, TextAnimation.getActivePlayback(player, TextAnimation.Channel.TITLE));
        assertFalse(playback1.isCancelled());
        assertTrue(service.isAnimating(uuid));

        TextAnimation.Playback playback2 = anim2.playTitle(plugin, player, () -> completed2.set(true));
        assertNotNull(playback2);
        assertTrue(playback1.isCancelled(), "Previous playback must be cancelled on replacement");
        assertTrue(completed1.get(), "Previous playback callback must be invoked upon cancellation");
        assertEquals(playback2, TextAnimation.getActivePlayback(player, TextAnimation.Channel.TITLE));
    }

    @Test
    @DisplayName("Expects actionbar and title playbacks to be tracked independently per channel")
    void testChannelsAreIndependent() {
        Plugin plugin = createMockPlugin();
        UUID uuid = UUID.randomUUID();
        Player player = createMockPlayer(uuid, new ArrayList<>());

        TextAnimation animBar = TextAnimation.builder().frame("Actionbar").finalStay(Duration.ofSeconds(10)).build();
        TextAnimation animTitle = TextAnimation.builder().frame("Title").finalTimes(TimeableTitle.of(0, 100, 0)).build();

        TextAnimation.Playback barPlayback = animBar.playActionbar(plugin, player, null);
        TextAnimation.Playback titlePlayback = animTitle.playTitle(plugin, player, null);

        assertNotNull(barPlayback);
        assertNotNull(titlePlayback);
        assertFalse(barPlayback.isCancelled());
        assertFalse(titlePlayback.isCancelled());
        assertEquals(barPlayback, TextAnimation.getActivePlayback(player, TextAnimation.Channel.ACTIONBAR));
        assertEquals(titlePlayback, TextAnimation.getActivePlayback(player, TextAnimation.Channel.TITLE));
    }

    @Test
    @DisplayName("Expects handle cancellation to clear animation state, restore HUD, and invoke callback exactly once")
    void testHandleCancellationClearsStateAndInvokesCallbackExactlyOnce() {
        Plugin plugin = createMockPlugin();
        UUID uuid = UUID.randomUUID();
        Player player = createMockPlayer(uuid, new ArrayList<>());
        ActionbarService service = new ActionbarService(plugin);

        AtomicInteger callbackCalls = new AtomicInteger(0);
        TextAnimation anim = TextAnimation.builder().frame("Frame").finalStay(Duration.ofSeconds(10)).build();

        TextAnimation.Playback playback = anim.playActionbar(plugin, player, callbackCalls::incrementAndGet);
        assertNotNull(playback);
        assertTrue(service.isAnimating(uuid));
        assertFalse(playback.isCancelled());

        playback.cancel();
        assertTrue(playback.isCancelled());
        assertFalse(service.isAnimating(uuid), "Animation state must be cleared on cancellation");
        assertEquals(1, callbackCalls.get(), "Callback must be invoked exactly once on cancellation");
        assertNull(TextAnimation.getActivePlayback(player, TextAnimation.Channel.ACTIONBAR));

        // Subsequent cancellation should be idempotent
        playback.cancel();
        assertEquals(1, callbackCalls.get(), "Subsequent cancellation must not re-invoke callback");
    }

    @Test
    @DisplayName("Expects returned handle to own chained stages and cancelling it during final stay cleans up properly")
    void testHandleOwnsChainedStagesAndCancellingHaltsSequence() {
        Plugin plugin = createMockPlugin();
        UUID uuid = UUID.randomUUID();
        Player player = createMockPlayer(uuid, new ArrayList<>());
        ActionbarService service = new ActionbarService(plugin);

        AtomicBoolean stage1Ran = new AtomicBoolean(false);
        AtomicBoolean stage2Ran = new AtomicBoolean(false);
        AtomicInteger completionCount = new AtomicInteger(0);

        TextAnimation stage1 = TextAnimation.builder().frame("Stage 1")
                .onComplete(_ -> stage1Ran.set(true))
                .build();
        TextAnimation stage2 = TextAnimation.builder().frame("Stage 2")
                .onComplete(_ -> stage2Ran.set(true))
                .finalStay(Duration.ofSeconds(10))
                .build();

        TextAnimation chained = stage1.then(stage2);
        TextAnimation.Playback handle = chained.playActionbar(plugin, player, completionCount::incrementAndGet);

        assertNotNull(handle);
        assertTrue(stage1Ran.get(), "Stage 1 should complete and transition to Stage 2");
        assertTrue(stage2Ran.get(), "Stage 2 should execute");
        assertTrue(service.isAnimating(uuid));
        assertEquals(handle, TextAnimation.getActivePlayback(player, TextAnimation.Channel.ACTIONBAR));

        handle.cancel();

        assertTrue(handle.isCancelled());
        assertFalse(service.isAnimating(uuid));
        assertEquals(1, completionCount.get());
        assertNull(TextAnimation.getActivePlayback(player, TextAnimation.Channel.ACTIONBAR));
    }

    @Test
    @DisplayName("Expects title cancellation to reset client title and invalidate composer cache while normal completion preserves fadeout")
    void testTitleCleanupDistinguishesCancellationFromNormalCompletion() {
        Plugin plugin = createMockPlugin();
        UUID uuid = UUID.randomUUID();
        AtomicInteger resetCalls = new AtomicInteger(0);
        List<String> titlesSent = new ArrayList<>();
        Player player = createMockPlayer(uuid, titlesSent, resetCalls);
        TitleService service = new TitleService(plugin);

        service.setPersistent(player, fr.moussax.bedrock.ui.title.PersistentTitle.of("Persistent Title", "Persistent Subtitle"));
        titlesSent.clear();

        TextAnimation anim = TextAnimation.builder().frame("Anim Frame").finalTimes(TimeableTitle.of(0, 100, 0)).build();
        TextAnimation.Playback playback = anim.playTitle(plugin, player, null);
        assertNotNull(playback);
        assertTrue(service.isAnimating(uuid));

        playback.cancel();
        assertTrue(playback.isCancelled());
        assertFalse(service.isAnimating(uuid));
        assertEquals(1, resetCalls.get(), "Cancelled title playback must reset client title");
        assertTrue(titlesSent.contains("Persistent Title"), "Invalidated cache must cause renderPlayer to resend persistent title");

        resetCalls.set(0);
        titlesSent.clear();
        TextAnimation normalAnim = TextAnimation.builder().frame("Normal Frame").finalTimes(TimeableTitle.of(0, 0, 0)).build();
        TextAnimation.Playback normalPlayback = normalAnim.playTitle(plugin, player, null);
        assertNotNull(normalPlayback);

        assertFalse(normalPlayback.isCancelled());
        assertEquals(0, resetCalls.get(), "Normal completion must NOT reset client title so fade-out is preserved");
        assertTrue(titlesSent.contains("Persistent Title"), "Normal completion must also restore persistent title via invalidated cache");
    }

    private Player createOfflineMockPlayer(UUID uuid) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (_, method, _) -> {
                    String name = method.getName();
                    if ("getUniqueId".equals(name)) return uuid;
                    if ("isOnline".equals(name)) return false;
                    return null;
                }
        );
    }
}
