package fr.moussax.blightedMC.server;

public class PluginSettings {
    private double defaultMaxMana;
    private double defaultManaRegenerationRate;
    private double defaultPlayerHealth;
    private double customLootChance;
    private boolean bannersOnJoin;

    public double getDefaultMaxMana() {
        return defaultMaxMana;
    }

    public double getDefaultManaRegenerationRate() {
        return defaultManaRegenerationRate;
    }

    public double getDefaultPlayerHealth() {
        return defaultPlayerHealth;
    }

    public double getCustomLootChance() {
        return customLootChance;
    }

    public boolean hasBannersOnJoin() {
        return bannersOnJoin;
    }
}
