package fr.moussax.blightedMod.registry;

import fr.moussax.bedrock.commands.CommandRegistrar;
import fr.moussax.bedrock.commands.TabSuggestionRegistry;
import fr.moussax.blightedMod.commands.impl.BanCommand;
import fr.moussax.blightedMod.commands.impl.CheckReportCommand;
import fr.moussax.blightedMod.commands.impl.EnderseeCommand;
import fr.moussax.blightedMod.commands.impl.FreezeCommand;
import fr.moussax.blightedMod.commands.impl.InvseeCommand;
import fr.moussax.blightedMod.commands.impl.KickCommand;
import fr.moussax.blightedMod.commands.impl.ModCommand;
import fr.moussax.blightedMod.commands.impl.ModTpCommands;
import fr.moussax.blightedMod.commands.impl.MuteCommand;
import fr.moussax.blightedMod.commands.impl.ReportCommand;
import fr.moussax.blightedMod.commands.impl.ReportsCommand;
import fr.moussax.blightedMod.commands.impl.SanctionsCommand;
import fr.moussax.blightedMod.commands.impl.SetSlowmodeCommand;
import fr.moussax.blightedMod.commands.impl.SocialSpyCommand;
import fr.moussax.blightedMod.commands.impl.TargetCommand;
import fr.moussax.blightedMod.commands.impl.TestModCommand;
import fr.moussax.blightedMod.commands.impl.UserInfoCommand;
import fr.moussax.blightedMod.commands.impl.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandsRegistry {

    private CommandsRegistry() {
    }

    public static void registerCommands(JavaPlugin plugin) {
        TabSuggestionRegistry suggestions = createSuggestionRegistry();
        CommandRegistrar registrar = new CommandRegistrar(plugin, suggestions);

        ModTpCommands teleportCommands = new ModTpCommands();
        registrar.register("mtp", teleportCommands);
        registrar.register("mtphere", teleportCommands);
        registrar.register("vanish", new VanishCommand());

        MuteCommand muteCommand = new MuteCommand();
        registrar.register("mute", muteCommand);
        registrar.register("unmute", muteCommand);

        registrar.register("mod", new ModCommand());
        registrar.register("invsee", new InvseeCommand());
        registrar.register("endersee", new EnderseeCommand());
        registrar.register("kick", new KickCommand());

        BanCommand banCommand = new BanCommand();
        registrar.register("ban", banCommand);
        registrar.register("unban", banCommand);
        registrar.register("banip", banCommand);
        registrar.register("unbanip", banCommand);

        registrar.register("freeze", new FreezeCommand());
        registrar.register("report", new ReportCommand());

        SetSlowmodeCommand slowmodeCommand = new SetSlowmodeCommand();
        registrar.register("slowmode", slowmodeCommand);
        registrar.register("setslowmode", slowmodeCommand);

        registrar.register("reports", new ReportsCommand());
        registrar.register("checkreport", new CheckReportCommand());
        registrar.register("target", new TargetCommand());
        registrar.register("sanctions", new SanctionsCommand());
        registrar.register("userinfo", new UserInfoCommand());
        registrar.register("chat", new fr.moussax.blightedMod.commands.impl.ChatCommand());

        SocialSpyCommand socialSpyCommand = new SocialSpyCommand();
        registrar.register("msginspect", socialSpyCommand);
        registrar.register("spy", socialSpyCommand);
        registrar.register("socialspy", socialSpyCommand);

        registrar.register("testmod", new TestModCommand());
    }









    private static TabSuggestionRegistry createSuggestionRegistry() {
        TabSuggestionRegistry suggestions = new TabSuggestionRegistry();

        suggestions.register("$players", () -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList()
        );

        suggestions.register("$channels", () -> java.util.List.of("staff", "all"));

        return suggestions;
    }
}
