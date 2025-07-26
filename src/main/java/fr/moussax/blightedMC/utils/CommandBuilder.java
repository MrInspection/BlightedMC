package fr.moussax.blightedMC.utils;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.managers.CommandsManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class CommandBuilder {
  private static BlightedMC instance;

  public static void initialize(BlightedMC plugin) {
    instance = plugin;
  }

  public static void createCommand(@Nonnull String command, @Nonnull CommandExecutor executor) {
    PluginCommand cmd = instance.getCommand(command);
    Objects.requireNonNull(cmd).setExecutor(executor);
  }

  public static void initializeCommands() {
    if (instance == null) {
      throw new IllegalStateException("CommandBuilder not initialized. Call initialize() first.");
    }
    CommandsManager.registerCommands();
  }
}
