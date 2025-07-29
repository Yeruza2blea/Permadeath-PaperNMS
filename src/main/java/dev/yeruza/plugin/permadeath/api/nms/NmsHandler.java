package dev.yeruza.plugin.permadeath.api.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public interface NmsHandler {
    <T extends Entity> T spawnCustomEntity(String classPath, Location loc, CreatureSpawnEvent.SpawnReason reason);

    <T extends Entity> T spawnCustomEntityEnder(String classPath, Location loc, CreatureSpawnEvent.SpawnReason reason);

    <T extends Entity> T spawnEntity(Class<T> type, Location loc, CreatureSpawnEvent.SpawnReason reason);

    void spawnMobMushrooms();
}
