package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.Objects;

public final class CommandBuilder {
  private static BlightedMC instance;

  private CommandBuilder() {}

  public static void initialize(BlightedMC plugin) {
    instance = Objects.requireNonNull(plugin, "Plugin instance cannot be null.");
  }

  public static void register(String command, CommandExecutor executor) {
    PluginCommand cmd = instance.getCommand(command);
    Objects.requireNonNull(cmd).setExecutor(executor);
  }

  public static void register(String command, CommandExecutor executor, TabCompleter completer) {
    PluginCommand cmd = instance.getCommand(command);
    Objects.requireNonNull(cmd).setExecutor(executor);
    Objects.requireNonNull(cmd).setTabCompleter(completer);
  }

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

  public static void initializeCommands() {
    if (instance == null) {
      throw new IllegalStateException("CommandBuilder not initialized. Call initialize() first.");
    }
    CommandsRegistry.registerAll();
  }
}

