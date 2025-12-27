package fr.moussax.blightedMC.smp.features.items;

import fr.moussax.blightedMC.smp.core.entities.loot.gems.GemsItem;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.Ability;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import org.bukkit.Material;

public class BlightedGemstone implements ItemProvider {

    @Override
    public void register() {
        BlightedItem blightedGemstone = new BlightedItem("BLIGHTED_GEMSTONE", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.PLAYER_HEAD);
        blightedGemstone.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2MjM1MjFjODExMWFkMjllOWRjZjdhY2M1NjA4NWE5YWIwN2RhNzMyZDE1MTg5NzZhZWU2MWQwYjNlM2JkNiJ9fX0=");
        blightedGemstone.setDisplayName("Blighted Gemstone");
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
        blightedGemstone.isUnstackable();
        blightedGemstone.addRule(ItemRule.PREVENT_PLACEMENT);
        blightedGemstone.addAbility(new Ability(new GemsItem.BlightedGemstoneAbility(), "Consume Gems", AbilityType.RIGHT_CLICK));

        add(blightedGemstone);
    }
}
