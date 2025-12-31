package fr.moussax.blightedMC.smp.core.shared.ui.sign;

import fr.moussax.blightedMC.utils.debug.Log;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

public final class SignInputListener implements Listener {
    private static final String HANDLER_NAME = "blighted_sign_input";
    private static final Field NETWORK_MANAGER_FIELD;

    static {
        try {
            NETWORK_MANAGER_FIELD = Class.forName("net.minecraft.server.network.ServerCommonPacketListenerImpl").getDeclaredField("e");
            NETWORK_MANAGER_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("SIGN: Could not find NetworkManager field 'e'", e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        SignInputManager.removeSession(event.getPlayer().getUniqueId());
    }

    public void inject(Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        try {
            NetworkManager networkManager = (NetworkManager) NETWORK_MANAGER_FIELD.get(nmsPlayer.g);
            networkManager.k.pipeline().addBefore("packet_handler", HANDLER_NAME, new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                    if (packet instanceof PacketPlayInUpdateSign signPacket && SignInputManager.hasActiveSession(player.getUniqueId())) {
                        SignInputManager.handleSignUpdate(player, signPacket.f());
                        return;
                    }
                    super.channelRead(ctx, packet);
                }
            });
        } catch (Exception e) {
            Log.error("SignInputListener", e.getMessage());
        }
    }
}
