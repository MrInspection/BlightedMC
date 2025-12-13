package fr.moussax.blightedMC.game.items;

import fr.moussax.blightedMC.core.entities.loot.gems.GemsItem;
import fr.moussax.blightedMC.core.items.ItemRarity;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.ItemType;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import org.bukkit.Material;

import java.util.List;

public class BlightedGemstone implements ItemRegistry {
    @Override
    public List<ItemTemplate> defineItems() {
        ItemTemplate blightedGemstone = new ItemTemplate("BLIGHTED_GEMSTONE", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.PLAYER_HEAD, "Blighted Gemstone");
        blightedGemstone.addLore(
            "§8Consumable Item",
            "",
            "§7 A gemstone §5corrupted§7 by shadow,",
            "§7 stolen from the heart of a §5fallen",
            "§5 abomination§7. Within its core lie §dGems ",
            "§7 sealed and waiting for a daring",
            "§7 hand to claim them.",
            "§8 Gems: §d50✵",
            "",
            "§d Right click to consume!",
            "",
            ItemRarity.SPECIAL.getName()
        );

        blightedGemstone.addNotEquippable();
        blightedGemstone.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2MjM1MjFjODExMWFkMjllOWRjZjdhY2M1NjA4NWE5YWIwN2RhNzMyZDE1MTg5NzZhZWU2MWQwYjNlM2JkNiJ9fX0=");
        blightedGemstone.addRule(new PreventPlacementRule());
        blightedGemstone.addAbility(new Ability(new GemsItem.BlightedGemstoneAbility(), "Consume Gems", AbilityType.RIGHT_CLICK));

        return ItemRegistry.add(blightedGemstone);
    }
}
