package fr.moussax.blightedMC.core.players.managers;

public class BlightedManaManager {
  private double currentMana;
  private double maxMana;
  private double manaRegenerationRate;

  public BlightedManaManager(double maxMana, double manaRegenerationRate) {
    this.maxMana = maxMana;
    this.manaRegenerationRate = manaRegenerationRate;
    this.currentMana = maxMana;
  }

  public double getCurrentMana() {
    return currentMana;
  }

  public double getMaxMana() {
    return maxMana;
  }

  public void setMaxMana(double maxMana) {
    this.maxMana = maxMana;
  }

  public void setManaRegenerationRate(double manaRegenerationRate) {
    this.manaRegenerationRate = manaRegenerationRate;
  }

  public boolean consumeMana(double amount) {
    if(currentMana < amount) return false;
    currentMana -= amount;
    return true;
  }

  public void regenerateMana() {
    currentMana += manaRegenerationRate;
    if(currentMana > maxMana) currentMana = maxMana;
  }
}
