package fr.moussax.blightedMC.core.items.blocks;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.server.PluginFiles;
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
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BlightedBlock {
    protected final Material material;
    protected final ItemTemplate itemTemplate;

    public BlightedBlock(@NonNull Material material, @NonNull ItemTemplate itemTemplate) {
        this.material = material;
        this.itemTemplate = itemTemplate;
        BlocksRegistry.addBlock(this);
    }

    public void onPlace(BlockPlaceEvent event) {
    }

    public void onInteract(PlayerInteractEvent event) {
    }

    public ItemStack onBreak(BlockBreakEvent event, ItemStack droppedItem) {
        return droppedItem;
    }

    public static class BlightedBlockListener implements Listener {
        private final BlightedMC plugin = BlightedMC.getInstance();
        private final NamespacedKey key = new NamespacedKey(plugin, "id");

        private final Map<String, String> placedBlocks = new HashMap<>();

        private final File dataFile;
        private final YamlConfiguration dataConfig;

        public BlightedBlockListener() {
            dataFile = PluginFiles.CUSTOM_BLOCKS.getFile();
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            loadData();
        }

        /**
         * Reads custom block ID using metadata or PDC
         */
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
                if (id != null) {
                    block.setMetadata("id", new FixedMetadataValue(plugin, id)); // cache
                    return id;
                }
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
            saveData();

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

            // Skip if it's not a custom block
            String id = getBlockId(block);
            if (id == null) return;
            BlightedBlock customBlock = BlocksRegistry.CUSTOM_BLOCKS.get(id);
            if (customBlock == null) return;

            Location loc = block.getLocation();
            event.setDropItems(false);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!loc.getBlock().getType().isAir()) return; // Block wasn't destroyed → skip

                ItemStack drop = customBlock.onBreak(event, customBlock.itemTemplate.toItemStack());
                if (drop != null) {
                    Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, drop);
                }

                String locKey = serializeLocation(loc);
                placedBlocks.remove(locKey);
                dataConfig.set(locKey, null);
                saveData();

                BlockState state = block.getState();
                if (state instanceof TileState tile) {
                    PersistentDataContainer pdc = tile.getPersistentDataContainer();
                    pdc.remove(key);
                    tile.update(true);
                }
            });
        }

        public void saveData() {
            try {
                dataConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save blighted block data: " + e.getMessage());
            }
        }

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

        private String serializeLocation(Location loc) {
            return Objects.requireNonNull(loc.getWorld()).getName() + ";" +
                loc.getBlockX() + ";" +
                loc.getBlockY() + ";" +
                loc.getBlockZ();
        }

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
