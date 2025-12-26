package fr.moussax.blightedMC.smp.features.blocks;

import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemTemplate;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
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
            " §7A crafting table infused with ",
            " §5blighted energy §7capable",
            " §7of weaving forbidden magic",
            " §7into physical form.",
            "",
            ItemRarity.UNCOMMON.getName() + " BLOCK"
        );
        blightedCraftingTable.addEnchantmentGlint();

        ItemTemplate blightedForge = new ItemTemplate(
            "BLIGHTED_FORGE", ItemType.BLOCK, ItemRarity.RARE, Material.BLAST_FURNACE, "Blighted Forge"
        );
        blightedForge.addLore(
            "§8Placeable Machine",
            "",
            " §7An industrial crucible powered by ",
            " §5blighted energy§7, designed to fuse ",
            " §7magic and metal under heat",
            " §7intolerable to mortal craft.",
            "",
            ItemRarity.RARE.getName() + " MACHINE"
        );
        blightedForge.addEnchantmentGlint();

        return ItemRegistry.add(List.of(blightedCraftingTable, blightedForge));
    }
}
