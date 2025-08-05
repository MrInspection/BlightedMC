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

/**
 * Represents a custom block within the BlightedMC plugin.
 * Each {@link BlightedBlock} can handle placement, interaction, and breaking events.
 */
public abstract class BlightedBlock {
  protected final Material material;
  protected final ItemManager itemManager;

  /**
   * Creates a new custom block and registers it in the {@link BlocksRegistry}.
   *
   * @param material    the Bukkit {@link Material} used to render the block
   * @param itemManager the {@link ItemManager} for generating its item form
   */
  public BlightedBlock(@Nonnull Material material, ItemManager itemManager) {
    this.material = material;
    this.itemManager = itemManager;
    BlocksRegistry.addBlock(this);
  }

  /**
   * Called when this block is placed.
   *
   * @param event the {@link BlockPlaceEvent} triggered by the placement
   */
  public void onPlace(BlockPlaceEvent event) {}

  /**
   * Called when a player interacts with this block.
   *
   * @param event the {@link PlayerInteractEvent} triggered by interaction
   */
  public void onInteract(PlayerInteractEvent event) {}

  /**
   * Called when this block is broken.
   *
   * @param event       the {@link BlockBreakEvent} triggered by the break
   * @param droppedItem the item initially set to drop
   * @return the final item to drop; may be {@code null} to drop nothing
   */
  public ItemStack onBreak(BlockBreakEvent event, ItemStack droppedItem){
    return droppedItem;
  }

  /**
   * Listener handling block events for all {@link BlightedBlock} instances.
   * It links placed blocks to their custom implementations using metadata and persistent data.
   */
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
