package dev.yeruza.plugin.permadeath.api.commands;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MinecraftCommandHandler implements AutoCloseable {
    private static final StringBuilder src = new StringBuilder("org.xel0yzyruza.permadeath.api.commands");
    private final Map<NamespacedKey, MinecraftCommand> commands = new HashMap<>();

    private CommandMap mapper;

    private final Permadeath plugin;

    public MinecraftCommandHandler(Permadeath plugin) {
        this.plugin = plugin;
        this.mapper = plugin.getServer().getCommandMap();

    }

    public void registerCommands(Supplier<MinecraftCommand> fabric) {
        MinecraftCommand command = fabric.get();
        commands.put(Permadeath.withCustomNamespace(command.getName()), command);
        mapper.register("permadeath", command.build());
    }


    @Override
    public void close() {

    }
}
