package fr.moussax.blightedMC.core.entities.LootTable.favors;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemGenerator;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class FavorsItem implements ItemGenerator {
  public final int amount;

  public FavorsItem(int amount) {
    this.amount = amount;
  }

  public FavorsItem(ItemStack itemStack) {
    ItemMeta meta = itemStack.getItemMeta();
    assert meta != null;
    Integer value = meta.getPersistentDataContainer()
      .get(new NamespacedKey(BlightedMC.getInstance(), "favorsValue"), PersistentDataType.INTEGER);
    this.amount = value != null ? value : 0;
  }

  public void addFavors(BlightedPlayer player) {
    player.addFavors(amount);
  }

  public static class BlightedGemstoneAbility implements AbilityManager<PlayerInteractEvent> {

    @Override
    public boolean triggerAbility(PlayerInteractEvent event) {
      if (event.getItem() == null) return false;
      BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(event.getPlayer());
      FavorsItem favorsItem = new FavorsItem(event.getItem());
      
      if (favorsItem.amount <= 0) {
        MessageUtils.warnSender(event.getPlayer(), "This gemstone doesn't have any favors to redeem.");
        return false;
      }
      
      favorsItem.addFavors(bPlayer);
      event.getPlayer().sendMessage("§8 ■ §7You received §6" + favorsItem.amount + "✵ Favors §7from a §5Blighted Gemstone.");
      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 100f, 0f);
      event.getPlayer().getInventory().remove(event.getItem());
      event.setCancelled(true);
      return true;
    }

    @Override
    public int getCooldownSeconds() {
      return 0;
    }

    @Override
    public int getManaCost() {
      return 0;
    }

    @Override
    public boolean canTrigger(BlightedPlayer player) {
      return true;
    }

    @Override
    public void start(BlightedPlayer player) {
    }

    @Override
    public void stop(BlightedPlayer player) {
    }
  }

  @Override
  public ItemStack createItemStack() {
    ItemManager item = ItemsRegistry.BLIGHTED_ITEMS.get("BLIGHTED_GEMSTONE");
    item.setLore("§7Favors trapped: §6" + amount + "✵", 6);
    ItemStack itemStack = item.toItemStack();
    
    ItemMeta meta = itemStack.getItemMeta();
    assert meta != null;
    meta.getPersistentDataContainer().set(new NamespacedKey(BlightedMC.getInstance(), "favorsValue"), PersistentDataType.INTEGER, amount);
    itemStack.setItemMeta(meta);
    
    return itemStack;
  }
}
