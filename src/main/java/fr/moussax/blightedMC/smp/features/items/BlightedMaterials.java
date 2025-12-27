package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import fr.moussax.blightedMC.smp.core.items.rules.common.PreventProjectileLaunchRule;
import org.bukkit.Material;

public class BlightedMaterials implements ItemProvider {

    @Override
    public void register() {
        BlightedItem enchantedIronIngot = new BlightedItem("ENCHANTED_IRON_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.IRON_INGOT);
        enchantedIronIngot.setDisplayName("Enchanted Iron Ingot");
        enchantedIronIngot.addEnchantmentGlint();
        enchantedIronIngot.addLore(ItemRarity.UNCOMMON.getName());

        BlightedItem enchantedIronBlock = new BlightedItem("ENCHANTED_IRON_BLOCK", ItemType.MATERIAL, ItemRarity.RARE, Material.IRON_BLOCK);
        enchantedIronBlock.setDisplayName("Enchanted Iron Block");
        enchantedIronBlock.addLore(ItemRarity.RARE.getName());
        enchantedIronBlock.addEnchantmentGlint();
        enchantedIronBlock.addRule(ItemRule.PREVENT_PLACEMENT);

        BlightedItem enchantedEnderPearl = new BlightedItem("ENCHANTED_ENDER_PEARL", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.ENDER_PEARL);
        enchantedEnderPearl.setDisplayName("Enchanted Ender Pearl");
        enchantedEnderPearl.addLore(ItemRarity.UNCOMMON.getName());
        enchantedEnderPearl.addEnchantmentGlint();
        enchantedEnderPearl.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);

        BlightedItem enchantedGhastTear = new BlightedItem("ENCHANTED_GHAST_TEAR", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.GHAST_TEAR);
        enchantedGhastTear.setDisplayName("Enchanted Ghast Tear");
        enchantedGhastTear.addLore(ItemRarity.UNCOMMON.getName());
        enchantedGhastTear.addEnchantmentGlint();

        add(
            enchantedIronIngot,
            enchantedIronBlock,
            enchantedEnderPearl,
            enchantedGhastTear
        );
    }
}
