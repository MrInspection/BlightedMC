package fr.moussax.blightedMC.commands.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares suggestions for one positional command argument.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandArguments.class)
public @interface CommandArgument {

    /**
     * Zero-based argument position.
     */
    int position();

    /**
     * Optional values required at the preceding argument position.
     */
    String[] after() default {};

    /**
     * Literal suggestions or registered suggestion keys.
     */
    String[] suggestions();
}
