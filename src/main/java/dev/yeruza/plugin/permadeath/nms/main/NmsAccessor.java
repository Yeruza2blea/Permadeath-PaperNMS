package dev.yeruza.plugin.permadeath.nms.main;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NmsAccessor {
    private Field mapper;
    private LivingEntity entity;
    private Permadeath plugin;

    public NmsAccessor(Permadeath plugin) {


       try {

            mapper = AttributeMap.class.getDeclaredField("attributes");
            mapper.setAccessible(true);
       } catch (NoSuchFieldException e) {
           e.printStackTrace();
       }
    }

    public NmsAccessor getNmsEntity(LivingEntity entity) {
        this.entity = entity;

        return this;
    }

    public NmsAccessor getNmsEntity(org.bukkit.entity.LivingEntity entity) {
        this.entity = ((CraftLivingEntity) entity).getHandle();

        return this;
    }

    public void setMaxHealth(double health) {
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
    }


    public double getMaxHealth() {
        return entity.getMaxHealth();
    }

    public void injectHostilePathfinders() {
        if (entity instanceof PathfinderMob pathfinder) {
            if (entity.getType() != EntityType.LLAMA && entity.getType() != EntityType.PANDA) {
                GoalSelector selector = pathfinder.goalSelector;

                AtomicBoolean meleeGoal = new AtomicBoolean(false);
                selector.removeAllGoals(goal -> {
                    if (goal.getClass() == MeleeAttackGoal.class) {
                        meleeGoal.set(true);
                    }
                    return (goal.getClass() == AvoidEntityGoal.class || goal.getClass() == PanicGoal.class);
                });

                if (!meleeGoal.get()) {
                    selector.addGoal(0, new MeleeAttackGoal(pathfinder, 1.0D, true));
                }
            }

            GoalSelector target = pathfinder.targetSelector;
            target.addGoal(0, new NearestAttackableTargetGoal<>(pathfinder, Player.class, true));
        }
    }

    public void registerAttribute(org.bukkit.attribute.Attribute attribute, double value) {
        Holder<Attribute> nmsAttribute = CraftAttribute.bukkitToMinecraftHolder(attribute);

        AttributeInstance modifier = new AttributeInstance(nmsAttribute, AttributeInstance::getAttribute);

        try {
            Map<Holder<Attribute>, AttributeInstance> attributes = (Map<Holder<Attribute>, AttributeInstance>) mapper.get(entity.getAttributes());
            attributes.put(nmsAttribute, modifier);

            mapper.set(entity.getAttributes(), attributes);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            Permadeath.getPlugin().getLogger().severe(e.getMessage());
        }

        entity.getAttribute(nmsAttribute).setBaseValue(value);
    }


    public void registerAttribute(org.bukkit.attribute.Attribute attribute, double value, LivingEntity entity) {
        Holder<Attribute> nmsAttribute = CraftAttribute.bukkitToMinecraftHolder(attribute);

        AttributeInstance modifier = new AttributeInstance(nmsAttribute, AttributeInstance::getAttribute);

        try {
            Map<Holder<Attribute>, AttributeInstance> attributes = (Map<Holder<Attribute>, AttributeInstance>) mapper.get(entity.getAttributes());
            attributes.put(nmsAttribute, modifier);

            mapper.set(entity.getAttributes(), attributes);
        } catch (IllegalAccessException | IllegalArgumentException e) {}

        entity.getAttribute(nmsAttribute).setBaseValue(value);
    }


    public void registerHostileMobs() {

    }

    public void drown(float amount) {
        if (entity instanceof ServerPlayer player) {
            this.drown(player, amount);
        }
    }

    public void drown(ServerPlayer player, float amount) {
        player.hurtServer(player.level(), player.damageSources().drown(), amount);
    }


    public void clearEntityPathfinders(GoalSelector goalSelector, GoalSelector targetSelector) {
        goalSelector.removeAllGoals((goal) -> true);
        targetSelector.removeAllGoals((goal) -> true);
    }
}
