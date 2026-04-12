package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.commands.*;
import fr.moussax.blightedMC.commands.TeleportPositionCommand;
import fr.moussax.blightedMC.commands.TestCommand;
import fr.moussax.blightedMC.commands.see.EnderSeeCommand;
import fr.moussax.blightedMC.commands.see.InvSeeCommand;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import fr.moussax.blightedMC.utils.commands.CommandBuilder;
import fr.moussax.blightedMC.utils.commands.TabSuggestionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CommandsRegistry {
    private CommandsRegistry() {
    }

    public static void register() {
        registerTabSuggestions();

        CommandBuilder.register("altar", new AltarCommand());
        CommandBuilder.register("craft", new CraftCommand());
        CommandBuilder.register("forge", new ForgeCommand());
        CommandBuilder.register("test", new TestCommand());

        // Administrator Commands
        CommandBuilder.register("spawncustommob", SpawnCustomMobCommand.class);
        CommandBuilder.register("gems", GemsCommand.class);
        CommandBuilder.register("giveitem", GiveItemCommand.class);
        CommandBuilder.register("loop", new LoopCommand());
        CommandBuilder.register("tppos", TeleportPositionCommand.class);
        CommandBuilder.register("fly", new FlyCommand());
        CommandBuilder.register("endersee", new EnderSeeCommand());
        CommandBuilder.register("invsee", new InvSeeCommand());
    }

    private static void registerTabSuggestions() {
        TabSuggestionRegistry.register("$players", () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        TabSuggestionRegistry.register("$items", () -> ItemRegistry.getAllItems().stream().map(BlightedItem::getItemId).toList());
        TabSuggestionRegistry.register("$entities", () -> EntitiesRegistry.getAll().stream().map(BlightedEntity::getEntityId).toList());
    }
}
