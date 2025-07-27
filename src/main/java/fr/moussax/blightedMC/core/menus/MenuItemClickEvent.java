package fr.moussax.blightedMC.core.menus;

import fr.moussax.blightedMC.core.events.BlightedPlayerEvent;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class MenuItemClickEvent extends BlightedPlayerEvent implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  private final ClickableItem item;
  private final ItemStack cursorItem;
  private final BlightedPlayer player;

  private boolean isCancelled;

  public MenuItemClickEvent(ClickableItem clickedItem, ItemStack cursorItem, BlightedPlayer player) {
    this.item = clickedItem;
    this.cursorItem = cursorItem;
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }

  public ClickableItem getClickedItem() {
    return item;
  }

  public ItemStack getCursorItem() {
    return cursorItem;
  }

  @Override
  public BlightedPlayer getBlightedPlayer() {
    return player;
  }
}
