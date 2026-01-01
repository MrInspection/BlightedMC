package fr.moussax.blightedMC.smp.core.shared.ui.sign;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public record SignInputResult(@NonNull Player player, String @NonNull [] lines) {
    public String getFirstLine() {
        return getLine(0);
    }

    public String getLine(int index) {
        if (index < 0 || index >= lines.length) return "";
        return lines[index] != null ? lines[index] : "";
    }
}
