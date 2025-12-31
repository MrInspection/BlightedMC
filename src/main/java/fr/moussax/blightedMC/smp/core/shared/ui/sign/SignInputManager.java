package fr.moussax.blightedMC.smp.core.shared.ui.sign;

import fr.moussax.blightedMC.BlightedMC;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SignInputManager {
    private static final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private record Session(SignInputMenu menu, BlockPosition position) {
    }

    static void register(UUID id, SignInputMenu menu, BlockPosition position) {
        sessions.put(id, new Session(menu, position));
    }

    public static void handleSignUpdate(Player player, String[] lines) {
        Session session = sessions.remove(player.getUniqueId());
        if (session == null) return;

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        var actualState = nmsPlayer.A().a_(session.position);
        nmsPlayer.g.b(new PacketPlayOutBlockChange(session.position, actualState));

        Bukkit.getScheduler().runTask(BlightedMC.getInstance(), () ->
            session.menu().handleComplete(player, lines)
        );
    }

    public static boolean hasActiveSession(UUID id) {
        return sessions.containsKey(id);
    }

    public static void removeSession(UUID id) {
        sessions.remove(id);
    }
}
