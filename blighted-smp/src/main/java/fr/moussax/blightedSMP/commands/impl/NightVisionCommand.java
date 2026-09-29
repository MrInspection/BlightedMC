package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.moussax.bedrock.text.Messenger.inform;

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
            inform(player," §fNight Vision §etoggled §aON§e.");
        }
    }
}
