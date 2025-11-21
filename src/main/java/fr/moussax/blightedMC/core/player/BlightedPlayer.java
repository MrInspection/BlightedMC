package fr.moussax.blightedMC.core.player;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.abilities.CooldownEntry;
import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.core.managers.ActionBarManager;
import fr.moussax.blightedMC.core.managers.GemsManager;
import fr.moussax.blightedMC.core.managers.ManaManager;
import fr.moussax.blightedMC.server.database.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;

public class BlightedPlayer {
    private static BlightedMC instance = BlightedMC.getInstance();
    private static final Map<UUID, BlightedPlayer> players = new HashMap<>();
    private static final double DEFAULT_MAX_MANA = instance.getSettings().getDefaultMaxMana();
    private static final double DEFAULT_MANA_REGEN_RATE = instance.getSettings().getDefaultManaRegenerationRate();

    private final Player player;
    private final UUID playerId;
    private final GemsManager gemsManager;
    private final PlayerDataHandler dataHandler;
    private final ActionBarManager actionBarManager;
    private final ManaManager manaManager;

    private final List<FullSetBonus> activeFullSetBonuses = new ArrayList<>();
    private final List<CooldownEntry> cooldowns = new ArrayList<>();
    private final EnumMap<ItemType, ItemTemplate> armorPieces = new EnumMap<>(ItemType.class);

    private ItemStack[] lastKnownArmor = new ItemStack[4];
    private final BukkitTask actionBarTask;

    public BlightedPlayer(@Nonnull Player player) {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.dataHandler = new PlayerDataHandler(playerId, player.getName());

        this.gemsManager = new GemsManager();
        this.gemsManager.setGems(dataHandler.getGems());

        this.manaManager = new ManaManager(DEFAULT_MAX_MANA, DEFAULT_MANA_REGEN_RATE);
        this.manaManager.setCurrentMana(dataHandler.getMana());

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
        for (ItemTemplate piece : armorPieces.values()) {
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

    public ItemTemplate getEquippedItemManager() {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        return ItemTemplate.fromItemStack(mainHandItem);
    }

    public List<FullSetBonus> getActiveFullSetBonuses() {
        return Collections.unmodifiableList(activeFullSetBonuses);
    }

    public void addArmorPiece(@Nonnull ItemType type, @Nonnull ItemTemplate itemTemplate) {
        setArmorPiece(type, itemTemplate);
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

    public void setArmorPiece(@Nonnull ItemType type, ItemTemplate itemTemplate) {
        armorPieces.put(type, itemTemplate);
    }

    public ItemTemplate getArmorPiece(@Nonnull ItemType type) {
        return armorPieces.get(type);
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public GemsManager getGems() {
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

    public void addItemToInventory(@Nonnull ItemStack item) {
        if (item.getType().isAir()) return;
        player.getInventory().addItem(item);
    }

    public void saveData() {
        dataHandler.setGems(gemsManager.getGems());
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
