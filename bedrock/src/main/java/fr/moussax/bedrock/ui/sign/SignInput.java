package fr.moussax.bedrock.ui.sign;

import fr.moussax.bedrock.scheduling.PluginContext;
import fr.moussax.bedrock.ui.menu.Menu;
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
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Client-side virtual sign prompt for collecting player text input.
 *
 * <p>Displays a sign editor client-side without altering the server world, restores the original block
 * upon submission, and manages input callbacks and cancellation handling.</p>
 */
public final class SignInput {

    private static final String DEFAULT_POINTER = "^^^^^^^^^^^^^^^";
    private static final Set<Plugin> ACTIVE_PLUGINS = ConcurrentHashMap.newKeySet();
    private static volatile SignInputListener listenerInstance;

    private final String[] lines;
    private final int inputLine;
    private final boolean frontSide;
    private final BiConsumer<Player, String> onSubmit;
    private final Consumer<Player> onCancel;
    private final Consumer<SignInputResult> onComplete;

    SignInput(
            String[] lines,
            int inputLine,
            boolean frontSide,
            BiConsumer<Player, String> onSubmit,
            Consumer<Player> onCancel,
            Consumer<SignInputResult> onComplete
    ) {
        this.lines = lines;
        this.inputLine = inputLine;
        this.frontSide = frontSide;
        this.onSubmit = onSubmit;
        this.onCancel = onCancel;
        this.onComplete = onComplete;
    }

    /**
     * Initializes sign input packet interception for an owning plugin and injects online players.
     *
     * @param plugin owning plugin instance
     */
    public static synchronized void initialize(@NonNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        ACTIVE_PLUGINS.add(plugin);

        if (listenerInstance == null) {
            listenerInstance = new SignInputListener();
            Bukkit.getPluginManager().registerEvents(listenerInstance, plugin);
            for (Player player : Bukkit.getOnlinePlayers()) {
                listenerInstance.inject(player);
            }
        }
    }

    /**
     * Unregisters an owning plugin. Uninjects listeners when all owning plugins have stopped.
     *
     * @param plugin owning plugin instance
     */
    public static synchronized void cleanup(@NonNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        ACTIVE_PLUGINS.remove(plugin);

        if (ACTIVE_PLUGINS.isEmpty() && listenerInstance != null) {
            listenerInstance.cleanup();
            listenerInstance = null;
        }
    }

    /**
     * Starts building a custom sign input prompt.
     *
     * @return a new sign input builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Opens a standard search sign input prompt for a player and returns to the parent menu on cancellation.
     *
     * @param player     target player
     * @param parentMenu menu to reopen if search is cancelled or empty
     * @param onSearch   callback invoked with the non-empty search query
     */
    public static void search(
            @NonNull Player player,
            @Nullable Menu parentMenu,
            @NonNull BiConsumer<Player, String> onSearch
    ) {
        search(player, parentMenu, "Enter your", "search!", onSearch);
    }

    /**
     * Opens a search sign input prompt with custom instruction lines.
     *
     * @param player            target player
     * @param parentMenu        menu to reopen if a search is canceled or empty
     * @param firstInstruction  first instruction line
     * @param secondInstruction second instruction line
     * @param onSearch          callback invoked with the non-empty search query
     */
    public static void search(
            @NonNull Player player,
            @Nullable Menu parentMenu,
            @NonNull String firstInstruction,
            @NonNull String secondInstruction,
            @NonNull BiConsumer<Player, String> onSearch
    ) {
        builder()
                .prompt(firstInstruction, secondInstruction)
                .reopenOnCancel(parentMenu)
                .onSubmit(onSearch)
                .open(player);
    }

    /**
     * Opens a generic sign input prompt with specified instruction lines.
     *
     * @param player            target player
     * @param firstInstruction  first instruction line
     * @param secondInstruction second instruction line
     * @param onSubmit          callback invoked when non-empty input is submitted
     */
    public static void prompt(
            @NonNull Player player,
            @NonNull String firstInstruction,
            @NonNull String secondInstruction,
            @NonNull BiConsumer<Player, String> onSubmit
    ) {
        builder()
                .prompt(firstInstruction, secondInstruction)
                .onSubmit(onSubmit)
                .open(player);
    }

    /**
     * Returns the configured initial lines for this sign input.
     *
     * @return copy of the initial sign lines
     */
    public String[] lines() {
        return lines.clone();
    }

    /**
     * Returns the line index configured to collect user input.
     *
     * @return input line index
     */
    public int inputLine() {
        return inputLine;
    }

    /**
     * Opens the sign editor for a player.
     *
     * <p>A client-side fake sign is dispatched above the player without altering the server world.</p>
     *
     * @param player player entering text
     */
    public void open(@NonNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        if (listenerInstance == null) {
            initialize(PluginContext.get());
        }

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
     * Invokes completion callbacks when input is submitted.
     *
     * @param player         player who submitted input
     * @param submittedLines submitted sign lines
     */
    void handleComplete(@NonNull Player player, String @NonNull [] submittedLines) {
        if (onComplete != null) {
            onComplete.accept(new SignInputResult(player, submittedLines));
        }

        if (onSubmit != null || onCancel != null) {
            String rawInput = (inputLine >= 0 && inputLine < submittedLines.length && submittedLines[inputLine] != null)
                    ? submittedLines[inputLine].trim()
                    : "";

            if (!rawInput.isEmpty()) {
                if (onSubmit != null) {
                    onSubmit.accept(player, rawInput);
                }
            } else {
                if (onCancel != null) {
                    onCancel.accept(player);
                }
            }
        }
    }

    /**
     * Fluent builder for configuring {@link SignInput} instances.
     */
    public static final class Builder {
        private String[] lines = new String[]{"", "", "", ""};
        private int inputLine = 0;
        private boolean frontSide = true;
        private BiConsumer<Player, String> onSubmit;
        private Consumer<Player> onCancel;
        private Consumer<SignInputResult> onComplete;

        private Builder() {
        }

        /**
         * Sets all 4 sign lines explicitly for full visual customization.
         *
         * @param lines sign lines (up to 4)
         * @return this builder
         */
        @NonNull
        public Builder lines(@NonNull String... lines) {
            Objects.requireNonNull(lines, "lines cannot be null");
            this.lines = new String[]{"", "", "", ""};
            System.arraycopy(lines, 0, this.lines, 0, Math.min(lines.length, 4));
            return this;
        }

        /**
         * Configures a standard Hypixel-style prompt layout.
         *
         * <p>Places the input on Line 0 (where client cursor starts), a caret pointer ({@code ^^^^^^^^^^^^^^^})
         * on Line 1, and optional instruction text on Lines 2 and 3.</p>
         *
         * @param instructions instruction lines displayed on Lines 2 and 3
         * @return this builder
         */
        @NonNull
        public Builder prompt(@NonNull String... instructions) {
            Objects.requireNonNull(instructions, "instructions cannot be null");
            this.inputLine = 0;
            this.lines = new String[]{"", "", "", ""};
            this.lines[0] = "";
            this.lines[1] = DEFAULT_POINTER;
            this.lines[2] = instructions.length > 0 ? instructions[0] : "";
            this.lines[3] = instructions.length > 1 ? instructions[1] : "";
            return this;
        }

        /**
         * Sets the line index expected to contain user input. Defaults to 0.
         *
         * @param index line index between 0 and 3
         * @return this builder
         */
        @NonNull
        public Builder inputLine(int index) {
            if (index < 0 || index > 3) {
                throw new IllegalArgumentException("inputLine must be between 0 and 3, got: " + index);
            }
            this.inputLine = index;
            return this;
        }

        /**
         * Configures whether the front or back side of the sign is opened.
         *
         * @param frontSide {@code true} for front side, {@code false} for back side
         * @return this builder
         */
        @NonNull
        public Builder frontSide(boolean frontSide) {
            this.frontSide = frontSide;
            return this;
        }

        /**
         * Configures a callback invoked when non-empty input is submitted.
         * The string received is automatically trimmed.
         *
         * @param onSubmit callback receiving player and trimmed input
         * @return this builder
         */
        @NonNull
        public Builder onSubmit(@NonNull BiConsumer<Player, String> onSubmit) {
            this.onSubmit = Objects.requireNonNull(onSubmit, "onSubmit cannot be null");
            return this;
        }

        /**
         * Configures a callback invoked when input is cancelled (e.g. submitted empty or aborted).
         *
         * @param onCancel cancellation callback
         * @return this builder
         */
        @NonNull
        public Builder onCancel(@NonNull Consumer<Player> onCancel) {
            this.onCancel = Objects.requireNonNull(onCancel, "onCancel cannot be null");
            return this;
        }

        /**
         * Reopens the specified menu if input is cancelled or submitted empty.
         *
         * @param menu menu to reopen, or closes inventory if {@code null}
         * @return this builder
         */
        @NonNull
        public Builder reopenOnCancel(@Nullable Menu menu) {
            this.onCancel = player -> {
                if (menu != null) {
                    menu.open(player);
                } else {
                    player.closeInventory();
                }
            };
            return this;
        }

        /**
         * Configures a low-level completion callback receiving raw lines and player.
         *
         * @param onComplete raw completion callback
         * @return this builder
         */
        @NonNull
        public Builder onComplete(@NonNull Consumer<SignInputResult> onComplete) {
            this.onComplete = Objects.requireNonNull(onComplete, "onComplete cannot be null");
            return this;
        }

        /**
         * Builds and opens the sign input prompt for the player.
         *
         * @param player player to prompt
         */
        public void open(@NonNull Player player) {
            build().open(player);
        }

        /**
         * Constructs the immutable {@link SignInput} instance.
         *
         * @return configured sign input instance
         */
        @NonNull
        public SignInput build() {
            return new SignInput(lines.clone(), inputLine, frontSide, onSubmit, onCancel, onComplete);
        }
    }
}
