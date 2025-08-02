package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.players.BlightedPlayer;

public enum Bonuses {

  ;

  private final FullSetBonus setBonus;
  Bonuses(FullSetBonus bonus) {
    this.setBonus = bonus;
  }

  public FullSetBonus getBonus(BlightedPlayer player) {
    if(setBonus.getType() == FullSetBonus.SetType.SNEAK) {
      SneakAbilityWrapper wrapper = new SneakAbilityWrapper(setBonus);
      wrapper.setPlayer(player);
      return wrapper;
    }
    return setBonus.createNew(player);
  }

  public boolean isSneak(){
    return setBonus.getType() == FullSetBonus.SetType.SNEAK;
  }
}
