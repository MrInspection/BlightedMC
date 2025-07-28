package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BlightedPlayer {
  private static final HashMap<UUID, BlightedPlayer> players = new HashMap<>();

  private final Player player;
  private final UUID uuid;
  private final BlightedFavorsManager favors;
  private final PlayerDataHandler dataHandler;
  private final ActionBarManager actionBarManager;

  public BlightedPlayer(Player player) {
    this.player = player;
    this.uuid = player.getUniqueId();
    this.dataHandler = new PlayerDataHandler(uuid);

    double storedFavors = dataHandler.getConfig().getDouble("blightedFavors", 0.0);
    this.favors = new BlightedFavorsManager();
    this.favors.setFavors(storedFavors);

    this.actionBarManager = new ActionBarManager(this);
    players.put(uuid, this);

    Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(), actionBarManager::tick, 0L, 20L);
  }

  public static BlightedPlayer getBlightedPlayer(Player player) {
    return players.get(player.getUniqueId());
  }

  public static void remove(Player player) {
    players.remove(player.getUniqueId());
  }

  public Player getPlayer() {
    return player;
  }

  public UUID getUUID() {
    return uuid;
  }

  public BlightedFavorsManager getFavors() {
    return favors;
  }

  public BlightedFavorsManager addFavors(double value) {
    favors.addFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public void saveData() {
    dataHandler.getConfig().set("blightedFavors", favors.getFavors());
    dataHandler.save();
  }
}
