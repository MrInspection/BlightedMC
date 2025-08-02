package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityListener implements Listener {

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    if (e.getHand() == EquipmentSlot.OFF_HAND) return;
    Bukkit.getLogger().info("[DEBUG] PlayerInteractEvent triggered: " + e.getPlayer().getName() + " - Action: " + e.getAction());
    trigger(e.getPlayer(), e);
  }

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player p) trigger(p, e);
  }

  @EventHandler
  public void onArmorEquip(InventoryClickEvent e) {
    if (!(e.getWhoClicked() instanceof Player player)) return;
    PlayerInventory inv = player.getInventory();
    if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
      new BukkitRunnable() {
        @Override public void run() { ArmorManager.updatePlayerArmor(new BlightedPlayer(player)); }
      }.runTaskLater(BlightedMC.getInstance(), 1);
    }
  }

  private <T extends Event> void trigger(Player player, T event) {
    BlightedPlayer bp = BlightedPlayer.getBlightedPlayer(player);
    if (bp == null) {
      // If no BlightedPlayer exists, create one
      bp = new BlightedPlayer(player);
    }
    
    ItemManager itemManager = bp.getEquippedItemManager();
    if (itemManager == null) {
      Bukkit.getLogger().info("[DEBUG] No ItemManager found for player: " + player.getName());
      return;
    }

    Bukkit.getLogger().info("[DEBUG] Found ItemManager: " + itemManager.getItemId() + " with " + itemManager.getAbilities().size() + " abilities");
    itemManager.triggerAbilities(bp, event);
  }
}