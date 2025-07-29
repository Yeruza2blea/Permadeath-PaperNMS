package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrownSplashPotion;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.craftbukkit.entity.CraftMinecart;
import org.bukkit.craftbukkit.entity.CraftThrownSplashPotion;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.minecart.SpawnerMinecart;
import dev.yeruza.plugin.permadeath.nms.main.KeyId;
import dev.yeruza.plugin.permadeath.nms.main.NmsEntity;

import java.util.Optional;

public class DeathModule extends NmsEntity<MinecartSpawner> {

    public DeathModule(Location where) {
        super(where, KeyId.create(ENTITY_ID, "death_module"));

        entity = new MinecartSpawner(EntityType.SPAWNER_MINECART, level);
    }

    public void spawnEntity() {
        entity.getType().create(level, EntitySpawnReason.NATURAL);
        SpawnerMinecart sm = where.getWorld().spawn(where, SpawnerMinecart.class);
        MinecartSpawner ms = (MinecartSpawner) ((CraftMinecart) sm).getHandle();
        BaseSpawner base = ms.getSpawner();

        base.maxSpawnDelay = 150;
        base.spawnDelay = 0;
        base.spawnRange = 5;
        base.minSpawnDelay = 60;
        base.requiredPlayerRange = 32;
        base.spawnCount = 4;
        CraftEntitySnapshot snapshot = createData(where);


        base.setEntityId(EntityType.SPLASH_POTION, level, RandomSource.create(), new BlockPos(where.getBlockX(), where.getBlockY(), where.getBlockZ()));
        base.nextSpawnData = new SpawnData(snapshot.getData(), Optional.empty(), Optional.empty());


        CaveSpider spider = where.getWorld().spawn(where, CaveSpider.class);
        Shulker shulker = where.getWorld().spawn(where, Shulker.class);
        shulker.setColor(DyeColor.RED);
        shulker.addPassenger(sm);
        spider.addPassenger(shulker);
    }


    protected CraftEntitySnapshot createData(Location location) {
        CraftEntitySnapshot snapshot = CraftEntitySnapshot.create(new CompoundTag(), org.bukkit.entity.EntityType.SPLASH_POTION);

        ThrownSplashPotion potion = ((CraftThrownSplashPotion) snapshot.createEntity(location)).getHandle();
        potion.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(Potions.INFESTED));


        return snapshot;
    }
}
