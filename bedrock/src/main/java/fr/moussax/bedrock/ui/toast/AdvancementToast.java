package fr.moussax.bedrock.ui.toast;

import fr.moussax.bedrock.scheduling.PluginContext;
import net.minecraft.advancements.*;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Sends custom advancement toast notifications to players.
 *
 * <p>Constructs and sends ephemeral packet-level advancements to render client-side
 * toast popups without registering persistent server advancements.</p>
 */
public final class AdvancementToast {

    private static final String TRIGGER = "toast_trigger";
    private static final long REMOVAL_DELAY_TICKS = 10L;

    private final NamespacedKey key;
    private final Material icon;
    private final String message;
    private final Type type;

    private AdvancementToast(@NonNull String message, @NonNull Material icon, @NonNull Type type) {
        this.key = new NamespacedKey(PluginContext.get(), UUID.randomUUID().toString());
        this.message = message;
        this.icon = icon.isItem() ? icon : Material.COMMAND_BLOCK;
        this.type = type;
    }

    /**
     * Displays a task toast to a player.
     *
     * @param player  player receiving the toast
     * @param message toast message text
     * @param icon    material icon displayed on the toast
     */
    public static void task(@NonNull Player player, @NonNull String message, @NonNull Material icon) {
        show(player, message, icon, Type.TASK);
    }

    /**
     * Displays a goal toast to a player.
     *
     * @param player  player receiving the toast
     * @param message toast message text
     * @param icon    material icon displayed on the toast
     */
    public static void goal(@NonNull Player player, @NonNull String message, @NonNull Material icon) {
        show(player, message, icon, Type.GOAL);
    }

    /**
     * Displays a challenge toast to a player.
     *
     * @param player  player receiving the toast
     * @param message toast message text
     * @param icon    material icon displayed on the toast
     */
    public static void challenge(@NonNull Player player, @NonNull String message, @NonNull Material icon) {
        show(player, message, icon, Type.CHALLENGE);
    }

    /**
     * Displays a toast with the specified {@link Type} to a player.
     *
     * @param player  player receiving the toast
     * @param message toast message text
     * @param icon    material icon displayed on the toast
     * @param type    toast display frame type
     */
    public static void show(@NonNull Player player, @NonNull String message, @NonNull Material icon, @NonNull Type type) {
        if (!player.isOnline()) return;
        new AdvancementToast(message, icon, type).display(player);
    }

    private void display(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        Identifier id = Identifier.fromNamespaceAndPath(
                key.getNamespace(),
                key.getKey()
        );

        AdvancementHolder holder = createAdvancement(id);
        AdvancementRequirements requirements = AdvancementRequirements.allOf(List.of(TRIGGER));
        AdvancementProgress progress = new AdvancementProgress();
        progress.update(requirements);
        progress.grantProgress(TRIGGER);

        nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                false,
                List.of(holder),
                Set.of(),
                Map.of(id, progress),
                true
        ));

        Bukkit.getScheduler().runTaskLater(PluginContext.get(), () -> {
                    if (!player.isOnline()) return;
                    nmsPlayer.connection.send(new ClientboundUpdateAdvancementsPacket(
                                    false,
                                    List.of(),
                                    Set.of(id),
                                    Map.of(),
                                    false
                            )
                    );
                },
                REMOVAL_DELAY_TICKS
        );
    }

    private AdvancementHolder createAdvancement(Identifier id) {
        Item item = CraftMagicNumbers.getItem(icon);
        ItemStackTemplate itemTemplate = new ItemStackTemplate(item);

        AdvancementType advancementType = switch (type) {
            case GOAL -> AdvancementType.GOAL;
            case TASK -> AdvancementType.TASK;
            case CHALLENGE -> AdvancementType.CHALLENGE;
        };

        DisplayInfo displayInfo = new DisplayInfo(
                itemTemplate,
                Component.literal(message),
                Component.empty(),
                Optional.empty(),
                advancementType,
                true,
                false,
                true
        );

        AdvancementRequirements requirements = AdvancementRequirements.allOf(List.of(TRIGGER));
        Advancement advancement = new Advancement(
                Optional.empty(),
                Optional.of(displayInfo),
                AdvancementRewards.EMPTY,
                Map.of(TRIGGER, new Criterion<>(null, null)),
                requirements,
                false
        );

        return new AdvancementHolder(id, advancement);
    }

    /**
     * Visual frame styles for advancement toast notifications.
     */
    public enum Type {
        TASK,
        GOAL,
        CHALLENGE
    }
}
