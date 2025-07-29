package dev.yeruza.plugin.permadeath.nms.main;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.nms.main.entities.CustomGhast;
import dev.yeruza.plugin.permadeath.nms.main.entities.boss_hunter.BossFighter;
import dev.yeruza.plugin.permadeath.core.PluginManager;

import java.lang.reflect.InvocationTargetException;

public final class NmsHandler {
    private final Permadeath plugin;

    public NmsHandler(Permadeath plugin) {
        this.plugin = plugin;
    }

    public <T extends org.bukkit.entity.Entity> T spawnEntity(Class<? extends T> clazz, Location pos, CreatureSpawnEvent.SpawnReason reason) {
        T entity = pos.getWorld().spawn(pos, clazz);

        EntityType<?> nmsType = CraftEntityType.bukkitToMinecraft(entity.getType());

        ServerLevel nmsWorld = ((CraftWorld) pos.getWorld()).getHandle();
        Vec3i coords = new Vec3i(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());


        if (isNotType(nmsType) || (reason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG || Math.random() <= 0.02004008016)) {
            Entity nmsEntity = ((CraftEntity) entity).getHandle();
            nmsEntity.setPos(coords.getX(), coords.getY(), coords.getZ());
            nmsEntity.setXRot(pos.getPitch());
            nmsEntity.setYRot(pos.getYaw());

            nmsWorld.addFreshEntity(nmsEntity, reason);

            return (T) nmsEntity.getBukkitEntity();
        }

        return entity;
    }


    public <T extends org.bukkit.entity.Entity> T spawnCustomEntity(String className, Location pos, CreatureSpawnEvent.SpawnReason reason) {
        try {
            ServerLevel world = ((CraftWorld) pos.getWorld()).getHandle();
            Class<?> clazz = PluginManager.getClass("entities.%s", className);
            LivingEntity nmsEntity = (LivingEntity) clazz.getConstructor(Location.class).newInstance(pos);

            nmsEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            nmsEntity.setXRot(pos.getPitch());
            nmsEntity.setYRot(pos.getYaw());

            world.addFreshEntity(nmsEntity, reason);

            return (T) nmsEntity.getBukkitEntity();
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {

            ex.printStackTrace();
        }

        return null;
    }

    public <E extends CraftLivingEntity, T extends BossFighter<?>> E spawnBoss(Class<T> classData, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();

        try {
           T boss = classData.getConstructor(Location.class).newInstance(loc);
           boss.initBossBar();
           boss.spawnEntity();

           world.addFreshEntity(boss.getNMS(), reason);

           return (E) boss.getEntity();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();

            return null;
        }
    }

    public CraftGhast spawnCustomGhast(Location location, boolean isEnder) {
        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();

        Ghast ghast = new CustomGhast(world);
        ghast.setPos(location.getX(), location.getY(), location.getZ());
        if (isEnder) {
            ghast.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
            ghast.setHealth(100.0F);
            ghast.setCustomName(CraftChatMessage.fromStringOrNull("§6Ender Ghast"));
            ghast.setCustomNameVisible(false);

            ghast.getBukkitEntity().getPersistentDataContainer().set(Permadeath.withCustomNamespace("ender_ghast"), PersistentDataType.BYTE, (byte) 1);
        }

        return (CraftGhast) ghast.getBukkitEntity();
    }


    public void spawnMobMushrooms() {
        Biome biome = CraftBiome.bukkitToMinecraft(org.bukkit.block.Biome.MUSHROOM_FIELDS);


    }

    private boolean isNotType(EntityType<?> type) {
        return type != EntityType.BAT && type != EntityType.COD && type != EntityType.SALMON && type !=EntityType.SQUID && type != EntityType.PUFFERFISH && type != EntityType.TROPICAL_FISH;
    }
}
