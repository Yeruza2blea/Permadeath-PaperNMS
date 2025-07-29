package dev.yeruza.plugin.permadeath.core;

import org.bukkit.NamespacedKey;

import java.util.Map;
import java.util.Set;

public interface CoreRegistry<T> {
    static <T> T register(CoreRegistry<T> customer, NamespacedKey id, T type) {
        if (customer instanceof LinkedMapper<T> mapper) {
            return mapper.register(id, type);
        }

        return type;
    }

    static <T> CoreRegistry<T> create(String id) {
        return new LinkedMapper<>(id);
    }

    T getId(NamespacedKey id);

    Set<Map.Entry<NamespacedKey, T>> entrySet();

    boolean containsKey(NamespacedKey id);

    Set<NamespacedKey> keys();

    Set<T> values();

    NamespacedKey getRegistryId();
}
