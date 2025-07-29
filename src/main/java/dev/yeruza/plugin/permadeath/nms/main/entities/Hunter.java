package dev.yeruza.plugin.permadeath.nms.main.entities;

import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Location;
import dev.yeruza.plugin.permadeath.nms.main.KeyId;
import dev.yeruza.plugin.permadeath.nms.main.NmsEntity;

import java.util.ArrayList;
import java.util.List;

public class Hunter extends NmsEntity<Zombie> {

    private final List<Zombie> hunters = new ArrayList<>();

    public Hunter(Location where) {
        super(where, KeyId.create(ENTITY_ID, "hunter"));

        this.entity = new Zombie(level);
        hunters.add(entity);


    }

    @Override
    public void spawnEntity() {


    }
}
