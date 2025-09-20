package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemFactory;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.abilities.CooldownEntry;
import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.core.players.data.PlayerDataHandler;
import fr.moussax.blightedMC.core.players.managers.ActionBarManager;
import fr.moussax.blightedMC.core.players.managers.FavorsManager;
import fr.moussax.blightedMC.core.players.managers.ManaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;

public class BlightedPlayer {
  private static final Map<UUID, BlightedPlayer> players = new HashMap<>();
  private static final double DEFAULT_MAX_MANA = 100.0;
  private static final double DEFAULT_MANA_REGEN_RATE = 1.0;

  private final Player player;
  private final UUID playerId;
  private final FavorsManager favors;
  private final PlayerDataHandler dataHandler;
  private final ActionBarManager actionBarManager;
  private final ManaManager manaManager;

  private final List<FullSetBonus> activeFullSetBonuses = new ArrayList<>();
  private final List<CooldownEntry> cooldowns = new ArrayList<>();
  private final EnumMap<ItemType, ItemFactory> armorPieces = new EnumMap<>(ItemType.class);

  private ItemStack[] lastKnownArmor = new ItemStack[4];
  private final BukkitTask actionBarTask;

  public BlightedPlayer(@Nonnull Player player) {
    this.player = player;
    this.playerId = player.getUniqueId();
    this.dataHandler = new PlayerDataHandler(playerId, player.getName());

    this.favors = new FavorsManager();
    this.favors.setFavors(dataHandler.getPlayerData().getFavors());

    this.manaManager = new ManaManager(DEFAULT_MAX_MANA, DEFAULT_MANA_REGEN_RATE);
    this.manaManager.setCurrentMana(dataHandler.getPlayerData().getMana());

    this.actionBarManager = new ActionBarManager(this);
    players.put(playerId, this);

    this.actionBarTask = Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(),
      actionBarManager::tick,
      0L,
      20L
    );

    initializeFullSetBonuses();
  }

  public static BlightedPlayer getBlightedPlayer(@Nonnull Player player) {
    return players.get(player.getUniqueId());
  }

  public static void removePlayer(@Nonnull Player player) {
    BlightedPlayer blightedPlayer = players.remove(player.getUniqueId());
    if (blightedPlayer != null) {
      blightedPlayer.cleanup();
    }
  }

  private void cleanup() {
    if (actionBarTask != null) {
      actionBarTask.cancel();
    }
    clearActiveBonuses();
    clearArmorPieces();
  }

  public List<CooldownEntry> getCooldowns() {
    return Collections.unmodifiableList(cooldowns);
  }

  public void addCooldown(@Nonnull CooldownEntry entry) {
    cooldowns.add(entry);
  }

  public void removeCooldown(@Nonnull CooldownEntry entry) {
    cooldowns.remove(entry);
  }

  public void initializeFullSetBonuses() {
    clearActiveBonuses();

    // Rebuild active bonuses based on equipped armor
    for (ItemFactory piece : armorPieces.values()) {
      if (piece == null) continue;

      List<FullSetBonus> bonuses = Collections.singletonList(piece.getFullSetBonus());

      for (FullSetBonus bonus : bonuses) {
        addActiveBonus(bonus);
      }
    }
  }

  public void clearArmorPieces() {
    armorPieces.clear();
  }

  public ItemFactory getEquippedItemManager() {
    ItemStack mainHandItem = player.getInventory().getItemInMainHand();
    return ItemFactory.fromItemStack(mainHandItem);
  }

  public List<FullSetBonus> getActiveFullSetBonuses() {
    return Collections.unmodifiableList(activeFullSetBonuses);
  }

  public void addArmorPiece(@Nonnull ItemType type, @Nonnull ItemFactory item) {
    setArmorPiece(type, item);
  }

  public void clearActiveBonuses() {
    for (FullSetBonus bonus : activeFullSetBonuses) {
      bonus.deactivate();
    }
    activeFullSetBonuses.clear();
  }

  public void addActiveBonus(@Nonnull FullSetBonus bonus) {
    activeFullSetBonuses.add(bonus);
    bonus.activate();
  }

  public void removeActiveBonus(@Nonnull FullSetBonus bonus) {
    if (activeFullSetBonuses.remove(bonus)) {
      bonus.deactivate();
    }
  }

  public void removeActiveBonusByClass(@Nonnull Class<? extends FullSetBonus> bonusClass) {
    activeFullSetBonuses.removeIf(bonus -> {
      if (bonusClass.isInstance(bonus)) {
        bonus.deactivate();
        return true;
      }
      return false;
    });
  }

  public void setArmorPiece(@Nonnull ItemType type, ItemFactory itemFactory) {
    armorPieces.put(type, itemFactory);
  }

  public ItemFactory getArmorPiece(@Nonnull ItemType type) {
    return armorPieces.get(type);
  }

  public Player getPlayer() {
    return player;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public FavorsManager getFavors() {
    return favors;
  }

  public void addFavors(int value) {
    if (value == 0) return;
    favors.addFavors(value);
    actionBarManager.tick();
  }

  public void removeFavors(int value) {
    if (value == 0) return;
    favors.removeFavors(value);
    actionBarManager.tick();
  }

  public void setFavors(int value) {
    favors.setFavors(value);
    actionBarManager.tick();
  }

  public ManaManager getMana() {
    return manaManager;
  }

  public void addItemToInventory(@Nonnull ItemStack item) {
    if (item.getType().isAir()) return;
    player.getInventory().addItem(item);
  }

  public void saveData() {
    dataHandler.setFavors(favors.getFavors());
    dataHandler.setMana(manaManager.getCurrentMana());
    dataHandler.save();
  }

  public ItemStack[] getLastKnownArmor() {
    return Arrays.copyOf(lastKnownArmor, lastKnownArmor.length);
  }

  public void setLastKnownArmor(@Nonnull ItemStack[] armor) {
    this.lastKnownArmor = Arrays.copyOf(armor, 4);
  }

  public ActionBarManager getActionBarManager() {
    return actionBarManager;
  }
}
