package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.ui.menu.Menu;
import fr.moussax.bedrock.ui.menu.TickableMenu;
import fr.moussax.bedrock.ui.menu.interaction.MenuElementPreset;
import fr.moussax.bedrock.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.net.InetSocketAddress;
import java.util.Objects;

public final class InvSeeMenu extends Menu implements TickableMenu {
    private final Player target;

    private static final ItemStack NO_HELMET = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7Empty Helmet Slot").toItemStack();
    private static final ItemStack NO_CHESTPLATE = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7Empty Chestplate Slot").toItemStack();
    private static final ItemStack NO_LEGGINGS = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7Empty Leggings Slot").toItemStack();
    private static final ItemStack NO_BOOTS = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7Empty Boots Slot").toItemStack();
    private static final ItemStack NO_OFFHAND = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§7Empty Offhand Slot").toItemStack();

    public InvSeeMenu(Player target) {
        super(target.getName() + "'s Inventory", 45);
        this.target = target;
    }

    @Override
    public long tickPeriodTicks() {
        return 1L;
    }

    @Override
    public void build(Player player) {
        fillSlots(new int[]{0, 6}, MenuElementPreset.EMPTY_SLOT_FILLER);

        setItem(
            8,
            new ItemBuilder(Material.ENDER_CHEST)
                .setDisplayName("§dView Ender Chest")
                .addLore("§7Click to view ender chest.")
                .toItemStack(),
            (_, _) -> openSubMenu(new EnderseeMenu(target, this))
        );

        updateContents();
    }

    @Override
    public void onTick(Player player) {
        if (!target.isOnline()) {
            close();
            return;
        }
        updateContents();
    }

    private void updateContents() {
        ItemStack playerInformation = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§d" + target.getName())
                .addLore( "",
                        "  §7Health: §f" + (int) target.getHealth() + "§c❤",
                        "  §7Food: §f" + target.getFoodLevel() + "§c\uD83C\uDF56",
                        "  §7Gamemode: §f" + target.getGameMode().name() + "  ",
                        ""
                )
                .setSkullOwner(target.getUniqueId())
                .addItemFlag(ItemFlag.HIDE_PROFILE)
                .toItemStack();
        updateSlot(7, playerInformation, null);

        updateSlot(1, target.getInventory().getHelmet(), NO_HELMET);
        updateSlot(2, target.getInventory().getChestplate(), NO_CHESTPLATE);
        updateSlot(3, target.getInventory().getLeggings(), NO_LEGGINGS);
        updateSlot(4, target.getInventory().getBoots(), NO_BOOTS);
        updateSlot(5, target.getInventory().getItemInOffHand(), NO_OFFHAND);

        Inventory playerInventory = target.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            updateSlot(slot + 9, playerInventory.getItem(slot), null);
        }
    }

    private void updateSlot(int menuSlot, ItemStack item, ItemStack placeholder) {
        ItemStack displayItem = isEmpty(item) ? placeholder : item;
        ItemStack currentItem = getInventory().getItem(menuSlot);

        if (!isSameItem(displayItem, currentItem)) {
            setSlotItem(menuSlot, displayItem);
        }
    }

    private boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    private boolean isSameItem(ItemStack first, ItemStack second) {
        if (isEmpty(first) && isEmpty(second)) return true;
        if (isEmpty(first) || isEmpty(second)) return false;
        return Objects.equals(first, second);
    }
}
