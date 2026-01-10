package fr.moussax.blightedMC.smp.core.player.mod;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public final class BlightedModerator {
    private static final String MODERATOR_PERMISSION = "blightedmc.moderator";
    private static final String PREFIX = " §9§lMOD §f| §7";

    private final BlightedPlayer blightedPlayer;
    private boolean moderationMode;

    private ItemStack[] savedInventory;
    private ItemStack[] savedArmor;
    private ItemStack savedOffHand;
    private GameMode savedGameMode;
    private float savedExperience;
    private int savedLevel;
    private Collection<PotionEffect> savedEffects;
    private boolean vanished;

    public BlightedModerator(BlightedPlayer blightedPlayer) {
        this.blightedPlayer = blightedPlayer;
        this.moderationMode = false;
        this.vanished = false;
    }

    public void enable() {
        saveState();
        clearPlayerState();
        applyModerationState();
        setVanished(true, true);
        giveModerationTools();
        this.moderationMode = true;

        blightedPlayer.getPlayer().sendMessage(PREFIX + "You are now in §9moderation §7mode.");
    }

    public void disable() {
        blightedPlayer.getPlayer().getInventory().clear();
        blightedPlayer.getActionBarManager().clearModTarget();
        restoreState();
        setVanished(false, true);
        this.moderationMode = false;

        blightedPlayer.getPlayer().sendMessage(PREFIX + "You are no longer in §9moderation §7mode.");
    }

    public boolean isModerationMode() {
        return moderationMode;
    }

    private void saveState() {
        Player player = blightedPlayer.getPlayer();
        PlayerInventory inventory = player.getInventory();
        this.savedInventory = inventory.getContents();
        this.savedArmor = inventory.getArmorContents();
        this.savedOffHand = inventory.getItemInOffHand();
        this.savedGameMode = player.getGameMode();
        this.savedExperience = player.getExp();
        this.savedLevel = player.getLevel();
        this.savedEffects = player.getActivePotionEffects();
    }

    private void restoreState() {
        Player player = blightedPlayer.getPlayer();
        PlayerInventory inventory = player.getInventory();
        inventory.setContents(savedInventory);
        inventory.setArmorContents(savedArmor);
        inventory.setItemInOffHand(savedOffHand);

        player.setGameMode(savedGameMode);
        player.setExp(savedExperience);
        player.setLevel(savedLevel);

        player.getActivePotionEffects().forEach(
            effect -> player.removePotionEffect(effect.getType())
        );
        player.addPotionEffects(savedEffects);

        player.setInvulnerable(false);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    private void clearPlayerState() {
        Player player = blightedPlayer.getPlayer();
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.getActivePotionEffects().forEach(
            effect -> player.removePotionEffect(effect.getType())
        );
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    private void applyModerationState() {
        Player player = blightedPlayer.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvulnerable(true);
    }

    public void giveModerationTools() {
        Player player = blightedPlayer.getPlayer();
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(0, ModerationTools.getInventoryInspector());
        inventory.setItem(1, ModerationTools.getRandomTeleporter());
        inventory.setItem(4, ModerationTools.getKnockbackStick());
        inventory.setItem(7, ModerationTools.getFreezer());
        inventory.setItem(8, ModerationTools.getVanishTool(vanished));
    }

    public void setVanished(boolean vanished, boolean silent) {
        Player player = blightedPlayer.getPlayer();
        this.vanished = vanished;
        ModerationManager.getInstance().setVanished(player, vanished);
        BlightedMC pluginInstance = BlightedMC.getInstance();

        if (vanished) {
            hideFromNonModerators(pluginInstance);
            if (!silent) player.sendMessage(PREFIX + "You are now §avanished§7.");
        } else {
            showToAllPlayers(pluginInstance);
            if (!silent) player.sendMessage(PREFIX + "You are now §cvisible§7.");
        }
        updateVanishTool();
    }

    public void setVanished(boolean vanished) {
        setVanished(vanished, false);
    }

    private void hideFromNonModerators(BlightedMC pluginInstance) {
        Player player = blightedPlayer.getPlayer();
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.hasPermission(MODERATOR_PERMISSION)) {
                onlinePlayer.hidePlayer(pluginInstance, player);
            }
        });
    }

    private void showToAllPlayers(BlightedMC pluginInstance) {
        Player player = blightedPlayer.getPlayer();
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
            onlinePlayer.showPlayer(pluginInstance, player)
        );
    }

    private void updateVanishTool() {
        Player player = blightedPlayer.getPlayer();
        ItemStack currentItem = player.getInventory().getItem(8);
        if (currentItem != null && currentItem.getType().name().contains("DYE")) {
            player.getInventory().setItem(8, ModerationTools.getVanishTool(vanished));
        }
    }

    public boolean isVanished() {
        return vanished;
    }

    public Player getPlayer() {
        return blightedPlayer.getPlayer();
    }
}
