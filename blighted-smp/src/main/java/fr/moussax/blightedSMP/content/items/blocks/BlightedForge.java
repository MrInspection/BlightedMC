package fr.moussax.blightedSMP.content.items.blocks;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.items.blocks.BlightedBlock;
import fr.moussax.blightedSMP.engine.items.recipes.forging.menu.ForgeMenu;
import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
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
            BlightedSMP.menuManager().openMenu(new ForgeMenu(null), player);
        }
    }
}
