package fr.moussax.blightedMC.shared.ui.sign;

import fr.moussax.blightedMC.utils.debug.Log;
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

public final class SignInputListener implements Listener {
    private static final String HANDLER_NAME = "blighted_sign_input";
    private static final Field NETWORK_CONNECTION_FIELD;

    static {
        try {
            NETWORK_CONNECTION_FIELD = ServerCommonPacketListenerImpl.class.getDeclaredField("connection");
            NETWORK_CONNECTION_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to locate NMS connection field via reflection", e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        uninject(player);
        try {
            SignInputManager.removeSession(player.getUniqueId());
        } catch (NoClassDefFoundError ignored) {
        }
    }

    public void inject(Player player) {
        try {
            ChannelPipeline pipeline = getPipeline(player);
            if (pipeline == null || pipeline.get(HANDLER_NAME) != null) return;

            pipeline.addBefore("packet_handler", HANDLER_NAME, new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                    if (packet instanceof ServerboundSignUpdatePacket signPacket) {
                        if (SignInputManager.hasActiveSession(player.getUniqueId())) {
                            SignInputManager.handleSignUpdate(player, signPacket.getLines());
                            return;
                        }
                    }
                    super.channelRead(ctx, packet);
                }
            });
        } catch (Exception e) {
            Log.error("SignInputListener", "Failed to inject: " + e.getMessage());
        }
    }

    public void uninject(Player player) {
        try {
            ChannelPipeline pipeline = getPipeline(player);
            if (pipeline != null && pipeline.get(HANDLER_NAME) != null) {
                pipeline.remove(HANDLER_NAME);
            }
        } catch (NoSuchElementException | IllegalArgumentException ignored) {
        } catch (Exception e) {
            Log.error("SignInputListener", "Failed to uninject: " + e.getMessage());
        }
    }

    private ChannelPipeline getPipeline(Player player) {
        try {
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            Connection connection = (Connection) NETWORK_CONNECTION_FIELD.get(nmsPlayer.connection);
            return connection.channel.pipeline();
        } catch (Exception e) {
            return null;
        }
    }

    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            uninject(player);
            try {
                SignInputManager.removeSession(player.getUniqueId());
            } catch (NoClassDefFoundError ignored) {
            }
        }
    }
}
