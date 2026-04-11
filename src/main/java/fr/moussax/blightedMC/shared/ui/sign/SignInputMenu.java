package fr.moussax.blightedMC.shared.ui.sign;

import fr.moussax.blightedMC.BlightedMC;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        Location location = player.getLocation();
        BlockPos blockPosition = new BlockPos(location.getBlockX(), (int) location.getY() + 3, location.getBlockZ());

        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        nmsPlayer.connection.send(new ClientboundBlockUpdatePacket(blockPosition, Blocks.PALE_OAK_SIGN.defaultBlockState()));

        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", "minecraft:sign");
        nbt.putInt("x", blockPosition.getX());
        nbt.putInt("y", blockPosition.getY());
        nbt.putInt("z", blockPosition.getZ());

        CompoundTag frontText = new CompoundTag();
        ListTag messages = new ListTag();

        for (int i = 0; i < 4; i++) {
            String line = (i < lines.length) ? lines[i] : "";
            messages.add(StringTag.valueOf(line));
        }

        frontText.put("messages", messages);
        nbt.put("front_text", frontText);

        nmsPlayer.connection.send(new ClientboundBlockUpdatePacket(blockPosition, Blocks.PALE_OAK_SIGN.defaultBlockState()));

        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
            if (!player.isOnline()) return;
            nmsPlayer.connection.send(new ClientboundBlockUpdatePacket(blockPosition, Blocks.PALE_OAK_SIGN.defaultBlockState()));
            nmsPlayer.connection.send(new ClientboundOpenSignEditorPacket(blockPosition, frontSide));
            SignInputManager.register(player.getUniqueId(), this, blockPosition);
        }, 1L);
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
