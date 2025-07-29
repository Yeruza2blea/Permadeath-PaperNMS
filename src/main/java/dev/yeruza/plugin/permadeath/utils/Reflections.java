package dev.yeruza.plugin.permadeath.utils;

public class Reflections {
    public static final String NMS = "net.minecraft.server";
    public static final String CRAFTBUKKIT = "org.bukkit.craftbukkit";

    public static Class<?> getNMSClass(String path, Object ...args) throws ClassNotFoundException {
        return Class.forName(NMS.concat(path).formatted(args));
    }

    public static Class<?> getOBCClass(String path, Object ...args) throws ClassNotFoundException {
        return Class.forName(CRAFTBUKKIT.concat(path).formatted(args));
    }

}
