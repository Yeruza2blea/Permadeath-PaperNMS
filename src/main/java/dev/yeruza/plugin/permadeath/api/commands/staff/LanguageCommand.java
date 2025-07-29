package dev.yeruza.plugin.permadeath.api.commands.staff;

import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.plugin.Language;
import dev.yeruza.plugin.permadeath.data.PlayerManager;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;

import java.util.ArrayList;

@Command(
        name = "language",
        description = "Puedes cambiar el idioma que prefieras",
        usage = "/language <set|reset> <language>",
        permission = Permissions.MOD
)
public class LanguageCommand extends MinecraftCommand {

    @Override
    protected void onExecute() {
        PlayerManager manager = new PlayerManager(player, plugin);

        switch (args[0]) {
            case "set" -> {
                Language language = findEnum(Language.class, args[1]);
                manager.setLanguage(language);
            }
            case "reset" -> {
                manager.setLanguage(Language.SPANISH);
            }
        }

    }

    @Override
    protected void onTabComplete() {
        tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("set");
            tab.add("reset");
        }


    }
}
