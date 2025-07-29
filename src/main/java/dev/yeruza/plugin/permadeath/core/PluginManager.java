package dev.yeruza.plugin.permadeath.core;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.nms.main.NmsAccessor;
import dev.yeruza.plugin.permadeath.nms.main.NmsBlock;
import dev.yeruza.plugin.permadeath.nms.main.NmsEntity;
import dev.yeruza.plugin.permadeath.nms.main.NmsHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;


public record PluginManager(Permadeath plugin) {
    private static PluginManager core;
    public static final StringBuilder DIR_NMS = new StringBuilder("org.xel0yzyruza.permadeath.nms.main");
    public static final String CRAFTBUKKIT_VERSION = Bukkit.getServer().getBukkitVersion();
    public static final String VERSION = Bukkit.getServer().getVersion();

    public static final String RESOURCE_PACK_LINK = "";

    private static NmsAccessor accessor;

    private static NmsHandler handler;
    private static NmsBlock block;


    public static NmsBlock getNmsBlock(BlockState type) {
        return null;
    }

    public static NmsAccessor getNmsEntity(@NotNull LivingEntity entity) {
        return accessor.getNmsEntity(entity);
    }

    public static NmsAccessor getNmsEntity(@NotNull org.bukkit.entity.LivingEntity entity) {
        return accessor.getNmsEntity(entity);
    }

    public static NmsAccessor getAccessor() {
        return accessor;
    }

    public static NmsHandler getHandler() {
        return handler;
    }

    public static void init(Permadeath plugin) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        accessor = new NmsAccessor(plugin);

        handler = new NmsHandler(plugin);
        core = new PluginManager(plugin);
    }

    public static void spawnNmsEntity(Location loc, Class<? extends NmsEntity<?>> meta) {
        try {
            NmsEntity<?> e = meta.getConstructor(Location.class).newInstance(loc);
            e.spawnEntity();

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isHostileMob(org.bukkit.entity.EntityType type) {
        return switch (type) {
            case WITHER,
                 ENDER_DRAGON,
                 WARDEN,
                 ZOMBIE,
                 SKELETON,
                 BLAZE,
                 CREEPER,
                 MAGMA_CUBE,
                 SLIME,
                 ZOMBIE_VILLAGER,
                 DROWNED,
                 WITHER_SKELETON,
                 WITCH, PILLAGER,
                 ZOMBIFIED_PIGLIN,
                 EVOKER,
                 VINDICATOR,
                 RAVAGER,
                 VEX,
                 GUARDIAN,
                 ELDER_GUARDIAN,
                 GHAST,
                 SHULKER,
                 HUSK,
                 STRAY,
                 PHANTOM -> true;
            default -> false;
        };

    }

    public static Class<?> getClass(String path, Object ...args) throws ClassNotFoundException {
       StringBuilder builder = DIR_NMS.append(".").append(path.formatted(args));

        return Class.forName(builder.toString());
    }

    public static <E> Class<E> getClass(String path) throws ClassNotFoundException {
        String dir = "org.xel0yzyruza.permadeath.nms.main";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();;

        try (Stream<Path> paths = Files.walk(Path.of(loader.getResource(dir).toURI()))) {



        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        String root = String.format("org.xel0yzyruza.permadeath.nms.main.%s", path);
        return (Class<E>) Class.forName(dir);
    }

    private static class NmsLoader {
        private NmsBlock block;
        private NmsEntity entity;

        public NmsLoader() {

        }
    }
}