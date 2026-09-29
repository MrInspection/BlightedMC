package fr.moussax.bedrock.ui.sign;

import fr.moussax.bedrock.scheduling.PluginContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * Represents a temporary sign editor used to collect player text input.
 *
 * <p>The sign is displayed client-side without modifying the server world and
 * is removed once the player submits their input.</p>
 */
public final class SignInputMenu {
    private final String[] lines;
    private final Consumer<SignInputResult> onComplete;
    private final boolean frontSide;

    /**
     * Creates a sign input menu.
     *
     * @param lines      initial sign lines
     * @param onComplete callback invoked when input is submitted
     * @param frontSide  whether to open the front side of the sign
     */
    public SignInputMenu(String[] lines, Consumer<SignInputResult> onComplete, boolean frontSide) {
        this.lines = lines;
        this.onComplete = onComplete;
        this.frontSide = frontSide;
    }

    /**
     * Opens the sign editor for a player.
     *
     * <p>The sign is displayed at a temporary client-side location above the
     * player and does not modify the server world.</p>
     *
     * @param player player entering text
     */
    public void open(@NonNull Player player) {
        Location location = player.getLocation().clone();
        location.setY(location.getY() + 1);

        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        BlockState blockState = Blocks.OAK_SIGN.defaultBlockState();
        nmsPlayer.connection.send(new ClientboundBlockUpdatePacket(blockPosition, blockState));

        SignBlockEntity signTile = new SignBlockEntity(blockPosition, blockState);
        signTile.setAllowedPlayerEditor(player.getUniqueId());
        signTile.setWaxed(false);

        SignTextSlot slot = frontSide ? SignTextSlot.FRONT : SignTextSlot.BACK;
        SignText.Mutable mutableText = SignText.EMPTY.asMutable();
        for (int i = 0; i < Math.min(4, lines.length); i++) {
            String lineText = lines[i] != null ? lines[i] : "";
            mutableText.setLine(i, Component.literal(lineText));
        }
        signTile.setText(mutableText.asImmutable(), slot);

        ClientboundBlockEntityDataPacket tilePacket = signTile.getUpdatePacket();
        nmsPlayer.connection.send(tilePacket);

        Bukkit.getScheduler().runTaskLater(PluginContext.get(), () -> {
            if (!player.isOnline()) {
                return;
            }
            nmsPlayer.connection.send(new ClientboundOpenSignEditorPacket(blockPosition, slot));
            SignInputManager.register(player.getUniqueId(), this, blockPosition);
        }, 2L);
    }

    /**
     * Completes the input session and invokes the configured callback.
     *
     * @param player player who submitted the input
     * @param lines  submitted sign lines
     */
    void handleComplete(@NonNull Player player, @NonNull String[] lines) {
        if (onComplete != null) onComplete.accept(new SignInputResult(player, lines));
    }

    /**
     * Creates a builder for configuring a sign input menu.
     *
     * @return new sign input builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds and opens a sign input menu.
     */
    public static class Builder {
        private String[] lines = new String[]{"", "", "", ""};
        private Consumer<SignInputResult> onComplete;
        private boolean isFrontSide = true;

        /**
         * Sets the initial text displayed on the sign.
         *
         * <p>Only the first four lines are used. Missing lines remain empty.</p>
         *
         * @param lines initial sign lines
         * @return this builder
         */
        public Builder lines(@NonNull String... lines) {
            this.lines = new String[]{"", "", "", ""};
            System.arraycopy(lines, 0, this.lines, 0, Math.min(lines.length, 4));
            return this;
        }

        /**
         * Sets the callback invoked when the player submits the sign.
         *
         * @param onComplete input completion callback
         * @return this builder
         */
        public Builder onComplete(@NonNull Consumer<SignInputResult> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        /**
         * Builds and opens the configured sign input menu.
         *
         * @param player player entering text
         */
        public void open(@NonNull Player player) {
            new SignInputMenu(lines, onComplete, isFrontSide).open(player);
        }
    }
}
