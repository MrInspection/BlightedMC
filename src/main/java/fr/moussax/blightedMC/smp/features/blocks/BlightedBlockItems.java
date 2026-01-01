package fr.moussax.blightedMC.smp.features.blocks;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.blocks.registry.BlockRegistry;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import org.bukkit.Material;

public class BlightedBlockItems implements ItemProvider {

    @Override
    public void register() {

        BlightedItem blightedWorkbench = new BlightedItem("BLIGHTED_WORKBENCH", ItemType.BLOCK, ItemRarity.UNCOMMON, Material.ENCHANTING_TABLE);
        blightedWorkbench.setDisplayName("Blighted Workbench");
        blightedWorkbench.addLore(
            "§8Placeable Block",
            "",
            " §7A crafting table infused with ",
            " §5blighted energy §7capable",
            " §7of weaving forbidden magic",
            " §7into physical form.",
            "",
            ItemRarity.UNCOMMON.getName() + " BLOCK"
        );
        blightedWorkbench.addEnchantmentGlint();

        BlightedItem blightedForge = new BlightedItem("BLIGHTED_FORGE", ItemType.BLOCK, ItemRarity.RARE, Material.BLAST_FURNACE);
        blightedForge.setDisplayName("Blighted Forge");
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

        add(blightedWorkbench, blightedForge);
    }
}
