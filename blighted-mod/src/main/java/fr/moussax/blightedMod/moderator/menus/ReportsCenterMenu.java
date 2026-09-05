package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.ui.book.BookMenu;
import fr.moussax.bedrock.ui.menu.TickableMenu;
import fr.moussax.bedrock.ui.menu.types.PaginatedMenu;
import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedMod.moderator.reports.ReportData;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class ReportsCenterMenu extends PaginatedMenu implements TickableMenu {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);

    public ReportsCenterMenu() {
        super("Reports Center", 54);
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
    protected int[] getDisplaySlots() {
        return INNER_GRID_SLOTS;
    }

    @Override
    protected ItemStack getEmptyStateItem(@NonNull Player player) {
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§cNo Active Reports")
                .addLore("§7There are currently no", "§7active reports.")
                .toItemStack();
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
        String dateText = DATE_FORMAT.format(new Date(report.timestamp()));

        ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(report.targetName())
                .setDisplayName("§b" + report.targetName() + " §7(#" + report.id() + ")")
                .addLore(
                        "",
                        "  §7Status: §a§lACTIVE",
                        "  §7Reporter: §e" + report.reporterName(),
                        "  §7Reason: §c" + report.reason(),
                        "  §7Submitted: §f" + dateText + " §7(" + timeAgo + ")"
                );

        if (report.message() != null && !report.message().isBlank()) {
            builder.addLore("  §7Message: §f\"" + report.message() + "\"");
        }

        builder.addItemFlag(ItemFlag.HIDE_PROFILE);
        builder.addLore(
                "",
                "§dLeft-click to teleport to target!",
                "§bShift-Left-Click to view report details!",
                "§cRight-click to dismiss report!"
        );

        return builder.toItemStack();
    }

    @Override
    public void build(@NonNull Player viewer) {
        super.build(viewer);
        renderHeader();
    }

    private void renderHeader() {
        int totalReports = ReportManager.getInstance().getActiveReports().size();
        ItemStack headerItem = new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("§eReports Center")
                .addLore(
                        "",
                        " §7View and manage every report ",
                        " §7submitted by players, then act",
                        " §7on them from this menu.",
                        "",
                        " §7Active Reports: §d" + totalReports,
                        ""
                )
                .toItemStack();
        setItem(4, headerItem);
    }

    @Override
    protected void onItemLeftClick(@NonNull Player moderator, int index) {
        ReportData report = ReportManager.getInstance().getActiveReports().get(index);
        close();
        moderator.performCommand("mtp " + report.targetName());
    }

    @Override
    protected void onItemRightClick(@NonNull Player moderator, int index) {
        ReportData report = ReportManager.getInstance().getActiveReports().get(index);
        ReportManager.getInstance().dismissReport(report.id());
        inform(moderator, " §eYou dismissed §creport #" + report.id() + " §eagainst §d" + report.targetName() + "§e.");
        refresh(moderator);
    }

    @Override
    protected void onItemShiftClick(@NonNull Player moderator, int index) {
        ReportData report = ReportManager.getInstance().getActiveReports().get(index);
        openReportBook(moderator, report);
    }

    public static void openReportBook(Player moderator, ReportData report) {
        String dateText = DATE_FORMAT.format(new Date(report.timestamp()));
        BookMenu bookMenu = BookMenu.builder()
                .addPage(page -> {
                    page.append("  §0§lREPORT DETAILS\n\n");
                    page.append("§0" + report.reporterName() + " §0reported §6" + report.targetName() + " §0for §c" + report.reason() + "§0.\n\n");

                    if (report.message() != null && !report.message().isBlank() && !report.message().equalsIgnoreCase("General player report") && !report.message().equalsIgnoreCase("None")) {
                        page.append("§0Chat Message:\n");
                        page.append("§8\"§3" + report.message() + "§8\"\n\n");
                    }

                    page.append("§0Submitted: §8" + dateText + "\n\n");
                    page.hoverAndExecute("§4▶ Dismiss Report", "§eClick to dismiss this report", "/reports dismiss " + report.id());
                });

        bookMenu.open(moderator);
    }
}
