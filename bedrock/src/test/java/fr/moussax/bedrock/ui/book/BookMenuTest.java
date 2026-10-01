package fr.moussax.bedrock.ui.book;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMenuTest {

    @Test
    @DisplayName("BookCanvas accurately measures standard and styled text pixel widths")
    void shouldMeasurePixelWidthsAccurately() {
        assertEquals(0, BookCanvas.measurePixelWidth(""));
        assertEquals(0, BookCanvas.measurePixelWidth(null));

        // '!' is 1px + 1px spacing = 2px
        assertEquals(2, BookCanvas.measurePixelWidth("!"));

        // 'a' is 5px + 1px spacing = 6px
        assertEquals(6, BookCanvas.measurePixelWidth("a"));

        // Formatting codes (e.g. §c) have 0 width
        assertEquals(6, BookCanvas.measurePixelWidth("§ca"));

        // Ampersand formatting codes (&c, &l) are also properly normalized and measured
        assertEquals(6, BookCanvas.measurePixelWidth("&ca"));
        assertEquals(7, BookCanvas.measurePixelWidth("&la"));

        // Bold (§l) adds 1px per character: 'a' bold is 6 + 1 = 7px
        assertEquals(7, BookCanvas.measurePixelWidth("§la"));
    }

    @Test
    @DisplayName("BookCanvas centers text horizontally on the 114px canvas")
    void shouldCenterTextCorrectly() {
        String centered = BookCanvas.center("TEST");
        assertTrue(centered.startsWith(" "), "Centered text should have prepended space padding");
        assertTrue(centered.endsWith("TEST"));
    }

    @Test
    @DisplayName("BookPage creates formatted titles, text, and fields")
    void shouldBuildPageWithStructure() {
        BookPage page = new BookPage()
                .title("RULES")
                .blank()
                .field("Category", "Gameplay")
                .quote("No cheating")
                .blank();

        BaseComponent[] components = page.build();
        assertTrue(components.length > 0);

        StringBuilder plainText = new StringBuilder();
        for (BaseComponent component : components) {
            plainText.append(component.toPlainText());
        }

        String fullContent = plainText.toString();
        assertTrue(fullContent.contains("RULES"));
        assertTrue(fullContent.contains("Category: Gameplay"));
        assertTrue(fullContent.contains("\"No cheating\""));
    }

    @Test
    @DisplayName("BookPage creates buttons with RUN_COMMAND and CHANGE_PAGE")
    void shouldCreateInteractiveButtonsAndLinks() {
        BookPage page = new BookPage()
                .button("§c[Dismiss]", "§7Click to dismiss", "/dismiss 123")
                .pageButton("§9[Next]", "§7Go to page 2", 2);

        BaseComponent[] components = page.build();
        assertTrue(components.length >= 2);

        // Find the dismiss button
        TextComponent dismissComponent = findComponentWithText(components, "[Dismiss]");
        assertNotNull(dismissComponent);
        assertNotNull(dismissComponent.getClickEvent());
        assertEquals(ClickEvent.Action.RUN_COMMAND, dismissComponent.getClickEvent().getAction());
        assertEquals("/dismiss 123", dismissComponent.getClickEvent().getValue());
        assertNotNull(dismissComponent.getHoverEvent());
        assertEquals(HoverEvent.Action.SHOW_TEXT, dismissComponent.getHoverEvent().getAction());

        // Find the page link component
        TextComponent pageLinkComponent = findComponentWithText(components, "[Next]");
        assertNotNull(pageLinkComponent);
        assertNotNull(pageLinkComponent.getClickEvent());
        assertEquals(ClickEvent.Action.CHANGE_PAGE, pageLinkComponent.getClickEvent().getAction());
        assertEquals("2", pageLinkComponent.getClickEvent().getValue());
    }

    @Test
    @DisplayName("BookPage sanitizes command clicks by automatically prepending slash if missing")
    void shouldSanitizeCommandsWithSlash() {
        BookPage page = new BookPage()
                .button("§c[Action]", "§7Run action", "say hello");

        BaseComponent[] components = page.build();
        TextComponent button = findComponentWithText(components, "[Action]");
        assertNotNull(button);
        assertNotNull(button.getClickEvent());
        assertEquals("/say hello", button.getClickEvent().getValue());
    }

    @Test
    @DisplayName("BookPage automatically wraps long paragraphs and lore lines")
    void shouldWrapParagraphAndLoreAccurately() {
        BookPage page = new BookPage()
                .paragraph("This is a paragraph of lore that exceeds the 114 pixel width of a standard Minecraft written book page and wraps neatly.")
                .lore("Short line", "Another moderately long lore description that will undergo automatic pixel width wrapping.");

        assertTrue(page.lineCount() >= 3, "Wrapped paragraph and lore should produce multiple lines");
        assertTrue(page.remainingLines() < 12);

        BaseComponent[] components = page.build();
        StringBuilder text = new StringBuilder();
        for (BaseComponent component : components) {
            text.append(component.toPlainText());
        }

        String content = text.toString();
        assertTrue(content.contains("This is a paragraph"));
        assertTrue(content.contains("Short line"));
        assertTrue(content.contains("Another moderately"));
        assertTrue(content.contains("long lore"));
    }

    @Test
    @DisplayName("BookPage creates centered buttons, links, page navigation, suggestions, and copy")
    void shouldCreateCenteredButtonsAndLinks() {
        BookPage page = new BookPage()
                .centeredButton("[Command]", "Run Command", "test command")
                .centeredLink("[Website]", "Open Web", "https://blighted.net")
                .centeredPageButton("[Next Page]", "Go Next", 3)
                .actionCopy("[IP]", "Copy IP", "play.blighted.net");

        BaseComponent[] components = page.build();

        TextComponent link = findComponentWithText(components, "[Website]");
        assertNotNull(link);
        assertNotNull(link.getClickEvent());
        assertEquals(ClickEvent.Action.OPEN_URL, link.getClickEvent().getAction());
        assertEquals("https://blighted.net", link.getClickEvent().getValue());

        TextComponent pageBtn = findComponentWithText(components, "[Next Page]");
        assertNotNull(pageBtn);
        assertNotNull(pageBtn.getClickEvent());
        assertEquals(ClickEvent.Action.CHANGE_PAGE, pageBtn.getClickEvent().getAction());
        assertEquals("3", pageBtn.getClickEvent().getValue());

        TextComponent copy = findComponentWithText(components, "[IP]");
        assertNotNull(copy);
        assertNotNull(copy.getClickEvent());
        assertEquals(ClickEvent.Action.COPY_TO_CLIPBOARD, copy.getClickEvent().getAction());
        assertEquals("play.blighted.net", copy.getClickEvent().getValue());

        // Verify leading whitespace padding is inert (no click or hover event)
        BaseComponent firstComponent = components[0];
        assertNull(firstComponent.getClickEvent(), "Leading center padding should be inert without click event");
        assertNull(firstComponent.getHoverEvent(), "Leading center padding should be inert without hover event");
    }

    @Test
    @DisplayName("BookPage supports Collection overload for lines")
    void shouldSupportCollectionLines() {
        BookPage page = new BookPage().lines(List.of("Line 1", "Line 2"));
        assertEquals(2, page.lineCount());
    }

    @Test
    @DisplayName("BookPage automatically wraps long field values and quotes across lines")
    void shouldWrapLongFieldsAndQuotes() {
        BookPage page = new BookPage()
                .field("Reason", "Player was caught using flying cheats and speed hacks in the warzone during combat")
                .quote("In the ancient forgotten depths of the underworld, eternal darkness reigns supreme without mercy.");

        assertTrue(page.lineCount() >= 4, "Long field and long quote should wrap into multiple lines");
        assertTrue(page.remainingLines() <= 10);

        BaseComponent[] components = page.build();
        StringBuilder text = new StringBuilder();
        for (BaseComponent component : components) {
            text.append(component.toPlainText());
        }

        String content = text.toString();
        assertTrue(content.contains("Reason:"));
        assertTrue(content.contains("flying"));
        assertTrue(content.contains("cheats"));
        assertTrue(content.contains("forgotten depths"));
    }

    @Test
    @DisplayName("BookPage supports concise fluent DSL without add prefix")
    void shouldSupportConciseFluentPageDsl() {
        BookPage page = new BookPage()
                .title("WELCOME")
                .blank()
                .field("Status", "Active")
                .quote("Stay vigilant")
                .paragraph("This is a concise paragraph.")
                .lore("Ancient lore line 1", "Ancient lore line 2")
                .blank()
                .actionButton("[Confirm]", "Click to confirm", "confirm");

        assertTrue(page.lineCount() >= 8);
        BaseComponent[] components = page.build();
        assertTrue(components.length > 0);

        StringBuilder sb = new StringBuilder();
        for (BaseComponent c : components) {
            sb.append(c.toPlainText());
        }
        String content = sb.toString();
        assertTrue(content.contains("WELCOME"));
        assertTrue(content.contains("Status: Active"));
        assertTrue(content.contains("\"Stay vigilant\""));
        assertTrue(content.contains("concise"));
        assertTrue(content.contains("paragraph"));
        assertTrue(content.contains("Ancient lore line 1"));
        assertTrue(content.contains("[Confirm]"));
    }

    @Test
    @DisplayName("BookPage supports action buttons and fits evaluation")
    void shouldSupportActionButtonsAndFitEvaluation() {
        BookPage page = new BookPage()
                .actionButton("[Action Command]", "Execute action", "say hi")
                .actionLink("[Wiki]", "Read wiki", "https://wiki.blighted.net")
                .actionPage("[Next]", "Page 2", 2);

        assertEquals(3, page.lineCount());
        assertTrue(page.fits("Short text"));
        assertFalse(page.fits("This is an extremely long multi-paragraph text that spans so many lines that it definitely cannot fit inside the remaining 11 lines of this single page. It goes on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on."));
    }

    @Test
    @DisplayName("BookPage tracks line count and remaining line capacity accurately")
    void shouldTrackLineBudget() {
        BookPage page = new BookPage();
        assertEquals(0, page.lineCount());
        assertEquals(14, page.remainingLines());
        assertFalse(page.isFull());

        page.line("Line 1");
        page.line("Line 2");
        page.blank();

        assertEquals(3, page.lineCount());
        assertEquals(11, page.remainingLines());

        for (int i = 0; i < 11; i++) {
            page.line("Fill " + i);
        }

        assertEquals(14, page.lineCount());
        assertEquals(0, page.remainingLines());
        assertTrue(page.isFull());
    }

    @Test
    @DisplayName("BookPaginator splits lines across multiple pages within 14-line limit")
    void shouldPaginateLinesAcrossPages() {
        List<String> thirtyLines = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            thirtyLines.add("Line " + i);
        }

        List<BookPage> pages = BookPaginator.paginateLines(thirtyLines);
        // 30 lines / 14 max per page = 3 pages (14, 14, 2)
        assertEquals(3, pages.size());
    }

    @Test
    @DisplayName("BookPaginator wraps long continuous text at 114 pixels")
    void shouldWrapLongTextIntoPages() {
        String longText = "This is a very long paragraph that will definitely exceed the standard one hundred and fourteen pixel canvas width of a Minecraft written book page and must be wrapped into multiple lines cleanly.";
        List<BookPage> pages = BookPaginator.paginateText(longText);

        assertFalse(pages.isEmpty());
        assertTrue(pages.getFirst().build().length > 0);
    }

    @Test
    @DisplayName("BookPaginator preserves ampersand colors across wrapped lines")
    void shouldPreserveAmpersandColorsAcrossWrappedLines() {
        List<String> wrappedLines = BookPaginator.wrapText("&a" + "green ".repeat(30));

        assertTrue(wrappedLines.size() > 1);
        assertTrue(wrappedLines.stream().allMatch(line -> line.startsWith("§a")));
    }

    @Test
    @DisplayName("BookMenu builder sets metadata and builds pages")
    void shouldBuildBookModel() {
        BookMenu menu = BookMenu.builder()
                .title("Custom Title")
                .author("Moussa")
                .generation(BookMeta.Generation.ORIGINAL)
                .page(page -> page.title("PAGE 1").line("Hello"))
                .page(page -> page.title("PAGE 2").line("World"))
                .paginatedText("Extra long text paginated")
                .paginatedLines(List.of("Line A", "Line B"));

        assertEquals("Custom Title", menu.title());
        assertEquals("Moussa", menu.author());
        assertEquals(BookMeta.Generation.ORIGINAL, menu.generation());
        assertEquals(4, menu.pages().size());
    }

    @Test
    @DisplayName("BookPage automatically spills excess lines onto consecutive pages in buildPages")
    void shouldAutoSpillExcessLinesAcrossPages() {
        BookPage page = new BookPage();
        for (int i = 1; i <= 14; i++) {
            page.line("Line " + i);
        }
        page.actionButton("[Spilled Action]", "Click spilled", "spilled");

        assertEquals(15, page.lineCount());
        assertEquals(2, page.pageCount());

        List<BaseComponent[]> pages = page.buildPages();
        assertEquals(2, pages.size(), "15 lines should automatically spill into 2 pages");

        // First page should contain Line 1
        TextComponent line1 = findComponentWithText(pages.get(0), "Line 1");
        assertNotNull(line1);

        // Second page should contain [Spilled Action]
        TextComponent action = findComponentWithText(pages.get(1), "[Spilled Action]");
        assertNotNull(action, "Spilled action button should be rendered on the second page");
    }

    @Test
    @DisplayName("BookMenu automatically resolves logical page jumps when previous pages spill")
    void shouldResolveLogicalPageJumpsAcrossSpilledPages() {
        BookMenu menu = BookMenu.builder()
                .page(page -> {
                    // Fill 14 lines + 2 buttons = 16 lines -> spills across physical pages 1 and 2
                    for (int i = 1; i <= 14; i++) {
                        page.line("TOC line " + i);
                    }
                    page.actionPage("[Go to Chapter 2]", "Chapter 2", 2);
                    page.actionPage("[Go to Chapter 3]", "Chapter 3", 3);
                })
                .page(page -> page
                        .title("CHAPTER 2")
                        .line("Chapter 2 content")
                        .actionPage("[Back to TOC]", "Return to TOC", 1)
                )
                .page(page -> page
                        .title("CHAPTER 3")
                        .line("Chapter 3 content")
                );

        List<BaseComponent[]> physicalPages = menu.buildPhysicalPages();
        // Page 1 has 16 lines -> 2 physical pages (Physical 1 & 2)
        // Page 2 is 1 physical page (Physical 3)
        // Page 3 is 1 physical page (Physical 4)
        assertEquals(4, physicalPages.size());

        // Buttons on Page 1 are on Physical Page 2
        TextComponent ch2Btn = findComponentWithText(physicalPages.get(1), "[Go to Chapter 2]");
        assertNotNull(ch2Btn);
        assertNotNull(ch2Btn.getClickEvent());
        assertEquals(ClickEvent.Action.CHANGE_PAGE, ch2Btn.getClickEvent().getAction());
        assertEquals("3", ch2Btn.getClickEvent().getValue(), "Logical page 2 must resolve to physical page 3");

        TextComponent ch3Btn = findComponentWithText(physicalPages.get(1), "[Go to Chapter 3]");
        assertNotNull(ch3Btn);
        assertNotNull(ch3Btn.getClickEvent());
        assertEquals("4", ch3Btn.getClickEvent().getValue(), "Logical page 3 must resolve to physical page 4");

        // Back button on Chapter 2 (Physical Page 3)
        TextComponent backBtn = findComponentWithText(physicalPages.get(2), "[Back to TOC]");
        assertNotNull(backBtn);
        assertNotNull(backBtn.getClickEvent());
        assertEquals("1", backBtn.getClickEvent().getValue(), "Logical page 1 must resolve to physical page 1");
    }

    @Test
    @DisplayName("BookMenu resolves page links independently for each build")
    void shouldResolvePageLinksWithoutMutatingConfiguredComponents() {
        BookMenu menu = BookMenu.builder()
                .page(page -> {
                    for (int i = 1; i <= 14; i++) {
                        page.line("TOC line " + i);
                    }
                    page.actionPage("[Go to Chapter 2]", "Chapter 2", 2);
                })
                .page(page -> page.title("CHAPTER 2"))
                .page(page -> page.title("CHAPTER 3"));

        List<BaseComponent[]> firstBuild = menu.buildPhysicalPages();
        List<BaseComponent[]> secondBuild = menu.buildPhysicalPages();

        TextComponent firstLink = findComponentWithText(firstBuild.get(1), "[Go to Chapter 2]");
        TextComponent secondLink = findComponentWithText(secondBuild.get(1), "[Go to Chapter 2]");
        assertNotNull(firstLink);
        assertNotNull(secondLink);
        assertEquals("3", firstLink.getClickEvent().getValue());
        assertEquals("3", secondLink.getClickEvent().getValue());
    }

    @Test
    @DisplayName("BookMenu resolves named page jumps across spilled pages")
    void shouldResolveNamedPageJumpsAcrossSpilledPages() {
        BookMenu menu = BookMenu.builder()
                .page("toc", page -> {
                    for (int i = 1; i <= 14; i++) {
                        page.line("TOC line " + i);
                    }
                    page.actionPage("[Go Beasts]", "Read beasts", "beasts");
                    page.actionPage("[Go Limits]", "Read limits", "LIMITS");
                })
                .page("beasts", page -> page
                        .title("BEASTS")
                        .actionPage("[Back]", "Return", "toc")
                )
                .page("limits", page -> page
                        .title("LIMITS")
                        .actionPage("[Back]", "Return", "toc")
                );

        List<BaseComponent[]> physicalPages = menu.buildPhysicalPages();
        assertEquals(4, physicalPages.size());

        // On TOC (Physical Page 2)
        TextComponent beastsBtn = findComponentWithText(physicalPages.get(1), "[Go Beasts]");
        assertNotNull(beastsBtn);
        assertEquals("3", beastsBtn.getClickEvent().getValue(), "Named 'beasts' target should resolve to physical page 3");

        TextComponent limitsBtn = findComponentWithText(physicalPages.get(1), "[Go Limits]");
        assertNotNull(limitsBtn);
        assertEquals("4", limitsBtn.getClickEvent().getValue(), "Case-insensitive 'LIMITS' target should resolve to physical page 4");

        // On Beasts (Physical Page 3)
        TextComponent backBtn = findComponentWithText(physicalPages.get(2), "[Back]");
        assertNotNull(backBtn);
        assertEquals("1", backBtn.getClickEvent().getValue(), "Named 'toc' target should resolve to physical page 1");
    }

    private TextComponent findComponentWithText(BaseComponent[] components, String substring) {
        for (BaseComponent component : components) {
            if (component instanceof TextComponent textComponent) {
                if (textComponent.getText() != null && textComponent.getText().contains(substring)) {
                    return textComponent;
                }
            }
            if (component.getExtra() != null) {
                for (BaseComponent extra : component.getExtra()) {
                    if (extra instanceof TextComponent extraText && extraText.getText() != null && extraText.getText().contains(substring)) {
                        return extraText;
                    }
                }
            }
        }
        return null;
    }
}
