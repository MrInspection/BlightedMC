package fr.moussax.blightedMC.smp.features.items.materials;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import org.bukkit.Material;

public class EndMaterials implements ItemProvider {
    @Override
    public void register() {
        BlightedItem enchantedEnderPearl = new BlightedItem("ENCHANTED_ENDER_PEARL", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.ENDER_PEARL);
        enchantedEnderPearl.setDisplayName("Enchanted Ender Pearl");
        enchantedEnderPearl.addLore(ItemRarity.UNCOMMON.getName());
        enchantedEnderPearl.addEnchantmentGlint();
        enchantedEnderPearl.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);

        BlightedItem enchantedEndstone = new BlightedItem("ENCHANTED_END_STONE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.END_STONE);
        enchantedEndstone.setDisplayName("Enchanted End Stone");
        enchantedEndstone.addLore(ItemRarity.UNCOMMON.getName());
        enchantedEndstone.addEnchantmentGlint();
        enchantedEndstone.addRule(ItemRule.PREVENT_PLACEMENT);

        BlightedItem enchantedChorusFruit = new BlightedItem("ENCHANTED_CHORUS_FRUIT", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.CHORUS_FRUIT);
        enchantedChorusFruit.setDisplayName("Enchanted Chorus Fruit");
        enchantedChorusFruit.addLore(ItemRarity.UNCOMMON.getName());
        enchantedChorusFruit.addEnchantmentGlint();

        BlightedItem voidResidue = new BlightedItem("VOID_RESIDUE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.PURPLE_DYE);
        voidResidue.setDisplayName("Voidling Residue");
        voidResidue.addLore("",
            " §7The tangible byproduct of entropy,",
            " §7harvested from the §5Outer Islands",
            " §7where reality begins to fray.",
            "",
            ItemRarity.UNCOMMON.getName()
        );
        voidResidue.addEnchantmentGlint();

        add(
            enchantedEnderPearl,
            enchantedEndstone,
            enchantedChorusFruit,
            voidResidue
        );
    }
}
