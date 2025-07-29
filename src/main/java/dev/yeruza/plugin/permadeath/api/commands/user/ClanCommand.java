package dev.yeruza.plugin.permadeath.api.commands.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.api.mongodb.models.ServerClan;
import dev.yeruza.plugin.permadeath.api.mongodb.models.ServerUser;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.List;
import java.util.UUID;

@Command(
        name = "clan",
        description = "te permite gestionar tu propio clan"
)
public class ClanCommand extends MinecraftCommand {


    @Override
    protected void onExecute() {
        MongoDatabase database = plugin.getMongoDriver().getDatabase();
        MongoCollection<ServerClan> clans = database.getCollection("clans", ServerClan.class);

        switch (args[0]) {
            case "create" -> {
                if (args[1] == null)
                    tellFail("&cTienes que poner un nombre al clan");

                String name = args[1];
                NamedTextColor color = NamedTextColor.nearestTo(TextColor.fromHexString(args[2]));


                Scoreboard template = Bukkit.getScoreboardManager().getNewScoreboard();
                Team clanBase = template.registerNewTeam(args[1].replace(" ", "_"));
                clanBase.displayName(TextFormat.write(args[1]));

                ServerUser user = ServerUser.of(player);

                clans.insertOne(new ServerClan(user, name, List.of(), UUID.randomUUID(), List.of(), color));
                tellSuccess("&aEl clan &6&l[" + "&r" + clans.find(Filters.eq("name", name)).first().name() + "&6&l] &a ha sido creado correctamente");
            }
            case "invite" -> {
                Player invited = findPlayerByIndex(1);

                if (invited == null) {
                    tellFail("&cNo se encontro a un jugador con ese nombre o uuid");
                    return;
                }

                ServerClan clan = clans.find(Filters.eq("leader", player.getName())).first();

                if (clan == null) {
                    tellFail("&cTienes que crear un clan");
                    return;
                }

                invited.sendMessage(TextFormat.withCodef("&6%s &ate invito a su clan %s", player.getName(), clan.name()));
                invited.addScoreboardTag("invited");
                clans.updateOne(clan, new ServerClan(ServerUser.of(player), clan.name(), clan.description(), clan.id(), clan.members(), clan.color()));


            }
            case "delete" -> {
                if (args[1] == null)
                    tellFail("&cTienes que poner un nombre del clan");

                String name = args[1];

                ServerClan clan = clans.find(Filters.eq("name", name)).first();

                clans.deleteOne(clan);
            }
            case "accept" -> {
                
            }
            case "deny" -> {

            }
        }
    }

}
