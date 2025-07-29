package dev.yeruza.plugin.permadeath.nms.main.entities.peace;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import dev.yeruza.plugin.permadeath.nms.main.KeyId;
import dev.yeruza.plugin.permadeath.nms.main.NmsEntity;

public abstract class MobChilling<E extends LivingEntity> extends NmsEntity<E> {
    public MobChilling(Location where, String value) {
        super(where, KeyId.create(ENTITY_ID, value));
    }
}
