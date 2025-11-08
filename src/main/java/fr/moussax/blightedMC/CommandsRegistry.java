package fr.moussax.blightedMC;

import fr.moussax.blightedMC.commands.CraftCommand;
import fr.moussax.blightedMC.commands.admin.*;
import fr.moussax.blightedMC.commands.testing.TestCommand;
import fr.moussax.blightedMC.utils.commands.CommandBuilder;

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
    CommandBuilder.register("fly", new FlyCommand());
  }
}
