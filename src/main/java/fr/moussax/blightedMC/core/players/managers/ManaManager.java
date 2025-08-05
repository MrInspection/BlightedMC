package fr.moussax.blightedMC.core.players.managers;

/**
 * Manages a player's mana pool, including current mana, maximum mana,
 * and mana regeneration rate.
 */
public class ManaManager {
  private double currentMana;
  private double maxMana;
  private double manaRegenerationRate;

  /**
   * Constructs a ManaManager with specified maximum mana and regeneration rate.
   * Initializes current mana to the maximum mana.
   *
   * @param maxMana           maximum mana capacity
   * @param manaRegenRate     mana regenerated per tick/cycle
   */
  public ManaManager(double maxMana, double manaRegenRate) {
    this.maxMana = maxMana;
    this.manaRegenerationRate = manaRegenRate;
    this.currentMana = maxMana;
  }

  /**
   * Returns the current mana value.
   *
   * @return current mana amount
   */
  public double getCurrentMana() {
    return currentMana;
  }

  /**
   * Returns the maximum mana capacity.
   *
   * @return maximum mana amount
   */
  public double getMaxMana() {
    return maxMana;
  }

  /**
   * Sets the maximum mana capacity.
   *
   * @param maxMana new maximum mana value
   */
  public void setMaxMana(double maxMana) {
    this.maxMana = maxMana;
  }

  /**
   * Sets the mana regeneration rate.
   *
   * @param manaRegenRate new mana regeneration rate per tick/cycle
   */
  public void setManaRegenerationRate(double manaRegenRate) {
    this.manaRegenerationRate = manaRegenRate;
  }

  /**
   * Attempts to consume the specified amount of mana.
   *
   * @param amount mana to consume
   * @return true if enough mana was available and consumed; false otherwise
   */
  public boolean consumeMana(double amount) {
    if (currentMana < amount) return false;
    currentMana -= amount;
    return true;
  }

  /**
   * Regenerates mana by the configured regeneration rate,
   * capping at the maximum mana.
   */
  public void regenerateMana() {
    currentMana += manaRegenerationRate;
    if (currentMana > maxMana) currentMana = maxMana;
  }
}
