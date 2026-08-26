package fr.moussax.blightedMC.shared.text;

import fr.moussax.blightedMC.utils.ColorUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Fluent builder for interactive chat messages.
 */
public final class InteractiveMessage {
    private final TextComponent root = new TextComponent("");

    public InteractiveMessage(String initialText) {
        root.addExtra(new TextComponent(ColorUtils.colorize(initialText)));
    }

    /**
     * Creates a new interactive chat message builder with automatic color processing.
     *
     * @param initialText the initial text component
     * @return a new interactive message builder
     */
    public static InteractiveMessage text(@NonNull String initialText) {
        return new InteractiveMessage(initialText);
    }

    /**
     * Appends plain text to the message.
     *
     * @param text the text to append
     * @return this builder
     */
    public InteractiveMessage append(@NonNull String text) {
        root.addExtra(new TextComponent(ColorUtils.colorize(text)));
        return this;
    }

    /**
     * Appends text with a hover tooltip.
     *
     * @param text      the visible text
     * @param hoverText the text displayed on hover
     * @return this builder
     */
    public InteractiveMessage hover(@NonNull String text, @NonNull String hoverText) {
        return apply(text, hoverText, null, null);
    }

    /**
     * Appends text with a hover tooltip and command suggestion.
     *
     * @param text      the visible text
     * @param hoverText the text displayed on hover
     * @param input     the command to suggest
     * @return this builder
     */
    public InteractiveMessage hoverAndSuggest(@NonNull String text, @NonNull String hoverText, @NonNull String input) {
        return apply(text, hoverText, ClickEvent.Action.SUGGEST_COMMAND, input);
    }

    /**
     * Appends text with a hover tooltip and command execution.
     *
     * @param text      the visible text
     * @param hoverText the text displayed on hover
     * @param command   the command to execute
     * @return this builder
     */
    public InteractiveMessage hoverAndExecute(@NonNull String text, @NonNull String hoverText, @NonNull String command) {
        return apply(text, hoverText, ClickEvent.Action.RUN_COMMAND, command);
    }

    /**
     * Appends text with a hover tooltip and URL action.
     *
     * @param text      the visible text
     * @param hoverText the text displayed on hover
     * @param url       the URL to open
     * @return this builder
     */
    public InteractiveMessage hoverAndOpenUrl(@NonNull String text, @NonNull String hoverText, @NonNull String url) {
        return apply(text, hoverText, ClickEvent.Action.OPEN_URL, url);
    }

    /**
     * Appends text with a hover tooltip and custom click action.
     *
     * @param text      the visible text
     * @param hoverText the text displayed on hover
     * @param action    the click action
     * @param value     the value associated with the click action
     * @return this builder
     */
    public InteractiveMessage hoverAndClick(@NonNull String text, @NonNull String hoverText, ClickEvent.@NonNull Action action, @NonNull String value) {
        return apply(text, hoverText, action, value);
    }

    private InteractiveMessage apply(String text, String hoverText, ClickEvent.@Nullable Action action, @Nullable String value) {
        TextComponent component = new TextComponent(ColorUtils.colorize(text));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorUtils.colorize(hoverText))));
        if (action != null && value != null) {
            component.setClickEvent(new ClickEvent(action, value));
        }
        root.addExtra(component);
        return this;
    }

    /**
     * Sends the message to a player.
     *
     * @param player the recipient
     */
    public void send(@NonNull Player player) {
        player.spigot().sendMessage(root);
    }

    /**
     * Builds the message as Bungee chat components.
     *
     * @return the built chat components
     */
    public BaseComponent[] build() {
        return new BaseComponent[]{root};
    }
}
