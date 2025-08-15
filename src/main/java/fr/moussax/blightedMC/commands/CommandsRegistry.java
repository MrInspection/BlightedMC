package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.commands.impl.CraftCommand;
import fr.moussax.blightedMC.commands.impl.admin.*;
import fr.moussax.blightedMC.commands.impl.testing.LaserCommand;
import fr.moussax.blightedMC.commands.impl.testing.TestCommand;

/**
 * Registry class responsible for registering all commands used in the plugin.
 * Commands are registered via {@link CommandBuilder}.
 * This class cannot be instantiated.
 */
public final class CommandsRegistry {
  private CommandsRegistry() {}

  public static void registerAll() {
    CommandBuilder.register("craft", new CraftCommand());
    CommandBuilder.register("test", new TestCommand());
    CommandBuilder.register("laser", new LaserCommand());

    // Admin Commands
    CommandBuilder.register("broadcast", new BroadcastCommand());
    CommandBuilder.register("spawncustommob", SpawnCustomMobCommand.class);
    CommandBuilder.register("favors", FavorsCommand.class);
    CommandBuilder.register("forcecommand", new ForceCommand());
    CommandBuilder.register("giveitem", GiveItemCommand.class);
    CommandBuilder.register("god", new GodCommand());
    CommandBuilder.register("kaboom", new KaboomCommand());
    CommandBuilder.register("loop", new LoopCommand());
    CommandBuilder.register("tp", new TeleportCommands());
    CommandBuilder.register("tphere", new TeleportCommands());
    CommandBuilder.register("tpall", new TeleportCommands());
    CommandBuilder.register("tppos", new TeleportCommands());
  }
}
