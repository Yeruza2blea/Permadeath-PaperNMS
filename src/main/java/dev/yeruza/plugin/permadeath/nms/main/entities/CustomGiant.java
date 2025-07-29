package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class CustomGiant extends Giant {
    public CustomGiant(Location where) {
        super(EntityType.GIANT, ((CraftWorld) where.getWorld()).getHandle());
        setPos(where.getX(), where.getY(), where.getZ());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2000.0D);
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(600.0D);
        setHealth(600.0F);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        targetSelector.addGoal(0, new MeleeAttackGoal(this, 1.0, true));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
}
