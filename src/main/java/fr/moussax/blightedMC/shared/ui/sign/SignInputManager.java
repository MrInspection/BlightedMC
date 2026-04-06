package fr.moussax.blightedMC.shared.ui.sign;

import fr.moussax.blightedMC.BlightedMC;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SignInputManager {
    private static final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private record Session(SignInputMenu menu, BlockPos position) {
    }

    static void register(UUID id, SignInputMenu menu, BlockPos position) {
        sessions.put(id, new Session(menu, position));
    }

    public static void handleSignUpdate(Player player, String[] lines) {
        Session session = sessions.remove(player.getUniqueId());
        if (session == null) return;

        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        var actualState = nmsPlayer.level().getBlockState(session.position);
        nmsPlayer.connection.send(new ClientboundBlockUpdatePacket(session.position, actualState));

        Bukkit.getScheduler().runTask(BlightedMC.getInstance(), () -> {
                if (player.isOnline()) {
                    session.menu().handleComplete(player, lines);
                }
            }
        );
    }

    public static boolean hasActiveSession(UUID id) {
        return sessions.containsKey(id);
    }

    public static void removeSession(UUID id) {
        sessions.remove(id);
    }
}
