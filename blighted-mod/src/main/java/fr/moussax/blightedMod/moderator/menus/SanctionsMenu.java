package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.ui.menu.TickableMenu;
import fr.moussax.bedrock.ui.menu.interaction.MenuElementPreset;
import fr.moussax.bedrock.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.bedrock.ui.menu.types.PaginatedMenu;
import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Paginated menu displaying historical sanctions for a specific player.
 * Features target player head, Hypixel-style hopper filtering, and real-time updates.
 */
public final class SanctionsMenu extends PaginatedMenu implements TickableMenu {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final int[] SANCTION_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public enum SanctionFilter {
        ALL("All Sanctions"),
        ACTIVE("Active Only"),
        BAN("Bans Only"),
        MUTE("Mutes Only"),
        KICK("Kicks Only");

        private final String displayName;

        SanctionFilter(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SanctionFilter next() {
            SanctionFilter[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }

    private final String targetName;
    private final List<PunishmentData> punishments;
    private SanctionFilter currentFilter = SanctionFilter.ALL;

    public SanctionsMenu(String targetName, List<PunishmentData> punishments) {
        super(targetName +"'s sanctions", 54);
        this.targetName = targetName;
        this.punishments = punishments;
    }

    @Override
    public void onTick(Player player) {
        refresh(player);
    }

    private List<PunishmentData> getFilteredPunishments() {
        return punishments.stream().filter(punishment -> switch (currentFilter) {
            case ALL -> true;
            case ACTIVE -> punishment.active() && !punishment.isExpired();
            case BAN -> punishment.type() == PunishmentData.PunishmentType.BAN || punishment.type() == PunishmentData.PunishmentType.IP_BAN;
            case MUTE -> punishment.type() == PunishmentData.PunishmentType.MUTE;
            case KICK -> punishment.type() == PunishmentData.PunishmentType.KICK;
        }).toList();
    }

    @Override
    protected int getTotalItems(@NonNull Player player) {
        return getFilteredPunishments().size();
    }

    @Override
    protected int getItemsPerPage() {
        return SANCTION_SLOTS.length;
    }

    @Override
    protected ItemStack getItem(@NonNull Player player, int index) {
        List<PunishmentData> filtered = getFilteredPunishments();
        if (index >= filtered.size()) {
            return new ItemStack(Material.AIR);
        }

        PunishmentData punishment = filtered.get(index);
        Material icon = switch (punishment.type()) {
            case BAN, IP_BAN -> Material.REDSTONE_BLOCK;
            case MUTE -> Material.WRITABLE_BOOK;
            case KICK -> Material.LEATHER_BOOTS;
        };

        String dateText = DATE_FORMAT.format(new Date(punishment.createdAt()));
        String durationText = punishment.expiresAt() == null ? "Permanent" : DATE_FORMAT.format(new Date(punishment.expiresAt()));
        String activeStatus = punishment.active() && !punishment.isExpired() ? "§aACTIVE" : "§cEXPIRED";

        return new ItemBuilder(icon)
                .setDisplayName("§c" + punishment.type().name().replace("_", " ") + " §f| §e#" + punishment.id())
                .addLore("§7Target: §b" + targetName)
                .addLore("§7Issued By: §e" + punishment.moderatorName())
                .addLore("§7Reason: §f" + punishment.reason())
                .addLore("§7Issued At: §e" + dateText)
                .addLore("§7Expires: §f" + durationText)
                .addLore("§7Status: " + activeStatus)
                .toItemStack();
    }

    @Override
    public void build(@NonNull Player viewer) {
        List<PunishmentData> filtered = getFilteredPunishments();
        totalItems = filtered.size();

        int itemsPerPage = getItemsPerPage();
        int maxPage = Math.max(0, (totalItems - 1) / itemsPerPage);
        currentPage = Math.min(currentPage, maxPage);

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        clearInventory();

        renderTargetHead();

        if (filtered.isEmpty()) {
            ItemStack noSanctionsItem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName("§cNo Sanctions Found")
                    .addLore("§7No sanctions match the current filter.")
                    .toItemStack();

            setItem(22, noSanctionsItem, MenuItemInteraction.ANY_CLICK, (player, _) -> { });
        } else {
            int slotIndex = 0;
            for (int i = startIndex; i < endIndex && slotIndex < SANCTION_SLOTS.length; i++, slotIndex++) {
                final int index = i;
                setItem(
                        SANCTION_SLOTS[slotIndex],
                        getItem(viewer, index),
                        MenuItemInteraction.ANY_CLICK,
                        (player, click) -> onItemClick(player, index, click)
                );
            }
        }

        if (currentPage > 0) {
            setBackButton(48, (player, _) -> {
                currentPage--;
                refresh(player);
            });
        }

        if (endIndex < totalItems) {
            setItem(50, MenuElementPreset.NEXT_BUTTON, (player, _) -> {
                currentPage++;
                refresh(player);
            });
        }

        renderFilterHopper();
        setCloseButton(49);
    }

    private void renderTargetHead() {
        ItemStack headStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headStack.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetName));
            skullMeta.setDisplayName("§d" + targetName);

            long activeBans = punishments.stream().filter(punishment -> (punishment.type() == PunishmentData.PunishmentType.BAN || punishment.type() == PunishmentData.PunishmentType.IP_BAN) && punishment.active() && !punishment.isExpired()).count();
            long activeMutes = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.MUTE && punishment.active() && !punishment.isExpired()).count();
            long totalKicks = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.KICK).count();

            skullMeta.setLore(List.of(
                    "§7Total Sanctions: §e" + punishments.size(),
                    "§7Active Bans: §c" + activeBans,
                    "§7Active Mutes: §d" + activeMutes,
                    "§7Total Kicks: §f" + totalKicks
            ));
            headStack.setItemMeta(skullMeta);
        }
        setItem(4, headStack, MenuItemInteraction.ANY_CLICK, (player, _) -> { });
    }

    private void renderFilterHopper() {
        ItemBuilder hopperBuilder = new ItemBuilder(Material.HOPPER)
                .setDisplayName("§fFilter")
                .addLore("")
                .addLore(currentFilter == SanctionFilter.ALL ? " §b▶ All Sanctions" : "§7   All Sanctions")
                .addLore(currentFilter == SanctionFilter.ACTIVE ? " §b▶ Active Only" : "§7   Active Only")
                .addLore(currentFilter == SanctionFilter.BAN ? " §b▶ Bans Only" : "§7   Bans Only")
                .addLore(currentFilter == SanctionFilter.MUTE ? " §b▶ Mutes Only" : "§7   Mutes Only")
                .addLore(currentFilter == SanctionFilter.KICK ? " §b▶ Kicks Only" : "§7   Kicks Only")
                .addLore("")
                .addLore("§bRight-click to go backwards!")
                .addLore("§eClick to switch filter!");

        setItem(52, hopperBuilder.toItemStack(), MenuItemInteraction.ANY_CLICK, (player, _) -> {
            currentFilter = currentFilter.next();
            currentPage = 0;
            refresh(player);
        });
    }
}
