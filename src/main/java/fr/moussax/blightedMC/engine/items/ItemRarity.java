package fr.moussax.blightedMC.engine.items;

import lombok.Getter;

/**
 * Defines the rarity tier of a BlightedMC item.
 *
 * <p>Each rarity provides a formatted display name and a color prefix used
 * when presenting the item in-game.</p>
 */
@Getter
public enum ItemRarity {

    /** Common items. */
    COMMON("§f§lCOMMON", "§f"),

    /** Uncommon items. */
    UNCOMMON("§e§lUNCOMMON", "§e"),

    /** Rare items. */
    RARE("§b§lRARE", "§b"),

    /** Epic items. */
    EPIC("§d§lEPIC", "§d"),

    /** Legendary items. */
    LEGENDARY("§c§lLEGENDARY", "§c"),

    /** Special items reserved for unique or exceptional content. */
    SPECIAL("§5§lSPECIAL", "§5");

    private final String name;
    private final String colorPrefix;

    ItemRarity(String name, String colorPrefix) {
        this.name = name;
        this.colorPrefix = colorPrefix;
    }
}
