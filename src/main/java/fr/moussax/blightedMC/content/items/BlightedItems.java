package fr.moussax.blightedMC.content.items;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemRarity;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.engine.items.abilities.AbilityType;
import java.util.function.Consumer;
import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.shared.loot.results.gems.GemsItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class BlightedItems implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {

        BlightedItem blightedBanner = new BlightedItem(
                "BLIGHTED_BANNER", ItemType.UNCATEGORIZED, ItemRarity.EPIC, Material.BLACK_BANNER);
        blightedBanner.setDisplayName("Crown of the Blight");
        blightedBanner.addLore(
                "",
                "§7 An ancient standard woven from",
                "§5 shadow-silk§7. Its fabric beats with ",
                "§7 a faint, living pulse, drawing the",
                "§7 corruption of the Blight like",
                "§7 a cosmic §5lightning rod§7.",
                "",
                ItemRarity.EPIC.getName()
        );
        blightedBanner.addBannerPatterns(List.of(
                new Pattern(DyeColor.PURPLE, PatternType.CURLY_BORDER),
                new Pattern(DyeColor.BLACK, PatternType.BRICKS),
                new Pattern(DyeColor.BLACK, PatternType.SMALL_STRIPES),
                new Pattern(DyeColor.BLACK, PatternType.GUSTER),
                new Pattern(DyeColor.PURPLE, PatternType.CIRCLE),
                new Pattern(DyeColor.BLACK, PatternType.FLOW)
        ));
        blightedBanner.addItemFlag(ItemFlag.HIDE_BANNER_PATTERNS);
        blightedBanner.addRule(ItemRule.PREVENT_PLACEMENT);
        blightedBanner.editEquippable(equippable -> equippable.setSlot(EquipmentSlot.HEAD));
        blightedBanner.fireResistant();
        blightedBanner.unstackable();

        BlightedItem blightedCodex = new BlightedItem("BLIGHTED_CODEX", ItemType.UNCATEGORIZED, ItemRarity.EPIC, Material.ENCHANTED_BOOK);
        blightedCodex.setDisplayName("Blighted Codex");
        blightedCodex.addLore(
                "",
                "§7 A forbidden ledger bound in cracked",
                "§7 leather. Its pages remain blank until",
                "§7 they absorb the essence of the Blight.",
                "",
                " &#D2A5FF§lSEALED RIDDLE!",
                "&#D2A5FF Wear the woven shadow as your crown,",
                "&#D2A5FF claim a Blighted soul for these pages.",
                "&#D2A5FF Hold the violet crystal in your left hand, ",
                "&#D2A5FF stand before the altar of runes.",
                "&#D2A5FF Lay your hand upon the ancient seal,",
                "&#D2A5FF and the hidden path shall awaken.",
                "",
                "§8 Souls trapped: §d0 ☠",
                "",
                ItemRarity.EPIC.getName()
        );
        blightedCodex.fireResistant();

        BlightedItem blightedGemstone = new BlightedItem("BLIGHTED_GEMSTONE", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.PLAYER_HEAD);
        blightedGemstone.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2MjM1MjFjODExMWFkMjllOWRjZjdhY2M1NjA4NWE5YWIwN2RhNzMyZDE1MTg5NzZhZWU2MWQwYjNlM2JkNiJ9fX0=");
        blightedGemstone.setDisplayName("Blighted Gemstone");
        blightedGemstone.addLore(
                "",
                "§7 A gemstone §5corrupted§7 by shadow,",
                "§7 stolen from the heart of a §5fallen",
                "§5 abomination§7. Within its core lie §dGems ",
                "§7 sealed and waiting for a daring",
                "§7 hand to claim them.",
                "§8 Gems: §d1✵",
                "",
                "§d Right click to consume!",
                "",
                ItemRarity.SPECIAL.getName()
        );

        blightedGemstone.preventEquipping();
        blightedGemstone.unstackable();
        blightedGemstone.addRule(ItemRule.PREVENT_PLACEMENT);
        blightedGemstone.addAbility(
                new Ability(new GemsItem.BlightedGemstoneAbility(), "Consume Gems", AbilityType.RIGHT_CLICK)
                , false
        );

        registry.accept(blightedBanner);
        registry.accept(blightedCodex);
        registry.accept(blightedGemstone);
    }
}
