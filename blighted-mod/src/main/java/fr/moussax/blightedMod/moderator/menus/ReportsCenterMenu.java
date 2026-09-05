package fr.moussax.blightedMod.moderator.menus;

import fr.moussax.bedrock.ui.menu.TickableMenu;
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

public final class ReportsCenterMenu extends PaginatedMenu implements TickableMenu {

    public ReportsCenterMenu() {
        super("Active Reports", 54);
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
                .addLore("§7There are currently no active reports.")
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
