package fr.moussax.blightedMC.gameplay.blocks;

import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import org.bukkit.Material;

import java.util.List;

public class BlockItemsRegistry implements ItemRegistry {

  @Override
  public List<ItemTemplate> defineItems() {
    ItemTemplate blightedCraftingTable = new ItemTemplate(
      "BLIGHTED_CRAFTING_TABLE",
      ItemType.BLOCK,
      ItemRarity.UNCOMMON,
      Material.FLETCHING_TABLE,
      "Blighted Crafting Table"
    );

    blightedCraftingTable.addLore(
      "",
      "§7 Forged in the aberrant halls of the §5Blighted ",
      "§5 Mansion §7by the §4Redstone Engineers§7, this table ",
      "§7 bends matter and magic alike. It feeds on",
      "§7 lingering blight to forge §fadvanced materials",
      "§7 no mundane workbench can hold.",
      "",
      ItemRarity.UNCOMMON.getName() + " BLOCK"
    );
    blightedCraftingTable.addEnchantmentGlint();

    return ItemRegistry.add(blightedCraftingTable);
  }
}
