package fr.moussax.blightedMC.smp.features.blocks;

import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.smp.core.items.forging.menu.ForgeMenu;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.core.shared.menu.MenuManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlightedForge extends BlightedBlock {
    public static BlightedForge instance;

    public BlightedForge() {
        super(Material.BLAST_FURNACE, ItemRegistry.getItem("BLIGHTED_FORGE"));
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            event.setCancelled(true);
            MenuManager.openMenu(new ForgeMenu(null), player);
        }
    }
}
