package dev.yeruza.plugin.permadeath.core;

import org.bukkit.NamespacedKey;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LinkedMapper<T> implements CoreRegistry<T> {
    public static final Map<NamespacedKey, ?> VALUES = new HashMap<>();

    private final Map<NamespacedKey, T> byNamespaces;
    private final NamespacedKey id;

    public LinkedMapper(String id) {
        this.byNamespaces = new HashMap<>();
        this.id = Permadeath.withCustomNamespace(id);
    }

    @Override
    public T getId(NamespacedKey id) {
        return byNamespaces.get(id);
    }

    @Override
    public Set<Map.Entry<NamespacedKey, T>> entrySet() {
        return byNamespaces.entrySet();
    }

    @Override
    public boolean containsKey(NamespacedKey id) {
        return byNamespaces.containsKey(id);
    }

    @Override
    public Set<NamespacedKey> keys() {
        return Set.copyOf(byNamespaces.keySet());
    }

    @Override
    public Set<T> values() {
        return Set.copyOf(byNamespaces.values());
    }

    @Override
    public NamespacedKey getRegistryId() {
        return id;
    }

    public T register(NamespacedKey id, T type) {
        return byNamespaces.put(id, type);
    }
}
