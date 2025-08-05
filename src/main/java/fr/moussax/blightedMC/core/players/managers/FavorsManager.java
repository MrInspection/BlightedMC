package fr.moussax.blightedMC.core.players.managers;

/**
 * Manages the favors resource for a player.
 * <p>
 * Provides methods to get, set, add, and remove favors with validation.
 */
public class FavorsManager {
  private int favors = 0;

  /**
   * Returns the current amount of favors.
   *
   * @return current favors count
   */
  public int getFavors() {
    return favors;
  }

  /**
   * Sets the favors amount.
   *
   * @param value new favors value, must be non-negative
   * @throws IllegalArgumentException if value is negative
   */
  public void setFavors(int value) {
    if (value < 0) {
      throw new IllegalArgumentException("Favors amount cannot be negative.");
    }
    this.favors = value;
  }

  /**
   * Adds favors to the current amount.
   *
   * @param value positive favors amount to add
   * @throws IllegalArgumentException if value is not positive
   */
  public void addFavors(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Added favors must be positive.");
    }
    favors += value;
  }

  /**
   * Removes favors from the current amount.
   * <p>
   * The favors count will not drop below zero.
   *
   * @param value positive favors amount to remove
   * @throws IllegalArgumentException if value is not positive
   */
  public void removeFavors(int value) {
    if (value <= 0) {
      throw new IllegalArgumentException("Removed favors must be positive.");
    }
    favors = Math.max(0, favors - value);
  }

  /**
   * Checks if the current favors count is enough for a given value.
   *
   * @param value favors amount to check against
   * @return true if current favors is greater or equal to value, false otherwise
   */
  public boolean hasEnoughFavors(int value) {
    return favors >= value;
  }
}
