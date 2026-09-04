package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.ui.menu.TickableMenu;
import fr.moussax.bedrock.ui.menu.interaction.MenuElementPreset;
import fr.moussax.bedrock.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.bedrock.ui.menu.types.PaginatedMenu;
import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.reports.ReportData;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Paginated menu displaying active player reports for staff review.
 * Ticks periodically to support real-time updates while open.
 */
public final class ReportsCenterMenu extends PaginatedMenu implements TickableMenu {

    private static final int[] REPORT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public ReportsCenterMenu() {
        super("Active Reports", 54);
    }

    @Override
    public long tickPeriodTicks() {
        return 20L;
    }

    @Override
    public void onTick(Player player) {
        refresh(player);
    }

    @Override
    protected int getTotalItems(@NonNull Player player) {
        return ReportManager.getInstance().getActiveReports().size();
    }

    @Override
    protected int getItemsPerPage() {
        return REPORT_SLOTS.length;
    }

    @Override
    protected ItemStack getItem(@NonNull Player player, int index) {
        List<ReportData> reports = ReportManager.getInstance().getActiveReports();
        if (index >= reports.size()) {
            return new ItemStack(Material.AIR);
        }

        ReportData report = reports.get(index);
        long elapsedSeconds = (System.currentTimeMillis() - report.timestamp()) / 1000L;
        String timeAgo = elapsedSeconds < 60 ? elapsedSeconds + "s ago" : (elapsedSeconds / 60) + "m ago";

        ItemBuilder builder = new ItemBuilder(Material.PAPER)
                .setDisplayName("§eReport: §b" + report.targetName())
                .addLore("§7Report ID: §f#" + report.id())
                .addLore("§7Target: §b" + report.targetName())
                .addLore("§7Reporter: §e" + report.reporterName())
                .addLore("§7Reason: §c" + report.reason());

        if (report.message() != null && !report.message().isBlank()) {
            builder.addLore("§7Message: §f\"" + report.message() + "\"");
        }

        builder.addLore("§7Submitted: §e" + timeAgo)
                .addLore("")
                .addLore("§eLeft-Click §7to teleport & target")
                .addLore("§cRight-Click §7to dismiss");

        return builder.toItemStack();
    }

    @Override
    public void build(@NonNull Player viewer) {
        totalItems = Math.max(0, getTotalItems(viewer));

        int itemsPerPage = getItemsPerPage();
        int maxPage = Math.max(0, (totalItems - 1) / itemsPerPage);
        currentPage = Math.min(currentPage, maxPage);

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        clearInventory();

        List<ReportData> reports = ReportManager.getInstance().getActiveReports();
        if (reports.isEmpty()) {
            ItemStack noReportsItem = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName("§cNo Active Reports")
                    .addLore("§7There are currently no active reports.")
                    .toItemStack();

            setItem(22, noReportsItem, MenuItemInteraction.ANY_CLICK, (player, _) -> { });
        } else {
            int slotIndex = 0;
            for (int i = startIndex; i < endIndex && slotIndex < REPORT_SLOTS.length; i++, slotIndex++) {
                final int index = i;
                setItem(
                        REPORT_SLOTS[slotIndex],
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

        setCloseButton(49);
    }

    @Override
    protected void onItemClick(@NonNull Player moderator, int index, @NonNull ClickType clickType) {
        List<ReportData> reports = ReportManager.getInstance().getActiveReports();
        if (index >= reports.size()) {
            return;
        }

        ReportData report = reports.get(index);

        if (clickType.isRightClick()) {
            ReportManager.getInstance().dismissReport(report.id());
            inform(moderator, "§aDismissed report #" + report.id() + " against §f" + report.targetName());
            refresh(moderator);
            return;
        }

        if (clickType.isLeftClick()) {
            Player target = Bukkit.getPlayerExact(report.targetName());
            if (target == null) {
                warn(moderator, "Target player §4" + report.targetName() + " §cis no longer online.");
                return;
            }

            moderator.teleport(target.getLocation());
            ModerationManager.getInstance().getModerator(moderator).setTargetPlayer(target);
            inform(moderator, "§aTeleported to §f" + target.getName() + " §aand set as HUD target.");
            close();
        }
    }
}
