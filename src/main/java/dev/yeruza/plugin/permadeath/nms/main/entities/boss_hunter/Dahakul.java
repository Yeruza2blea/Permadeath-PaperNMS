package dev.yeruza.plugin.permadeath.nms.main.entities.boss_hunter;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import org.bukkit.Location;

public class Dahakul extends BossFighter<Ghast> {
    public Dahakul(Location pos) {
        super(pos, "dahakul");

        this.entity = new Ghast(EntityType.GHAST, level) {
            @Override
            protected void registerGoals() {
                super.registerGoals();
            }

            @Override
            public boolean isPersistenceRequired() {
                return true;
            }
        };
    }

    @Override
    public void spawnEntity() {
    }
}
