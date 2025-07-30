package fr.moussax.blightedMC.core.items.blocks;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

public abstract class BlightedBlock {
  protected final Material material;
  protected final ItemManager itemManager;

  public BlightedBlock(@Nonnull Material material, ItemManager itemManager) {
    this.material = material;
    this.itemManager = itemManager;
    BlocksRegistry.addBlock(this);
  }

  public void onPlace(BlockPlaceEvent event) {}

  public void onInteract(PlayerInteractEvent event) {}

  public ItemStack onBreak(BlockBreakEvent event, ItemStack droppedItem){
    return droppedItem;
  }

  public static class BlightedBlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
      ItemStack item = event.getItemInHand();
      if (!item.hasItemMeta()) return;

      var meta = item.getItemMeta();
      if (meta == null) return;

      if (!meta.getPersistentDataContainer().has(new NamespacedKey(BlightedMC.getInstance(), "id"), PersistentDataType.STRING)) return;

      String id = meta.getPersistentDataContainer().get(new NamespacedKey(BlightedMC.getInstance(), "id"), PersistentDataType.STRING);
      if (id == null) return;

      BlightedBlock block = BlocksRegistry.CUSTOM_BLOCKS.get(id);
      if (block == null) return;

      event.getBlockPlaced().setType(block.material);
      event.getBlockPlaced().setMetadata("id", new FixedMetadataValue(BlightedMC.getInstance(), id));

      block.onPlace(event);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
      if (event.getClickedBlock() == null) return;

      var block = event.getClickedBlock();
      if (!block.hasMetadata("id")) return;

      String id = block.getMetadata("id").getFirst().asString();
      if (id == null) return;

      BlightedBlock customBlock = BlocksRegistry.CUSTOM_BLOCKS.get(id);
      if (customBlock == null) return;

      customBlock.onInteract(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
      var block = event.getBlock();
      if (!block.hasMetadata("id")) return;

      String id = block.getMetadata("id").getFirst().asString();
      if (id == null) return;

      BlightedBlock customBlock = BlocksRegistry.CUSTOM_BLOCKS.get(id);
      if (customBlock == null) return;

      event.setDropItems(false);

      ItemStack drop = customBlock.onBreak(event, customBlock.itemManager.toItemStack());
      if (drop != null) {
        block.getWorld().dropItemNaturally(block.getLocation(), drop);
      }
    }
  }
}
