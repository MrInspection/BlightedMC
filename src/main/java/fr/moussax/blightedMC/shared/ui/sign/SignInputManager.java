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

/**
 * Manages active sign input sessions and their completion callbacks.
 *
 * <p>Tracks the temporary sign location used by each player and restores its
 * actual block state when input is submitted.</p>
 */
public final class SignInputManager {
    private static final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private record Session(SignInputMenu menu, BlockPos position) {
    }

    /**
     * Registers an active sign input session for a player.
     *
     * @param PlayerId player unique identifier
     * @param menu sign input menu handling the session
     * @param position temporary sign position
     */
    static void register(UUID PlayerId, SignInputMenu menu, BlockPos position) {
        sessions.put(PlayerId, new Session(menu, position));
    }

    /**
     * Handles sign input submitted by a player.
     *
     * <p>The temporary sign is restored before the completion callback is
     * invoked.</p>
     *
     * @param player player who submitted the input
     * @param lines submitted sign lines
     */
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

    /**
     * Checks whether a player has an active sign input session.
     *
     * @param PlayerId player unique identifier
     * @return {@code true} if an active session exists
     */
    public static boolean hasActiveSession(UUID PlayerId) {
        return sessions.containsKey(PlayerId);
    }

    /**
     * Removes the active sign input session for a player.
     *
     * @param PlayerId player unique identifier
     */
    public static void removeSession(UUID PlayerId) {
        sessions.remove(PlayerId);
    }
}
