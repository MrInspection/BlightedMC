package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.commands.*;
import fr.moussax.blightedMC.commands.admin.*;
import fr.moussax.blightedMC.commands.admin.TpAllCommand;
import fr.moussax.blightedMC.commands.admin.TpPosCommand;
import fr.moussax.blightedMC.commands.moderator.*;
import fr.moussax.blightedMC.commands.testing.TestCommand;
import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.utils.commands.CommandBuilder;
import fr.moussax.blightedMC.utils.commands.TabSuggestionRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Registry class responsible for registering all commands used in the plugin.
 * Commands are registered via {@link CommandBuilder}.
 * This class cannot be instantiated.
 */
public final class CommandsRegistry {
    private CommandsRegistry() {
    }

    public static void register() {
        registerTabSuggestions();

        CommandBuilder.register("altar", new AltarCommand());
        CommandBuilder.register("craft", new CraftCommand());
        CommandBuilder.register("forge", new ForgeCommand());
        CommandBuilder.register("test", new TestCommand());

        // Admin Commands
        CommandBuilder.register("broadcast", new BroadcastCommand());
        CommandBuilder.register("spawncustommob", SpawnCustomMobCommand.class);
        CommandBuilder.register("gems", GemsCommand.class);
        CommandBuilder.register("forcecommand", new ForceCommand());
        CommandBuilder.register("giveitem", GiveItemCommand.class);
        CommandBuilder.register("god", new GodCommand());
        CommandBuilder.register("kaboom", new KaboomCommand());
        CommandBuilder.register("loop", new LoopCommand());
        CommandBuilder.register("tpall", new TpAllCommand());
        CommandBuilder.register("tppos", TpPosCommand.class);
        CommandBuilder.register("fly", new FlyCommand());

        // Moderator Commands
        CommandBuilder.register("mod", new ModCommand());
        CommandBuilder.register("mtp", new ModTPCommands());
        CommandBuilder.register("mtphere", new ModTPCommands());
        CommandBuilder.register("endersee", new EnderSeeCommand());
        CommandBuilder.register("invsee", new InvSeeCommand());
        CommandBuilder.register("ban", new BanCommand());
        CommandBuilder.register("banip", new BanCommand());
        CommandBuilder.register("unban", new BanCommand());
        CommandBuilder.register("unbanip", new BanCommand());
        CommandBuilder.register("vanish", new VanishCommand());
        CommandBuilder.register("mute", new MuteCommand());
        CommandBuilder.register("unmute", new MuteCommand());
        CommandBuilder.register("kick", new KickCommand());
    }

    private static void registerTabSuggestions() {
        TabSuggestionRegistry.register("$players", () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        TabSuggestionRegistry.register("$items", () -> ItemRegistry.getAllItems().stream().map(BlightedItem::getItemId).toList());
        TabSuggestionRegistry.register("$entities", () -> EntitiesRegistry.getAll().stream().map(AbstractBlightedEntity::getEntityId).toList());
    }
}
