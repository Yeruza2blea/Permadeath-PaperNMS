package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class SpecialBee extends Bee {
    public SpecialBee(Location where) {
        super(EntityType.BEE, ((CraftWorld) where.getWorld()).getHandle());

        setPos(where.getX(), where.getY(), where.getZ());

        getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12.0D);
        setHealth(100.0F);
        setRemainingPersistentAngerTime(1);

        //goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        setPersistenceRequired(false);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }
}
