package dev.yeruza.plugin.permadeath.api.nms;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public interface NmsAccessor {
    void setMaxHealth(double health);

    double getMaxHealth();

    NmsAccessor getNmsEntity(LivingEntity entity);

    void injectHostilePathfinders(LivingEntity entity);

    void registerAttribute(Attribute attribute, double value);

    void drown(float amount);
}
