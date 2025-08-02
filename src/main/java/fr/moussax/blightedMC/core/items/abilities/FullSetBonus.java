package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public interface FullSetBonus {
  void startAbility();
  void stopAbility();
  int getPieces();
  int getMaxPieces();
  void setPlayer(BlightedPlayer player);

  default SetType getType() { return SetType.NORMAL; }

  default FullSetBonus createNew(BlightedPlayer player) {
    try {
      FullSetBonus clone = this.getClass().getDeclaredConstructor().newInstance();
      clone.setPlayer(player);
      return clone;
    } catch (Exception e) {
      throw new IllegalStateException("Cannot instantiate FullSetBonus", e);
    }
  }

  enum SetType { NORMAL, SNEAK }
}

class SneakAbilityWrapper implements FullSetBonus {
  private final FullSetBonus delegate;
  private BlightedPlayer player;
  private BukkitRunnable task;

  public SneakAbilityWrapper(FullSetBonus delegate) { this.delegate = delegate; }

  @Override public void startAbility() {
    if (player.getPlayer().isSneaking()) delegate.startAbility();
    task = new BukkitRunnable() {
      private boolean sneaking = player.getPlayer().isSneaking();
      @Override
      public void run() {
        boolean nowSneaking = player.getPlayer().isSneaking();
        if (sneaking && !nowSneaking) delegate.stopAbility();
        else if (!sneaking && nowSneaking) delegate.startAbility();
        sneaking = nowSneaking;
      }
    };
    task.runTaskTimer(BlightedMC.getInstance(), 0, 1);
  }

  @Override public void stopAbility() { if (task != null) task.cancel(); }
  @Override public int getPieces() { return delegate.getPieces(); }
  @Override public int getMaxPieces() { return delegate.getMaxPieces(); }
  @Override public void setPlayer(BlightedPlayer player) { this.player = player; delegate.setPlayer(player); }
  @Override public FullSetBonus createNew(BlightedPlayer player) {
    SneakAbilityWrapper wrapper = new SneakAbilityWrapper(delegate);
    wrapper.setPlayer(player);
    return wrapper;
  }
  @Override public SetType getType() { return delegate.getType(); }
}