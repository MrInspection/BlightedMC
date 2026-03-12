package fr.moussax.blightedMC.engine.player.menus;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class InvSeeMenu extends Menu {
    private final Player target;
    private BukkitTask refreshTask;

    private static final ItemStack NO_HELMET = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName("§7Empty Helmet Slot").toItemStack();
    private static final ItemStack NO_CHESTPLATE = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName("§7Empty Chestplate Slot").toItemStack();
    private static final ItemStack NO_LEGGINGS = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName("§7Empty Leggings Leggings").toItemStack();
    private static final ItemStack NO_BOOTS = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName("§7Empty Boots Slot").toItemStack();
    private static final ItemStack NO_OFFHAND = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName("§7Empty Offhand Slot").toItemStack();

    public InvSeeMenu(Player target) {
        super(target.getName() + "'s Inventory", 45);
        this.target = target;
    }

    @Override
    public void build(Player player) {
        fillSlots(new int[]{0, 6}, MenuElementPreset.EMPTY_SLOT_FILLER);

        ItemStack playerInformation = new ItemBuilder(Material.PLAYER_HEAD)
            .setDisplayName("§d" + target.getName())
            .addLore(
                "§7Health: §f" + (int) target.getHealth() + "§c❤",
                "§7Food: §f" + target.getFoodLevel() + "§6\uD83C\uDF56",
                "§7Gamemode: §f" + target.getGameMode().name()
            )
            .setSkullOwner(target.getUniqueId())
            .toItemStack();

        setItem(7, playerInformation, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
        setItem(
            8,
            new ItemBuilder(Material.ENDER_CHEST)
                .setDisplayName("§dView Ender Chest")
                .addLore("§7Click to view ender chest.")
                .toItemStack(),
            MenuItemInteraction.ANY_CLICK,
            (p, _) -> BlightedMC.menuManager().openMenu(
                new EnderSeeMenu(target, this), p
            )
        );

        updateContents();
    }

    @Override
    public void open(@NonNull Player player) {
        super.open(player);
        this.refreshTask = Bukkit.getScheduler().runTaskTimer(BlightedMC.getInstance(), () -> {
            if (!player.isOnline() || player.getOpenInventory().getTopInventory() != getInventory()) {
                if (refreshTask != null && !refreshTask.isCancelled()) {
                    refreshTask.cancel();
                }
                return;
            }
            updateContents();
        }, 1L, 1L);
    }

    private void updateContents() {
        Inventory playerInventory = target.getInventory();

        updateSlot(1, target.getInventory().getHelmet(), NO_HELMET);
        updateSlot(2, target.getInventory().getChestplate(), NO_CHESTPLATE);
        updateSlot(3, target.getInventory().getLeggings(), NO_LEGGINGS);
        updateSlot(4, target.getInventory().getBoots(), NO_BOOTS);
        updateSlot(5, target.getInventory().getItemInOffHand(), NO_OFFHAND);

        for (int slot = 0; slot < 36; slot++) {
            ItemStack realItem = playerInventory.getItem(slot);
            int menuSlot = slot + 9;
            ItemStack currentItem = getInventory().getItem(menuSlot);
            if (!Objects.equals(realItem, currentItem)) {
                getInventory().setItem(menuSlot, realItem);
            }
            if (realItem != null) {
                setItem(menuSlot, realItem, MenuItemInteraction.ANY_CLICK, (_, _) -> {
                });
            }
        }
    }

    private void updateSlot(int slot, ItemStack item, ItemStack placeholder) {
        ItemStack displayItem = (item != null && item.getType() != Material.AIR) ? item : placeholder;
        if (!Objects.equals(getInventory().getItem(slot), displayItem)) {
            getInventory().setItem(slot, displayItem);
        }
        setItem(slot, displayItem, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }
}
