package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.abilities.Bonuses;
import fr.moussax.blightedMC.core.items.abilities.CooldownEntry;
import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.core.players.managers.ActionBarManager;
import fr.moussax.blightedMC.core.players.managers.FavorsManager;
import fr.moussax.blightedMC.core.players.managers.ManaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Arrays;

/**
 * Represents a custom player wrapper managing player-specific data and mechanics.
 * <p>
 * Maintains per-player state such as favors, mana, equipped custom items,
 * cooldowns, full set bonuses, and handles persistence.
 * <p>
 * Provides static access to player instances via UUID mapping.
 */
public class BlightedPlayer {
  private static final HashMap<UUID, BlightedPlayer> players = new HashMap<>();
  private final Player player;
  private final UUID uuid;
  private final FavorsManager favors;
  private final PlayerDataHandler dataHandler;
  private final ActionBarManager actionBarManager;

  // Full Set Bonus System:
  public ItemManager helmet;
  public ItemManager chestplate;
  public ItemManager leggings;
  public ItemManager boots;
  public HashMap<Bonuses, Integer> bonuses = new HashMap<>();
  public ArrayList<FullSetBonus> activeFullSetBonuses = new ArrayList<>();

  private final ManaManager manaManager;
  private final ArrayList<CooldownEntry> cooldowns = new ArrayList<>();
  private ItemStack[] lastKnownArmor = new ItemStack[4];

  /**
   * Initializes a BlightedPlayer instance for the given Bukkit player.
   * <p>
   * Loads persisted favors, initializes mana and action bar management,
   * registers itself, and starts periodic action bar updates.
   *
   * @param player the Bukkit player to wrap
   */
  public BlightedPlayer(Player player) {
    this.player = player;
    this.uuid = player.getUniqueId();
    this.dataHandler = new PlayerDataHandler(uuid);

    int storedFavors = dataHandler.getConfig().getInt("blightedFavors", 0);
    this.favors = new FavorsManager();
    this.favors.setFavors(storedFavors);
    this.manaManager = new ManaManager(100.0, 0.5);
    this.actionBarManager = new ActionBarManager(this);

    players.put(uuid, this);

    initializeFullSetBonuses();

    Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(), actionBarManager::tick, 0L, 20L);
  }

  /**
   * Retrieves the BlightedPlayer instance associated with a Bukkit player.
   *
   * @param player the Bukkit player
   * @return the corresponding BlightedPlayer or null if not found
   */
  public static BlightedPlayer getBlightedPlayer(Player player) {
    return players.get(player.getUniqueId());
  }

  /**
   * Returns the list of active cooldown entries for abilities.
   *
   * @return the cooldown entries list
   */
  public ArrayList<CooldownEntry> getCooldowns() {
    return cooldowns;
  }

  /**
   * Adds a cooldown entry to this player.
   *
   * @param entry the cooldown entry to add
   */
  public void addCooldown(CooldownEntry entry) {
    cooldowns.add(entry);
  }

  /**
   * Removes a cooldown entry from this player.
   *
   * @param entry the cooldown entry to remove
   */
  public void removeCooldown(CooldownEntry entry) {
    cooldowns.remove(entry);
  }

  /**
   * Restarts all active full set bonuses, stopping then starting each.
   */
  public void initializeFullSetBonuses() {
    for(FullSetBonus bonus : activeFullSetBonuses) {
      bonus.stopAbility();
    }

    for(FullSetBonus bonus : activeFullSetBonuses) {
      bonus.startAbility();
    }
  }

  public void clearArmorPieces() {
    helmet = null;
    chestplate = null;
    leggings = null;
    boots = null;
  }

  public ItemManager getEquippedItemManager() {
    ItemStack mainHandItem = player.getInventory().getItemInMainHand();
    return ItemManager.fromItemStack(mainHandItem);
  }

  /**
   * Returns the list of currently active full set bonuses.
   *
   * @return active full set bonuses list
   */
  public ArrayList<FullSetBonus> getActiveFullSetBonuses() {
    return activeFullSetBonuses;
  }

  /**
   * Adds a custom armor piece to the player's equipped set.
   *
   * @param type the armor piece type
   * @param item the corresponding ItemManager instance
   */
  public void addArmorPiece(ItemType type, ItemManager item) {
    setArmorPiece(type, item);
  }

  public void clearActiveBonuses() {
    for (FullSetBonus bonus : activeFullSetBonuses) {
      bonus.stopAbility();
    }
    activeFullSetBonuses.clear();
  }

  /**
   * Activates and adds a full set bonus to the player's active list.
   *
   * @param bonus the full set bonus to activate
   */
  public void addActiveBonus(FullSetBonus bonus) {
    activeFullSetBonuses.add(bonus);
    bonus.startAbility();
  }

  /**
   * Deactivates and removes a full set bonus from the player's active list.
   *
   * @param bonus the full set bonus to deactivate
   */
  public void removeActiveBonus(FullSetBonus bonus) {
    activeFullSetBonuses.remove(bonus);
    bonus.stopAbility();
  }

  /**
   * Removes and deactivates full set bonuses matching the given class.
   *
   * @param bonusClass the class type of the bonus to remove
   */
  public void removeActiveBonusByClass(Class<? extends FullSetBonus> bonusClass) {
    activeFullSetBonuses.removeIf(bonus -> {
      if (bonus.getClass().equals(bonusClass)) {
        bonus.stopAbility();
        return true;
      }
      return false;
    });
  }

  /**
   * Sets a specific equipped armor piece.
   *
   * @param type the armor piece type
   * @param itemManager the associated ItemManager
   */
  public void setArmorPiece(ItemType type, ItemManager itemManager) {
    switch(type) {
      case HELMET:
        helmet = itemManager;
        break;
      case CHESTPLATE:
        chestplate = itemManager;
        break;
      case LEGGINGS:
        leggings = itemManager;
        break;
      case BOOTS:
        boots = itemManager;
        break;
      default:
        break;
    }
  }

  /**
   * Removes the BlightedPlayer instance for a given Bukkit player.
   *
   * @param player the Bukkit player to remove
   */
  public static void remove(Player player) {
    players.remove(player.getUniqueId());
  }

  /**
   * Returns the underlying Bukkit player.
   *
   * @return the Bukkit player instance
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the player's UUID.
   *
   * @return the UUID of the player
   */
  public UUID getUUID() {
    return uuid;
  }

  /**
   * Returns the FavorsManager handling player's favors.
   *
   * @return the favors manager instance
   */
  public FavorsManager getFavors() {
    return favors;
  }

  /**
   * Adds favors to the player and updates the action bar.
   *
   * @param value the amount to add
   * @return the updated favors manager
   */
  public FavorsManager addFavors(int value) {
    favors.addFavors(value);
    actionBarManager.tick();
    return favors;
  }

  /**
   * Removes favors from the player and updates the action bar.
   *
   * @param value the amount to remove
   * @return the updated favors manager
   */
  public FavorsManager removeFavors(int value) {
    favors.removeFavors(value);
    actionBarManager.tick();
    return favors;
  }

  /**
   * Sets the player's favors to a specific value and updates the action bar.
   *
   * @param value the new favors value
   * @return the updated favors manager
   */
  public FavorsManager setFavors(int value) {
    favors.setFavors(value);
    actionBarManager.tick();
    return favors;
  }

  /**
   * Returns the player's mana manager.
   *
   * @return the mana manager instance
   */
  public ManaManager getMana() {
    return manaManager;
  }

  /**
   * Adds an item to the player's inventory, ignoring air items.
   *
   * @param item the ItemStack to add
   */
  public void addItemToInventory(@Nonnull ItemStack item) {
    if (item.getType().isAir()) return;
    player.getInventory().addItem(item);
  }

  /**
   * Saves persistent player data such as favors to disk.
   */
  public void saveData() {
    dataHandler.getConfig().set("blightedFavors", favors.getFavors());
    dataHandler.save();
  }

  /**
   * Returns the last known armor ItemStacks equipped by the player.
   *
   * @return array of ItemStacks representing armor
   */
  public ItemStack[] getLastKnownArmor() {
    return lastKnownArmor;
  }

  /**
   * Updates the last known armor pieces equipped by the player.
   * <p>
   * Copies the input array defensively.
   *
   * @param armor the new armor ItemStack array, or null to clear
   */
  public void setLastKnownArmor(ItemStack[] armor) {
    if (armor == null) armor = new ItemStack[4];
    this.lastKnownArmor = Arrays.copyOf(armor, 4);
  }

  /**
   * Returns the action bar manager responsible for updating the player's action bar.
   *
   * @return the action bar manager
   */
  public ActionBarManager getActionBarManager() {
    return actionBarManager;
  }
}
