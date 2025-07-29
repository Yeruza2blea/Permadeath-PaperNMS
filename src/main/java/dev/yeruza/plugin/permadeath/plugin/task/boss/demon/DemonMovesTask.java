package dev.yeruza.plugin.permadeath.plugin.task.boss.demon;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.ArrayList;
import java.util.List;

public class DemonMovesTask extends BukkitRunnable {
    private float yaw;
    private Permadeath plugin;
    private EnderDragon demon;

    private int ticksRotating = 0;
    private int currentPitchRotation = -360;
    private Location teleportLoc;
    private boolean spawnedPaticles = false;

    public DemonMovesTask(Permadeath plugin, EnderDragon demon, Location teleportLoc) {
        this.plugin = plugin;
        this.demon = demon;
        this.teleportLoc = teleportLoc;
    }

    @Override
    public void run() {

        if (demon.isDead() || plugin.getEndTask() == null) {
            cancel();
            return;
        }

        int ticks = 20 * 15;

        if (ticksRotating == ticks) {
            cancel();
            return;
        }

        ticksRotating += 5;

        if (currentPitchRotation >= 0)
            currentPitchRotation = -360;


        if (currentPitchRotation < 0) {

            if (demon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL)
                demon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);


            demon.setRotation(currentPitchRotation, 0);

            currentPitchRotation = currentPitchRotation + 30;
            if (!spawnedPaticles) {

                spawnedPaticles = true;

                List<Location> locations = new ArrayList<>();
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(7, 0, 7)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(7, 0, 0)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(7, 0, -7)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(0, 0, -7)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(0, 0, 7)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(-7, 0, 7)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(-7, 0, 0)).getLocation());
                locations.add(plugin.getEnd().getHighestBlockAt(demon.getLocation().add(-7, 0, -7)).getLocation());

                for (Location locs : locations) {

                    AreaEffectCloud a = (AreaEffectCloud) locs.getWorld().spawnEntity(locs.add(0, 1, 0), EntityType.AREA_EFFECT_CLOUD);

                    a.setParticle(Particle.CLOUD);
                    a.setRadius(5.0F);
                    a.setDuration(15 * 20);
                    a.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5, 3), false);
                    a.setColor(Color.WHITE);
                }
            }
        }
    }
}
