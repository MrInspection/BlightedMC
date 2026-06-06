package fr.moussax.blightedMC.content.items.armors;

import fr.moussax.blightedMC.content.items.abilities.HomodeusFlightAbility;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.engine.items.registry.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class HomodeusArmor implements ItemProvider {

    @Override
    public void register() {
        FullSetBonus homodeusFlightBonus = new HomodeusFlightAbility();

        BlightedItem homodeusHelmet = new BlightedItem("HOMODEUS_HELMET", ItemType.HELMET, ItemRarity.LEGENDARY, Material.LEATHER_HELMET);
        homodeusHelmet.setDisplayName("Homodeus Helmet");
        setupHomodeusPiece(homodeusHelmet, homodeusFlightBonus);

        BlightedItem homodeusChestplate = new BlightedItem("HOMODEUS_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.LEGENDARY, Material.LEATHER_CHESTPLATE);
        homodeusChestplate.setDisplayName("Homodeus Chestplate");
        setupHomodeusPiece(homodeusChestplate, homodeusFlightBonus);

        BlightedItem homodeusLeggings = new BlightedItem("HOMODEUS_LEGGINGS", ItemType.LEGGINGS, ItemRarity.LEGENDARY, Material.LEATHER_LEGGINGS);
        homodeusLeggings.setDisplayName("Homodeus Leggings");
        setupHomodeusPiece(homodeusLeggings, homodeusFlightBonus);

        BlightedItem homodeusBoots = new BlightedItem("HOMODEUS_BOOTS", ItemType.BOOTS, ItemRarity.LEGENDARY, Material.LEATHER_BOOTS);
        homodeusBoots.setDisplayName("Homodeus Boots");
        setupHomodeusPiece(homodeusBoots, homodeusFlightBonus);

        add(homodeusHelmet, homodeusChestplate, homodeusLeggings, homodeusBoots);
    }

    private void setupHomodeusPiece(BlightedItem piece, FullSetBonus bonus) {
        piece.setUnbreakable(true);
        piece.addEnchantmentGlint();
        piece.setLeatherColor("#ffffff");
        piece.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        piece.setFullSetBonus(bonus);
        piece.addLore("", ItemRarity.LEGENDARY.getName());
    }
}
