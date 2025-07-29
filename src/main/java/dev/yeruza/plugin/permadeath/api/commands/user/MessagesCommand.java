package dev.yeruza.plugin.permadeath.api.commands.user;

import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;

@Command(
        name = "messages",
        description = "Te permite poner mensajes personalizado",
        usage = "/messages <join|leave|death> <text>"
)
public class MessagesCommand extends MinecraftCommand {

    @Override
    protected void onExecute() {
        if (args.length == 0) {
            tellSuccess(getUsageMessage());
        }

        switch (args[0]) {
            case "join" -> {

            }
            case "leave" -> {

            }
            case "death" -> {

            }
        }

    }

    @Override
    protected void onTabComplete() {
        super.onTabComplete();
    }
}
