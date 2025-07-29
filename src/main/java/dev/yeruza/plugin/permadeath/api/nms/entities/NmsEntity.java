package dev.yeruza.plugin.permadeath.api.nms.entities;

import org.bukkit.NamespacedKey;

public interface NmsEntity<E> {
    void spawnEntity();

    NamespacedKey getId();

    E getEntity();

}
