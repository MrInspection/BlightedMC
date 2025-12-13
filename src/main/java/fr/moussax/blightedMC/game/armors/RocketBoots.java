package fr.moussax.blightedMC.game.armors;

import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.game.abilities.RocketBootsAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.List;

public class RocketBoots implements ItemRegistry {

    @Override
    public List<ItemTemplate> defineItems() {
        ItemTemplate rocketBoots = new ItemTemplate("ROCKET_BOOTS", ItemType.BOOTS, ItemRarity.UNCOMMON, Material.LEATHER_BOOTS, "Rocket Boots");
        rocketBoots.setLeatherColor("#B02E26").setArmorTrim(TrimMaterial.QUARTZ, TrimPattern.BOLT);
        rocketBoots.addItemFlag(List.of(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ARMOR_TRIM));
        rocketBoots.addLore(
            "",
            "§5 Ability: Double Jump",
            "§7 Allows you to double jump while ",
            "§7 being on the ground.",
            "",
            ItemRarity.UNCOMMON.getName() + " BOOTS"
        );
        rocketBoots.setFullSetBonus(new RocketBootsAbility());

        return ItemRegistry.add(rocketBoots);
    }
}
