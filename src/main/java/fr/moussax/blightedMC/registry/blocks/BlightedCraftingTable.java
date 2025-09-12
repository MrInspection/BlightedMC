package fr.moussax.blightedMC.registry.blocks;

import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.core.items.crafting.menu.CraftingTableMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlightedCraftingTable extends BlightedBlock {
  public static BlightedCraftingTable INSTANCE;

  public BlightedCraftingTable() {
    super(Material.FLETCHING_TABLE, ItemsRegistry.BLIGHTED_ITEMS.get("BLIGHTED_CRAFTING_TABLE"));
    INSTANCE = this;
  }

  @Override
  public void onPlace(BlockPlaceEvent event) {
    // Optionally: custom placement logic
  }

  @Override
  public void onInteract(PlayerInteractEvent event) {
    if (event.getHand() != EquipmentSlot.HAND) return;
    Player player = event.getPlayer();
    if (event.getAction().toString().contains("RIGHT_CLICK")) {
      event.setCancelled(true);
      player.openInventory(CraftingTableMenu.createInventory());
    }
  }
}
