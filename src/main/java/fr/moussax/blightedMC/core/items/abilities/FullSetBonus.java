package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public interface FullSetBonus {
  void startAbilityEffect();

  void stopAbilityEffect();

  int getPieces();

  int getMaxPieces();

  void setPlayer(BlightedPlayer player);

  default SetType getType() {
    return SetType.NORMAL;
  }

  default boolean hasListener() {
    return this instanceof Listener;
  }

  default void activate() {
    if (hasListener()) {
      Bukkit.getPluginManager().registerEvents((Listener) this, BlightedMC.getInstance());
    }
    startAbilityEffect();
  }

  default void deactivate() {
    stopAbilityEffect();
    if (hasListener()) {
      HandlerList.unregisterAll((Listener) this);
    }
  }

  default FullSetBonus createNew(BlightedPlayer player) {
    try {
      FullSetBonus clone = this.getClass().getDeclaredConstructor().newInstance();
      clone.setPlayer(player);
      return clone;
    } catch (Exception e) {
      throw new IllegalStateException("Cannot instantiate FullSetBonus", e);
    }
  }

  /**
   * Checks whether the given player is the owner of this ability.
   * <p>
   * Use this inside event handlers to ensure only the owning player triggers the ability.
   *
   * @param eventPlayer the player from the event
   * @return true if eventPlayer owns this ability
   */
  default boolean isAbilityOwner(Player eventPlayer) {
    BlightedPlayer owner = getAbilityOwner();
    return owner == null
      || owner.getPlayer() == null
      || !eventPlayer.getUniqueId().equals(owner.getPlayer().getUniqueId());
  }

  /**
   * Returns the owning {@link BlightedPlayer} of this ability.
   * <p>
   * Implement this in the ability class to allow {@link #isAbilityOwner(Player)} to work.
   *
   * @return owning BlightedPlayer
   */
  default BlightedPlayer getAbilityOwner() {
    return null;
  }

  enum SetType {NORMAL, SNEAK}
}

class SneakAbilityWrapper implements FullSetBonus {
  private final FullSetBonus delegate;
  private BlightedPlayer player;
  private BukkitRunnable task;

  public SneakAbilityWrapper(FullSetBonus delegate) {
    this.delegate = delegate;
  }

  @Override
  public void startAbilityEffect() {
    if (player.getPlayer().isSneaking()) delegate.activate();
    task = new BukkitRunnable() {
      private boolean sneaking = player.getPlayer().isSneaking();

      @Override
      public void run() {
        boolean nowSneaking = player.getPlayer().isSneaking();
        if (sneaking && !nowSneaking) delegate.deactivate();
        else if (!sneaking && nowSneaking) delegate.activate();
        sneaking = nowSneaking;
      }
    };
    task.runTaskTimer(BlightedMC.getInstance(), 0, 1);
  }

  @Override
  public void stopAbilityEffect() {
    if (task != null) {
      task.cancel();
      task = null;
    }
    delegate.deactivate();
  }

  @Override
  public int getPieces() {
    return delegate.getPieces();
  }

  @Override
  public int getMaxPieces() {
    return delegate.getMaxPieces();
  }

  @Override
  public void setPlayer(BlightedPlayer player) {
    this.player = player;
    delegate.setPlayer(player);
  }

  @Override
  public FullSetBonus createNew(BlightedPlayer player) {
    SneakAbilityWrapper wrapper = new SneakAbilityWrapper(delegate);
    wrapper.setPlayer(player);
    return wrapper;
  }

  @Override
  public SetType getType() {
    return delegate.getType();
  }
}
