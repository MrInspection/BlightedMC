package fr.moussax.bedrock.ui.title;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Low-level packet dispatcher for title and subtitle packets.
 *
 * <p>Dispatches text-only packets without re-sending timing animation packets,
 * preventing Minecraft client opacity resets and visual flickering during in-place text updates.</p>
 */
public final class TitlePacketSender {

    /**
     * Delegate interface for custom packet handling or test assertions.
     */
    @FunctionalInterface
    public interface PacketHandler {
        void sendText(@NonNull Player player, @Nullable String title, @Nullable String subtitle);
    }

    private static volatile PacketHandler customHandler;

    private TitlePacketSender() {
    }

    /**
     * Overrides the text packet handler for testing or custom packet pipelines.
     *
     * @param handler custom packet handler, or null to revert to default NMS dispatch
     */
    public static void setCustomHandler(@Nullable PacketHandler handler) {
        customHandler = handler;
    }

    /**
     * Sends in-place title and subtitle text packets to a player without touching client animation timings.
     *
     * <p>On production Spigot servers, dispatches {@link ClientboundSetTitleTextPacket} and
     * {@link ClientboundSetSubtitleTextPacket} directly via the player's network connection.
     * This avoids sending {@link net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket},
     * which resets client fade-in opacity and causes flickering.</p>
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     */
    public static void sendTextOnly(@NonNull Player player, @Nullable String title, @Nullable String subtitle) {
        if (!player.isOnline()) {
            return;
        }

        PacketHandler handler = customHandler;
        if (handler != null) {
            handler.sendText(player, title, subtitle);
            return;
        }

        if (player instanceof CraftPlayer craftPlayer) {
            var handle = craftPlayer.getHandle();
            if (handle == null) {
                return;
            }
            var connection = handle.connection;
            if (title != null) {
                Component[] components = CraftChatMessage.fromString(title);
                Component component = (components != null && components.length > 0) ? components[0] : Component.empty();
                connection.send(new ClientboundSetTitleTextPacket(component));
            }
            if (subtitle != null) {
                Component[] components = CraftChatMessage.fromString(subtitle);
                Component component = (components != null && components.length > 0) ? components[0] : Component.empty();
                connection.send(new ClientboundSetSubtitleTextPacket(component));
            }
            return;
        }

        player.sendTitle(title != null ? title : "", subtitle != null ? subtitle : "", 0, 70, 0);
    }

    /**
     * Sends complete title display parameters including fade timings and text.
     *
     * @param player   target player
     * @param title    title text
     * @param subtitle subtitle text
     * @param fadeIn   fade-in ticks
     * @param stay     stay ticks
     * @param fadeOut  fade-out ticks
     */
    public static void sendFull(
            @NonNull Player player,
            @Nullable String title,
            @Nullable String subtitle,
            int fadeIn,
            int stay,
            int fadeOut
    ) {
        if (!player.isOnline()) {
            return;
        }
        player.sendTitle(title != null ? title : "", subtitle != null ? subtitle : "", fadeIn, stay, fadeOut);
    }
}
