package fr.moussax.blightedMC.gameplay.abilities;

import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HomodeusFlightAbility implements FullSetBonus {
  private BlightedPlayer player;
  private boolean isActive = false;

  @Override
  public void startAbilityEffect() {
    if (isActive) return;

    Player bukkitPlayer = player.getPlayer();
    if (bukkitPlayer == null) return;

    if (bukkitPlayer.getGameMode() == GameMode.SURVIVAL) {
      bukkitPlayer.setAllowFlight(true);
      bukkitPlayer.setFlying(true);
      bukkitPlayer.sendMessage("§8 ■ §7Ascension mode initiated. §d(Homodeus)");
      bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1.5f);
    }

    isActive = true;
  }

  @Override
  public void stopAbilityEffect() {
    if (!isActive) return;

    Player bukkitPlayer = player.getPlayer();
    if (bukkitPlayer == null) return;

    bukkitPlayer.setAllowFlight(false);
    bukkitPlayer.setFlying(false);
    bukkitPlayer.sendMessage("§8 ■ §7Ascension mode terminated.");
    isActive = false;
  }

  @Override
  public int getPieces() {
    return 4;
  }

  @Override
  public int getMaxPieces() {
    return 4;
  }

  @Override
  public void setPlayer(BlightedPlayer player) {
    this.player = player;
  }

  @Override
  public BlightedPlayer getAbilityOwner() { return this.player; }
}
