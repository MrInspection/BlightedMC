package fr.moussax.blightedSMP.content.items.abilities;

import fr.moussax.blightedSMP.engine.items.abilities.AbstractFullSetBonus;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Full set bonus granting flight to players wearing full Homodeus armor.
 */
public class HomodeusFlightAbility extends AbstractFullSetBonus {

    private boolean isActive = false;

    @Override
    public String getName() {
        return "Homodeus";
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Ascend beyond mortal limits, harnessing",
                "divine technology to defy gravity and soar",
                "through the skies."
        };
    }

    @Override
    public void startAbilityEffect() {
        if (isActive) return;

        Player bukkitPlayer = getAbilityOwner().getPlayer();

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

        Player bukkitPlayer = getAbilityOwner().getPlayer();

        bukkitPlayer.setAllowFlight(false);
        bukkitPlayer.setFlying(false);
        bukkitPlayer.sendMessage("§8 ■ §7Ascension mode terminated.");
        isActive = false;
    }
}
