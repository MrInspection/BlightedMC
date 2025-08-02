package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.commands.impl.CraftCommand;
import fr.moussax.blightedMC.commands.impl.admin.*;
import fr.moussax.blightedMC.commands.impl.testing.LaserCommand;
import fr.moussax.blightedMC.commands.impl.testing.SpawnMobCommand;
import fr.moussax.blightedMC.commands.impl.testing.TestCommand;

public final class CommandsRegistry {
  private CommandsRegistry() {}

  public static void registerAll() {
    CommandBuilder.register("broadcast", new BroadcastCommand());
    CommandBuilder.register("spawnmob", new SpawnMobCommand());
    CommandBuilder.register("craft", new CraftCommand());
    CommandBuilder.register("test", new TestCommand());
    CommandBuilder.register("laser", new LaserCommand());
    CommandBuilder.register("favors", FavorsCommand.class);
    CommandBuilder.register("loop", new LoopCommand());
    CommandBuilder.register("kaboom", new KaboomCommand());
    CommandBuilder.register("testmenu", new TestCommand());
    CommandBuilder.register("refreshplugin", new ReloadCommand());
  }
}
