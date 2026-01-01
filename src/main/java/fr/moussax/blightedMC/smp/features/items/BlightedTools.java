package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import org.bukkit.Material;

public class BlightedTools implements ItemProvider {
    @Override
    public void register() {
        BlightedItem magmaRod = new BlightedItem("MAGMA_ROD", ItemType.LAVA_FISHING_ROD, ItemRarity.UNCOMMON, Material.FISHING_ROD);
        magmaRod.setDisplayName("Magma Rod");

        magmaRod.addLore("",
            " §7Impervious to the inferno,",
            " §7this rod casts where others ",
            " §7burn to dredge §6molten depths ",
            " §7for treasures.",
            ""
        );
        magmaRod.addLore(ItemRarity.UNCOMMON.getName() + " ROD");
        magmaRod.setFireResistant(true);

        add(magmaRod);
    }
}
