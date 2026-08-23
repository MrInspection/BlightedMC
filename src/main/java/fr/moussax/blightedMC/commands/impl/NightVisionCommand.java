package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.moussax.blightedMC.utils.Formatter.*;

public final class NightVisionCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        toggleNightVision(player);
        return true;
    }

    private void toggleNightVision(Player player) {
        if (player.getActivePotionEffects().stream().anyMatch(effect -> effect.getType().equals(PotionEffectType.NIGHT_VISION))) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            inform(player," §fNight Vision §etoggled §cOFF§e.");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false));
            text(" §fNight Vision §etoggled §aON§e. ").hoverAndExecute("§4[§c➟ Disable§4]", "§eClick to disable your §6Night Vision §eeffect.", "/nv").send(player);
        }
    }
}
