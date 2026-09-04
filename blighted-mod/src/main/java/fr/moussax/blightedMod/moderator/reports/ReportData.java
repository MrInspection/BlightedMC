package fr.moussax.blightedMod.moderator.reports;

/**
 * Immutable data carrier for a player report.
 */
public record ReportData(
        int id,
        String reporterName,
        String targetName,
        String reason,
        String message,
        long timestamp
) {
}
