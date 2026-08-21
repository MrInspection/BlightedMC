package fr.moussax.blightedMC.content.items.abilities.tools;

import fr.moussax.blightedMC.engine.items.abilities.AbilityManager;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class VeinmineAbility implements AbilityManager<BlockBreakEvent> {

    private static final int MAX_BLOCK = 64;
    private static final Set<Material> VEIN_BLOCKS = Set.of(
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.COPPER_ORE,
            Material.GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE
    );

    @Override
    public boolean triggerAbility(BlockBreakEvent event) {
        Block origin = event.getBlock();
        if (!VEIN_BLOCKS.contains(origin.getType())) {
            return false;
        }

        for (Block block : findVein(origin)) {
            if (block.equals(origin)) {
                continue;
            }
            block.breakNaturally(event.getPlayer().getInventory().getItemInMainHand());
        }
        return true;
    }

    @Override
    public boolean cancelEvent(boolean success) {
        return false; // let vanilla break the origin block either way
    }

    private Set<Block> findVein(Block origin) {
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new ArrayDeque<>();

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && visited.size() < MAX_BLOCK) {
            Block current = queue.poll();

            for (Block nearby : getNeighbours(current)) {
                if (visited.contains(nearby)) {
                    continue;
                }

                if (nearby.getType() != origin.getType()) {
                    continue;
                }

                visited.add(nearby);
                queue.add(nearby);
            }
        }
        return visited;
    }

    private List<Block> getNeighbours(Block block) {
        List<Block> blocks = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    blocks.add(block.getRelative(x, y, z));
                }
            }
        }
        return blocks;
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
