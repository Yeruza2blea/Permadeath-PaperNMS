package dev.yeruza.plugin.permadeath.api.commands.user;

import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;

@Command(
        name = "day",
        description = "Puedes ver cuantos días tiene el servidor"
)
public class DayCommand extends MinecraftCommand {

    @Override
    protected void onExecute() {
        tellSuccess("Estamos en el día &c" + plugin.getDay());
    }
}
