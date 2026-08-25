package fr.moussax.blightedMC.shared.ui.sign;

import fr.moussax.blightedMC.BlightedMC;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
        location.setY(location.getY() + 3);
        player.sendBlockChange(location, Material.PALE_OAK_SIGN.createBlockData());

        String[] safeLines = new String[]{"", "", "", ""};
        for (int i = 0; i < Math.min(4, lines.length); i++) {
            safeLines[i] = lines[i] != null ? lines[i] : "";
        }
        player.sendSignChange(location, safeLines);

        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
            if (!player.isOnline()) {
                return;
            }
            nmsPlayer.connection.send(new ClientboundOpenSignEditorPacket(blockPosition, frontSide));
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
