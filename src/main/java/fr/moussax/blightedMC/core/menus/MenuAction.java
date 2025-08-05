package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface MenuAction {
    void execute(Player player, ClickType clickType);

    static MenuAction left(MenuAction action) {
        return (player, clickType) -> {
            if (clickType == ClickType.LEFT) action.execute(player, clickType);
        };
    }

    static MenuAction right(MenuAction action) {
        return (player, clickType) -> {
            if (clickType == ClickType.RIGHT) action.execute(player, clickType);
        };
    }
}