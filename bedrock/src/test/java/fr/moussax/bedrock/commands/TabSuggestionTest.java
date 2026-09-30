package fr.moussax.bedrock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TabSuggestionTest {

    @CommandArgument(position = 0, suggestions = {"start", "stop", "status"})
    @CommandArgument(position = 1, path = {"start|stop"}, suggestions = {"$targets"})
    @CommandArgument(position = 1, path = {"status"}, suggestions = {"verbose", "summary"})
    private static class SampleBranchingCommand {
    }

    private CommandSender createMockSender(String name, boolean hasPermission) {
        return (CommandSender) Proxy.newProxyInstance(
                CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class},
                (proxy, method, args) -> {
                    if ("hasPermission".equals(method.getName())) {
                        return hasPermission;
                    }
                    if ("getName".equals(method.getName())) {
                        return name;
                    }
                    return null;
                }
        );
    }

    private Command createMockCommand(String name) {
        return new Command(name) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };
    }

    @Test
    @DisplayName("Expects TabSuggestionRegistry to register and resolve dynamic suggestions case-insensitively")
    void testRegistryResolution() {
        TabSuggestionRegistry registry = new TabSuggestionRegistry();

        registry.register("$STATIC", () -> List.of("alpha", "beta"));
        registry.register("$sender", (sender) -> sender != null ? List.of(sender.getName()) : List.of());

        assertEquals(List.of("alpha", "beta"), registry.resolve("$static"));
        assertEquals(List.of("alpha", "beta"), registry.resolve("$STATIC"));

        CommandSender sender = createMockSender("Steve", true);
        assertEquals(List.of("Steve"), registry.resolve(sender, "$sender"));
        assertNull(registry.resolve("$unknown"));
    }

    @Test
    @DisplayName("Expects TabSuggestionBuilder to complete root argument matching prefix")
    void testRootCompletion() {
        TabSuggestionRegistry registry = new TabSuggestionRegistry();
        TabSuggestionBuilder builder = new TabSuggestionBuilder(SampleBranchingCommand.class, registry);

        CommandSender sender = createMockSender("Steve", true);
        Command command = createMockCommand("sample");

        List<String> results = builder.onTabComplete(sender, command, "sample", new String[]{"st"});
        assertEquals(List.of("start", "status", "stop"), results);

        List<String> specific = builder.onTabComplete(sender, command, "sample", new String[]{"sto"});
        assertEquals(List.of("stop"), specific);
    }

    @Test
    @DisplayName("Expects pipe-delimited branch path matching to complete targets for start and stop")
    void testBranchPathCompletion() {
        TabSuggestionRegistry registry = new TabSuggestionRegistry();
        registry.register("$targets", sender -> List.of("server1", "server2"));

        TabSuggestionBuilder builder = new TabSuggestionBuilder(SampleBranchingCommand.class, registry);
        CommandSender sender = createMockSender("Steve", true);
        Command command = createMockCommand("sample");

        // After "start"
        List<String> startResults = builder.onTabComplete(sender, command, "sample", new String[]{"start", "ser"});
        assertEquals(List.of("server1", "server2"), startResults);

        // After "stop"
        List<String> stopResults = builder.onTabComplete(sender, command, "sample", new String[]{"stop", "ser"});
        assertEquals(List.of("server1", "server2"), stopResults);

        // After "status" (different path)
        List<String> statusResults = builder.onTabComplete(sender, command, "sample", new String[]{"status", "v"});
        assertEquals(List.of("verbose"), statusResults);

        // Unknown subcommand does not match
        List<String> unknownResults = builder.onTabComplete(sender, command, "sample", new String[]{"unknown", ""});
        assertTrue(unknownResults.isEmpty());
    }

    @Test
    @DisplayName("Expects PlayerCommand to reject non-player senders with a warning message and return true")
    void testPlayerCommandNonPlayerRejection() {
        java.util.concurrent.atomic.AtomicReference<String> sentMessage = new java.util.concurrent.atomic.AtomicReference<>();
        CommandSender consoleSender = (CommandSender) Proxy.newProxyInstance(
                CommandSender.class.getClassLoader(),
                new Class<?>[]{CommandSender.class},
                (proxy, method, args) -> {
                    if ("sendMessage".equals(method.getName()) && args != null && args.length > 0) {
                        sentMessage.set(String.valueOf(args[0]));
                    }
                    return null;
                }
        );

        PlayerCommand command = new PlayerCommand() {
            @Override
            protected boolean execute(org.bukkit.entity.Player player, Command command, String label, String[] arguments) {
                return true;
            }
        };

        boolean handled = command.onCommand(consoleSender, createMockCommand("test"), "test", new String[0]);
        assertTrue(handled);
        assertNotNull(sentMessage.get());
        assertTrue(sentMessage.get().contains("in-game players"));
    }
}
