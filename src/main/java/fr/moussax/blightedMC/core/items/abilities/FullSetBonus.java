package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.players.BlightedPlayer;

public interface FullSetBonus {
  void startAbility();

  void stopAbility();

  int getPieces();

  int getMaxPieces();

  void setPlayer(BlightedPlayer player);

  Bonuses getBonus();

  default setType type() {
    return setType.NORMAL;
  }

  default FullSetBonus createNew(BlightedPlayer player) {
    FullSetBonus bonus;

    try {
      bonus = this.getClass().getDeclaredConstructor(BlightedPlayer.class).newInstance(player);
      bonus.setPlayer(player);
    } catch (Exception ignored) {
    }

    return null;
  }

  enum setType {
    NORMAL,
    SNEAK
  }
}
