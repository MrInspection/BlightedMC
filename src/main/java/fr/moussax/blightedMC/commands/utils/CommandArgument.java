package fr.moussax.blightedMC.commands.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares positional tab completion suggestions for a command.
 *
 * <p>Supports literal suggestions and dynamic key placeholders resolved by a
 * {@link TabSuggestionRegistry}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandArguments.class)
public @interface CommandArgument {

    /**
     * Zero-based argument position to complete.
     */
    int position();

    /**
     * Optional values required at the preceding argument position for these suggestions to apply.
     */
    String[] after() default {};

    /**
     * Literal argument suggestions or dynamic suggestion keys (such as {@code $players}).
     */
    String[] suggestions();
}
