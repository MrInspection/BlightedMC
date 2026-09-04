package fr.moussax.blightedMod.moderator;

import fr.moussax.blightedMod.BlightedMod;
import fr.moussax.blightedMod.utils.PluginPermissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public final class BlightedModerator {

    private final Player player;
    private boolean isInModerationMode;
    private boolean isVanished;
    private Player targetPlayer;

    private ItemStack[] savedInventory;
    private ItemStack[] savedArmor;
    private ItemStack savedOffHand;
    private GameMode savedGameMode;
    private float savedExperience;
    private int savedLevel;
    private Collection<PotionEffect> savedPotionEffects;

    public BlightedModerator(Player player) {
        this.player = player;
        this.isInModerationMode = false;
        this.isVanished = false;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isInModerationMode() {
        return isInModerationMode;
    }

    public boolean isVanished() {
        return isVanished;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public void onEnable() {
        saveState();
        clearPlayerState();
        applyModerationState();
        setVanished(true, true);
        giveModerationTools();
        this.isInModerationMode = true;

        player.sendMessage(" §dModeration Mode §etoggled §aON§e.");
    }

    public void onDisable() {
        player.getInventory().clear();
        restoreState();
        setVanished(false, true);
        this.isInModerationMode = false;
        this.targetPlayer = null;

        player.sendMessage(" §dModeration Mode §etoggled §cOFF§e.");
    }

    private void saveState() {
        PlayerInventory inventory = player.getInventory();
        this.savedInventory = inventory.getContents();
        this.savedArmor = inventory.getArmorContents();
        this.savedOffHand = inventory.getItemInOffHand();
        this.savedGameMode = player.getGameMode();
        this.savedExperience = player.getExp();
        this.savedLevel = player.getLevel();
        this.savedPotionEffects = player.getActivePotionEffects();
    }

    private void restoreState() {
        PlayerInventory inventory = player.getInventory();
        inventory.setContents(savedInventory);
        inventory.setArmorContents(savedArmor);
        inventory.setItemInOffHand(savedOffHand);

        player.setGameMode(savedGameMode);
        player.setExp(savedExperience);
        player.setLevel(savedLevel);

        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.addPotionEffects(savedPotionEffects);

        player.setInvulnerable(false);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    private void clearPlayerState() {
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    private void applyModerationState() {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvulnerable(true);
    }

    public void giveModerationTools() {
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(0, ModerationTools.getRandomTeleporter());
        inventory.setItem(1, ModerationTools.getReportViewer());
        inventory.setItem(2, ModerationTools.getSanctionsInspector());
        inventory.setItem(3, ModerationTools.getKnockbackStick());
        inventory.setItem(4, ModerationTools.getModerationMenu());
        inventory.setItem(6, ModerationTools.getFreezer());
        inventory.setItem(7, ModerationTools.getInventoryInspector());
        inventory.setItem(8, ModerationTools.getVanishTool(isVanished));
    }

    public void setVanished(boolean vanished, boolean notifyPlayer) {
        this.isVanished = vanished;
        JavaPlugin instance = BlightedMod.getInstance();

        if (vanished) {
            hideFromNonModerators(instance);
            if (!notifyPlayer) player.sendMessage(" §dVanish §etoggled §aON§e.");
        } else {
            showToAllPlayers(instance);
            if (!notifyPlayer) player.sendMessage(" §dVanish §etoggled §cOFF§e.");
        }
        updateVanishTool();
    }

    public void setVanished(boolean vanished) {
        setVanished(vanished, false);
    }

    private void hideFromNonModerators(JavaPlugin instance) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.hasPermission(PluginPermissions.MODERATOR.toString())) {
                onlinePlayer.hidePlayer(instance, player);
            }
        });
    }

    private void showToAllPlayers(JavaPlugin instance) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.showPlayer(instance, player));
    }

    private void updateVanishTool() {
        ItemStack currentItem = player.getInventory().getItem(8);
        if (currentItem != null && currentItem.getType().name().contains("DYE")) {
            player.getInventory().setItem(8, ModerationTools.getVanishTool(isVanished));
        }
    }
}
