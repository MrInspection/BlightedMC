package fr.moussax.blightedMC.engine.player.managers;

import lombok.Getter;
import lombok.Setter;

public final class ManaManager {
    @Getter
    private double currentMana;
    @Getter
    private double maxMana;
    @Setter
    private double manaRegenerationRate;

    public ManaManager(double maxMana, double manaRegenRate) {
        this.maxMana = maxMana;
        this.manaRegenerationRate = manaRegenRate;
        this.currentMana = maxMana;
    }

    public void setCurrentMana(double currentMana) {
        if (currentMana < 0) currentMana = 0;
        if (currentMana > maxMana) currentMana = maxMana;
        this.currentMana = currentMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
        if (this.currentMana > maxMana) this.currentMana = maxMana;
    }

    public void consumeMana(double amount) {
        if (currentMana < amount) return;
        currentMana -= amount;
    }

    public void regenerateMana() {
        currentMana += manaRegenerationRate;
        if (currentMana > maxMana) currentMana = maxMana;
    }
}
