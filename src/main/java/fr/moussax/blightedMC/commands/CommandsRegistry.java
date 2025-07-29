package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.commands.impl.admin.BroadcastCommand;
import fr.moussax.blightedMC.commands.impl.testing.SpawnMobCommand;
import fr.moussax.blightedMC.commands.impl.testing.TestCommand;
import fr.moussax.blightedMC.commands.impl.admin.FavorsCommand;

public final class CommandsRegistry {
  private CommandsRegistry() {}

  public static void registerAll() {
    CommandBuilder.register("broadcast", new BroadcastCommand());
    CommandBuilder.register("spawnmob", new SpawnMobCommand());
    CommandBuilder.register("test", new TestCommand());
    CommandBuilder.register("favors", FavorsCommand.class);
  }
}
