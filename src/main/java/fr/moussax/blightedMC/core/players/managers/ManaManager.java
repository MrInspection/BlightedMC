package fr.moussax.blightedMC.core.players.managers;

public class ManaManager {
  private double currentMana;
  private double maxMana;
  private double manaRegenerationRate;

  public ManaManager(double maxMana, double manaRegenRate) {
    this.maxMana = maxMana;
    this.manaRegenerationRate = manaRegenRate;
    this.currentMana = maxMana;
  }

  public double getCurrentMana() {
    return currentMana;
  }

  public void setCurrentMana(double currentMana) {
    if (currentMana < 0) currentMana = 0;
    if (currentMana > maxMana) currentMana = maxMana;
    this.currentMana = currentMana;
  }

  public double getMaxMana() {
    return maxMana;
  }

  public void setMaxMana(double maxMana) {
    this.maxMana = maxMana;
    if (this.currentMana > maxMana) this.currentMana = maxMana;
  }

  public void setManaRegenerationRate(double manaRegenRate) {
    this.manaRegenerationRate = manaRegenRate;
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
