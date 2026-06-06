package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class VoidStrikeAffix implements EntityComponent {

    private final Map<UUID, Long> markedPlayers = new HashMap<>();
    private static final long MARK_DURATION_MS = 5000L;
    private static final double MAX_MULTIPLIER = 3.0;

    @Override
    public String getId() {
        return "AFFIX_VOID_STRIKE";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {
        markedPlayers.clear();
    }

    @Override
    public void onTick(BlightedEntity owner) {
        if (markedPlayers.isEmpty()) return;

        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, Long>> iterator = markedPlayers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            Player player = owner.getEntity().getServer().getPlayer(entry.getKey());

            if (player == null || !player.isOnline() || player.isDead() || now - entry.getValue() > MARK_DURATION_MS) {
                iterator.remove();
                continue;
            }

            player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0, 1, 0), 2, 0.2, 0.4, 0.2, 0.02);
        }
    }

    @Override
    public void onDealDamage(BlightedEntity owner, EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (markedPlayers.containsKey(playerId)) {
            long timeApplied = markedPlayers.get(playerId);
            double secondsElapsed = (now - timeApplied) / 1000.0;

            double multiplier = 1.0 + Math.min(MAX_MULTIPLIER - 1.0, secondsElapsed * 0.4);
            event.setDamage(event.getDamage() * multiplier);

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0.2);
        }

        markedPlayers.put(playerId, now);
    }
}
