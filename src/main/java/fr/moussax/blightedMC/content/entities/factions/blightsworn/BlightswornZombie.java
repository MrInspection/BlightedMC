package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class BlightswornZombie extends BlightswornBruteArchetype {

    BlightswornZombie() {
        super("BLIGHTSWORN_ZOMBIE", "Blightsworn Zombie", EntityType.ZOMBIE);
    }

    @Override
    protected void applySurgeHitEffects(Player player) {

    }

    @Override
    protected void defineSpawnConditions() {

    }
}
