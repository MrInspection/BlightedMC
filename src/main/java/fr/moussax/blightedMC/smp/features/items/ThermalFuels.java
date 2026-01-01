package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import fr.moussax.blightedMC.smp.core.items.rules.common.PreventBucketInteractionsRule;
import org.bukkit.Material;

public class ThermalFuels implements ItemProvider {

    @Override
    public void register() {
        BlightedItem enchantedCoal = new BlightedItem("ENCHANTED_COAL", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.COAL);
        enchantedCoal.setDisplayName("Enchanted Coal");
        enchantedCoal.addLore(
            "§8Thermal Fuel", "",
            " §7Ultra-dense carbon radiating ",
            " §7with intense heat that adds ",
            " §6\uD83E\uDEA3 3,000 mB §7to a refuelable ",
            " §7machine.", ""
        );
        enchantedCoal.addLore(ItemRarity.UNCOMMON.getName());
        enchantedCoal.addEnchantmentGlint();

        BlightedItem enchantedLavaBucket = new BlightedItem("ENCHANTED_LAVA_BUCKET", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.LAVA_BUCKET);
        enchantedLavaBucket.setDisplayName("Enchanted Lava Bucket");
        enchantedLavaBucket.addLore(
            "§8Thermal Fuel", "",
            " §7Enriched lava capable of ",
            " §7prolonged burning that adds ",
            " §6\uD83E\uDEA3 10,000 mB §7to a refuelable ",
            " §7machine.", ""
        );
        enchantedLavaBucket.addLore(ItemRarity.RARE.getName());
        enchantedLavaBucket.addEnchantmentGlint();
        enchantedLavaBucket.addRule(ItemRule.PREVENT_BUCKET_INTERACTIONS);

        BlightedItem magmaBucket = new BlightedItem("MAGMA_BUCKET", ItemType.UNCATEGORIZED, ItemRarity.EPIC, Material.LAVA_BUCKET);
        magmaBucket.setDisplayName("Magma Bucket");
        magmaBucket.addLore(
            "§8Thermal Fuel", "",
            " §7A superheated amalgam of ",
            " §7compressed magma that adds ",
            " §6\uD83E\uDEA3 20,000 mB §7to a refuelable",
            " §7machine.", ""
        );
        magmaBucket.addLore(ItemRarity.EPIC.getName());
        magmaBucket.addEnchantmentGlint();
        magmaBucket.addRule(ItemRule.PREVENT_BUCKET_INTERACTIONS);

        BlightedItem plasmaBucket = new BlightedItem("PLASMA_BUCKET", ItemType.UNCATEGORIZED, ItemRarity.LEGENDARY, Material.LAVA_BUCKET);
        plasmaBucket.setDisplayName("Plasma Bucket");
        plasmaBucket.addLore(
            "§8Thermal Fuel", "",
            " §7Stable ionized matter containing ",
            " §7stellar-grade heat that adds",
            " §6\uD83E\uDEA3 50,000 mB §7to a refuelable",
            " §7machine.", ""
        );
        plasmaBucket.addLore(ItemRarity.LEGENDARY.getName());
        plasmaBucket.addEnchantmentGlint();
        plasmaBucket.addRule(ItemRule.PREVENT_BUCKET_INTERACTIONS);

        add(enchantedCoal, enchantedLavaBucket, magmaBucket, plasmaBucket);
    }
}
