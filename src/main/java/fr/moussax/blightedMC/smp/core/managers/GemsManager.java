package fr.moussax.blightedMC.smp.core.managers;

public class GemsManager {
    private int gems = 0;

    public void addGems(int value) {
        gems += value;
    }

    public void setGems(int value) {
        this.gems = value;
    }

    public void removeGems(int value) {
        gems = Math.max(0, gems - value);
    }

    public boolean hasEnoughGems(int value) {
        return gems >= value;
    }

    public int getGems() {
        return gems;
    }
}
