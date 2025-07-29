package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import dev.yeruza.plugin.permadeath.core.PluginManager;

public class CustomCod extends Cod {
    public CustomCod(Location where) {
        super(EntityType.COD, ((CraftWorld) where.getWorld()).getHandle());
        this.setPos(where.getX(), where.getY(), where.getZ());

        PluginManager.getNmsEntity(this).registerAttribute(CraftAttribute.ATTACK_DAMAGE, 30.0D);
        PluginManager.getNmsEntity(this).registerAttribute(CraftAttribute.ATTACK_DAMAGE, 30.0D);

        this.setPersistenceRequired(false);
    }

    @Override
    public boolean isPersistenceRequired() {
        return false;
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
