package fr.moussax.blightedMod.moderator.punishments;

import java.util.Arrays;
import org.jspecify.annotations.Nullable;

/**
 * Encapsulates parsed optional expiration timestamp and reason from punishment command arguments.
 *
 * @param expiresAt expiration timestamp in milliseconds, or {@code null} if permanent
 * @param reason    punishment reason specified, or "No reason specified" if omitted
 */
public record PunishmentArguments(@Nullable Long expiresAt, String reason) {

    /**
     * Parses expiration duration and reason from command arguments starting at a specified index.
     *
     * @param arguments  raw command arguments
     * @param startIndex index after target player argument (typically 1)
     * @return parsed punishment arguments context
     */
    public static PunishmentArguments parse(String[] arguments, int startIndex) {
        Long expiresAt = arguments.length > startIndex ? DurationParser.parseDuration(arguments[startIndex]) : null;
        int reasonStartIndex = expiresAt != null ? startIndex + 1 : startIndex;

        String reason = arguments.length > reasonStartIndex
                ? String.join(" ", Arrays.copyOfRange(arguments, reasonStartIndex, arguments.length))
                : "No reason specified";

        return new PunishmentArguments(expiresAt, reason);
    }
}
