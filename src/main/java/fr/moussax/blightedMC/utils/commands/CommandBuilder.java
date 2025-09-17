package fr.moussax.blightedMC.utils.commands;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.CommandsRegistry;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.Objects;

/**
 * Utility class for registering and managing plugin commands for BlightedMC.
 * <p>
 * Provides multiple ways to register commands with executors and optional tab completers,
 * as well as reflection-based registration using command executor classes.
 */
public final class CommandBuilder {
  private static BlightedMC instance;

  private CommandBuilder() {}

  /**
   * Initializes the CommandBuilder with the current plugin instance.
   *
   * @param plugin the main BlightedMC plugin instance, cannot be null
   */
  public static void initialize(BlightedMC plugin) {
    instance = Objects.requireNonNull(plugin, "Plugin instance cannot be null.");
  }

  /**
   * Registers a command with a specified executor.
   *
   * @param command the command name as defined in plugin.yml
   * @param executor the command executor instance
   */
  public static void register(String command, CommandExecutor executor) {
    PluginCommand cmd = instance.getCommand(command);
    Objects.requireNonNull(cmd).setExecutor(executor);
  }

  /**
   * Registers a command with both a command executor and a tab completer.
   *
   * @param command the command name as defined in plugin.yml
   * @param executor the command executor instance
   * @param completer the tab completer instance
   */
  public static void register(String command, CommandExecutor executor, TabCompleter completer) {
    PluginCommand cmd = instance.getCommand(command);
    Objects.requireNonNull(cmd).setExecutor(executor);
    Objects.requireNonNull(cmd).setTabCompleter(completer);
  }

  /**
   * Registers a command using a command executor class instantiated via reflection.
   * <p>
   * If the class has {@link CommandArgument} or {@link CommandArguments} annotations,
   * a {@link CommandTabSuggestionBuilder} is automatically set as its tab completer.
   *
   * @param command the command name as defined in plugin.yml
   * @param commandClass the class implementing {@link CommandExecutor}
   * @throws IllegalStateException if the builder has not been initialized
   * @throws IllegalArgumentException if the command is not defined in plugin.yml
   * @throws RuntimeException if instantiation of the command executor fails
   */
  public static void register(String command, Class<? extends CommandExecutor> commandClass) {
    if (instance == null) {
      throw new IllegalStateException("CommandBuilder not initialized.");
    }

    PluginCommand cmd = instance.getCommand(command);
    if (cmd == null) {
      throw new IllegalArgumentException("Command \"" + command + "\" not defined in plugin.yml");
    }

    try {
      CommandExecutor executor = commandClass.getDeclaredConstructor().newInstance();
      cmd.setExecutor(executor);

      if (commandClass.isAnnotationPresent(CommandArgument.class)
          || commandClass.isAnnotationPresent(CommandArguments.class)) {
        cmd.setTabCompleter(new CommandTabSuggestionBuilder(commandClass));
      }
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Failed to instantiate command: " + commandClass.getSimpleName(), e);
    }
  }

  /**
   * Initializes all plugin commands by delegating to {@link CommandsRegistry#registerAll()}.
   *
   * @throws IllegalStateException if the builder has not been initialized
   */
  public static void initializeCommands() {
    if (instance == null) {
      throw new IllegalStateException("CommandBuilder not initialized. Call initialize() first.");
    }
    CommandsRegistry.registerAll();
  }
}

