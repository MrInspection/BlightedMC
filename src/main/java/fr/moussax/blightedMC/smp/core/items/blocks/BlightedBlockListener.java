package fr.moussax.blightedMC.smp.core.items.blocks;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.server.database.PluginDatabase;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.blocks.registry.BlockRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class BlightedBlockListener implements Listener {
    private final BlightedMC plugin = BlightedMC.getInstance();
    private final NamespacedKey BLOCK_ID_KEY = new NamespacedKey(plugin, "blighted_block_id");
    private final PluginDatabase database;

    public BlightedBlockListener() {
        this.database = plugin.getDatabase();
    }

    private String getBlockId(Block block) {
        // 1. Check Metadata (Cache)
        if (block.hasMetadata("blighted_id")) {
            for (MetadataValue value : block.getMetadata("blighted_id")) {
                if (value.getOwningPlugin() == plugin) return value.asString();
            }
        }

        // 2. Check PDC
        BlockState state = block.getState();
        if (state instanceof TileState tile) {
            PersistentDataContainer pdc = tile.getPersistentDataContainer();
            if (pdc.has(BLOCK_ID_KEY, PersistentDataType.STRING)) {
                String id = pdc.get(BLOCK_ID_KEY, PersistentDataType.STRING);
                cacheMetadata(block, id);
                return id;
            }
        }

        // 3. Check Database (Fallback)
        String id = database.getBlockId(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        if (id != null) {
            cacheMetadata(block, id);
            return id;
        }

        return null;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!item.hasItemMeta()) return;

        var meta = item.getItemMeta();
        String id = Objects.requireNonNull(meta).getPersistentDataContainer().get(BlightedItem.BLIGHTED_ID_KEY, PersistentDataType.STRING);

        if (id == null) return;

        BlightedBlock customBlock = BlockRegistry.get(id);
        if (customBlock == null) return;

        Block placed = event.getBlockPlaced();

        // Save to PDC if applicable
        BlockState state = placed.getState();
        if (state instanceof TileState tile) {
            tile.getPersistentDataContainer().set(BLOCK_ID_KEY, PersistentDataType.STRING, id);
            tile.update();
        }

        // Save to Database
        database.addBlock(placed.getWorld().getUID(), placed.getX(), placed.getY(), placed.getZ(), id);

        // Cache Metadata
        cacheMetadata(placed, id);
        customBlock.onPlace(event);
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        String id = getBlockId(block);
        if (id == null) return;

        BlightedBlock customBlock = BlockRegistry.get(id);
        if (customBlock != null) {
            customBlock.onInteract(event);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        String id = getBlockId(block);
        if (id == null) return;

        BlightedBlock customBlock = BlockRegistry.get(id);
        if (customBlock == null) return;

        event.setDropItems(false); // Cancel vanilla drops
        event.setExpToDrop(0);

        Location blockLocation = block.getLocation();

        // Handle Drops
        ItemStack drop = customBlock.onBreak(event, customBlock.getBlightedItem().toItemStack());
        if (drop != null) {
            block.getWorld().dropItemNaturally(blockLocation, drop);
        }

        cleanupBlockData(block);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (getBlockId(block) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (getBlockId(block) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplosion(event.blockList(), event.getYield());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList(), event.getYield());
    }

    private void handleExplosion(List<Block> blocks, float yield) {
        var iterator = blocks.iterator();
        Random random = new Random();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            String id = getBlockId(block);

            if (id == null) continue;

            BlightedBlock customBlock = BlockRegistry.get(id);
            if (customBlock == null) continue;

            iterator.remove();
            block.setType(Material.AIR);

            if (random.nextFloat() <= yield) {
                ItemStack drop = customBlock.onBreak(null, customBlock.getBlightedItem().toItemStack());
                if (drop != null) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }

            cleanupBlockData(block);
        }
    }

    private void cleanupBlockData(Block block) {
        database.removeBlock(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        block.removeMetadata("blighted_id", plugin);

        BlockState state = block.getState();
        if (state instanceof TileState tile) {
            tile.getPersistentDataContainer().remove(BLOCK_ID_KEY);
            tile.update();
        }
    }

    private void cacheMetadata(Block block, String id) {
        block.setMetadata("blighted_id", new FixedMetadataValue(plugin, id));
    }
}