package dev.yeruza.plugin.permadeath.plugin.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.core.PluginManager;

public class HostilyEntityListener implements Listener {
    private final Permadeath plugin;

    public HostilyEntityListener(Permadeath plugin) {
        this.plugin = plugin;
        init();
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled())
            return;

        if (plugin.getDay() >= 20 && PluginManager.isHostileMob(event.getEntityType()) && event.getEntityType() != EntityType.ARMOR_STAND && event.getEntityType() != EntityType.ENDERMAN)
            injectHostileBehavior(event.getEntity());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getDay() < 20 || event.isNewChunk()) return;

        for (Entity entity : event.getChunk().getEntities()) {
            if (!entity.isValid() || entity.isDead()) continue;
            if (!(entity instanceof LivingEntity) || entity instanceof Player)
                continue;

            if (entity instanceof Villager && plugin.getDay() >= 60) {
                entity.getWorld().spawn(entity.getLocation(), Vindicator.class);
                entity.remove();
                return;
            }

            injectHostileBehavior((LivingEntity) entity);
        }
    }

    public void init() {
        if (plugin.getDay() >= 20) {
            for (World world : Bukkit.getWorlds())
                for (LivingEntity entity : world.getLivingEntities()) {
                    EntityType type = entity.getType();

                    if (PluginManager.isHostileMob(type) && type != EntityType.ENDERMAN)
                        injectHostileBehavior(entity);

                }
        }
    }

    private void injectHostileBehavior(LivingEntity entity) {
        plugin.getNmsEntity(entity).injectHostilePathfinders();
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) == null)
            plugin.getNmsEntity(entity).registerAttribute(Attribute.ATTACK_DAMAGE, 8.0D);
    }
}
