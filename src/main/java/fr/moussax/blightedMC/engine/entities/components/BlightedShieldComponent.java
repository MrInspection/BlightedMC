package fr.moussax.blightedMC.engine.entities.components;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class BlightedShieldComponent implements EntityComponent {
    private final double arcDegrees;
    private long disableUntil = 0;

    public BlightedShieldComponent(double arcDegrees) {
        this.arcDegrees = arcDegrees;
    }

    @Override
    public String getId() {
        return "BLIGHTED_SHIELD";
    }

    public boolean isAttackBlocked(Entity defender, Entity attacker) {
        if (System.currentTimeMillis() < disableUntil) return false;

        Location defenderLocation = defender.getLocation();
        Location attackerLocation = attacker.getLocation();

        Vector defenderDirection = defenderLocation.getDirection();
        Vector attackerDirection = attackerLocation.toVector()
                .subtract(defenderLocation.toVector()).normalize();

        double dotProduct = defenderDirection.dot(attackerDirection);
        double angle = Math.toDegrees(Math.acos(dotProduct));
        return angle <= (arcDegrees / 2.0);
    }

    public void disable(long durationMillis) {
        this.disableUntil = System.currentTimeMillis() + durationMillis;
    }
}
