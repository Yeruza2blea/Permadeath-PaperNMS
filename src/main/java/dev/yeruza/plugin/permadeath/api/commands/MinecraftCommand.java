package dev.yeruza.plugin.permadeath.api.commands;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class MinecraftCommand {
    private String name;
    private List<String> aliases;
    private String description;
    private String usageMessage;

    private Permissions permission;

    private String permissionMessage;

    private final BukkitCommand command;

    protected final Permadeath plugin;

    protected CommandSender sender;
    protected Player player;


    protected String[] args;
    protected List<String> tab;

    public MinecraftCommand() {
        plugin = Permadeath.getPlugin();
        if (this.getClass().isAnnotationPresent(Command.class)) {
            Command annotation = getClass().getAnnotation(Command.class);

            name = annotation.name();
            description = annotation.description();
            usageMessage = annotation.usage().contains("{command.name}") ? annotation.usage().replace("{command.name}", name) : annotation.usage();
            aliases = List.of(annotation.aliases());
            permission = annotation.permission();
        }

        command = new BukkitCommand(name, description, usageMessage, aliases) {
            final MinecraftCommand pdCommand = MinecraftCommand.this;

            public boolean execute(@NotNull CommandSender sender, @NotNull String commandName, @NotNull String[] args) {
                pdCommand.onExecute(sender, commandName, args);

                return true;
            }

            @NotNull
            @Override
            public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String cmdName, @NotNull String[] args) throws IllegalArgumentException {
                pdCommand.onTabComplete(sender, cmdName, args);

                return tab;
            }
        };
    }

    protected void tellWithPrefix(String message) {
        CommandHelper.callTarget(player).sendMessage(TextFormat.showWithPrefix(message));
    }

    protected void tellSuccess(String message) {
        CommandHelper.callTarget(player).sendMessage(TextFormat.write("&a" + message));
    }

    protected void tellFail(String message) {
        CommandHelper.callTarget(player).sendMessage(TextFormat.write("&c" + message));
    }

    protected abstract void onExecute();

    protected void onTabComplete() {

    }

    public boolean hasPermission(String perm) {
        return player.hasPermission(perm);
    }

    protected final void onExecute(CommandSender sender, String cmdName, String[] args) {
        if (!cmdName.contains(name))
            return;

        this.sender = sender;
        this.args = args;
        this.player = sender instanceof Player p ? p : null;

        onExecute();
    }

    protected final void onTabComplete(CommandSender sender, String cmdName, String[] args) {
        if (!cmdName.contains(name))
            return;

        this.sender = sender;
        this.args = args;
        this.player = sender instanceof Player p ? p : null;

        if (player.hasPermission(permission.getPath())) {
            if (tab == null) {
                tab = new ArrayList<>();
            }
        }

        onTabComplete();
    }

    public World findWorld(String name) {
        if (name.equals("self")) {
            return player.getWorld();
        }

        World world = Bukkit.getWorld(name);

        return world;
    }

    public Material findMaterial(String constant) {
        return Material.getMaterial(constant);
    }

    public Material findMaterial(String constant, boolean legacy) {
        return Material.getMaterial(constant, legacy);
    }

    public Player findPlayerByIndex(int index) {
        return findPlayerByName(args[index]);
    }

    public Player findPlayerByName(String name) {
        return Bukkit.getPlayer(name);
    }

    public OfflinePlayer findOfflinePlayer(String name) {


        return Bukkit.getOfflinePlayer(name);
    }

    public OfflinePlayer findOfflinePlayer(UUID id) {


        return Bukkit.getOfflinePlayer(id);
    }

    public String getFirstArg() {
        return args[0];
    }

    public String getLastArg() {
        return args.length > 0 ? args[args.length - 1] : "";
    }

    public <E extends Enum<E>> E findEnum(Class<E> enumClass) {
        if (!enumClass.isEnum())
            return null;


        return Stream.of(enumClass.getFields())
                .filter(Field::isEnumConstant)
                .map((f) -> findEnum(enumClass, f.getName()))
                .iterator()
                .next();
    }

    public <E extends Enum<E>> E findEnum(Class<E> enumClass, String constant) {
        return Enum.valueOf(enumClass, constant);
    }

    public BukkitCommand build() {
        return command;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

    public Permissions getPermission() {
        return permission;
    }
}
