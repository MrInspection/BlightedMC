package fr.moussax.blightedMod.registry;

import fr.moussax.bedrock.ui.actionbar.ActionbarService;
import fr.moussax.bedrock.ui.menu.system.MenuListener;
import fr.moussax.bedrock.ui.menu.system.MenuSystem;
import fr.moussax.bedrock.ui.sign.SignInputListener;
import fr.moussax.blightedMod.BlightedMod;
import fr.moussax.blightedMod.moderator.listeners.InteractiveChatListener;
import fr.moussax.blightedMod.moderator.listeners.ModerationListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import fr.moussax.blightedMod.moderator.hud.ModerationHud;

public final class EventsRegistry {

    private final BlightedMod instance = BlightedMod.getInstance();
    private MenuSystem menuSystem;
    private SignInputListener signInputListener;
    private ActionbarService actionBarService;

    public void initializeListeners() {
        PluginManager manager = Bukkit.getPluginManager();

        signInputListener = new SignInputListener();
        menuSystem = new MenuSystem(instance);
        actionBarService = new ActionbarService(instance);
        actionBarService.start(10L);

        actionBarService.registerSection(ModerationHud.createSection(instance.getModerationManager()));

        manager.registerEvents(signInputListener, instance);
        manager.registerEvents(new MenuListener(menuSystem), instance);
        manager.registerEvents(new ModerationListener(instance.getModerationManager()), instance);
        manager.registerEvents(new InteractiveChatListener(), instance);
    }


    public void cleanup() {
        if (signInputListener != null) {
            signInputListener.cleanup();
        }
        if (actionBarService != null) {
            actionBarService.stop();
        }
    }

    public void shutdownMenus() {
        if (menuSystem != null) {
            menuSystem.shutdown();
        }
    }
}

