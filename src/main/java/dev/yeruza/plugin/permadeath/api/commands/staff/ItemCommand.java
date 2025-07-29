package dev.yeruza.plugin.permadeath.api.commands.staff;


import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.api.commands.Command;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommand;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;
import dev.yeruza.plugin.permadeath.plugin.item.PermadeathItems;

import java.util.ArrayList;


@Command(
        name = "item",
        description = "Puedes obtener los items de permadeath",
        usage = "/item <user> <item_id|kit>",
        permission = Permissions.ADMIN
)
public class ItemCommand extends MinecraftCommand {

    @Override
    protected void onExecute() {
        if (args.length == 0) {
            tellFail("Tienes que mencionar un objecto");
        }
        ItemStack stack = new ItemStack(PermadeathItems.HYPER_GOLDEN_APPLE_PLUS);

        tellSuccess("&ahaz recibido el item de &6" + stack.getItemMeta().getDisplayName() + " &acorrectamente");
        player.setItemOnCursor(stack);
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 1.0F);
    }

    @Override
    protected void onTabComplete() {
        tab = new ArrayList<>();


        if (args.length == 1) {
            tab.add("hyper_golden_apple_plus");
        }

    }
}
