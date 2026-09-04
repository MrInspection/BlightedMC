package fr.moussax.bedrock.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares positional tab completion suggestions for a command.
 *
 * <p>Multiple annotations may be declared on a single command class to define completion rules across
 * different argument positions, preceding argument paths, or permission constraints.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandArguments.class)
public @interface CommandArgument {

    /**
     * Zero-based argument index to complete.
     */
    int position();

    /**
     * Sequence of preceding literal argument steps required for these suggestions to apply.
     *
     * <p>Evaluated sequentially against the preceding arguments leading up to {@link #position()}.
     */
    String[] path() default {};

    /**
     * Permission node required for the sender to receive these suggestions, or an empty string if unconstrained.
     */
    String permission() default "";

    /**
     * Literal argument suggestions or dynamic suggestion keys starting with {@code $}.
     */
    String[] suggestions();
}
