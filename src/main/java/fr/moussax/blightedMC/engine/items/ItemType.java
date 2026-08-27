package fr.moussax.blightedMC.engine.items;

import lombok.Getter;

/**
 * Defines the functional classification and category mapping for custom items.
 */
@Getter
public enum ItemType {

    /** Helmet armor piece. */
    HELMET(Category.ARMOR),
    /** Chestplate armor piece. */
    CHESTPLATE(Category.ARMOR),
    /** Leggings armor piece. */
    LEGGINGS(Category.ARMOR),
    /** Boots armor piece. */
    BOOTS(Category.ARMOR),

    /** One-handed sword melee weapon. */
    SWORD(Category.MELEE_WEAPON),
    /** Two-handed longsword melee weapon. */
    LONGSWORD(Category.MELEE_WEAPON),
    /** Thrown or thrusting spear weapon. */
    SPEAR(Category.RANGE_WEAPON),
    /** Magical wand weapon. */
    WAND(Category.MELEE_WEAPON),

    /** Ranged bow weapon. */
    BOW(Category.RANGE_WEAPON),

    /** Mining pickaxe tool. */
    PICKAXE(Category.TOOLS),
    /** Mining drill tool. */
    DRILL(Category.TOOLS),
    /** Woodcutting axe tool. */
    AXE(Category.TOOLS),
    /** Farming hoe tool. */
    HOE(Category.TOOLS),
    /** Excavating shovel tool. */
    SHOVEL(Category.TOOLS),

    /** Standard fishing rod. */
    FISHING_ROD(Category.TOOLS),
    /** Specialized lava fishing rod. */
    LAVA_FISHING_ROD(Category.TOOLS),
    /** Specialized void fishing rod. */
    VOID_FISHING_ROD(Category.TOOLS),

    /** Crafting material item. */
    MATERIAL(Category.MATERIAL),
    /** Socketable or crafting gemstone item. */
    GEMSTONE(Category.MATERIAL),
    /** Equipment upgrade module item. */
    UPGRADE_MODULE(Category.MATERIAL),
    /** Placeable custom block item. */
    BLOCK(Category.BLOCKS),

    /** Uncategorized or miscellaneous item. */
    UNCATEGORIZED(Category.MISCELLANEOUS);

    private final Category category;

    ItemType(Category category) {
        this.category = category;
    }

    /**
     * Broad grouping for item types used in inventory filtering and display categories.
     */
    public enum Category {
        /** Armor equipment category. */
        ARMOR,
        /** Melee weapon category. */
        MELEE_WEAPON,
        /** Ranged weapon category. */
        RANGE_WEAPON,
        /** Tool and utility item category. */
        TOOLS,
        /** Placeable block category. */
        BLOCKS,
        /** Material and resource category. */
        MATERIAL,
        /** Miscellaneous item category. */
        MISCELLANEOUS
    }
}
