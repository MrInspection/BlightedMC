package fr.moussax.blightedMC.core.items.blocks;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a custom block within the BlightedMC plugin.
 */
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

  public ItemStack onBreak(BlockBreakEvent event, ItemStack droppedItem) {
    return droppedItem;
  }

  /**
   * Handles all BlightedBlock events and persistence.
   */
  public static class BlightedBlockListener implements Listener {
    private final BlightedMC plugin = BlightedMC.getInstance();
    private final NamespacedKey key = new NamespacedKey(plugin, "id");

    // In-memory cache
    private final Map<String, String> placedBlocks = new HashMap<>();

    // Data file
    private final File dataFile;
    private final YamlConfiguration dataConfig;

    public BlightedBlockListener() {
      dataFile = new File(plugin.getDataFolder(), "blighted_blocks.yml");
      dataConfig = YamlConfiguration.loadConfiguration(dataFile);
      loadData();
    }

    /** Reads custom block ID using metadata or PDC */
    private String getBlockId(Block block) {
      // 1) Metadata fast path
      if (block.hasMetadata("id")) {
        for (MetadataValue mv : block.getMetadata("id")) {
          if (mv.getOwningPlugin() == plugin) {
            return mv.asString();
          }
        }
      }

      // 2) TileState Persistent fallback
      BlockState state = block.getState();
      if (!(state instanceof TileState tile)) return null;

      PersistentDataContainer pdc = tile.getPersistentDataContainer();
      if (pdc.has(key, PersistentDataType.STRING)) {
        String id = pdc.get(key, PersistentDataType.STRING);
        block.setMetadata("id", new FixedMetadataValue(plugin, id)); // cache
        return id;
      }

      return null;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
      ItemStack item = event.getItemInHand();
      if (!item.hasItemMeta()) return;

      var meta = item.getItemMeta();
      if (meta == null) return;

      if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) return;
      String id = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
      if (id == null) return;

      BlightedBlock block = BlocksRegistry.CUSTOM_BLOCKS.get(id);
      if (block == null) return;

      Block placed = event.getBlockPlaced();
      placed.setType(block.material);

      // Store in TileState PDC if supported
      BlockState state = placed.getState();
      if (state instanceof TileState tile) {
        tile.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
        tile.update(true);
      }

      // Cache metadata
      placed.setMetadata("id", new FixedMetadataValue(plugin, id));

      // Track and persist immediately
      String locKey = serializeLocation(placed.getLocation());
      placedBlocks.put(locKey, id);
      dataConfig.set(locKey, id);
      saveData(); // persist immediately

      block.onPlace(event);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
      Block block = event.getClickedBlock();
      if (block == null) return;

      String id = getBlockId(block);
      if (id == null) return;

      BlightedBlock customBlock = BlocksRegistry.CUSTOM_BLOCKS.get(id);
      if (customBlock == null) return;

      customBlock.onInteract(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      Location loc = block.getLocation();
      String id = getBlockId(block);
      if (id == null) return;

      BlightedBlock customBlock = BlocksRegistry.CUSTOM_BLOCKS.get(id);
      if (customBlock == null) return;

      event.setDropItems(false);

      // Preventing player in CREATIVE to drop the item when hitting the block with a sword
      Bukkit.getScheduler().runTask(plugin, () -> {
        if (!loc.getBlock().getType().isAir()) return; // Block wasn't destroyed → skip

        ItemStack drop = customBlock.onBreak(event, customBlock.itemManager.toItemStack());
        if (drop != null) {
          Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, drop);
        }

        String locKey = serializeLocation(loc);
        placedBlocks.remove(locKey);
        dataConfig.set(locKey, null);
        saveData();

        BlockState state = block.getState();
        if (state instanceof TileState tile) {
          tile.getPersistentDataContainer().remove(key);
          tile.update(true);
        }
      });
    }

    /** Save all placed custom blocks to disk */
    public void saveData() {
      try {
        dataConfig.save(dataFile);
      } catch (IOException e) {
        plugin.getLogger().severe("Failed to save blighted block data: " + e.getMessage());
      }
    }

    /** Load all blocks from disk and restore metadata */
    private void loadData() {
      if (!dataFile.exists()) return;

      for (String locKey : dataConfig.getKeys(false)) {
        String id = dataConfig.getString(locKey);
        if (id == null) continue;

        Location loc = deserializeLocation(locKey);
        if (loc == null) continue;

        Block block = loc.getBlock();
        block.setMetadata("id", new FixedMetadataValue(plugin, id));
        placedBlocks.put(locKey, id);
      }

      plugin.getLogger().info("Loaded " + placedBlocks.size() + " blighted blocks.");
    }

    /** Serialize location to string key */
    private String serializeLocation(Location loc) {
      return Objects.requireNonNull(loc.getWorld()).getName() + ";" +
        loc.getBlockX() + ";" +
        loc.getBlockY() + ";" +
        loc.getBlockZ();
    }

    /** Deserialize location string back to Location */
    private Location deserializeLocation(String key) {
      String[] parts = key.split(";");
      if (parts.length != 4) return null;
      World world = Bukkit.getWorld(parts[0]);
      if (world == null) return null;
      return new Location(world,
        Integer.parseInt(parts[1]),
        Integer.parseInt(parts[2]),
        Integer.parseInt(parts[3]));
    }
  }
}
