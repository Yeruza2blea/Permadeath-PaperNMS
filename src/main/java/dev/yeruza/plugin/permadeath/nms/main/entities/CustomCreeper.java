package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import dev.yeruza.plugin.permadeath.nms.main.NmsEnderMob;

public class CustomCreeper extends Creeper implements NmsEnderMob {
    private final boolean ender;

    public CustomCreeper(EntityType<? extends Creeper> type, Level level, boolean ender) {
        super(type, level);
        this.ender = ender;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new SwellGoal(this));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void markHurt() {
        super.markHurt();
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource damagesource, float f) {
        if (isInvulnerableTo(world, damagesource)) {
            return false;
        } else if (ender) {
            boolean flag1;
            if (!damagesource.is(DamageTypeTags.IS_PROJECTILE) && !(damagesource.getDirectEntity() instanceof AbstractThrownPotion)) {
                flag1 = super.hurtServer(world, damagesource, f);
                if (!level().isClientSide() && !(damagesource.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
                    teleport();
                }

                return flag1;
            } else {
                for(int i = 0; i < 64; ++i)
                    if (teleport())
                        return true;

                return super.hurtServer(world,damagesource, f);
            }
        }

        return super.hurtServer(world, damagesource, f);
    }

    @Override
    public boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = getX() + (random.nextDouble() - 0.5) * 64.0;
            double d1 = getY() + (double) (random.nextInt(64) - 32);
            double d2 = getZ() + (random.nextDouble() - 0.5) * 64.0;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }


    private boolean teleport(double d0, double d1, double d2) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(d0, d1, d2);

        while (mutableBlockPos.getY() > level().getMinY() && !this.level().getBlockState(mutableBlockPos).blocksMotion()) {
            mutableBlockPos.move(Direction.DOWN);
        }

        BlockState state = level().getBlockState(mutableBlockPos);
        boolean flag = state.blocksMotion();
        boolean flag1 = state.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            Vec3 vec3d = position();
            boolean flag2 = randomTeleport(d0, d1, d2, true);
            if (flag2) {
                level().gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(this));
                if (!isSilent()) {
                    level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, getSoundSource(), 1.0F, 1.0F);
                    playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }
}
