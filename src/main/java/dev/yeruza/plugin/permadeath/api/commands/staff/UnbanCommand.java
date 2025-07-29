package dev.yeruza.plugin.permadeath.api.commands.staff;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.OfflinePlayer;
import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.time.Instant;

@Command(
        name = "unban",
        description = "Puedes desbanear a un jugador",
        usage = "/unban <username> [reason]",
        permission = Permissions.MOD
)
public class UnbanCommand extends MinecraftCommand {
    @Override
    protected void onExecute() {
        if (args.length == 0) {
            tellFail("Debe escribir el nombre del jugador o su UUID");
            return;
        }
        OfflinePlayer target = findPlayerByName(args[0]);

        TextComponent reason = TextFormat.write(args.length == 2 ? args[1] : "&cNo se especifico un motivo.");

        if (!target.isBanned())
            tellFail("&cEl jugador &6 " + target.getName() + " &cno se encuentra baneado o no existe");

        if (args[1] == null)
            reason = TextFormat.write("&cNo se especifico un motivo.\nConsulte con un administrador");

        target.ban(reason.content(), (Instant) null, "console");
        tellSuccess("&aEl jugador &6 " + target.getName() + " &aha sido baneado correctamente");
    }
}
