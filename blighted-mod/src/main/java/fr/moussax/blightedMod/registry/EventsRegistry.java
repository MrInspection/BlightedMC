package fr.moussax.blightedMod.registry;

import fr.moussax.bedrock.ui.actionbar.Actionbar;
import fr.moussax.bedrock.ui.menu.system.MenuListener;
import fr.moussax.bedrock.ui.menu.system.MenuSystem;
import fr.moussax.bedrock.ui.sign.SignInput;
import fr.moussax.blightedMod.BlightedMod;
import fr.moussax.blightedMod.moderator.hud.ModerationHud;
import fr.moussax.blightedMod.moderator.listeners.InteractiveChatListener;
import fr.moussax.blightedMod.moderator.listeners.ModerationListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class EventsRegistry {

    private final BlightedMod instance = BlightedMod.getInstance();
    private MenuSystem menuSystem;

    public void initializeListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        SignInput.initialize(instance);
        menuSystem = new MenuSystem(instance);

        Actionbar.initialize(instance, 10L);
        Actionbar.register(instance, ModerationHud.createSection(instance.getModerationManager()));

        pluginManager.registerEvents(new MenuListener(menuSystem), instance);
        pluginManager.registerEvents(new ModerationListener(instance.getModerationManager()), instance);
        pluginManager.registerEvents(new InteractiveChatListener(instance.getModerationManager()), instance);
    }

    public void cleanup() {
        SignInput.cleanup(instance);
        Actionbar.unregisterAll(instance);
    }

    public void shutdownMenus() {
        if (menuSystem != null) {
            menuSystem.shutdown();
        }
    }
}
