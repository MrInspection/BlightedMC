package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.managers.BlightedFavorsManager;
import fr.moussax.blightedMC.core.players.managers.BlightedManaManager;
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

  private final BlightedManaManager manaManager;

  public BlightedPlayer(Player player) {
    this.player = player;
    this.uuid = player.getUniqueId();
    this.dataHandler = new PlayerDataHandler(uuid);

    int storedFavors = dataHandler.getConfig().getInt("blightedFavors", 0);
    this.favors = new BlightedFavorsManager();
    this.favors.setFavors(storedFavors);

    this.manaManager = new BlightedManaManager(100.0, 1.0);

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

  public BlightedFavorsManager addFavors(int value) {
    favors.addFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public BlightedFavorsManager removeFavors(int value) {
    favors.removeFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public BlightedFavorsManager setFavors(int value) {
    favors.setFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public BlightedManaManager getMana() {
    return manaManager;
  }

  public void saveData() {
    dataHandler.getConfig().set("blightedFavors", favors.getFavors());
    dataHandler.save();
  }
}
