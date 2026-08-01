package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.commands.utils.CommandArgument;
import fr.moussax.blightedMC.commands.utils.TabSuggestionBuilder;
import fr.moussax.blightedMC.commands.utils.TabSuggestionRegistry;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Registers command executors and tab completers declared in {@code plugin.yml}.
 */
public final class CommandRegistrar {

    private final JavaPlugin plugin;
    private final TabSuggestionRegistry suggestionRegistry;

    public CommandRegistrar(JavaPlugin plugin, TabSuggestionRegistry suggestionRegistry) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.suggestionRegistry = Objects.requireNonNull(suggestionRegistry, "suggestionRegistry");
    }

    /**
     * Registers an executor and automatically installs annotation-driven tab
     * completion when its class declares {@link CommandArgument} entries.
     *
     * @param commandName command name declared in {@code plugin.yml}
     * @param executor    executor handling the command
     * @throws IllegalArgumentException when the command is not declared
     */
    public void register(String commandName, CommandExecutor executor) {
        Objects.requireNonNull(executor, "executor");

        PluginCommand command = requireCommand(commandName);
        command.setExecutor(executor);

        Class<?> executorType = executor.getClass();

        if (executorType.getAnnotationsByType(CommandArgument.class).length > 0) {
            command.setTabCompleter(new TabSuggestionBuilder(executorType, suggestionRegistry)
            );
        }
    }

    /**
     * Registers an executor with an explicit tab completer.
     *
     * @param commandName command name declared in {@code plugin.yml}
     * @param executor    executor handling the command
     * @param completer   completer providing argument suggestions
     * @throws IllegalArgumentException when the command is not declared
     */
    public void register(
            String commandName,
            CommandExecutor executor,
            TabCompleter completer
    ) {
        Objects.requireNonNull(executor, "executor");
        Objects.requireNonNull(completer, "completer");

        PluginCommand command = requireCommand(commandName);
        command.setExecutor(executor);
        command.setTabCompleter(completer);
    }

    private PluginCommand requireCommand(String commandName) {
        Objects.requireNonNull(commandName, "commandName");

        PluginCommand command = plugin.getCommand(commandName);

        if (command == null) {
            throw new IllegalArgumentException(
                    "Command '%s' is not declared in plugin.yml"
                            .formatted(commandName)
            );
        }

        return command;
    }
}
