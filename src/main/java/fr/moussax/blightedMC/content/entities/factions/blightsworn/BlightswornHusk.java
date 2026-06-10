package fr.moussax.blightedMC.content.entities.factions.blightsworn;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class BlightswornHusk extends BlightswornBruteArchetype {
    BlightswornHusk() {
        super("BLIGHTSWORN_HUSK", "Blightsworn Husk", EntityType.HUSK);
    }

    @Override
    protected void applySurgeHitEffects(Player player) {

    }

    @Override
    protected void defineSpawnConditions() {

    }
}
