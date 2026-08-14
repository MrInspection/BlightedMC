package fr.moussax.blightedMC.engine.player;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.abilities.*;
import fr.moussax.blightedMC.server.database.PlayerDataHandler;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionBarManager;
import fr.moussax.blightedMC.engine.player.managers.GemsManager;
import fr.moussax.blightedMC.engine.player.managers.ManaManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Represents the BlightedMC-specific state and services associated with a
 * connected {@link Player}.
 *
 * <p>A {@code BlightedPlayer} acts as the server-side player context for
 * persistent resources, equipment, active set bonuses, ability cooldowns,
 * action bar updates, and forge fuel.</p>
 *
 * <p>Instances are tracked by the player's UUID and should be obtained through
 * {@link #getBlightedPlayer(Player)} when an existing player context is
 * required.</p>
 */
public final class BlightedPlayer {

    private static final Map<UUID, BlightedPlayer> players = new HashMap<>();

    private static final double DEFAULT_MAX_MANA = 100;
    private static final double DEFAULT_MANA_REGEN_RATE = 0.5;

    @Getter
    private final Player player;
    @Getter
    private final UUID playerId;
    @Getter
    private final GemsManager gemsManager;
    private final PlayerDataHandler dataHandler;
    @Getter
    private final ActionBarManager actionBarManager;
    private final ManaManager manaManager;

    private final List<FullSetBonus> activeFullSetBonuses = new ArrayList<>();
    private final List<CooldownEntry> cooldowns = new ArrayList<>();
    private final EnumMap<ItemType, BlightedItem> armorPieces = new EnumMap<>(ItemType.class);

    private ItemStack[] lastKnownArmor = new ItemStack[4];
    private final BukkitTask actionBarTask;
    @Getter
    @Setter
    private int forgeFuel;

    /**
     * Creates and registers a BlightedMC player context for the given player.
     *
     * <p>Persistent player data is loaded during construction, and the
     * player's action bar update task and armor state are initialized.</p>
     *
     * @param player the Bukkit player represented by this context
     */
    public BlightedPlayer(Player player) {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.dataHandler = new PlayerDataHandler(playerId, player.getName());
        this.gemsManager = new GemsManager(dataHandler.getGems());
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

    /**
     * Retrieves the BlightedMC player context associated with a Bukkit player.
     *
     * @param player the player whose context should be retrieved
     * @return the registered player context, or {@code null} if none exists
     */
    public static BlightedPlayer getBlightedPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Removes and cleans up the BlightedMC player context associated with a
     * player.
     *
     * <p>Active tasks and temporary equipment-related state are released before
     * the context is discarded.</p>
     *
     * @param player the player whose context should be removed
     */
    public static void removePlayer(Player player) {
        BlightedPlayer blightedPlayer = players.remove(player.getUniqueId());
        if (blightedPlayer != null) {
            blightedPlayer.cleanup();
        }
    }

    /**
     * Releases temporary resources and state held by this player context.
     */
    private void cleanup() {
        if (actionBarTask != null) {
            actionBarTask.cancel();
        }
        clearActiveBonuses();
        clearArmorPieces();
    }

    /**
     * Returns the cooldown entries currently associated with the player.
     *
     * @return an unmodifiable view of the player's cooldowns
     */
    public List<CooldownEntry> getCooldowns() {
        return Collections.unmodifiableList(cooldowns);
    }

    /**
     * Adds a cooldown entry to the player.
     *
     * @param entry the cooldown entry to add
     */
    public void addCooldown(CooldownEntry entry) {
        cooldowns.add(entry);
    }

    /**
     * Removes a cooldown entry from the player.
     *
     * @param entry the cooldown entry to remove
     */
    public void removeCooldown(CooldownEntry entry) {
        cooldowns.remove(entry);
    }

    /**
     * Sets or replaces the cooldown for a specific ability manager and ability
     * type.
     *
     * @param managerClass the ability manager associated with the cooldown
     * @param type         the ability type associated with the cooldown
     * @param seconds      the cooldown duration in seconds
     */
    public void setCooldown(Class<? extends AbilityManager> managerClass, AbilityType type, int seconds) {
        long expire = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.removeIf(currentCooldown ->
                currentCooldown.abilityManager().equals(managerClass) && currentCooldown.abilityType() == type);
        cooldowns.add(new CooldownEntry(managerClass, type, expire));
    }

    /**
     * Returns the remaining duration of a specific ability cooldown.
     *
     * <p>Expired cooldown entries are removed before the lookup is performed.</p>
     *
     * @param managerClass the ability manager associated with the cooldown
     * @param type         the ability type associated with the cooldown
     * @return the remaining cooldown in seconds, or {@code 0} if no active
     * cooldown exists
     */
    public double getRemainingCooldown(Class<? extends AbilityManager> managerClass, AbilityType type) {
        cooldowns.removeIf(CooldownEntry::isExpired);

        for (CooldownEntry entry : cooldowns) {
            if (entry.abilityManager().equals(managerClass) && entry.abilityType() == type) {
                return entry.getRemainingCooldownTimeInSeconds();
            }
        }
        return 0;
    }

    /**
     * Removes all tracked armor pieces from the player's current equipment state.
     */
    public void clearArmorPieces() {
        armorPieces.clear();
    }

    /**
     * Resolves the custom item currently held in the player's main hand.
     *
     * @return the corresponding {@link BlightedItem}, or {@code null} if the
     * held item is not a registered BlightedMC item
     */
    public BlightedItem getEquippedItemManager() {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        return BlightedItem.fromItemStack(mainHandItem);
    }

    /**
     * Returns the full-set bonuses currently active for the player.
     *
     * @return an unmodifiable view of the active full-set bonuses
     */
    public List<FullSetBonus> getActiveFullSetBonuses() {
        return Collections.unmodifiableList(activeFullSetBonuses);
    }

    /**
     * Registers an equipped armor piece for the specified item type.
     *
     * @param type         the armor item type
     * @param blightedItem the custom item occupying that armor slot
     */
    public void addArmorPiece(ItemType type, BlightedItem blightedItem) {
        setArmorPiece(type, blightedItem);
    }

    /**
     * Deactivates and removes all currently active full-set bonuses.
     */
    public void clearActiveBonuses() {
        for (FullSetBonus bonus : activeFullSetBonuses) {
            bonus.deactivate();
        }
        activeFullSetBonuses.clear();
    }

    /**
     * Activates and registers a full-set bonus for the player.
     *
     * @param bonus the full-set bonus to activate
     */
    public void addActiveBonus(FullSetBonus bonus) {
        activeFullSetBonuses.add(bonus);
        bonus.activate();
    }

    /**
     * Deactivates and removes a full-set bonus if it is currently active.
     *
     * @param bonus the full-set bonus to remove
     */
    public void removeActiveBonus(FullSetBonus bonus) {
        if (activeFullSetBonuses.remove(bonus)) {
            bonus.deactivate();
        }
    }

    /**
     * Checks whether the player currently has an active full-set bonus of the
     * specified class.
     *
     * @param bonusClass the full-set bonus class to search for
     * @return {@code true} if a matching bonus is active
     */
    public boolean hasFullSetBonus(Class<? extends FullSetBonus> bonusClass) {
        for (FullSetBonus bonus : activeFullSetBonuses) {
            if (bonusClass.isInstance(bonus)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deactivates and removes all active full-set bonuses matching the
     * specified class.
     *
     * @param bonusClass the full-set bonus class to remove
     */
    public void removeActiveBonusByClass(Class<? extends FullSetBonus> bonusClass) {
        activeFullSetBonuses.removeIf(bonus -> {
            if (bonusClass.isInstance(bonus)) {
                bonus.deactivate();
                return true;
            }
            return false;
        });
    }

    /**
     * Associates a custom armor item with an armor item type.
     *
     * @param type         the armor item type
     * @param blightedItem the custom armor item to associate
     */
    public void setArmorPiece(ItemType type, BlightedItem blightedItem) {
        armorPieces.put(type, blightedItem);
    }

    /**
     * Returns the custom armor item associated with an armor item type.
     *
     * @param type the armor item type to query
     * @return the associated custom item, or {@code null} if none is equipped
     */
    public BlightedItem getArmorPiece(ItemType type) {
        return armorPieces.get(type);
    }

    /**
     * Adds gems to the player's balance.
     *
     * <p>The action bar is refreshed when the balance changes.</p>
     *
     * @param value the number of gems to add
     */
    public void addGems(int value) {
        if (value == 0) return;
        gemsManager.addGems(value);
        actionBarManager.tick();
    }

    /**
     * Removes gems from the player's balance.
     *
     * <p>The action bar is refreshed when the balance changes.</p>
     *
     * @param value the number of gems to remove
     */
    public void removeGems(int value) {
        if (value == 0) return;
        gemsManager.removeGems(value);
        actionBarManager.tick();
    }

    /**
     * Sets the player's gem balance to the specified value.
     *
     * @param value the new gem balance; must not be negative
     * @throws IllegalArgumentException if {@code value} is negative
     */
    public void setGems(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Gems value cannot be negative");
        }
        int current = gemsManager.getGems();
        if (value > current) {
            addGems(value - current);
        } else if (value < current) {
            removeGems(current - value);
        }
    }

    /**
     * Returns the player's mana manager.
     *
     * @return the mana manager associated with this player
     */
    public ManaManager getMana() {
        return manaManager;
    }

    /**
     * Adds forge fuel to the player's stored fuel balance.
     *
     * @param amount the amount of forge fuel to add
     */
    public void addForgeFuel(int amount) {
        this.forgeFuel += amount;
    }

    /**
     * Removes forge fuel from the player's stored fuel balance.
     *
     * <p>The resulting balance cannot fall below zero.</p>
     *
     * @param amount the amount of forge fuel to remove
     */
    public void removeForgeFuel(int amount) {
        this.forgeFuel = Math.max(0, this.forgeFuel - amount);
    }

    /**
     * Adds an item to the player's inventory.
     *
     * <p>Air items and {@code null} values are ignored.</p>
     *
     * @param item the item to add
     */
    public void addItemToInventory(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        player.getInventory().addItem(item);
    }

    /**
     * Persists the player's current resources and forge fuel asynchronously.
     */
    public void saveData() {
        dataHandler.setGems(gemsManager.getGems());
        dataHandler.setMana(manaManager.getCurrentMana());
        dataHandler.setForgeFuel(forgeFuel);
        Bukkit.getScheduler().runTaskAsynchronously(BlightedMC.getInstance(), dataHandler::save);
    }

    /**
     * Returns a defensive copy of the player's last known armor state.
     *
     * @return a copy of the last known armor array
     */
    public ItemStack[] getLastKnownArmor() {
        return Arrays.copyOf(lastKnownArmor, lastKnownArmor.length);
    }

    /**
     * Updates the player's last known armor state.
     *
     * <p>The supplied array is copied to prevent external modification of the
     * stored state. If {@code armor} is {@code null}, the state is reset to an
     * empty four-slot armor array.</p>
     *
     * @param armor the armor state to store
     */
    public void setLastKnownArmor(ItemStack[] armor) {
        if (armor == null) {
            this.lastKnownArmor = new ItemStack[4];
            return;
        }
        this.lastKnownArmor = Arrays.copyOf(armor, 4);
    }
}
