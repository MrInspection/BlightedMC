package fr.moussax.blightedMC.content.items.abilities.tools;

import fr.moussax.blightedMC.engine.items.abilities.AbilityManager;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.event.block.BlockBreakEvent;

public class HammerAbility implements AbilityManager<BlockBreakEvent> {
    @Override
    public boolean triggerAbility(BlockBreakEvent event) {
        return false;
    }

    @Override
    public int getCooldownSeconds() {
        return 0;
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public boolean canTrigger(BlightedPlayer player) {
        return false;
    }

    @Override
    public void start(BlightedPlayer player) {

    }

    @Override
    public void stop(BlightedPlayer player) {

    }
}
