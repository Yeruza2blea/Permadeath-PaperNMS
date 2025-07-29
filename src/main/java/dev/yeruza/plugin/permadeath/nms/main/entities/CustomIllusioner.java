package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Illusioner;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class CustomIllusioner extends Illusioner {
    public CustomIllusioner(Location where) {
        super(EntityType.ILLUSIONER, ((CraftWorld) where.getWorld()).getHandle());
    }


}

