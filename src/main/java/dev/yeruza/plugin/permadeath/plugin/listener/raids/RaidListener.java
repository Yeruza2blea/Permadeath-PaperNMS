package dev.yeruza.plugin.permadeath.plugin.listener.raids;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

public class RaidListener implements Listener {

    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
        if (Permadeath.getPlugin().getDay() < 50) return;
        if (event.getWinners().isEmpty()) return;

        Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {
            for (Player player : event.getWinners())
                if (player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {

                    PotionEffect effect = player.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
                    player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);

                    int min = 5 * 60;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, min, effect.getAmplifier()));
                }
        }, 10L);
    }

    @EventHandler
    public void onRaidSpawnWave(RaidSpawnWaveEvent event) {
        if (Permadeath.getPlugin().getDay() < 50) return;

        Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {
            if (event.getRaid().isStarted()) {
                for (Player player : event.getWorld().getPlayers()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.MASTER, 1, 1);

                    Bukkit.createBossBar(TextFormat.write("&6Death Raid").content(), BarColor.RED, BarStyle.SOLID);

                }

            }
        }, 10L);
    }
}
