package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.commands.CommandRegistrar;
import fr.moussax.blightedMC.commands.impl.*;
import fr.moussax.blightedMC.commands.utils.TabSuggestionRegistry;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CommandsRegistry {

    private CommandsRegistry() {
    }

    public static void register(BlightedMC plugin) {
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
