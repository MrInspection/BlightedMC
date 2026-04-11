package fr.moussax.blightedMC.engine.items;

import lombok.Getter;

@Getter
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
}
