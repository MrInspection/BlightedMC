package fr.moussax.blightedMod.moderator.reports;

import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.moderator.ModerationManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class ReportManager {
    private static final ReportManager INSTANCE = new ReportManager();

    private final List<ReportData> reports = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idCounter = new AtomicInteger(1);

    private ReportManager() {
    }

    public static ReportManager getInstance() {
        return INSTANCE;
    }

    public ReportData submitReport(String reporterName, String targetName, String reason, String message) {
        ReportData report = new ReportData(
                idCounter.getAndIncrement(),
                reporterName,
                targetName,
                reason,
                message,
                System.currentTimeMillis()
        );
        reports.addFirst(report);
        return report;
    }

    public void submitAndNotify(Player reporter, String targetName, String reason, String message) {
        ReportData report = submitReport(reporter.getName(), targetName, reason, message);
        inform(reporter, " §a⚑ §7Your report §7has been submitted to online staff.");

        InteractiveMessage notificationMessage = InteractiveMessage.text(" §d§lSTAFF! §f" + reporter.getName() + " §ereported §d" + targetName + " §efor §c" + reason + "§e. ")
                .hoverAndExecute("§6[DETAILS]", "§fClick to view §dreport §fdetails.", "/checkreport " + report.id())
                .append(" ")
                .hoverAndExecute("§b[MTP]", "§fClick to teleport to §d" + targetName + "§f.", "/mtp " + targetName)
                .append(" ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + targetName + "§f.", "/userinfo " + targetName);

        ModerationManager.getInstance().broadcastToModerators(notificationMessage);
    }

    public List<ReportData> getActiveReports() {
        synchronized (reports) {
            return new ArrayList<>(reports);
        }
    }

    public boolean dismissReport(int reportId) {
        return reports.removeIf(report -> report.id() == reportId);
    }

    public ReportData getReportById(int reportId) {
        synchronized (reports) {
            return reports.stream()
                    .filter(report -> report.id() == reportId)
                    .findFirst()
                    .orElse(null);
        }
    }
}
