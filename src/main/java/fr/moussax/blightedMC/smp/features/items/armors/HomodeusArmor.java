package fr.moussax.blightedMC.smp.features.items.armors;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.features.items.abilities.HomodeusFlightAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class HomodeusArmor implements ItemProvider {

    @Override
    public void register() {
        FullSetBonus homodeusFlightBonus = new HomodeusFlightAbility();

        BlightedItem homodeusHelmet = new BlightedItem("HOMODEUS_HELMET", ItemType.HELMET, ItemRarity.LEGENDARY, Material.LEATHER_HELMET);
        homodeusHelmet.setDisplayName("Homodeus Helmet");
        homodeusHelmet.addLore(
            "",
            " §5Full Set Bonus: Homodeus",
            " §7Ascend beyond mortal limits, harnessing",
            " §7divine technology to defy gravity and soar",
            " §7through the skies.",
            "",
            ItemRarity.LEGENDARY.getName()
        );
        homodeusHelmet.setUnbreakable(true);
        homodeusHelmet.addEnchantmentGlint();
        homodeusHelmet.setLeatherColor("#ffffff");
        homodeusHelmet.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);


        BlightedItem homodeusChestplate = new BlightedItem("HOMODEUS_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.LEGENDARY, Material.LEATHER_CHESTPLATE);
        homodeusChestplate.setDisplayName("Homodeus Chestplate");
        homodeusChestplate.addLore(
            "",
            " §5Full Set Bonus: Homodeus",
            " §7Ascend beyond mortal limits, harnessing",
            " §7divine technology to defy gravity and soar",
            " §7through the skies.",
            "",
            ItemRarity.LEGENDARY.getName()
        );
        homodeusChestplate.addEnchantmentGlint();
        homodeusChestplate.setLeatherColor("#ffffff");
        homodeusChestplate.setUnbreakable(true);
        homodeusChestplate.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        BlightedItem homodeusLeggings = new BlightedItem("HOMODEUS_LEGGINGS", ItemType.LEGGINGS, ItemRarity.LEGENDARY, Material.LEATHER_LEGGINGS);
        homodeusLeggings.setDisplayName("Homodeus Leggings");
        homodeusLeggings.addLore(
            "",
            " §5Full Set Bonus: Homodeus",
            " §7Ascend beyond mortal limits, harnessing",
            " §7divine technology to defy gravity and soar",
            " §7through the skies.",
            "",
            ItemRarity.LEGENDARY.getName()
        );
        homodeusLeggings.addEnchantmentGlint();
        homodeusLeggings.setLeatherColor("#ffffff");
        homodeusLeggings.setUnbreakable(true);
        homodeusLeggings.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        BlightedItem homodeusBoots = new BlightedItem("HOMODEUS_BOOTS", ItemType.BOOTS, ItemRarity.LEGENDARY, Material.LEATHER_BOOTS);
        homodeusBoots.setDisplayName("Homodeus Boots");

        homodeusBoots.addLore(
            "",
            " §5Full Set Bonus: Homodeus",
            " §7Ascend beyond mortal limits, harnessing",
            " §7divine technology to defy gravity and soar",
            " §7through the skies.",
            "",
            ItemRarity.LEGENDARY.getName()
        );
        homodeusBoots.addEnchantmentGlint();
        homodeusBoots.setLeatherColor("#ffffff");
        homodeusBoots.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        homodeusBoots.setUnbreakable(true);

        homodeusHelmet.setFullSetBonus(homodeusFlightBonus);
        homodeusChestplate.setFullSetBonus(homodeusFlightBonus);
        homodeusLeggings.setFullSetBonus(homodeusFlightBonus);
        homodeusBoots.setFullSetBonus(homodeusFlightBonus);

        add(
            homodeusHelmet,
            homodeusChestplate,
            homodeusLeggings,
            homodeusBoots
        );
    }
}
