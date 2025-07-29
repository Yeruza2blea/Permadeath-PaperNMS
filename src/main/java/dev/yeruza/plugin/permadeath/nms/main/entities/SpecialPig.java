package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.yeruza.plugin.permadeath.core.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpecialPig extends Pig {
    public SpecialPig(Location where) {
        super(EntityType.PIG, ((CraftWorld) where.getWorld()).getHandle());
        this.setPos(where.getX(), where.getY(), where.getZ());

        PluginManager.getNmsEntity(this).registerAttribute(CraftAttribute.ATTACK_DAMAGE, 40.0D);

        // org.bukkit.entity.Pig pig = (org.bukkit.entity.Pig) getBukkitEntity();

        List<PotionEffect> effects = new ArrayList<>();
        effects.add(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
        effects.add(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 3));
        effects.add(new PotionEffect(PotionEffectType.STRENGTH, 9999999, 3));
        effects.add(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 0));
        effects.add(new PotionEffect(PotionEffectType.JUMP_BOOST, 9999999, 4));
        effects.add(new PotionEffect(PotionEffectType.SLOW_FALLING, 9999999, 0));
        effects.add(new PotionEffect(PotionEffectType.GLOWING, 9999999, 0));
        effects.add(new PotionEffect(PotionEffectType.RESISTANCE, 9999999, 2));


        for (int i = 0; i < 5; i++) {
            Random random = new Random();
            int randomIndex = random.nextInt(effects.size());

            PotionEffect effect = effects.get(randomIndex);
            MobEffectInstance effectInstance = new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraftHolder(effect.getType()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isInfinite());

            if (effect.getType() == PotionEffectType.SPEED)
                addEffect0(effectInstance); //pig.addPotionEffect(effect);

            if (effect.getType() == PotionEffectType.REGENERATION)
                addEffect0(effectInstance);

            if (effect.getType() == PotionEffectType.STRENGTH)
                addEffect0(effectInstance);

            if (effect.getType() == PotionEffectType.INVISIBILITY)
                addEffect0(effectInstance);

            if (effect.getType() == PotionEffectType.JUMP_BOOST)
                addEffect0(effectInstance);

            if (effect.getType() == PotionEffectType.SLOW_FALLING)
                addEffect0(effectInstance);

            if (effect.getType() == PotionEffectType.GLOWING)
                addEffect0(effectInstance);

            if (effect.getType() == PotionEffectType.RESISTANCE)
                addEffect0(effectInstance);
        }
        setPersistenceRequired(false);
    }

    private void addEffect0(MobEffectInstance mobeffect) {
        super.addEffect(mobeffect, EntityPotionEffectEvent.Cause.PLUGIN);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
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
