package fr.moussax.blightedMC.core.players;

public class BlightedFavorsManager {
  private double favors = 0;

  public double getFavors() {
    return favors;
  }

  public void setFavors(double value) {
    if (favors < 0) {
      throw new IllegalArgumentException("Favors amount cannot be negative.");
    }
    this.favors = value;
  }

  public void addFavors(double value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Added favors must be positive.");
    }
    favors += value;
  }

  public void removeFavors(double value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Removed favors must be positive.");
    }
    favors = Math.max(0, favors - value);
  }

  public boolean hasEnoughFavors(double value) {
    return favors >= value;
  }
}
