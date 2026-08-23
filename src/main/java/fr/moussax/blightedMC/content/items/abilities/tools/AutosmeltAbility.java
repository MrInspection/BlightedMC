package fr.moussax.blightedMC.content.items.abilities.tools;

import fr.moussax.blightedMC.engine.items.abilities.AbilityManager;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Material;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AutosmeltAbility implements AbilityManager<BlockDropItemEvent> {

    private static final Map<Material, Material> SMELTABLE = Map.ofEntries(
            Map.entry(Material.COBBLESTONE, Material.STONE),
            Map.entry(Material.STONE, Material.SMOOTH_STONE),
            Map.entry(Material.COBBLED_DEEPSLATE, Material.DEEPSLATE),
            Map.entry(Material.SAND, Material.GLASS),
            Map.entry(Material.RED_SAND, Material.GLASS),
            Map.entry(Material.SANDSTONE, Material.SMOOTH_SANDSTONE),
            Map.entry(Material.RED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE),
            Map.entry(Material.NETHERRACK, Material.NETHER_BRICK),
            Map.entry(Material.BASALT, Material.SMOOTH_BASALT),
            Map.entry(Material.QUARTZ_BLOCK, Material.SMOOTH_QUARTZ),
            Map.entry(Material.CLAY, Material.TERRACOTTA),

            Map.entry(Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS),
            Map.entry(Material.DEEPSLATE_BRICKS, Material.CRACKED_DEEPSLATE_BRICKS),
            Map.entry(Material.DEEPSLATE_TILES, Material.CRACKED_DEEPSLATE_TILES),
            Map.entry(Material.NETHER_BRICKS, Material.CRACKED_NETHER_BRICKS),
            Map.entry(Material.POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS),

            Map.entry(Material.IRON_ORE, Material.IRON_INGOT),
            Map.entry(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
            Map.entry(Material.GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.NETHER_GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.COPPER_ORE, Material.COPPER_INGOT),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),
            Map.entry(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP),

            Map.entry(Material.RAW_IRON, Material.IRON_INGOT),
            Map.entry(Material.RAW_GOLD, Material.GOLD_INGOT),
            Map.entry(Material.RAW_COPPER, Material.COPPER_INGOT),
            Map.entry(Material.RAW_IRON_BLOCK, Material.IRON_BLOCK),
            Map.entry(Material.RAW_GOLD_BLOCK, Material.GOLD_BLOCK),
            Map.entry(Material.RAW_COPPER_BLOCK, Material.COPPER_BLOCK)
    );

    @Override
    public boolean triggerAbility(BlockDropItemEvent event) {
        Material block = event.getBlockState().getType();
        Material smelted = SMELTABLE.get(block);

        if (smelted == null) return false;

        event.getItems().clear();
        event.getItems().add(event.getBlock()
                .getWorld()
                .dropItemNaturally(
                        event.getBlock().getLocation(),
                        new ItemStack(smelted)
                )
        );

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
