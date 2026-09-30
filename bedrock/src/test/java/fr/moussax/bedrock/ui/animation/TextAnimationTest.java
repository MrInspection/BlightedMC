package fr.moussax.bedrock.ui.animation;

import fr.moussax.bedrock.ui.actionbar.Actionbar;
import fr.moussax.bedrock.ui.actionbar.ActionbarService;
import fr.moussax.bedrock.ui.title.TimeableTitle;
import fr.moussax.bedrock.ui.title.Title;
import fr.moussax.bedrock.ui.title.TitlePacketSender;
import fr.moussax.bedrock.ui.title.TitleService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import fr.moussax.bedrock.scheduling.PluginContext;
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

import static org.junit.jupiter.api.Assertions.*;

class TextAnimationTest {

    @BeforeEach
    void setUp() {
        PluginContext.bind(createMockPlugin());
    }

    @AfterEach
    void tearDown() {
        TitlePacketSender.setCustomHandler(null);
        TextAnimation.setCustomActionbarHandler(null);
        ActionbarService.setInstance(null);
        TitleService.setInstance(null);
        PluginContext.unbind();
    }

    private Player createMockPlayer(UUID uuid, List<String> titleRef) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (_, method, args) -> {
                    String name = method.getName();
                    if ("getUniqueId".equals(name)) return uuid;
                    if ("isOnline".equals(name)) return true;
                    if ("getName".equals(name)) return "TestPlayer";
                    if ("getLocation".equals(name)) return new Location(null, 0, 0, 0);
                    if ("playSound".equals(name)) return null;
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
}
