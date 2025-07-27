package fr.moussax.blightedMC.managers;

import fr.moussax.blightedMC.commands.BroadcastCommand;
import fr.moussax.blightedMC.commands.SpawnMobCommand;
import fr.moussax.blightedMC.utils.CommandBuilder;

public class CommandsManager {
  public static void registerCommands() {
    CommandBuilder.createCommand("broadcast", new BroadcastCommand());
    CommandBuilder.createCommand("spawnmob", new SpawnMobCommand());
  }
}
