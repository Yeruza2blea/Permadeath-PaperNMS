package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ravager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class UltraRavager extends Ravager {
    public UltraRavager(Location where) {
        super(EntityType.RAVAGER, ((CraftWorld) where.getWorld()).getHandle());
        setPos(where.getX(), where.getY(), where.getZ());
        setPersistenceRequired();
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }
}
