package fr.moussax.bedrock.ui.title;

import fr.moussax.bedrock.ui.animation.TextAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TitleTest {

    private record TitleCall(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    }

    private record SimpleTitleCall(String title, String subtitle) {
    }

    private Player createMockPlayer(
            AtomicReference<TitleCall> titleRef,
            AtomicReference<SimpleTitleCall> simpleTitleRef,
            AtomicBoolean resetRef
    ) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("sendTitle".equals(name) && args != null) {
                        if (args.length == 5) {
                            titleRef.set(new TitleCall(
                                    (String) args[0],
                                    (String) args[1],
                                    (int) args[2],
                                    (int) args[3],
                                    (int) args[4]
                            ));
                            return null;
                        }
                        if (args.length == 2) {
                            simpleTitleRef.set(new SimpleTitleCall(
                                    (String) args[0],
                                    (String) args[1]
                            ));
                            return null;
                        }
                    }
                    if ("resetTitle".equals(name)) {
                        resetRef.set(true);
                        return null;
                    }
                    if ("isOnline".equals(name)) {
                        return true;
                    }
                    if ("getLocation".equals(name)) {
                        return new Location(null, 0, 0, 0);
                    }
                    if ("playSound".equals(name)) {
                        return null;
                    }
                    return null;
                }
        );
    }

    @Test
    @DisplayName("Expects TimeableTitle presets and clamp behavior to be consistent")
    void testTimeableTitleBasics() {
        assertEquals(10, TimeableTitle.DEFAULT.fadeIn());
        assertEquals(60, TimeableTitle.DEFAULT.stay());
        assertEquals(20, TimeableTitle.DEFAULT.fadeOut());
        assertEquals(90, TimeableTitle.DEFAULT.totalTicks());

        assertEquals(0, TimeableTitle.ALERT.fadeIn());
        assertEquals(30, TimeableTitle.ALERT.stay());
        assertEquals(10, TimeableTitle.ALERT.fadeOut());

        assertEquals(0, TimeableTitle.INSTANT.fadeIn());
        assertEquals(5, TimeableTitle.SUBTITLE_ONLY.fadeIn());
        assertEquals(0, TimeableTitle.PERMANENT.fadeIn());
        assertEquals(100, TimeableTitle.PERMANENT.stay());
        assertEquals(0, TimeableTitle.ANIMATION_FRAME.fadeIn());
        assertEquals(0, TimeableTitle.ANIMATION_FRAME.fadeOut());

        // Clamping negative values
        TimeableTitle clamped = TimeableTitle.of(-5, -10, -2);
        assertEquals(0, clamped.fadeIn());
        assertEquals(0, clamped.stay());
        assertEquals(0, clamped.fadeOut());
        assertEquals(0, clamped.totalTicks());
    }

    @Test
    @DisplayName("Expects TimeableTitle duration conversion to convert milliseconds to ticks accurately")
    void testTimeableTitleDurationConversion() {
        TimeableTitle times = TimeableTitle.of(
                Duration.ofMillis(500),  // 10 ticks
                Duration.ofSeconds(3),   // 60 ticks
                Duration.ofSeconds(1)    // 20 ticks
        );

        assertEquals(10, times.fadeIn());
        assertEquals(60, times.stay());
        assertEquals(20, times.fadeOut());

        TimeableTitle stayOnly = TimeableTitle.stay(Duration.ofSeconds(2)); // 40 ticks
        assertEquals(10, stayOnly.fadeIn());
        assertEquals(40, stayOnly.stay());
        assertEquals(20, stayOnly.fadeOut());
    }

    @Test
    @DisplayName("Expects TitleBuilder to substitute single space when title is empty to guarantee subtitle rendering")
    void testSubtitleOnlySpaceSubstitution() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        Title.sendSubtitle(player, "§e+100 Coins", TimeableTitle.SUBTITLE_ONLY);

        TitleCall call = titleRef.get();
        assertNotNull(call);
        assertEquals(" ", call.title(), "Empty title with non-empty subtitle must be a single space");
        assertEquals("§e+100 Coins", call.subtitle());
        assertEquals(5, call.fadeIn());
        assertEquals(35, call.stay());
        assertEquals(10, call.fadeOut());
    }

    @Test
    @DisplayName("Expects TitleBuilder to dispatch full title and subtitle with custom timings")
    void testFullTitleDispatch() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        Title.builder()
                .title("§a§lVICTORY!")
                .subtitle("§7Dungeon Floor 7")
                .times(TimeableTitle.PROLONGED)
                .send(player);

        TitleCall call = titleRef.get();
        assertNotNull(call);
        assertEquals("§a§lVICTORY!", call.title());
        assertEquals("§7Dungeon Floor 7", call.subtitle());
        assertEquals(20, call.fadeIn());
        assertEquals(90, call.stay());
        assertEquals(30, call.fadeOut());
    }

    @Test
    @DisplayName("Expects Title.clear to call player.resetTitle()")
    void testTitleClear() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        Title.clear(player);
        assertTrue(resetRef.get());
    }

    @Test
    @DisplayName("Expects TitleComposer to render persistent title and diff-suppress duplicate sends")
    void testTitleComposerPersistent() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        TitleComposer composer = new TitleComposer();
        composer.setPersistentTitle(PersistentTitle.of("§6§lLOBBY", "§7Waiting for players..."));

        // First render initializes timing and text
        composer.render(player);
        assertNotNull(titleRef.get());
        assertEquals("§6§lLOBBY", titleRef.get().title());
        assertEquals("§7Waiting for players...", titleRef.get().subtitle());

        // Subsequent render with unchanged text suppresses packet dispatch
        titleRef.set(null);
        simpleTitleRef.set(null);
        composer.render(player);
        assertNull(titleRef.get(), "Duplicate packet must be suppressed");
        assertNull(simpleTitleRef.get(), "Duplicate packet must be suppressed");
    }

    @Test
    @DisplayName("Expects TitleComposer to override persistent title with alert and restore it upon expiration")
    void testTitleComposerAlertOverrideAndRestore() throws InterruptedException {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        TitleComposer composer = new TitleComposer();
        composer.setPersistentTitle(PersistentTitle.of("§6§lLOBBY", "§7Waiting for players..."));

        // Render baseline persistent title
        composer.render(player);
        assertEquals("§6§lLOBBY", titleRef.get().title());

        // Send short modal alert (50ms)
        titleRef.set(null);
        composer.sendModalAlert("§c§lALERT!", "§fIncoming raid!", 10, Duration.ofMillis(50));
        composer.render(player);

        // Alert should immediately display with alert timings
        assertNotNull(titleRef.get());
        assertEquals("§c§lALERT!", titleRef.get().title());
        assertEquals("§fIncoming raid!", titleRef.get().subtitle());

        // Wait for alert to expire
        Thread.sleep(60L);

        // Render pass after expiration: Persistent title must be automatically restored!
        titleRef.set(null);
        simpleTitleRef.set(null);
        composer.render(player);

        assertNotNull(titleRef.get(), "Persistent title must be restored when alert expires");
        assertEquals("§6§lLOBBY", titleRef.get().title());
        assertEquals("§7Waiting for players...", titleRef.get().subtitle());
    }

    @AfterEach
    void tearDown() {
        TitlePacketSender.setCustomHandler(null);
    }

    @Test
    @DisplayName("Expects TitleComposer to update text in-place without re-sending timing packets when timings are unchanged")
    void testInPlaceTextUpdateWithoutTimesReset() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        AtomicReference<String> textOnlyTitle = new AtomicReference<>();
        TitlePacketSender.setCustomHandler((p, t, s) -> textOnlyTitle.set(t));

        TitleComposer composer = new TitleComposer();
        final List<String> counter = new ArrayList<>(List.of("Frame 1"));
        composer.setPersistentTitle(PersistentTitle.of(() -> counter.getFirst(), () -> "Subtitle"));

        // Frame 1: Initial full packet with timings
        composer.render(player);
        assertNotNull(titleRef.get());
        assertEquals("Frame 1", titleRef.get().title());

        // Frame 2: Only text changes, timings remain identical
        titleRef.set(null);
        counter.set(0, "Frame 2");
        composer.render(player);

        // Crucial test: titleRef (times packet) must be null, TitlePacketSender text-only must be called!
        assertNull(titleRef.get(), "Times packet must not be resent when timings are unchanged");
        assertEquals("Frame 2", textOnlyTitle.get(), "In-place text update packet must be sent");
    }

    @Test
    @DisplayName("Expects multiple modal alerts to respect priority precedence and sequentially restore lower priority alerts")
    void testMultipleModalAlertsPriorityAndSequentialRestoration() throws InterruptedException {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        AtomicReference<String> textOnlyTitle = new AtomicReference<>();
        TitlePacketSender.setCustomHandler((p, t, s) -> textOnlyTitle.set(t));

        TitleComposer composer = new TitleComposer();
        composer.setPersistentTitle(PersistentTitle.of("§6§lBASE", "§7Base Subtitle"));

        // Queue Alert A: Low priority (1), long duration (600ms)
        composer.sendModalAlert("§aLow Alert", "Subtitle A", 1, Duration.ofMillis(600));

        // Queue Alert B: Highest priority (10), short duration (60ms)
        composer.sendModalAlert("§cHighest Alert", "Subtitle B", 10, Duration.ofMillis(60));

        // Queue Alert C: Medium priority (5), medium duration (300ms)
        composer.sendModalAlert("§eMedium Alert", "Subtitle C", 5, Duration.ofMillis(300));

        // Initial render: Highest Alert (B) must win
        composer.render(player);
        assertEquals("§cHighest Alert", titleRef.get().title());

        // Wait for B to expire (90ms)
        Thread.sleep(90L);
        titleRef.set(null);
        textOnlyTitle.set(null);
        composer.render(player);

        // Medium Alert (C) must now be displayed (in-place text update)
        assertEquals("§eMedium Alert", textOnlyTitle.get());

        // Wait for C to expire (additional 240ms -> total ~330ms)
        Thread.sleep(240L);
        titleRef.set(null);
        textOnlyTitle.set(null);
        composer.render(player);

        // Low Alert (A) must now be displayed (in-place text update)
        assertEquals("§aLow Alert", textOnlyTitle.get());

        // Wait for A to expire (additional 300ms -> total ~630ms)
        Thread.sleep(300L);
        titleRef.set(null);
        textOnlyTitle.set(null);
        composer.render(player);

        // Finally, underlying persistent title must be restored (with PERMANENT timings)
        assertNotNull(titleRef.get());
        assertEquals("§6§lBASE", titleRef.get().title());
    }

    @Test
    @DisplayName("Expects clearAlerts to dismiss active alerts and immediately restore persistent title")
    void testClearAlertsPreservesPersistentTitle() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        TitleComposer composer = new TitleComposer();
        composer.setPersistentTitle(PersistentTitle.of("§6§lLOBBY", "§7Waiting..."));

        composer.render(player);
        assertEquals("§6§lLOBBY", titleRef.get().title());

        // Send alert
        titleRef.set(null);
        composer.sendModalAlert("§cALERT", "Warning", 5, Duration.ofSeconds(10));
        composer.render(player);
        assertEquals("§cALERT", titleRef.get().title());

        // Clear only alerts
        titleRef.set(null);
        composer.clearAlerts();
        composer.render(player);

        // Persistent title should immediately reappear
        assertNotNull(titleRef.get());
        assertEquals("§6§lLOBBY", titleRef.get().title());
        assertTrue(composer.hasPersistentTitle());
    }

    @Test
    @DisplayName("Expects modal alert with null or empty title to substitute single space for subtitle visibility")
    void testSubtitleOnlyModalAlertSubstitutesSpace() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        TitleComposer composer = new TitleComposer();
        composer.sendModalAlert(null, "§e+50 EXP", 0, Duration.ofSeconds(2));
        composer.render(player);

        assertNotNull(titleRef.get());
        assertEquals(" ", titleRef.get().title(), "Must be single space to ensure subtitle renders on client");
        assertEquals("§e+50 EXP", titleRef.get().subtitle());
    }

    @Test
    @DisplayName("Expects offline players to be completely ignored during render")
    void testOfflinePlayerIgnored() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);

        Player offlinePlayer = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if ("isOnline".equals(method.getName())) return false;
                    if ("sendTitle".equals(method.getName())) {
                        fail("sendTitle must never be called on offline player");
                    }
                    return null;
                }
        );

        TitleComposer composer = new TitleComposer();
        composer.setPersistentTitle(PersistentTitle.of("Title", "Sub"));
        composer.render(offlinePlayer); // Should be safely ignored
    }

    @Test
    @DisplayName("Expects Title.send with Duration to calculate stay ticks accurately")
    void testSendWithDuration() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        Title.send(player, "§aTitle", "§7Subtitle", Duration.ofSeconds(4));

        TitleCall call = titleRef.get();
        assertNotNull(call);
        assertEquals("§aTitle", call.title());
        assertEquals("§7Subtitle", call.subtitle());
        assertEquals(10, call.fadeIn());
        assertEquals(80, call.stay()); // 4 seconds * 20 = 80 ticks
        assertEquals(20, call.fadeOut());
    }

    @Test
    @DisplayName("Expects Title.send and Title.alert to dispatch to collections of players")
    void testCollectionDispatch() {
        AtomicReference<TitleCall> titleRef1 = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleRef1 = new AtomicReference<>();
        AtomicBoolean reset1 = new AtomicBoolean(false);
        Player player1 = createMockPlayer(titleRef1, simpleRef1, reset1);

        AtomicReference<TitleCall> titleRef2 = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleRef2 = new AtomicReference<>();
        AtomicBoolean reset2 = new AtomicBoolean(false);
        Player player2 = createMockPlayer(titleRef2, simpleRef2, reset2);

        List<Player> players = List.of(player1, player2);

        Title.send(players, "§6Global", "§fMessage", Duration.ofSeconds(2));
        assertNotNull(titleRef1.get());
        assertNotNull(titleRef2.get());
        assertEquals("§6Global", titleRef1.get().title());
        assertEquals("§6Global", titleRef2.get().title());
        assertEquals(40, titleRef1.get().stay());
        assertEquals(40, titleRef2.get().stay());
    }

    @Test
    @DisplayName("Expects zero-second countdown to fire completion title and callback immediately")
    void testZeroSecondCountdownFiresImmediately() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        AtomicBoolean completed = new AtomicBoolean(false);
        Title.countdown(player, 0, "§a§lSTART!", "§7Go!", () -> completed.set(true));

        assertTrue(completed.get());
        TitleCall call = titleRef.get();
        assertNotNull(call);
        assertEquals("§a§lSTART!", call.title());
        assertEquals("§7Go!", call.subtitle());
    }

    @Test
    @DisplayName("Expects chained animations with three or more stages to preserve and execute all stages in sequence")
    void testChainedAnimationPreservesAllStages() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        List<String> renderedTitles = new ArrayList<>();
        Player player = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("sendTitle".equals(name) && args != null && args.length > 0) {
                        renderedTitles.add((String) args[0]);
                        return null;
                    }
                    if ("isOnline".equals(name)) return true;
                    if ("getLocation".equals(name)) return new Location(null, 0, 0, 0);
                    return null;
                }
        );
        TitlePacketSender.setCustomHandler((_, title, _) -> {
            if (title != null) renderedTitles.add(title);
        });

        TextAnimation stage1 = TextAnimation.builder().frame("Stage 1").build();
        TextAnimation stage2 = TextAnimation.builder().frame("Stage 2").build();
        TextAnimation stage3 = TextAnimation.builder().frame("Stage 3").build();

        org.bukkit.plugin.Plugin mockPlugin = (org.bukkit.plugin.Plugin) Proxy.newProxyInstance(
                org.bukkit.plugin.Plugin.class.getClassLoader(),
                new Class<?>[]{org.bukkit.plugin.Plugin.class},
                (_, _, _) -> null
        );

        AtomicBoolean completed = new AtomicBoolean(false);
        TextAnimation chained = stage1.then(stage2).then(stage3);
        chained.playTitle(mockPlugin, player, () -> completed.set(true));

        assertTrue(completed.get(), "Chained sequence must reach completion");
        assertTrue(renderedTitles.contains("Stage 1"), "Must execute Stage 1");
        assertTrue(renderedTitles.contains("Stage 2"), "Must execute Stage 2 and not discard it");
        assertTrue(renderedTitles.contains("Stage 3"), "Must execute Stage 3");
    }

    @Test
    @DisplayName("Expects rapid text changes not to suppress stay renewal when nearing expiration")
    void testRapidTextChangesAllowNearExpirationRenewal() throws InterruptedException {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player player = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        AtomicReference<String> textOnlyRef = new AtomicReference<>();
        TitlePacketSender.setCustomHandler((_, title, _) -> textOnlyRef.set(title));

        AtomicReference<String> dynamicText = new AtomicReference<>("Frame A");
        TitleComposer composer = new TitleComposer();
        // 4 ticks = 200ms stay. Expiration renewal threshold = max(1000ms, 150ms) = 1000ms.
        // Let's use stay of 30 ticks = 1500ms stay. Expiration renewal threshold = max(1000ms, (1500*3)/4) = 1125ms.
        composer.setPersistentTitle(PersistentTitle.of(dynamicText::get, () -> "", TimeableTitle.of(0, 30, 0)));

        // T=0: Initial render (timesChanged = true -> sendFull)
        composer.render(player);
        assertNotNull(titleRef.get());
        assertEquals("Frame A", titleRef.get().title());

        // T+50ms: Text change -> sendTextOnly (does NOT reset lastTimingSentTimestamp)
        dynamicText.set("Frame B");
        titleRef.set(null);
        composer.render(player);
        assertEquals("Frame B", textOnlyRef.get());
        assertNull(titleRef.get(), "sendFull should not be called for text change");

        // Wait to cross nearExpiration (1150ms)
        Thread.sleep(1150L);
        dynamicText.set("Frame C");
        composer.render(player);

        // Even though text changed, nearExpiration is triggered -> sendFull with 0 fade-in
        assertNotNull(titleRef.get(), "sendFull must be called when nearing expiration even if text changed");
        assertEquals("Frame C", titleRef.get().title());
        assertEquals(0, titleRef.get().fadeIn());
        assertEquals(30, titleRef.get().stay());
    }

    @Test
    @DisplayName("Expects Collection dispatch helpers to safely ignore null or offline elements")
    void testCollectionNullAndOfflineFiltering() {
        AtomicReference<TitleCall> titleRef = new AtomicReference<>();
        AtomicReference<SimpleTitleCall> simpleTitleRef = new AtomicReference<>();
        AtomicBoolean resetRef = new AtomicBoolean(false);
        Player onlinePlayer = createMockPlayer(titleRef, simpleTitleRef, resetRef);

        List<Player> mixed = new ArrayList<>();
        mixed.add(null);
        mixed.add(onlinePlayer);

        assertDoesNotThrow(() -> Title.send(mixed, "Title", "Sub"));
        assertNotNull(titleRef.get());
        assertEquals("Title", titleRef.get().title());
    }
}
