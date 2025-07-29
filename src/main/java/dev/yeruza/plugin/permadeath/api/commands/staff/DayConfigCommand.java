package dev.yeruza.plugin.permadeath.api.commands.staff;

import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;

import java.util.ArrayList;
import java.util.List;

@Command(
        name = "day-config",
        description = "Puedes gestionar los días en el servidor",
        usage = "/pd_day <add|remove|set> <number>",
        permission = Permissions.MOD
)
public class DayConfigCommand extends MinecraftCommand {
    @Override
    protected void onExecute() {
        switch (args[0]) {
            case "add" -> {
                int value = Integer.parseInt(args[1]);
                if (value > 120)
                    tellFail("No se puede agregar más días de 120");

                int day = (int) (plugin.getDay() + value);
                plugin.getDateData().setDay(sender, String.valueOf(day));
                tellSuccess("&cSe establecio en el día &4&l" + day);
            }

            case "remove" -> {
                int day = (int) (plugin.getDay() - Integer.parseInt(args[1]));
                if (day <= 0)
                    plugin.getDateData().setDay(sender, String.valueOf(0));

                plugin.getDateData().setDay(sender, String.valueOf(day));
                tellSuccess("&cSe establecio en el día &4&l" + day);
            }

            case "set" -> {
                int day = Integer.parseInt(args[1]);
                if (day < 0 || day > 120)
                    tellFail("&cNo se puede cambiar de día al valor número" + day);

                plugin.getDateData().setDay(sender, String.valueOf(day));
                tellSuccess("&cSe establecio en el día &4&l" + plugin.getDay());
            }

            default -> tellFail("&cUso correcto del comando:" + getUsageMessage());

        }
    }

    @Override
    protected void onTabComplete() {
        tab = new ArrayList<>();

        if (args.length == 1) {
            tab.addAll(List.of("add", "remove", "set"));
        }
    }
}
