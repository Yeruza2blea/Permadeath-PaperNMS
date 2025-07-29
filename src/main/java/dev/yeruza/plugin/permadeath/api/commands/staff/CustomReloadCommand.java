package dev.yeruza.plugin.permadeath.api.commands.staff;

import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;

@Command(
        name = "pd-reload",
        description = "Vale",
        permission = Permissions.ADMIN
)
public class CustomReloadCommand extends MinecraftCommand {
    @Override
    protected void onExecute() {

        plugin.reload(sender);
    }
}
