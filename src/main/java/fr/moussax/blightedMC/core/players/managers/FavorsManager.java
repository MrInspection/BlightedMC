package fr.moussax.blightedMC.core.players.managers;

import org.checkerframework.checker.index.qual.Positive;

public class FavorsManager {
  private int favors = 0;

  public void addFavors(@Positive int value) {
    favors += value;
  }

  public void setFavors(@Positive int value) {
    this.favors = value;
  }

  public void removeFavors(@Positive int value) {
    favors = Math.max(0, favors - value);
  }

  public boolean hasEnoughFavors(int value) {
    return favors >= value;
  }

  public int getFavors() {
    return favors;
  }
}
