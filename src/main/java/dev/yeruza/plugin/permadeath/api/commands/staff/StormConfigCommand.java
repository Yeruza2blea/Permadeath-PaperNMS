package dev.yeruza.plugin.permadeath.api.commands.staff;

import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;

@Command(
        name = "storm",
        description = "puedes configurar la tormenta del servidor",
        usage = "/storm <add|remove> <time_format>",
        permission = Permissions.ADMIN
)
public class StormConfigCommand extends MinecraftCommand {
    long stormTicks;

    @Override
    protected void onExecute() {


        switch (args[0]) {
            case "add" -> {
                if (player.getWorld().hasStorm())
                    tellFail("Solo se aumenta el tiempo sí el mundo tiene una death-train");

                int stormDuration = plugin.getOverWorld().getWeatherDuration();
                int stormTicks = stormDuration / 20;
                long increment = stormTicks + this.stormTicks;
                int ticks = (int) this.stormTicks;
                int inc = (int) increment;

                player.getWorld().setWeatherDuration(inc * 20);
            }
            case "remove" -> {

            }
        }
    }

    @Override
    protected void onTabComplete() {
        if (args.length == 1) {
            
        }
    }
}
