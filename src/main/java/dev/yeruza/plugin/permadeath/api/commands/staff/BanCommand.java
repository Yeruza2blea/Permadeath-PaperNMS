package dev.yeruza.plugin.permadeath.api.commands.staff;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.time.Instant;
import java.util.ArrayList;

@Command(
        name = "ban",
        description = "Puedes banear a un jugador",
        usage = "/ban <player> [reason]",
        permission = Permissions.MOD
)
public class BanCommand extends MinecraftCommand {
    @Override
    protected void onExecute() {
        if (args.length == 0) {
            tellFail("Debe escribir el nombre del jugador o su UUID");
            return;
        }

        Player target = findPlayerByName(args[0]);

        TextComponent reason = TextFormat.write(args.length == 2 ? args[1] : "&cNo se especifico un motivo.\nConsulte con un administrador");

        if (target == null) {
            tellFail("El nombre de '&6" + args[0] + "&a' no existe");
            return;
        }

        if (target.isBanned()) {
            tellFail("El jugador &6" + target.getName() + " &cya estaba baneado");
            return;
        }

        target.ban(reason.content(), (Instant) null, "console");
        tellSuccess("&aEl jugador &6" + target.getName() + " &aha sido baneado correctamente");
    }

    @Override
    protected void onTabComplete() {
        tab = new ArrayList<>();

        if (args.length == 1)
            for (Player p : Bukkit.getOnlinePlayers()) {
                tab.add(p.getName());
            }

    }
}
