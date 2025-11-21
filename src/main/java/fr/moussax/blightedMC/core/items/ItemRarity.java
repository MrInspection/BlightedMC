package fr.moussax.blightedMC.core.items;

/**
 * Represents the rarity levels of custom items in BlightedMC.
 * <p>
 * Each rarity defines a formatted display name and a color prefix
 * used for item naming and visual distinction.
 */
public enum ItemRarity {
    COMMON("§f§lCOMMON", "§f"),
    UNCOMMON("§e§lUNCOMMON", "§e"),
    RARE("§b§lRARE", "§b"),
    EPIC("§d§lEPIC", "§d"),
    LEGENDARY("§c§lLEGENDARY", "§c"),
    SPECIAL("§5§lSPECIAL", "§5");

    private final String name;
    private final String colorPrefix;

    ItemRarity(String name, String colorPrefix) {
        this.name = name;
        this.colorPrefix = colorPrefix;
    }

    /**
     * Returns the formatted rarity name.
     *
     * @return the display name of the rarity
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the color prefix associated with this rarity.
     *
     * @return the color prefix string
     */
    public String getColorPrefix() {
        return colorPrefix;
    }
}
