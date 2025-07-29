package dev.yeruza.plugin.permadeath.nms.main.entities.boss_hunter;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.MagmaCube;
import org.bukkit.Location;

public class CerebrateLottash extends BossFighter<MagmaCube> {
    public static final int MAX_HEALTH = 2000;

    public static AttributeSupplier.Builder createAttributes() {
        return BossFighter.createAttributes();
    }

    public CerebrateLottash(Location where) {
        super(where,"lottash");

        this.entity = new MagmaCube(EntityType.MAGMA_CUBE, level) {
            @Override
            protected void registerGoals() {
                super.registerGoals();
            }

            public boolean isPersistenceRequired() {
                return true;
            }
        };

    }

    @Override
    public void spawnEntity() {

    }

    @Override
    public org.bukkit.entity.MagmaCube getEntity() {
        return (org.bukkit.entity.MagmaCube) super.getEntity();
    }
}
