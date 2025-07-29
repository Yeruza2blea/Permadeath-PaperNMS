package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class SpecialCow extends Cow {
    public SpecialCow(Location where) {
        super(EntityType.COW, ((CraftWorld) where.getWorld()).getHandle());

        getAttribute(Attributes.MAX_HEALTH).setBaseValue(50.0D);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(15.0D);
        setHealth(50.0F);
    }

    @Override
    public void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));

        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
