package dev.yeruza.plugin.permadeath.nms.main.entities.boss_hunter;

import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import net.minecraft.world.entity.monster.warden.Warden;
import dev.yeruza.plugin.permadeath.nms.main.NmsEnderMob;

public class Salkkuytah extends BossFighter<Warden> implements NmsEnderMob {
    public Salkkuytah(Location where) {
        super(where,"salkkuytah");

        this.entity = new Warden(EntityType.WARDEN, level) {
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

    @Override
    public org.bukkit.entity.Warden getEntity() {
        return (org.bukkit.entity.Warden) super.getEntity();
    }

    @Override
    public boolean teleport() {
        return false;
    }
}
