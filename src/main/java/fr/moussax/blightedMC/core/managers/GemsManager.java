package fr.moussax.blightedMC.core.managers;

import org.checkerframework.checker.index.qual.Positive;

public class GemsManager {
  private int gems = 0;

  public void addGems(@Positive int value) {
    gems += value;
  }

  public void setGems(@Positive int value) {
    this.gems = value;
  }

  public void removeGems(@Positive int value) {
    gems = Math.max(0, gems - value);
  }

  public boolean hasEnoughGems(int value) {
    return gems >= value;
  }

  public int getGems() {
    return gems;
  }
}
