package dev.yeruza.plugin.permadeath.api.commands;

import dev.yeruza.plugin.permadeath.gaming.client.Permissions;

import java.lang.annotation.*;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String description();

    String usage() default "/{command.name}";

    String[] aliases() default {};

    Permissions permission() default Permissions.USER;
}
