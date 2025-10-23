package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityListener implements Listener {

  public AbilityListener() {
    // Periodic armor refresh for all online players (every 2 seconds)
    Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(), () -> {
      for (Player player : Bukkit.getOnlinePlayers()) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer != null) ArmorManager.updatePlayerArmor(blightedPlayer);
      }
    }, 0L, 40L);
  }

  private void checkArmorChange(Player player) {
    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
    if (blightedPlayer == null) return;
    ItemStack[] before = blightedPlayer.getLastKnownArmor();
    ItemStack[] after = player.getInventory().getArmorContents();
    if (hasArmorChanged(before, after)) {
      ArmorManager.updatePlayerArmor(blightedPlayer);
      blightedPlayer.setLastKnownArmor(after);
    }
  }

  private boolean hasArmorChanged(ItemStack[] before, ItemStack[] after) {
    if (before == null || after == null) return true;
    if (before.length != after.length) return true;
    for (int i = 0; i < before.length; i++) {
      if (!isSameItem(before[i], after[i])) return true;
    }
    return false;
  }

  private boolean isSameItem(ItemStack a, ItemStack b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    return a.isSimilar(b) && a.getAmount() == b.getAmount();
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (e.getWhoClicked() instanceof Player player) {
      Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> checkArmorChange(player), 1L);
    }
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent e) {
    if (e.getWhoClicked() instanceof Player player) {
      Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> checkArmorChange(player), 1L);
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    if (e.getHand() == EquipmentSlot.OFF_HAND) return;
    trigger(e.getPlayer(), e);
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> checkArmorChange(e.getPlayer()), 1L);
  }

  @EventHandler
  public void onPlayerItemBreak(PlayerItemBreakEvent e) {
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> checkArmorChange(e.getPlayer()), 1L);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    checkArmorChange(e.getEntity());
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent e) {
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> checkArmorChange(e.getPlayer()), 1L);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> checkArmorChange(e.getPlayer()), 1L);
  }


  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager() instanceof Player p) trigger(p, e);
  }

  @EventHandler
  public void onArmorEquip(InventoryClickEvent e) {
    if (!(e.getWhoClicked() instanceof Player player)) return;
    new BukkitRunnable() {
      @Override
      public void run() {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer != null) ArmorManager.updatePlayerArmor(blightedPlayer);
      }
    }.runTaskLater(BlightedMC.getInstance(), 1);
  }

  private <T extends Event> void trigger(Player player, T event) {
    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
    if (blightedPlayer == null) {
      blightedPlayer = new BlightedPlayer(player);
    }

    ItemTemplate itemTemplate = blightedPlayer.getEquippedItemManager();
    if (itemTemplate == null || itemTemplate.getAbilities().isEmpty()) {
      return;
    }

    // Only trigger if the event matches the ability's trigger type
    for (Ability ability : itemTemplate.getAbilities()) {
      if (ability.type().matches(event)) {
        itemTemplate.triggerAbilities(blightedPlayer, event);
        break;
      }
    }
  }
}