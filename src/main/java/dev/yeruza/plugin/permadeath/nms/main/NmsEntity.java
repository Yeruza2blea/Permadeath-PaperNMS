package dev.yeruza.plugin.permadeath.nms.main;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import dev.yeruza.plugin.permadeath.Permadeath;

public abstract class NmsEntity<E extends Entity> {
    public static final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
    public static final ResourceLocation ENTITY_ID = CraftNamespacedKey.toMinecraft(Permadeath.withCustomNamespace("nms_entity"));

    private final KeyId<String> key;
    protected final ResourceLocation id;
    protected final CompoundTag data = new CompoundTag();
    protected final ServerLevel level;
    protected final Location where;
    protected E entity;

    public NmsEntity(E entity, KeyId<String> key) {
        this.key = key;
        this.entity = entity;
        this.where = entity.getBukkitEntity().getLocation();
        this.level = (ServerLevel) entity.level();
        this.id = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getValue());


        data.putString(key.getKey(), key.getValue());
    }

    public NmsEntity(Location where, KeyId<String> key) {
        this.level = ((CraftWorld) where.getWorld()).getHandle();
        this.key = key;
        this.where = where;
        this.id = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getValue());
        

        data.putString(key.getKey(), key.getValue());
    }

    public abstract void spawnEntity();


    public org.bukkit.entity.Entity getEntity() {
        return entity.getBukkitEntity();
    }

    public final NamespacedKey getId() {
        return CraftNamespacedKey.fromMinecraft(id);
    }

    public final org.bukkit.World getWorld() {
        return level.getWorld();
    }

    public CraftEntity getCB() {
        return entity.getBukkitEntity();
    }

    public E getNMS() {
        return entity;
    }

    public record Settings() {
        public Settings {

        }
    }
}
