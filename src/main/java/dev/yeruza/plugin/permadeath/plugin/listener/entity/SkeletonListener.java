package dev.yeruza.plugin.permadeath.plugin.listener.entity;

import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import dev.yeruza.plugin.permadeath.Permadeath;

public class SkeletonListener implements Listener {
    private final Permadeath plugin;

    public SkeletonListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Skeleton skeleton)) return;

        if (plugin.getDay() >= 60 && skeleton.getPersistentDataContainer().has(Permadeath.withCustomNamespace("demon_skeleton"), PersistentDataType.BYTE)) {

            if (event.getEntity() != null) {

                Entity hit = event.getHitEntity();
                hit.getWorld().createExplosion(hit.getLocation(), 3f, true, true, skeleton);

            }
            if (event.getHitBlock() != null) {
                event.getEntity().getWorld().createExplosion(event.getHitBlock().getLocation(), 3f, true, true, skeleton);
            }
        }
    }

    @EventHandler
    public void onNDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Skeleton skeleton && skeleton.getPersistentDataContainer().has(Permadeath.withCustomNamespace("skeleton_definitive"), PersistentDataType.BYTE)) {

                try {
                    if (event.getEntity() instanceof LivingEntity entity) {
                        entity.damage(entity.getHealth());
                    }
                } catch (Exception x) {
                    x.printStackTrace(System.err);
                }
            }
        }
        if (event.getDamager() instanceof ShulkerBullet bullet) {
            if (bullet.getShooter() instanceof Shulker shulker && shulker.getColor() == DyeColor.RED && event.getEntity().getType() == EntityType.CAVE_SPIDER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Shulker shulker && shulker.getColor() == DyeColor.RED && event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
            event.setCancelled(true);
        }
    }
}
