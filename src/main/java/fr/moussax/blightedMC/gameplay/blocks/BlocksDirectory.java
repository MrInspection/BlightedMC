package fr.moussax.blightedMC.gameplay.blocks;

import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import org.bukkit.Material;

import java.util.List;

public class BlocksDirectory implements ItemRegistry {

    @Override
    public List<ItemTemplate> defineItems() {
        ItemTemplate blightedCraftingTable = new ItemTemplate(
                "BLIGHTED_WORKBENCH",
                ItemType.BLOCK,
                ItemRarity.UNCOMMON,
                Material.ENCHANTING_TABLE,
                "Blighted Workbench"
        );

        blightedCraftingTable.addLore(
                "§8Placeable Block",
                "",
                "§7 Forged §beons ago §7in the §5Voidling Edges§7,",
                "§7 this workbench thrums with §5blighted energy§7, ",
                "§7 twisting §dmatter§7 and §dmagic§7 alike to forge",
                "§7 items beyond the reach of ordinary",
                "§7 crafting tables.",
                "",
                ItemRarity.RARE.getName() + " BLOCK"
        );
        blightedCraftingTable.addEnchantmentGlint();

        return ItemRegistry.add(blightedCraftingTable);
    }
}
