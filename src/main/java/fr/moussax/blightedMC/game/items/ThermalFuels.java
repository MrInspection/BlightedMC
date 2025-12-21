package fr.moussax.blightedMC.game.items;

import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.core.items.rules.PreventFluidPlacementRule;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import org.bukkit.Material;

import java.util.List;

public class ThermalFuels implements ItemRegistry {
    @Override
    public List<ItemTemplate> defineItems() {
        ItemTemplate enchantedCoal = new ItemTemplate(
            "ENCHANTED_COAL", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.COAL, "Enchanted Coal"
        );
        enchantedCoal.addEnchantmentGlint();
        enchantedCoal.addLore(
            "§8Thermal Fuel", "",
            " §7Ultra-dense carbon radiating ",
            " §7with intense heat that adds ",
            " §6\uD83E\uDEA3 3,000 mB §7to a refuelable ",
            " §7machine.", ""
        );
        enchantedCoal.addLore(ItemRarity.UNCOMMON.getName());

        ItemTemplate enchantedLavaBucket = new ItemTemplate(
            "ENCHANTED_LAVA_BUCKET", ItemType.MATERIAL, ItemRarity.RARE, Material.LAVA_BUCKET, "Enchanted Lava Bucket"
        );
        enchantedLavaBucket.addEnchantmentGlint();
        enchantedLavaBucket.addRule(new PreventPlacementRule());
        enchantedLavaBucket.addLore(
            "§8Thermal Fuel", "",
            " §7Enriched lava capable of ",
            " §7prolonged burning that adds ",
            " §6\uD83E\uDEA3 10,000 mB §7to a refuelable ",
            " §7machine.", ""
        );
        enchantedLavaBucket.addLore(ItemRarity.RARE.getName());

        ItemTemplate magmaBucket = new ItemTemplate(
            "MAGMA_BUCKET", ItemType.MATERIAL, ItemRarity.EPIC, Material.LAVA_BUCKET, "Magma Bucket"
        );
        magmaBucket.addEnchantmentGlint();
        magmaBucket.addRule(new PreventFluidPlacementRule());
        magmaBucket.addLore(
            "§8Thermal Fuel", "",
            " §7A superheated amalgam of ",
            " §7compressed magma that adds ",
            " §6\uD83E\uDEA3 20,000 mB §7to a refuelable",
            " §7machine.", ""
        );
        magmaBucket.addLore(ItemRarity.EPIC.getName());

        ItemTemplate plasmaBucket = new ItemTemplate(
            "PLASMA_BUCKET", ItemType.MATERIAL, ItemRarity.LEGENDARY, Material.LAVA_BUCKET, "Plasma Bucket"
        );
        plasmaBucket.addEnchantmentGlint();
        plasmaBucket.addRule(new PreventFluidPlacementRule());
        plasmaBucket.addLore(
            "§8Thermal Fuel", "",
            " §7Stable ionized matter containing ",
            " §7stellar-grade heat that adds",
            " §6\uD83E\uDEA3 50,000 mB §7to a refuelable",
            " §7machine.", ""
        );
        plasmaBucket.addLore(ItemRarity.LEGENDARY.getName());

        return ItemRegistry.add(List.of(enchantedCoal, enchantedLavaBucket, magmaBucket, plasmaBucket));
    }
}
