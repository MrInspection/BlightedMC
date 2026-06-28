package fr.moussax.blightedMC.content.items;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistryHandler;
import org.bukkit.Material;

public class BlightedTools implements RegistryModule<ItemRegistryHandler> {
    @Override
    public void register(ItemRegistryHandler registry) {
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

        registry.register(magmaRod);
    }
}
