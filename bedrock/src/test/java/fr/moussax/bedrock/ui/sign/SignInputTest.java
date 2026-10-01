package fr.moussax.bedrock.ui.sign;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SignInputTest {

    @Test
    @DisplayName("Expects builder to store explicitly configured sign lines")
    void testExplicitLines() {
        SignInput input = SignInput.builder()
                .lines("custom 1", "custom 2", "custom 3", "custom 4")
                .build();

        assertArrayEquals(new String[]{"custom 1", "custom 2", "custom 3", "custom 4"}, input.lines());
        assertEquals(0, input.inputLine());
    }

    @Test
    @DisplayName("Expects prompt shortcut to format caret pointer and instructions on lines 2 and 3")
    void testPromptShortcut() {
        SignInput input = SignInput.builder()
                .prompt("Enter your", "search query")
                .build();

        String[] lines = input.lines();
        assertEquals("", lines[0]);
        assertEquals("^^^^^^^^^^^^^^^", lines[1]);
        assertEquals("Enter your", lines[2]);
        assertEquals("search query", lines[3]);
        assertEquals(0, input.inputLine());
    }

    @Test
    @DisplayName("Expects onSubmit to be called with trimmed input when non-empty text is submitted")
    void testOnSubmitInvokedOnValidInput() {
        AtomicReference<String> submittedText = new AtomicReference<>();
        AtomicBoolean cancelled = new AtomicBoolean(false);

        SignInput input = SignInput.builder()
                .prompt("Search items")
                .onSubmit((_, text) -> submittedText.set(text))
                .onCancel(_ -> cancelled.set(true))
                .build();

        input.handleComplete(null, new String[]{"  Excalibur  ", "^^^^^^^^^^^^^^^", "Search items", ""});

        assertEquals("Excalibur", submittedText.get());
        assertFalse(cancelled.get());
    }

    @Test
    @DisplayName("Expects onCancel to be called when submitted input is blank or whitespace")
    void testOnCancelInvokedOnBlankInput() {
        AtomicReference<String> submittedText = new AtomicReference<>();
        AtomicBoolean cancelled = new AtomicBoolean(false);

        SignInput input = SignInput.builder()
                .prompt("Search items")
                .onSubmit((_, text) -> submittedText.set(text))
                .onCancel(_ -> cancelled.set(true))
                .build();

        input.handleComplete(null, new String[]{"     ", "^^^^^^^^^^^^^^^", "Search items", ""});

        assertNull(submittedText.get());
        assertTrue(cancelled.get());
    }

    @Test
    @DisplayName("Expects custom inputLine to extract input from specified line index")
    void testCustomInputLineExtraction() {
        AtomicReference<String> submittedText = new AtomicReference<>();

        SignInput input = SignInput.builder()
                .lines("Header", "Subtext", "", "Footer")
                .inputLine(2)
                .onSubmit((_, text) -> submittedText.set(text))
                .build();

        assertEquals(2, input.inputLine());

        input.handleComplete(null, new String[]{"Header", "Subtext", "My Custom Input", "Footer"});

        assertEquals("My Custom Input", submittedText.get());
    }

    @Test
    @DisplayName("Expects SignInputResult to return lines and bounds-safe values")
    void testSignInputResult() {
        SignInputResult result = new SignInputResult(null, new String[]{"First Line", "Second Line"});

        assertEquals("First Line", result.getFirstLine());
        assertEquals("First Line", result.getLine(0));
        assertEquals("Second Line", result.getLine(1));
        assertEquals("", result.getLine(2));
        assertEquals("", result.getLine(-1));
    }
}
