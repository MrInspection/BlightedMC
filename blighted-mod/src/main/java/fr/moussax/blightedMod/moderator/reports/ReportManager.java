package fr.moussax.blightedMod.moderator.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
