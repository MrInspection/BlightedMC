package fr.moussax.bedrock.ui.sign;

import fr.moussax.bedrock.utils.debug.Log;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

/**
 * Listens for player connections and intercepts incoming sign update packets in the Netty channel pipeline.
 */
public final class SignInputListener implements Listener {
    private static final String HANDLER_NAME = "blighted_sign_input";
    private static final Field NETWORK_CONNECTION_FIELD;

    static {
        try {
            NETWORK_CONNECTION_FIELD = ServerCommonPacketListenerImpl.class.getDeclaredField("connection");
            NETWORK_CONNECTION_FIELD.setAccessible(true);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to locate NMS connection field via reflection", exception);
        }
    }

    /**
     * Constructs a sign input listener and injects packet interception for all currently online players.
     */
    public SignInputListener() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            inject(player);
        }
    }

    /**
     * Intercepts the player's network pipeline upon joining the server.
     *
     * @param event the join event containing the connecting player
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    /**
     * Cleans up packet interception and active sign input sessions when a player disconnects.
     *
     * @param event the quit event containing the disconnecting player
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        uninject(player);
        try {
            SignInputManager.removeSession(player.getUniqueId());
        } catch (NoClassDefFoundError _) {
        }
    }

    /**
     * Injects the sign update packet interceptor into a player's Netty channel pipeline.
     *
     * @param player player to inject
     */
    public void inject(Player player) {
        try {
            ChannelPipeline pipeline = getPipeline(player);
            if (pipeline == null || pipeline.get(HANDLER_NAME) != null) return;

            pipeline.addBefore("packet_handler", HANDLER_NAME, new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                    if (packet instanceof ServerboundSignUpdatePacket signPacket) {
                        if (SignInputManager.hasActiveSession(player.getUniqueId())) {
                            SignInputManager.handleSignUpdate(player, signPacket.lines().toArray(String[]::new));
                            return;
                        }
                    }
                    super.channelRead(context, packet);
                }
            });
        } catch (Exception exception) {
            Log.error("SignInputListener", "Failed to inject: " + exception.getMessage());
        }
    }

    /**
     * Removes the sign update packet interceptor from a player's channel pipeline.
     *
     * @param player player to uninject
     */
    public void uninject(Player player) {
        try {
            ChannelPipeline pipeline = getPipeline(player);
            if (pipeline != null && pipeline.get(HANDLER_NAME) != null) {
                pipeline.remove(HANDLER_NAME);
            }
        } catch (NoSuchElementException | IllegalArgumentException _) {
        } catch (Exception exception) {
            Log.error("SignInputListener", "Failed to uninject: " + exception.getMessage());
        }
    }

    private ChannelPipeline getPipeline(Player player) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            Connection connection = (Connection) NETWORK_CONNECTION_FIELD.get(nmsPlayer.connection);
            return connection.channel.pipeline();
        } catch (Exception _) {
            return null;
        }
    }

    /**
     * Uninjects packet interceptors from all currently online players and clears active sessions.
     */
    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            uninject(player);
            try {
                SignInputManager.removeSession(player.getUniqueId());
            } catch (NoClassDefFoundError _) {
            }
        }
    }
}
