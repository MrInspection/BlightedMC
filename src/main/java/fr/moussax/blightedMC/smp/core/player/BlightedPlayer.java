package fr.moussax.blightedMC.smp.core.player;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityExecutor;
import fr.moussax.blightedMC.smp.core.items.abilities.ArmorManager;
import fr.moussax.blightedMC.smp.core.items.abilities.CooldownEntry;
import fr.moussax.blightedMC.smp.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.smp.core.managers.ActionBarManager;
import fr.moussax.blightedMC.smp.core.managers.GemsManager;
import fr.moussax.blightedMC.smp.core.managers.ManaManager;
import fr.moussax.blightedMC.server.database.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BlightedPlayer {
    private static final Map<UUID, BlightedPlayer> players = new HashMap<>();

    private static double DEFAULT_MAX_MANA = -1;
    private static double DEFAULT_MANA_REGEN_RATE = -1;

    private final Player player;
    private final UUID playerId;
    private final GemsManager gemsManager;
    private final PlayerDataHandler dataHandler;
    private final ActionBarManager actionBarManager;
    private final ManaManager manaManager;

    private final List<FullSetBonus> activeFullSetBonuses = new ArrayList<>();
    private final List<CooldownEntry> cooldowns = new ArrayList<>();
    private final EnumMap<ItemType, BlightedItem> armorPieces = new EnumMap<>(ItemType.class);

    private ItemStack[] lastKnownArmor = new ItemStack[4];
    private final BukkitTask actionBarTask;
    private int forgeFuel;

    public BlightedPlayer(Player player) {
        initSettings();

        this.player = player;
        this.playerId = player.getUniqueId();
        this.dataHandler = new PlayerDataHandler(playerId, player.getName());

        this.gemsManager = new GemsManager();
        this.gemsManager.setGems(dataHandler.getGems());

        this.manaManager = new ManaManager(DEFAULT_MAX_MANA, DEFAULT_MANA_REGEN_RATE);
        this.manaManager.setCurrentMana(dataHandler.getMana());
        this.forgeFuel = dataHandler.getForgeFuel();

        this.actionBarManager = new ActionBarManager(this);
        players.put(playerId, this);

        this.actionBarTask = Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(),
            actionBarManager::tick,
            0L,
            20L
        );

        ArmorManager.updatePlayerArmor(this);
    }

    private static void initSettings() {
        if (DEFAULT_MAX_MANA == -1) {
            BlightedMC instance = BlightedMC.getInstance();
            DEFAULT_MAX_MANA = instance.getSettings().getDefaultMaxMana();
            DEFAULT_MANA_REGEN_RATE = instance.getSettings().getDefaultManaRegenerationRate();
        }
    }

    public static BlightedPlayer getBlightedPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public static void removePlayer(Player player) {
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
        return cooldowns;
    }

    public void addCooldown(CooldownEntry entry) {
        cooldowns.add(entry);
    }

    public void removeCooldown(CooldownEntry entry) {
        cooldowns.remove(entry);
    }

    public void clearArmorPieces() {
        armorPieces.clear();
    }

    public BlightedItem getEquippedItemManager() {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        return BlightedItem.fromItemStack(mainHandItem);
    }

    public List<FullSetBonus> getActiveFullSetBonuses() {
        return Collections.unmodifiableList(activeFullSetBonuses);
    }

    public void addArmorPiece(ItemType type, BlightedItem blightedItem) {
        setArmorPiece(type, blightedItem);
    }

    public void clearActiveBonuses() {
        for (FullSetBonus bonus : activeFullSetBonuses) {
            bonus.deactivate();
        }
        activeFullSetBonuses.clear();
    }

    public void addActiveBonus(FullSetBonus bonus) {
        activeFullSetBonuses.add(bonus);
        bonus.activate();
    }

    public void removeActiveBonus(FullSetBonus bonus) {
        if (activeFullSetBonuses.remove(bonus)) {
            bonus.deactivate();
        }
    }

    public void removeActiveBonusByClass(Class<? extends FullSetBonus> bonusClass) {
        activeFullSetBonuses.removeIf(bonus -> {
            if (bonusClass.isInstance(bonus)) {
                bonus.deactivate();
                return true;
            }
            return false;
        });
    }

    public void setArmorPiece(ItemType type, BlightedItem blightedItem) {
        armorPieces.put(type, blightedItem);
    }

    public BlightedItem getArmorPiece(ItemType type) {
        return armorPieces.get(type);
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public GemsManager getGemsManager() {
        return gemsManager;
    }

    public void addGems(int value) {
        if (value == 0) return;
        gemsManager.addGems(value);
        actionBarManager.tick();
    }

    public void removeGems(int value) {
        if (value == 0) return;
        gemsManager.removeGems(value);
        actionBarManager.tick();
    }

    public void setGems(int value) {
        gemsManager.setGems(value);
        actionBarManager.tick();
    }

    public ManaManager getMana() {
        return manaManager;
    }

    public int getForgeFuel() {
        return forgeFuel;
    }

    public void addForgeFuel(int amount) {
        this.forgeFuel += amount;
    }

    public void removeForgeFuel(int amount) {
        this.forgeFuel = Math.max(0, this.forgeFuel - amount);
    }

    public void setForgeFuel(int amount) {
        this.forgeFuel = amount;
    }

    public void addItemToInventory(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        player.getInventory().addItem(item);
    }

    public void saveData() {
        dataHandler.setGems(gemsManager.getGems());
        dataHandler.setMana(manaManager.getCurrentMana());
        dataHandler.setForgeFuel(forgeFuel);
        dataHandler.save();
    }

    public ItemStack[] getLastKnownArmor() {
        return Arrays.copyOf(lastKnownArmor, lastKnownArmor.length);
    }

    public void setLastKnownArmor(ItemStack[] armor) {
        if (armor == null) {
            this.lastKnownArmor = new ItemStack[4];
            return;
        }
        this.lastKnownArmor = Arrays.copyOf(armor, 4);
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }
}
