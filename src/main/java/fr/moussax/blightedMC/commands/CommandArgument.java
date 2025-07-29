package fr.moussax.blightedMC.commands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CommandArguments.class)
public @interface CommandArgument {
  int position() default 0;
  String[] after() default {};
  String[] suggestions();
}

