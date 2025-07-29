package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

public class CustomGhast extends Ghast {
    public CustomGhast(Level level) {
        super(EntityType.GHAST, level);
    }
}