package dev.yeruza.plugin.permadeath.plugin.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotemListener implements Listener {
    private final Permadeath plugin;

    public TotemListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNerfTotem(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING || player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
            if (!plugin.getConfig().getBoolean("totem-options.enable")) return;

            String playerName = player.getName();

            int failProb = 0;
            boolean containsDay;

            if (plugin.getConfig().contains("totem-options.fail-probs")) {

                List<String> data = plugin.getConfig().getStringList("totem-options.fail-probs");
                Pattern pattern = Pattern.compile("\\[(\\d[0-9])-(\\d[0-9])]:\\s\\d");

                for (String format : plugin.getConfig().getStringList("totem-options.fail-probs-list")) {
                    Matcher matcher = pattern.matcher(format);
                      while (matcher.find()) {
                          String[] splitter = matcher.group().split(":\\s");
                          String[] days = splitter[0].split("-");

                          int firstDay = Integer.parseInt(days[0].replace("[", ""));
                          int lastDay = Integer.parseInt(days[1].replace("]", ""));
                          if (firstDay > plugin.getDay() && plugin.getDay() < lastDay)
                              failProb = Integer.parseInt(splitter[1]);

                      }
                }
                containsDay = true;
            } else {
                System.out.println("[INFO] La probabilidad del tótem se encuentra desactivada para el día: " + plugin.getDay());
                containsDay = false;
            }

            String fail = plugin.getConfig().getString("totem-options.player-fail-message.totem");
            String msg = plugin.getConfig().getString("totem-options.played-used-message.totem");

            if (plugin.getDay() >= 40) {
                if (plugin.getDay() < 60) {
                    msg = plugin.getConfig().getString("totem-options.played-used-message.totems,").replace("{amount}", "2").replace("{player}", playerName);
                } else {
                    msg = plugin.getConfig().getString("totem-options.played-used-message.totem").replace("{amount}", "3").replace("{player}", playerName);
                }
            }

            for (String section : plugin.getConfig().getConfigurationSection("totem-option.fail-probs").getKeys(false)) {
                try {
                    int i = Integer.parseInt(section);
                    if (i > Permadeath.getPlugin().getDay())
                        containsDay = true;
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Ha ocurrido un error al cargar la probabilidad de tótem del día '" + section + "'");
                }
            }

            if (!containsDay) return;

            if (failProb >= 101)
                failProb = 100;
            if (failProb < 0)
                failProb = 1;

            if (failProb == 100) {
                Bukkit.broadcast(TextFormat.write(msg.replace( "{player}", playerName).replace("{porcent}", "=").replace("{totem-fail}", String.valueOf(100)).replace("{number}", String.valueOf(failProb))));
                Bukkit.broadcast(TextFormat.write(fail.replace("{player}", playerName)));
                event.setCancelled(true);
            } else {
                int random = (int) (Math.random() * 100) + 1;
                int minus = 100 - failProb;
                int show = minus;

                if (minus == random) show -= 1;

                int raShow = random;

                if (random == minus) raShow -= 1;

                if (Permadeath.getPlugin().getDay() < 40) {
                    if (doPlayerHaveSpecialTotem(player)) {
                        ItemStack item = getTotem(player);
                        player.getInventory().removeItem(item);
                        Bukkit.broadcast(TextFormat.write(Permadeath.getPlugin().getConfig().getString("totem-options.medal").replace("%player%", player.getName())));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0, 1);
                        return;
                    }

                    String bcMsg = msg.replace("{player}", playerName).replace("{porcent}", "!=").replace("%totem-fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(minus));

                    if (random < minus) {
                        Bukkit.broadcast(TextFormat.write(bcMsg));
                        Bukkit.broadcast(TextFormat.write(msg.replace("{player}", playerName)));
                        event.setCancelled(true);
                    } else {
                        Bukkit.broadcast(TextFormat.write(bcMsg));
                    }
                } else {
                    int neededTotems = (Permadeath.getPlugin().getDay() < 60 ? 2 : 3);
                    int totems = player.getInventory().all(Material.TOTEM_OF_UNDYING).size();

                    if (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING)
                        totems++;

                    int removedTotems = 0;
                    boolean hasTotem = doPlayerHaveSpecialTotem(player);


                    if (hasTotem) {
                        ItemStack item = getTotem(player);

                        if (getSpecialTotem(player) == EquipmentSlot.OFF_HAND) {
                            player.getInventory().setItemInOffHand(null);
                        } else {
                            player.getInventory().removeItem(item);
                        }
                        removedTotems++;
                    } else {
                        if (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                            player.getInventory().setItemInOffHand(null);
                            removedTotems++;
                        }
                    }

                    for (ItemStack item : player.getInventory().getContents())
                        if (item != null && item.getType() == Material.TOTEM_OF_UNDYING)
                            if (removedTotems < neededTotems) {
                                player.getInventory().removeItem(item);
                                neededTotems++;
                            }

                    if (totems < neededTotems) {
                        Bukkit.broadcast(TextFormat.write(Permadeath.getPlugin().getConfig().getString("totem-options.not-enough-totems").replace("{player}", playerName).replace("{porcent}", "=").replace("{totem-fail}", String.valueOf(show)).replace("{number}", String.valueOf(minus))));
                        event.setCancelled(true);
                        return;
                    }

                    if (hasTotem) {
                        Bukkit.broadcast(TextFormat.write(Permadeath.getPlugin().getConfig().getString("totem-options.medal").replace("{player}", player.getName())));
                        return;
                    }

                    if (random > minus) {
                        Bukkit.broadcast(TextFormat.write(msg.replace("{player}", playerName).replace("{porcent}", "=").replace("{totem-fail}", String.valueOf(show)).replace("{number}", String.valueOf(minus))));
                        Bukkit.broadcast(TextFormat.write(plugin.getConfig().getString("totem-options.message-fails").replace("{player}", playerName)));
                        event.setCancelled(true);
                    } else {

                        Bukkit.broadcast(TextFormat.write(msg.replace("{player}", playerName).replace("%porcent%", "!=").replace("%totem-fail%", String.valueOf(raShow)).replace("%number%", String.valueOf(minus))));
                    }
                }
            }
        }
    }

    private ItemStack getTotem(Player player) {
        return getSpecialTotem(player) == EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
    }

    private EquipmentSlot getSpecialTotem(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (isEspecial(mainHand))
            return EquipmentSlot.HAND;
        else if (isEspecial(offHand))
            return EquipmentSlot.OFF_HAND;
        else
            return null;

    }

    private boolean doPlayerHaveSpecialTotem(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (mainHand.getType() == Material.TOTEM_OF_UNDYING && mainHand.getItemMeta().getCustomModelDataComponent().getStrings().contains("medal"))
            return true;

        return offHand.getType() == Material.TOTEM_OF_UNDYING && offHand.getItemMeta().getCustomModelDataComponent().getStrings().contains("medal");
    }

    private boolean isEspecial(@NotNull ItemStack hand) {
        return hand.getType() == Material.TOTEM_OF_UNDYING && hand.getItemMeta().isUnbreakable();
    }
}
