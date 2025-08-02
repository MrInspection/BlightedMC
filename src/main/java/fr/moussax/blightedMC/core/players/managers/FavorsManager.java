package fr.moussax.blightedMC.core.players.managers;

public class FavorsManager {
  private int favors = 0;

  public int getFavors() {
    return favors;
  }

  public void setFavors(int value) {
    if (favors < 0) {
      throw new IllegalArgumentException("Favors amount cannot be negative.");
    }
    this.favors = value;
  }

  public void addFavors(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Added favors must be positive.");
    }
    favors += value;
  }

  public void removeFavors(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Removed favors must be positive.");
    }
    favors = Math.max(0, favors - value);
  }

  public boolean hasEnoughFavors(int value) {
    return favors >= value;
  }
}
