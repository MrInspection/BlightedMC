package fr.moussax.blightedSMP.registry;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.commands.CommandRegistrar;
import fr.moussax.blightedSMP.commands.impl.*;
import fr.moussax.blightedSMP.commands.utils.TabSuggestionRegistry;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Central registry for all plugin commands.
 *
 * <p>Registers player-facing, administrative, and utility commands with the
 * plugin's {@link CommandRegistrar}. It also initializes the shared tab
 * completion suggestions used by registered commands.</p>
 */
public final class CommandsRegistry {

    private CommandsRegistry() {
    }

    /**
     * Registers all plugin commands and their associated tab completion suggestions.
     *
     * @param plugin plugin instance used by the command registrar
     */
    public static void register(BlightedSMP plugin) {
        TabSuggestionRegistry suggestions = createSuggestionRegistry();
        CommandRegistrar commands = new CommandRegistrar(plugin, suggestions);

        commands.register("altar", new AltarCommand());
        commands.register("craft", new CraftCommand());
        commands.register("forge", new ForgeCommand());

        // Administrator Commands
        commands.register("spawncustommob", new SpawnCustomMobCommand());
        commands.register("giveitem", new GiveItemCommand());
        commands.register("gems", new GemsCommand());
        commands.register("tppos", new TeleportPositionCommand());
        commands.register("invsee", new InvseeCommand());
        commands.register("endersee", new EnderseeCommand());

        // Administrator Utilities
        GamemodeCommands gamemodeCommands = new GamemodeCommands();
        commands.register("gmc", gamemodeCommands);
        commands.register("gms", gamemodeCommands);
        commands.register("gma", gamemodeCommands);
        commands.register("gmspec", gamemodeCommands);
        commands.register("fly", new FlyCommand());
        commands.register("god", new GodCommand());
        commands.register("nightvision", new NightVisionCommand());
        commands.register("speed", new SpeedCommand());
        commands.register("butcher", new ButcherCommand());
        commands.register("loop", new LoopCommand());
        commands.register("test", new TestCommand());
    }

    /**
     * Creates and configures the shared tab completion suggestion registry.
     *
     * <p>The registry exposes dynamic suggestions for online players,
     * registered custom items, and registered custom entities.</p>
     *
     * @return configured tab suggestion registry
     */
    private static TabSuggestionRegistry createSuggestionRegistry() {
        TabSuggestionRegistry suggestions = new TabSuggestionRegistry();

        suggestions.register(
                "$players",
                () -> Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .toList()
        );

        suggestions.register(
                "$items",
                () -> ItemRegistry.getAllItems()
                        .stream()
                        .map(BlightedItem::getItemId)
                        .toList()
        );

        suggestions.register(
                "$entities",
                () -> EntitiesRegistry.getAll()
                        .stream()
                        .map(BlightedEntity::getEntityId)
                        .toList()
        );

        return suggestions;
    }
}
