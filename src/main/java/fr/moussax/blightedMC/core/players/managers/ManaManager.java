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

  public double getMaxMana() {
    return maxMana;
  }

  public void setMaxMana(double maxMana) {
    this.maxMana = maxMana;
  }

  public void setManaRegenerationRate(double manaRegenRate) {
    this.manaRegenerationRate = manaRegenRate;
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
