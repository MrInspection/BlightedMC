package fr.moussax.blightedMC.smp.core.shared.ui.sign;

import fr.moussax.blightedMC.BlightedMC;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
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
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), (int) location.getY() + 3, location.getBlockZ());

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        nmsPlayer.g.b(new PacketPlayOutBlockChange(blockPosition, Blocks.di.m()));

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.a("id", "minecraft:sign");
        nbt.a("x", blockPosition.u());
        nbt.a("y", blockPosition.v());
        nbt.a("z", blockPosition.w());

        NBTTagCompound frontText = new NBTTagCompound();
        NBTTagList messages = new NBTTagList();

        for (int i = 0; i < 4; i++) {
            String line = (i < lines.length) ? lines[i] : "";
            messages.add(NBTTagString.a(line));
        }

        frontText.a("messages", messages);
        nbt.a("front_text", frontText);

        nmsPlayer.g.b(new PacketPlayOutTileEntityData(blockPosition, TileEntityTypes.h, nbt));

        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
            if (!player.isOnline()) return;
            nmsPlayer.g.b(new PacketPlayOutBlockChange(blockPosition, Blocks.di.m()));
            nmsPlayer.g.b(new PacketPlayOutOpenSignEditor(blockPosition, frontSide));
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
