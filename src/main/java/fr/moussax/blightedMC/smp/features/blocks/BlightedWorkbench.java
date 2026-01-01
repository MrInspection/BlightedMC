package fr.moussax.blightedMC.smp.features.blocks;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.smp.core.items.crafting.menu.CraftingTableMenu;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlightedWorkbench extends BlightedBlock {

    public BlightedWorkbench() {
        super(Material.ENCHANTING_TABLE, ItemRegistry.getItem("BLIGHTED_WORKBENCH"));
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        event.getPlayer().playSound(event.getBlockPlaced().getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 100.0F, 0.85F);
        Formatter.inform(event.getPlayer(), "You have placed a §dBlighted Workbench§7!");
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            event.setCancelled(true);
            BlightedMC.menuManager().openMenu(new CraftingTableMenu(), player);
        }
    }
}
