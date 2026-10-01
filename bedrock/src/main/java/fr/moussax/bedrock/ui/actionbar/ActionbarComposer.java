package fr.moussax.bedrock.ui.actionbar;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Assembles and formats per-player action bar text from active sections and temporary alerts.
 *
 * <p>Supports modular standard sections joined by a separator, priority-based exclusive sections,
 * localized slot alerts overriding specific sections, dynamic countdowns, and high-priority modal broadcast alerts.</p>
 */
public final class ActionbarComposer {

    private final Map<String, ActionbarSection> localSections = new ConcurrentHashMap<>();
    private final Supplier<Map<String, ActionbarSection>> globalSectionsSupplier;
    private final Map<String, TimedAlert> slotAlerts = new ConcurrentHashMap<>();
    private final PriorityQueue<TimedAlert> modalAlerts = new PriorityQueue<>();
    private final Object alertLock = new Object();

    @Getter
    @Setter
    @NonNull
    private String separator = "     ";

    /**
     * Constructs a standalone action bar composer with no shared global sections.
     */
    public ActionbarComposer() {
        this(Collections::emptyMap);
    }

    /**
     * Constructs an action bar composer querying shared global sections from a supplier.
     *
     * @param globalSectionsSupplier supplier returning active global sections
     */
    public ActionbarComposer(@NonNull Supplier<Map<String, ActionbarSection>> globalSectionsSupplier) {
        this.globalSectionsSupplier = Objects.requireNonNull(globalSectionsSupplier, "globalSectionsSupplier cannot be null");
    }

    /**
     * Registers a local content section for this composer. Local sections override global sections
     * sharing the same identifier.
     *
     * @param section section to register
     */
    public void registerSection(@NonNull ActionbarSection section) {
        localSections.put(section.id(), section);
    }

    /**
     * Unregisters a local content section and any active slot alert associated with it.
     *
     * @param id identifier of a section to unregister
     */
    public void unregisterSection(@NonNull String id) {
        localSections.remove(id);
        slotAlerts.remove(id);
    }

    /**
     * Queues a timed alert object as a modal alert that overrides all standard and exclusive sections.
     *
     * @param alert alert to queue
     */
    public void sendModalAlert(@NonNull TimedAlert alert) {
        Objects.requireNonNull(alert, "alert cannot be null");
        synchronized (alertLock) {
            modalAlerts.add(alert);
        }
    }

    /**
     * Queues a high-priority modal alert that overrides all standard and exclusive sections.
     *
     * @param message  alert text to display
     * @param priority precedence priority (higher values take precedence)
     * @param duration display duration
     */
    public void sendModalAlert(@NonNull String message, int priority, @NonNull Duration duration) {
        sendModalAlert(new TimedAlert(message, priority, duration.toMillis()));
    }

    /**
     * Queues a default-priority modal alert that overrides all sections.
     *
     * @param message  alert text to display
     * @param duration display duration
     */
    public void sendModalAlert(@NonNull String message, @NonNull Duration duration) {
        sendModalAlert(message, 0, duration);
    }

    /**
     * Queues a dynamic modal alert that evaluates text dynamically on every render pass.
     *
     * @param supplier dynamic text supplier
     * @param priority precedence priority
     * @param duration display duration
     */
    public void sendModalAlert(@NonNull Function<Player, @Nullable String> supplier, int priority, @NonNull Duration duration) {
        sendModalAlert(TimedAlert.of(supplier, priority, duration));
    }

    /**
     * Overrides the content of a specific action bar section with an alert.
     * If the target section is not registered locally or globally, it falls back to a modal alert
     * so that the message is never dropped.
     *
     * @param sectionId target section identifier
     * @param alert     timed alert
     */
    public void sendSlotAlert(@NonNull String sectionId, @NonNull TimedAlert alert) {
        Objects.requireNonNull(sectionId, "sectionId cannot be null");
        Objects.requireNonNull(alert, "alert cannot be null");
        Map<String, ActionbarSection> globalSections = globalSectionsSupplier.get();
        if (!localSections.containsKey(sectionId) && (globalSections == null || !globalSections.containsKey(sectionId))) {
            sendModalAlert(alert);
            return;
        }
        slotAlerts.put(sectionId, alert);
    }

    /**
     * Overrides the content of a specific action bar section with an alert message for a duration.
     *
     * @param sectionId target section identifier
     * @param message   alert message text
     * @param duration  display duration
     */
    public void sendSlotAlert(@NonNull String sectionId, @NonNull String message, @NonNull Duration duration) {
        sendSlotAlert(sectionId, TimedAlert.of(message, duration));
    }

    /**
     * Clears all active modal and slot alerts, restoring underlying sections.
     */
    public void clearAlerts() {
        synchronized (alertLock) {
            modalAlerts.clear();
        }
        slotAlerts.clear();
    }

    /**
     * Compiles the formatted action bar message string for a player.
     *
     * <p>Evaluation order:
     * <ol>
     *   <li>Active modal alerts: returns the highest priority alert message immediately.</li>
     *   <li>Visible exclusive sections: returns highest precedence non-empty exclusive section text.</li>
     *   <li>Visible standard sections: evaluates each section (or its active slot alert), sorted
     *       by layout order ascending, and joins non-empty results with {@code separator}.</li>
     * </ol>
     *
     * @param player viewing player, or {@code null} during detached evaluation
     * @return compiled action bar text, or empty string if no content is visible
     */
    @NonNull
    public String compile(@Nullable Player player) {
        synchronized (alertLock) {
            modalAlerts.removeIf(TimedAlert::isExpired);
            if (!modalAlerts.isEmpty()) {
                String text = modalAlerts.peek().message(player);
                if (text != null && !text.isEmpty()) {
                    return text;
                }
            }
        }

        Map<String, ActionbarSection> globalSections = globalSectionsSupplier.get();
        Collection<ActionbarSection> sectionsToEvaluate;

        if (localSections.isEmpty()) {
            sectionsToEvaluate = globalSections != null ? globalSections.values() : Collections.emptyList();
        } else {
            Map<String, ActionbarSection> combined = new HashMap<>(globalSections != null ? globalSections : Collections.emptyMap());
            combined.putAll(localSections);
            sectionsToEvaluate = combined.values();
        }

        List<ActionbarSection> exclusiveSections = null;
        List<ActionbarSection> normalSections = null;

        for (ActionbarSection section : sectionsToEvaluate) {
            if (player != null && !section.visibility().test(player)) {
                continue;
            }
            if (section.exclusive()) {
                if (exclusiveSections == null) {
                    exclusiveSections = new ArrayList<>(2);
                }
                exclusiveSections.add(section);
            } else {
                if (normalSections == null) {
                    normalSections = new ArrayList<>(sectionsToEvaluate.size());
                }
                normalSections.add(section);
            }
        }

        if (exclusiveSections != null && !exclusiveSections.isEmpty()) {
            exclusiveSections.sort(Comparator.comparingInt(ActionbarSection::priority).reversed()
                    .thenComparing(ActionbarSection::id));
            for (ActionbarSection exclusiveSection : exclusiveSections) {
                String text = evaluateSection(exclusiveSection, player);
                if (text != null && !text.isEmpty()) {
                    return text;
                }
            }
        }

        if (normalSections == null || normalSections.isEmpty()) {
            return "";
        }

        normalSections.sort(Comparator.comparingInt(ActionbarSection::priority));
        List<ActionbarSection> activeSections = new ArrayList<>(normalSections.size());
        List<String> activeTexts = new ArrayList<>(normalSections.size());

        for (ActionbarSection section : normalSections) {
            String text = evaluateSection(section, player);
            if (text != null && !text.isEmpty()) {
                activeSections.add(section);
                activeTexts.add(text);
            }
        }

        if (activeTexts.isEmpty()) {
            return "";
        }

        if (activeTexts.size() == 1) {
            return activeTexts.getFirst();
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < activeTexts.size(); i++) {
            builder.append(activeTexts.get(i));
            if (i < activeTexts.size() - 1) {
                String delimiter = activeSections.get(i).separator();
                builder.append(delimiter != null ? delimiter : separator);
            }
        }

        return builder.toString();
    }

    private String evaluateSection(ActionbarSection section, @Nullable Player player) {
        TimedAlert alert = slotAlerts.get(section.id());
        if (alert != null) {
            if (!alert.isExpired()) {
                return alert.message(player);
            }
            slotAlerts.remove(section.id());
        }
        return section.textSupplier().apply(player);
    }
}
