package fr.moussax.blightedMC.shared.ui.actionbar;

import lombok.Setter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Assembles and formats per-player action bar text from active sections and temporary alerts.
 */
public final class ActionbarComposer {
    private final Map<String, ActionbarSection> sections = new ConcurrentHashMap<>();
    private final Map<String, TimedAlert> slotAlerts = new ConcurrentHashMap<>();
    private final PriorityQueue<TimedAlert> modalAlerts = new PriorityQueue<>();
    private final Object alertLock = new Object();

    @Setter
    @NonNull
    private String separator = "     ";

    /**
     * Registers a content section to be rendered by this composer.
     *
     * @param section section to register
     */
    public void registerSection(@NonNull ActionbarSection section) {
        sections.put(section.id(), section);
    }

    /**
     * Unregisters a content section and any active slot alert associated with it.
     *
     * @param id identifier of section to unregister
     */
    public void unregisterSection(@NonNull String id) {
        sections.remove(id);
        slotAlerts.remove(id);
    }

    /**
     * Queues a high-priority modal alert that overrides all standard sections for a duration.
     *
     * @param message  alert text to display
     * @param priority alert priority; higher values take precedence
     * @param duration display duration
     */
    public void sendModalAlert(@NonNull String message, int priority, @NonNull Duration duration) {
        synchronized (alertLock) {
            modalAlerts.add(new TimedAlert(message, priority, duration.toMillis()));
        }
    }

    /**
     * Queues a default-priority modal alert that overrides all standard sections for a duration.
     *
     * @param message  alert text to display
     * @param duration display duration
     */
    public void sendModalAlert(@NonNull String message, @NonNull Duration duration) {
        sendModalAlert(message, 0, duration);
    }

    /**
     * Overrides the content of a specific action bar section with an alert message for a duration.
     *
     * @param sectionId target section identifier
     * @param message   alert message text
     * @param duration  display duration
     */
    public void sendSlotAlert(@NonNull String sectionId, @NonNull String message, @NonNull Duration duration) {
        slotAlerts.put(sectionId, TimedAlert.of(message, duration));
    }

    /**
     * Clears all active modal and slot alerts.
     */
    public void clearAlerts() {
        synchronized (alertLock) {
            modalAlerts.clear();
        }
        slotAlerts.clear();
    }

    /**
     * Compiles the formatted action bar message string for a given player.
     *
     * <p>If an active modal alert exists, its message is returned immediately. Otherwise,
     * visible sections are sorted by priority and joined with the configured separator.</p>
     *
     * @param player player for whom to compile action bar content
     * @return compiled action bar text, or an empty string if no content is visible
     */
    @NonNull
    public String compile(@NonNull Player player) {
        synchronized (alertLock) {
            modalAlerts.removeIf(TimedAlert::isExpired);
            if (!modalAlerts.isEmpty()) {
                return modalAlerts.peek().message();
            }
        }

        return sections.values().stream()
                .filter(section -> section.visibility().test(player))
                .sorted(Comparator.comparingInt(ActionbarSection::priority))
                .map(section -> {
                    TimedAlert alert = slotAlerts.get(section.id());
                    if (alert != null) {
                        if (!alert.isExpired()) {
                            return alert.message();
                        }
                        slotAlerts.remove(section.id());
                    }
                    return section.textSupplier().apply(player);
                })
                .filter(Objects::nonNull)
                .filter(sectionText -> !sectionText.isEmpty())
                .collect(Collectors.joining(separator));
    }
}
