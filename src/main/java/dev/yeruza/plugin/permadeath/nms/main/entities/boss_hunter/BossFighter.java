package dev.yeruza.plugin.permadeath.nms.main.entities.boss_hunter;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import dev.yeruza.plugin.permadeath.nms.main.KeyId;
import dev.yeruza.plugin.permadeath.nms.main.NmsEntity;

public abstract class BossFighter<E extends LivingEntity> extends NmsEntity<E> {
    public static final int PHASES = 3;


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    protected KeyedBossBar bar;
    protected int times;

    public BossFighter(E entity, String value) {
        super(entity, KeyId.create(ENTITY_ID, value));

        this.bar = Bukkit.createBossBar(getId(), entity.getCustomName().getString(), BarColor.RED, BarStyle.SEGMENTED_10);
        times = 3;
    }

    public BossFighter(Location pos, String value) {
        super(pos, KeyId.create(ENTITY_ID, value));


        this.bar = Bukkit.createBossBar(getId(), entity.getCustomName().getString(), BarColor.RED, BarStyle.SEGMENTED_10);
        times = 3;
    }

    protected void registerGoals() {
        if (entity instanceof PathfinderMob mob) {

        }
    }

    public void initBossBar() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(player);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 9999, 2, 1);
        }
    }
}
