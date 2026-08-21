package fr.moussax.blightedMC.content.items;

import fr.moussax.blightedMC.content.items.abilities.tools.VeinmineAbility;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.engine.items.abilities.AbilityType;
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

        BlightedItem voidRod = new BlightedItem("VOID_ROD", ItemType.VOID_FISHING_ROD, ItemRarity.RARE, Material.FISHING_ROD);
        voidRod.setDisplayName("Void Rod");
        voidRod.addLore("§8Demonstration tool");
        voidRod.addLore("");
        voidRod.addLore(ItemRarity.RARE.getName() + " ROD");


        BlightedItem demoPickaxe = new BlightedItem("DEMO_PICKAXE", ItemType.PICKAXE, ItemRarity.SPECIAL, Material.DIAMOND_PICKAXE);
        demoPickaxe.setDisplayName("Demo Pickaxe");
        demoPickaxe.addAbility(new Ability(new VeinmineAbility(), "Veinmine", AbilityType.PASSIVE));
        demoPickaxe.addLore("§8Demonstration tool");

        registry.register(demoPickaxe);
        registry.register(magmaRod);
        registry.register(voidRod);
    }
}
