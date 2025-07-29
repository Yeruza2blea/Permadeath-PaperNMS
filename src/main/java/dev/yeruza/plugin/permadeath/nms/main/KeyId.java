package dev.yeruza.plugin.permadeath.nms.main;

import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import dev.yeruza.plugin.permadeath.Permadeath;

public class KeyId<T> {
    private final NamespacedKey key;
    private final T value;

    public static <T> KeyId<T> create(String id, T value) {
        return new KeyId<>(Permadeath.withCustomNamespace(id), value);
    }

    public static <T> KeyId<T> create(ResourceLocation id, T value) {
        return new KeyId<>(Permadeath.withCustomNamespace(id.getPath()), value);
    }

    public KeyId(NamespacedKey key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getNamespace() {
        return key.getNamespace();
    }

    public String getKey() {
        return key.getKey();
    }

    public T getValue() {
        return value;
    }
}
