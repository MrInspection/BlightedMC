package fr.moussax.blightedMC.shared.ui.actionbar;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class ActionBarComposer {
    private final Map<String, ActionBarComponent> components = new LinkedHashMap<>();
    private final Map<String, ActionBarOverride> overrides = new HashMap<>();
    @Setter
    private String separator = "     ";

    public void registerComponent(ActionBarComponent component) {
        components.put(component.getId(), component);
    }

    public void unregisterComponent(String componentId) {
        components.remove(componentId);
        overrides.remove(componentId);
    }

    public void setOverride(String componentId, ActionBarOverride override) {
        overrides.put(componentId, override);
    }

    public void clearOverride(String componentId) {
        overrides.remove(componentId);
    }

    @Nullable
    public ActionBarComponent getComponent(String id) {
        return components.get(id);
    }

    public String compose(BlightedPlayer player) {
        ActionBarOverride activeOverride = getActiveOverride(player);
        if (activeOverride != null) {
            return activeOverride.resolve(player);
        }

        return components.values().stream()
            .filter(component -> component.shouldDisplay(player))
            .sorted(Comparator.comparingInt(ActionBarComponent::getPriority))
            .map(component -> component.resolve(player))
            .filter(Objects::nonNull)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining(separator));
    }

    @Nullable
    private ActionBarOverride getActiveOverride(BlightedPlayer player) {
        return overrides.values().stream()
            .filter(override -> override.isActive(player))
            .max(Comparator.comparingInt(ActionBarOverride::getPriority))
            .orElse(null);
    }
}