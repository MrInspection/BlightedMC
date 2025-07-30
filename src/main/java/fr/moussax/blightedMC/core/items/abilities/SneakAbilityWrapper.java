package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class SneakAbilityWrapper implements FullSetBonus{
  private final FullSetBonus fullSetBonus;
  private BlightedPlayer player;
  private BukkitRunnable runnable;

  public SneakAbilityWrapper(FullSetBonus setBonus) {
    this.fullSetBonus = setBonus;
  }

  @Override
  public void startAbility() {
    if(player.getPlayer().isSneaking()) {
      fullSetBonus.startAbility();
    }

    runnable = new BukkitRunnable() {
      private boolean wasSneaking = player.getPlayer().isSneaking();

      @Override
      public void run() {
        if(wasSneaking && !player.getPlayer().isSneaking()) {
          fullSetBonus.stopAbility();
          wasSneaking = false;
          return;
        }

        if(!wasSneaking && player.getPlayer().isSneaking()) {
          fullSetBonus.startAbility();
          wasSneaking = true;
        }
      }
    };
    runnable.runTaskTimer(BlightedMC.getInstance(), 0, 1);
  }

  @Override
  public void stopAbility() {
    runnable.cancel();
  }

  @Override
  public int getPieces() {
    return fullSetBonus.getPieces();
  }

  @Override
  public int getMaxPieces() {
    return fullSetBonus.getMaxPieces();
  }

  @Override
  public void setPlayer(BlightedPlayer player) {
    this.player = player;
  }

  @Override
  public Bonuses getBonus() {
    return fullSetBonus.getBonus();
  }

  @Override
  public FullSetBonus createNew(BlightedPlayer player) {
    SneakAbilityWrapper wrapper = new SneakAbilityWrapper(fullSetBonus);
    wrapper.setPlayer(player);
    return wrapper;
  }
}
