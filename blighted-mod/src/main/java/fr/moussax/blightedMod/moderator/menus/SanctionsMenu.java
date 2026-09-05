package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.ui.menu.TickableMenu;
import fr.moussax.bedrock.ui.menu.types.PaginatedMenu;
import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class SanctionsMenu extends PaginatedMenu implements TickableMenu {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);

    @Getter
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

        public SanctionFilter next() {
            SanctionFilter[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public SanctionFilter previous() {
            SanctionFilter[] values = values();
            return values[(ordinal() - 1 + values.length) % values.length];
        }
    }

    private final String targetName;
    private final List<PunishmentData> punishments;
    private SanctionFilter currentFilter = SanctionFilter.ALL;
    private List<PunishmentData> cachedFilteredPunishments;

    public SanctionsMenu(String targetName, List<PunishmentData> punishments) {
        super(targetName + "'s sanctions", 54);
        this.targetName = targetName;
        this.punishments = punishments;
    }

    @Override
    public void onTick(Player player) {
        refresh(player);
    }

    private void updateFilteredPunishmentsCache() {
        this.cachedFilteredPunishments = punishments.stream()
                .filter(punishment -> switch (currentFilter) {
                    case ALL -> true;
                    case ACTIVE -> punishment.active() && !punishment.isExpired();
                    case BAN -> punishment.type() == PunishmentData.PunishmentType.BAN || punishment.type() == PunishmentData.PunishmentType.IP_BAN;
                    case MUTE -> punishment.type() == PunishmentData.PunishmentType.MUTE;
                    case KICK -> punishment.type() == PunishmentData.PunishmentType.KICK;
                })
                .sorted(Comparator.comparingInt(PunishmentData::id))
                .toList();
    }

    @Override
    protected int getTotalItems(@NonNull Player player) {
        if (cachedFilteredPunishments == null) {
            updateFilteredPunishmentsCache();
        }
        return cachedFilteredPunishments.size();
    }

    @Override
    protected ItemStack getItem(@NonNull Player player, int index) {
        if (cachedFilteredPunishments == null) {
            updateFilteredPunishmentsCache();
        }
        if (index >= cachedFilteredPunishments.size()) {
            return new ItemStack(Material.AIR);
        }

        PunishmentData punishment = cachedFilteredPunishments.get(index);

        String typeTitle = switch (punishment.type()) {
            case BAN -> "§c§lBAN";
            case IP_BAN -> "§c§lIP BAN";
            case MUTE -> "§e§lMUTE";
            case KICK -> "§f§lKICK";
        };

        Material icon = switch (punishment.type()) {
            case BAN, IP_BAN -> Material.REDSTONE_BLOCK;
            case MUTE -> Material.WRITABLE_BOOK;
            case KICK -> Material.COPPER_BOOTS;
        };

        boolean isActive = punishment.active() && !punishment.isExpired();
        String activeStatus = isActive ? "§a§lACTIVE" : "§c§lEXPIRED";
        String dateText = DATE_FORMAT.format(new Date(punishment.createdAt()));

        ItemBuilder builder = new ItemBuilder(icon)
                .setDisplayName(typeTitle + " §7(#" + punishment.id() + ")");

        if (punishment.type() != PunishmentData.PunishmentType.KICK) {
            builder.addLore(
                    "",
                    "  §7Status: " + activeStatus,
                    "  §7Reason: §f" + punishment.reason(),
                    "",
                    "  §7Issued By: §e" + punishment.moderatorName(),
                    "  §7Issued At: §f" + dateText + " "
            );

            String durationText = punishment.isPermanent()
                    ? "§cPermanent"
                    : formatDurationSeconds(Math.max(1, (punishment.expiresAt() - punishment.createdAt()) / 1000L));

            builder.addLore("  §7Duration: §d" + durationText);

            if (punishment.expiresAt() != null) {
                builder.addLore("  §7Expires At: §f" + DATE_FORMAT.format(new Date(punishment.expiresAt())) + " ");
            }

            if (punishment.type() == PunishmentData.PunishmentType.IP_BAN && punishment.ipAddress() != null && !punishment.ipAddress().equals("0.0.0.0")) {
                builder.addLore("  §7IP Address: §d" + punishment.ipAddress());
            }
        } else {
            builder.addLore(
                    "",
                    "  §7Reason: §f" + punishment.reason(),
                    "",
                    "  §7Issued By: §e" + punishment.moderatorName(),
                    "  §7Issued At: §f" + dateText + " "
            );
        }

        builder.addLore("");
        builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        return builder.toItemStack();
    }

    private static String formatDurationSeconds(long totalSeconds) {
        if (totalSeconds >= 31536000L) {
            long years = totalSeconds / 31536000L;
            return years + " year" + (years > 1 ? "s" : "");
        }
        if (totalSeconds >= 604800L) {
            long weeks = totalSeconds / 604800L;
            return weeks + " week" + (weeks > 1 ? "s" : "");
        }
        if (totalSeconds >= 86400L) {
            long days = totalSeconds / 86400L;
            return days + " day" + (days > 1 ? "s" : "");
        }
        if (totalSeconds >= 3600L) {
            long hours = totalSeconds / 3600L;
            return hours + " hour" + (hours > 1 ? "s" : "");
        }
        if (totalSeconds >= 60L) {
            long minutes = totalSeconds / 60L;
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        }
        return totalSeconds + " second" + (totalSeconds > 1 ? "s" : "");
    }

    @Override
    protected int[] getDisplaySlots() {
        return INNER_GRID_SLOTS;
    }

    @Override
    protected ItemStack getEmptyStateItem(@NonNull Player player) {
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§cNo Sanctions Found")
                .addLore("§7No sanctions match the current filter.")
                .toItemStack();
    }

    @Override
    public void build(@NonNull Player viewer) {
        updateFilteredPunishmentsCache();
        super.build(viewer);
        renderTargetHead();
        renderFilterHopper();
    }

    private void renderTargetHead() {
        long activeBans = punishments.stream().filter(punishment -> (punishment.type() == PunishmentData.PunishmentType.BAN || punishment.type() == PunishmentData.PunishmentType.IP_BAN) && punishment.active() && !punishment.isExpired()).count();
        long activeMutes = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.MUTE && punishment.active() && !punishment.isExpired()).count();
        long totalKicks = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.KICK).count();

        ItemStack headItem = new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(targetName)
                .setDisplayName("§d" + targetName)
                .addLore(
                        "",
                        "  §7Total Sanctions: §e" + punishments.size() + "  ",
                        "",
                        "  §7Active Bans: §c" + activeBans,
                        "  §7Active Mutes: §e" + activeMutes,
                        "  §7Total Kicks: §f" + totalKicks,
                        ""
                )
                .addItemFlag(ItemFlag.HIDE_PROFILE)
                .toItemStack();

        setItem(4, headItem);
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

        setItem(
                53,
                hopperBuilder.toItemStack(),
                (player, _) -> {
                    currentFilter = currentFilter.next();
                    currentPage = 0;
                    updateFilteredPunishmentsCache();
                    refresh(player);
                },
                (player, _) -> {
                    currentFilter = currentFilter.previous();
                    currentPage = 0;
                    updateFilteredPunishmentsCache();
                    refresh(player);
                }
        );
    }
}
