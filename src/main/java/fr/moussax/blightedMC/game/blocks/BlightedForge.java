package fr.moussax.blightedMC.game.blocks;

import fr.moussax.blightedMC.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.core.items.forging.menu.ForgeMenu;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.core.menus.MenuManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlightedForge extends BlightedBlock {
    public static BlightedForge instance;

    public BlightedForge() {
        super(Material.BLAST_FURNACE, ItemDirectory.getItem("BLIGHTED_FORGE"));
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
