package fr.moussax.blightedMC.smp.features.items.armors;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.features.items.abilities.RocketBootsAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class RocketBoots implements ItemProvider {

    @Override
    public void register() {
        BlightedItem rocketBoots = new BlightedItem("ROCKET_BOOTS", ItemType.BOOTS, ItemRarity.UNCOMMON, Material.LEATHER_BOOTS);
        rocketBoots.setDisplayName("Rocket Boots");
        rocketBoots.addLore(
            "",
            "§5 Ability: Double Jump",
            "§7 Allows you to double jump while ",
            "§7 being on the ground.",
            "",
            ItemRarity.UNCOMMON.getName() + " BOOTS"
        );

        rocketBoots.setLeatherColor("#B02E26").setArmorTrim(TrimMaterial.QUARTZ, TrimPattern.BOLT);
        rocketBoots.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ARMOR_TRIM);
        rocketBoots.setFullSetBonus(new RocketBootsAbility());

        add(rocketBoots);
    }
}
