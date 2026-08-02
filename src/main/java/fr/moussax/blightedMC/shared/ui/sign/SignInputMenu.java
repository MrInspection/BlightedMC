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

public final class SignInputMenu {
    private final String[] lines;
    private final Consumer<SignInputResult> onComplete;
    private final boolean frontSide;

    public SignInputMenu(String[] lines, Consumer<SignInputResult> onComplete, boolean frontSide) {
        this.lines = lines;
        this.onComplete = onComplete;
        this.frontSide = frontSide;
    }

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
            if (!player.isOnline()) return;

            nmsPlayer.connection.send(new ClientboundOpenSignEditorPacket(blockPosition, frontSide));
            SignInputManager.register(player.getUniqueId(), this, blockPosition);
        }, 2L);
    }

    void handleComplete(@NonNull Player player, @NonNull String[] lines) {
        if (onComplete != null) onComplete.accept(new SignInputResult(player, lines));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String[] lines = new String[]{"", "", "", ""};
        private Consumer<SignInputResult> onComplete;
        private boolean frontSide = true;

        public Builder lines(@NonNull String... lines) {
            this.lines = new String[]{"", "", "", ""};
            System.arraycopy(lines, 0, this.lines, 0, Math.min(lines.length, 4));
            return this;
        }

        public Builder onComplete(@NonNull Consumer<SignInputResult> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public void open(@NonNull Player player) {
            new SignInputMenu(lines, onComplete, frontSide).open(player);
        }
    }
}
