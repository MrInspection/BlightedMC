package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.players.BlightedPlayer;

public interface FullSetBonus {
  void startAbility();
  void stopAbility();

  int getPieces();
  int getMaxPieces();

  void setPlayer(BlightedPlayer player);
  Bonuses getBonus();

  default SetType type() {
    return SetType.NORMAL;
  }

  default FullSetBonus createNew(BlightedPlayer player) {
    try {
      FullSetBonus bonus = this.getClass().getDeclaredConstructor().newInstance();
      bonus.setPlayer(player);
      return bonus;
    } catch (Exception e) {
      throw new IllegalStateException("Cannot create new FullSetBonus instance", e);
    }
  }

  enum SetType {
    NORMAL,
    SNEAK
  }
}
