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

  public static BlightedPlayer getBlightedPlayer(Player player) {
    return players.get(player.getUniqueId());
  }

  public ArrayList<CooldownEntry> getCooldowns() {
    return cooldowns;
  }

  public void addCooldown(CooldownEntry entry) {
    cooldowns.add(entry);
  }

  public void removeCooldown(CooldownEntry entry) {
    cooldowns.remove(entry);
  }

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

  public ArrayList<FullSetBonus> getActiveFullSetBonuses() {
    return activeFullSetBonuses;
  }

  public void addArmorPiece(ItemType type, ItemManager item) {
    setArmorPiece(type, item);
  }

  public void clearActiveBonuses() {
    for (FullSetBonus bonus : activeFullSetBonuses) {
      bonus.stopAbility();
    }
    activeFullSetBonuses.clear();
  }

  public void addActiveBonus(FullSetBonus bonus) {
    activeFullSetBonuses.add(bonus);
    bonus.startAbility();
  }

  public void removeActiveBonus(FullSetBonus bonus) {
    activeFullSetBonuses.remove(bonus);
    bonus.stopAbility();
  }

  public void removeActiveBonusByClass(Class<? extends FullSetBonus> bonusClass) {
    activeFullSetBonuses.removeIf(bonus -> {
      if (bonus.getClass().equals(bonusClass)) {
        bonus.stopAbility();
        return true;
      }
      return false;
    });
  }

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

  public static void remove(Player player) {
    players.remove(player.getUniqueId());
  }

  public Player getPlayer() {
    return player;
  }

  public UUID getUUID() {
    return uuid;
  }

  public FavorsManager getFavors() {
    return favors;
  }

  public FavorsManager addFavors(int value) {
    favors.addFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public FavorsManager removeFavors(int value) {
    favors.removeFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public FavorsManager setFavors(int value) {
    favors.setFavors(value);
    actionBarManager.tick();
    return favors;
  }

  public ManaManager getMana() {
    return manaManager;
  }

  public void addItemToInventory(@Nonnull ItemStack item) {
    if (item.getType().isAir()) return;
    player.getInventory().addItem(item);
  }

  public void saveData() {
    dataHandler.getConfig().set("blightedFavors", favors.getFavors());
    dataHandler.save();
  }

  public ItemStack[] getLastKnownArmor() {
    return lastKnownArmor;
  }

  public void setLastKnownArmor(ItemStack[] armor) {
    if (armor == null) armor = new ItemStack[4];
    this.lastKnownArmor = Arrays.copyOf(armor, 4);
  }

  public ActionBarManager getActionBarManager() {
    return actionBarManager;
  }
}
