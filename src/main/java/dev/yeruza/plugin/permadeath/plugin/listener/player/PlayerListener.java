package dev.yeruza.plugin.permadeath.plugin.listener.player;

import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitScheduler;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.core.PluginManager;
import dev.yeruza.plugin.permadeath.data.EndManager;
import dev.yeruza.plugin.permadeath.data.PlayerManager;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;
import dev.yeruza.plugin.permadeath.plugin.item.PermadeathItems;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class PlayerListener implements Listener {
    private final List<Player> sleeping = new ArrayList<>();
    private final List<Player> globalSleeping = new ArrayList<>();

    private long stormTicks;
    private long stormHours;

    private final Permadeath plugin;

    public PlayerListener(Permadeath plugin) {
        this.plugin = plugin;

        loadTicks();
    }

    public void loadTicks() {
        if (plugin.getDay() <= 24) {
            this.stormTicks = plugin.getDay() * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDay() >= 25 && plugin.getDay() < 50) {
            long define = plugin.getDay() - 24;
            this.stormTicks = define * 3600;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDay() == 50) {
            final int init = 1800 / 60;
            this.stormTicks = 1800;
            this.stormHours = stormTicks / 60 / 60;
        }

        if (plugin.getDay() > 50 && plugin.getDay() < 75) {
            long define = plugin.getDay() - 49;

            this.stormTicks = define * 3600 / 2;
            this.stormHours = stormTicks / 60 / 60;
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        if (event.getInventory().getType() == InventoryType.ANVIL && event.getSlotType() == InventoryType.SlotType.RESULT) {
            AnvilView anvil = (AnvilView) event.getInventory();
            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();

            meta.displayName(TextFormat.write(anvil.getRenameText()));
            item.setItemMeta(meta);

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();

        OfflinePlayer off = p;

        String dead = off.getName();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        for (Player player : Bukkit.getOnlinePlayers()) {
            TextComponent msg = plugin.getMessageData().getMessage("death-chat", player, value -> value.replace("{player}", dead));
            player.sendMessage(msg);

            String msgTitle = plugin.getMessageData().getMessage("death-title", player);
            String msgSubTitle = plugin.getMessageData().getMessage("death-subtitle", player).replace("{player}", dead);

            player.showTitle(TextFormat.createTitle(msgTitle, msgSubTitle, 2, 2 * 5, 2));

            if (plugin.getConfig().getBoolean("toggles.default-death-sounds-enabled"))
                player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, Float.MAX_VALUE, -0.1f);

            player.playSound(player.getLocation(), "pdc_death", Float.MAX_VALUE, 1.0F);


        }

        loadTicks();
        int stormDuration = plugin.getOverWorld().getWeatherDuration();
        int stormTicks = stormDuration / 20;
        long increment = stormTicks + this.stormTicks;
        int ticks = (int) this.stormTicks;
        int inc = (int) increment;

        boolean doEnableOp = plugin.getConfig().getBoolean("toggles.op-ban");
        boolean causingProblems = true;

        if (!doEnableOp)
            if (p.hasPermission("permadeath.staff.ban_player"))
                causingProblems = false;

        if (causingProblems) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:weather thunder");

            if (plugin.getOverWorld().hasStorm()) {
                plugin.getOverWorld().setWeatherDuration(inc * 40);
            } else {
                plugin.getOverWorld().setWeatherDuration(ticks * 40);
            }

            if (plugin.getDay() >= 50 && plugin.getBeginning() != null) {
                plugin.getBeginning().closeWorld();

                Bukkit.broadcast(TextFormat.showWithPrefix("&e¡Ha comenzado el modo UHC!"));
                plugin.getOverWorld().setGameRule(GameRule.NATURAL_REGENERATION, false);
            }

            scheduler.scheduleSyncDelayedTask(plugin, () -> {
                loadTicks();
                if (plugin.getDay() < 50) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String message = plugin.getMessageData().getMessage("death-train-message", player).replace("{time_left}", String.valueOf(stormHours));

                        player.sendMessage(message);
                        if (plugin.getConfig().getBoolean("toggles.default-death-sounds-enabled")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_HORSE_DEATH, 10, 1);
                        }
                    }
                    plugin.getMessageData().sendConsole("death-train-message", value -> value.replace("{time_left}", String.valueOf(stormHours)));
                } else {
                    long hours = stormTicks / 60 / 60;
                    long minutes = stormTicks / 60 % 60;

                    StringBuilder ct = new StringBuilder();
                    StringBuilder path = new StringBuilder("death-train-message");

                    if (minutes == 30 || minutes == 60)
                        path.append("-minutes");
                    if (hours >= 1)
                        ct.append(hours).append("Horas y ").append(minutes);
                    else
                        ct.append(minutes);

                    if (minutes == 0)
                        ct.append(hours);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String message = plugin.getMessageData().getMessage(path.toString(), player).replace("{time_left}", ct);

                        player.sendMessage(message);
                        if (plugin.getConfig().getBoolean("toggles.default-death-sounds-enabled")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_HORSE_DEATH, 10, 1);
                        }
                    }
                    plugin.getMessageData().sendConsole(path.toString(), value -> value.replace("{time_left}", String.valueOf(stormHours)));
                }
            }, 100L);

        } else {
            Bukkit.broadcast(TextFormat.showWithPrefix("&eEl jugador &b" + p.getName() + " &eno puede dar más horas de tormenta."));
        }

        PlayerManager data = new PlayerManager(event.getEntity().getPlayer(), plugin);
        data.setAutoDeathCause(event.getEntity().getPlayer().getLastDamageCause().getCause());
        data.setDeathTime();
        data.setDeathDay();
        data.setDeathCoords(event.getEntity().getPlayer().getLocation());

        if (plugin.getConfig().contains("server-options.messages.death-custom-path")) {
            File path = new File(plugin.getDataFolder(), plugin.getConfig().getString("server-options.messages.death-custom-path"));
            FileConfiguration config = YamlConfiguration.loadConfiguration(path);



           // Bukkit.broadcastMessage(TextFormat.write(StringUtils.capitalize(config.getString("")) + (path.toString().endsWith(".") ? "" : ".")));
        } else {
            String message = plugin.getConfig().getString("server.messages.death-default").replace("{player}", p.getName());
            Bukkit.broadcast(TextFormat.write(StringUtils.capitalize(message) + (message.endsWith(".") ? "" : ".")));
           // Bukkit.broadcastMessage(TextFormat.write(StringUtils.capitalize(message) + (message.endsWith(".") ? "" : ".")));
        }

        plugin.getMessageData().sendConsole("death-chat", value -> value.replace("{player}", dead));

        if (plugin.getConfig().getBoolean("server-options.messages.coords-enable")) {
            int dX = event.getEntity().getLocation().getBlockX();
            int dY = event.getEntity().getLocation().getBlockY();
            int dZ = event.getEntity().getLocation().getBlockZ();

            Bukkit.broadcast(TextFormat.withCodef("&6&lx: %s &c| &6&ly: %s &c| &6&lz: %s", dX, dY, dZ));
        }

        if (!plugin.getOverWorld().isHardcore())
            p.setGameMode(GameMode.SPECTATOR);

        scheduler.runTaskLater(plugin, () -> {
            boolean activeBan = !doEnableOp || !p.hasPermission("permadeath.staff.ban_player");
            if (plugin.getConfig().getBoolean("ban-enabled") && activeBan) {

                if (off.isOnline()) {
                    off.ban(ChatColor.RED + "Has sido PERMABANEADO", (Instant) null, "console");
                }

                Bukkit.getBanList(BanListType.PROFILE).addBan(off.getName(), ":(", null, "console");
            }
        }, 40L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getConfig().getBoolean("toggles.player-skulls")) {
                Location loc = p.getEyeLocation().clone();
                if (loc.getY() < 3)
                    loc.setY(3);

                Block skullBlock = loc.getBlock();
                skullBlock.setType(Material.PLAYER_HEAD);

                Skull skullState = (Skull) skullBlock.getState();
                skullState.setOwningPlayer(p);
                skullState.update();

                Rotatable rotatable = (Rotatable) skullBlock.getBlockData();
                rotatable.setRotation(getRotation(p));
                skullBlock.setBlockData(rotatable);

                skullBlock.getRelative(BlockFace.DOWN).setType(Material.NETHER_BRICK_FENCE);
                skullBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).setType(Material.BEDROCK);
            }
        }, 10L);
    }

    private BlockFace getRotation(Player player) {
        float rotation = player.getLocation().getYaw();

        if (rotation < 0) {
            rotation += 360.0F;
        }


        if (0 <= rotation && rotation < 22.5)
            return BlockFace.NORTH;

        if (22.5 <= rotation && rotation < 67.5)
            return BlockFace.NORTH_EAST;

        if (67.5 <= rotation && rotation < 112.5)
            return BlockFace.EAST;

        if (112.5 <= rotation && rotation < 157.5)
            return BlockFace.SOUTH_EAST;

        if (157.5 <= rotation && rotation < 202.5)
            return BlockFace.SOUTH;

        if (202.5 <= rotation && rotation < 247.5)
            return BlockFace.SOUTH_WEST;

        if (247.5 <= rotation && rotation < 292.5)
            return BlockFace.WEST;

        if (292.5 <= rotation && rotation < 337.5)
            return BlockFace.NORTH_WEST;

        if (337.5 <= rotation && rotation <= 360)
            return BlockFace.NORTH;


        return BlockFace.WEST;
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() != World.Environment.NORMAL) {
            event.getPlayer().sendMessage(TextFormat.write("&cSolo puedes dormir en el overworld"));
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            event.getPlayer().sendMessage(TextFormat.write("&cNo puedes dormir ahora."));
            return;
        }

        if (plugin.getDay() >= 20) {
            Location locBed = event.getBed().getLocation().add(0, 1, 0);
            plugin.getOverWorld().playSound(locBed, Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
            plugin.getOverWorld().spawnParticle(Particle.ENCHANT, locBed, 1);

            SplittableRandom random = new SplittableRandom();

            if (plugin.getDay() >= 50) {
                if ((random.nextInt(100) + 1) <= 10) {
                    event.getPlayer().sendMessage(TextFormat.showWithPrefix("&aHa sido establecido el contador de Phantoms"));
                    event.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
                }
            } else {
                event.getPlayer().sendMessage(TextFormat.showWithPrefix("&aHa sido establecido el contador de Phantoms"));
                event.getPlayer().setStatistic(Statistic.TIME_SINCE_REST, 0);
            }

            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        long time = plugin.getOverWorld().getTime();

        int needPlayers = 1;
        if (plugin.getDay() >= 10)
            needPlayers = 4;

        if (Bukkit.getOnlinePlayers().size() < needPlayers) {
            player.sendMessage(TextFormat.withCodef("&cNo puedes dormir porque no hay suficientes personas en línea (%d)", needPlayers));
            event.setCancelled(true);
            return;
        }

        if (time < 13000) {
            player.sendMessage(TextFormat.write("&cSolo puedes dormir de noche."));
            event.setCancelled(true);
        }

        if (plugin.getDay() < 10 && time >= 13000) {
            List<Player> sent = new ArrayList<>();

            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                event.getPlayer().getWorld().setTime(0L);
                if (!sent.contains(player)) {

                    //Bukkit.broadcastMessage(instance.format(Objects.requireNonNull(instance.getConfig().getString("Server-Messages.Sleep").replace("%player%", player.getName()))));

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        TextComponent msg = plugin.getMessageData().getMessage("sleep", p, value -> value.replace("{player}", player.getName()));

                        p.sendMessage(msg);

                    }
                    plugin.getMessageData().sendConsole("sleep", value -> value.replace("{player}", player.getName()));

                    sent.add(player);
                    player.damage(0.1);
                }
            }, 60L);
        }

        if (plugin.getDay() >= 10 && plugin.getDay() <= 19 && time >= 13000) {
            globalSleeping.add(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                String msg = plugin.getMessageData().getMessage("sleeping", p)
                        .replace("{needed}", String.valueOf(4))
                        .replace("{players_size}", String.valueOf(globalSleeping.size()))
                        .replace("{player}", player.getName());

                p.sendMessage(msg);
            }

            plugin.getMessageData().sendConsole("sleeping", value -> value
                            .replace("{needed}", String.valueOf(4))
                            .replace("{player_size}", String.valueOf(globalSleeping.size())
                            .replace("{player}", player.getName())));

            if (globalSleeping.size() >= needPlayers && globalSleeping.size() < Bukkit.getOnlinePlayers().size()) {
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (globalSleeping.size() >= 4) {
                        event.getPlayer().getWorld().setTime(0L);

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (all.isSleeping()) {
                                all.setStatistic(Statistic.TIME_SINCE_REST, 0);
                                all.damage(0.1);

                                String msg = plugin.getConfig().getString("sleep").replace("{player}", all.getName());

                                Bukkit.broadcast(TextFormat.write(msg));
                            }
                            Bukkit.broadcast(TextFormat.write("&eHan dormido suficientes jugadores (&b4&e)."));
                            globalSleeping.clear();
                        }
                    }
                }, 40L);
            }

            if (globalSleeping.size() == Bukkit.getOnlinePlayers().size()) {
                event.getPlayer().getWorld().setTime(0L);

                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.setStatistic(Statistic.TIME_SINCE_REST, 0);
                    all.damage(0.1);
                    Bukkit.broadcast(TextFormat.write(plugin.getConfig().getString("sleep").replace("{player}", all.getName())));
                }

                Bukkit.broadcast(TextFormat.write("&eHan dormido todos los jugadores."));

                globalSleeping.clear();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) return;

        if (sleeping.contains(player))
            sleeping.remove(player);


        if (globalSleeping.contains(player))
            globalSleeping.remove(player);


        if (player.getWorld().getTime() >= 0 && player.getWorld().getTime() < 13000)
            return;


        player.sendMessage(TextFormat.write("&eHas abandonado la cama, ya no contarás para pasar la noche."));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        event.joinMessage(null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            TextComponent msg = plugin.getMessageData().getMessage("on-join", p, value -> value.replace("{player}", player.getName()));

            event.joinMessage(msg);
        }

       // plugin.getMessageData().sendConsole("on-join", value -> value.replace("{player}", player.getName()));

        if (plugin.getShulkerShellEvent().isRunning())
            plugin.getShulkerShellEvent().addPlayer(event.getPlayer());

        if (Permadeath.isOptifineEnabled()) {
            player.setResourcePack(PluginManager.RESOURCE_PACK_LINK);
        }

        if (plugin.getBeginning() != null && plugin.getBeginning().getWorld() != null) {
            if (plugin.getBeginning().isClosed() && world.getName().equalsIgnoreCase(plugin.getBeginning().getWorld().getName()))
                event.getPlayer().teleport(plugin.getOverWorld().getSpawnLocation());

            if (Permadeath.isHasWorldEdit()) {
                if (!plugin.getBeginningData().generatedOverworldBeginningPortal()) {

                    plugin.getBeginning().generatePortal(null, true);
                }
                if (!plugin.getBeginningData().generatedBeginningPortal()) {
                    Location coords = new Location(plugin.getBeginning().getWorld(), 50, 150, 50);

                    plugin.getBeginning().generatePortal(coords, false);
                    plugin.getBeginning().getWorld().setSpawnLocation(coords);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.quitMessage(null);
        for (Player player : Bukkit.getOnlinePlayers()) {
            TextComponent msg = plugin.getMessageData().getMessage("on-leave", player, value -> value.replace("{player}", event.getPlayer().getName()));

            event.quitMessage(msg);
           // player.sendMessage(msg);
        }

      //  plugin.getMessageData().sendConsole("on-leave", value -> value.replace("{player}", event.getPlayer().getName()));
        plugin.getShulkerShellEvent().removePlayer(event.getPlayer());
        plugin.getLifeOrbEvent().removePlayer(event.getPlayer());


        Player player = event.getPlayer();
        if (sleeping.contains(player))
            sleeping.remove(player);

        if (globalSleeping.contains(player))
            globalSleeping.remove(player);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getConfig().getBoolean("afk-options.enabled")) {
            if (plugin.getConfig().getStringList("afk-options.bypass").contains(event.getName()))
                return;

            PlayerManager data = new PlayerManager(Bukkit.getPlayer(event.getName()), plugin);

            long currentDay = plugin.getDay();
            long lastConnection = data.getLastDay();
            if (currentDay < lastConnection) {
                data.setLastDay(currentDay);
                return;
            }

            OfflinePlayer off = Bukkit.getOfflinePlayer(event.getUniqueId());


            if (off.isBanned() || !off.isWhitelisted()) return;

            long result = currentDay + lastConnection;

            if (result >= plugin.getConfig().getInt("afk-options.days-limit")) {
                TextComponent reason = TextFormat.writef(
                        "&c&lHas sido Permabeaneado",
                        "&eRazón: &c&lAFK",
                        "&7Si crees que es un error,",
                        "&7contacta un administrador."
                   );

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
                Bukkit.getBanList(BanListType.PROFILE).addBan(event.getPlayerProfile(), reason.content(), (Date) null, "console");

            } else {
                data.setLastDay(plugin.getDay());
            }
        }
    }

    @EventHandler
    public void onAirChange(EntityAirChangeEvent event) {
        if (!(event.getEntity() instanceof Player player) || plugin.getDay() < 50) return;
        if (player.getRemainingAir() < event.getAmount()) return;

        int speed = (plugin.getDay() < 60 ? 5 : 10);
        float damage = (plugin.getDay() < 60 ? 5.0F : 10.0F);

        if (event.getAmount() < 20) return;

        int seconds = event.getAmount() / 20;
        int remain = seconds / speed;
        int newAmount = remain * 20;

        if (remain <= 0) {
            newAmount = 0;
            event.setAmount(newAmount);
            plugin.getNmsEntity(player).drown(damage);
            return;
        }

        event.setAmount(newAmount);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {

        if (plugin.getDay() >= 40) {
            if (event.getItem().hasItemMeta()) {
                if (event.getItem().getItemMeta().hasDisplayName()) {
                    ItemMeta meta = event.getItem().getItemMeta();

                    if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "super_golden_apple_plus"), PersistentDataType.BYTE)) {
                        Player player = event.getPlayer();
                        int min = 60 * 5;

                        if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * min, 0));
                    } else if (event.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "hyper_stacks"), PersistentDataType.BYTE)) {
                        if (plugin.getDay() < 60) {
                            if (Objects.equals(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(plugin, "hyper_stacks"), PersistentDataType.BYTE), (byte) 0)) {
                                event.getPlayer().sendMessage(TextFormat.showWithPrefix("&c¡Ya has consumido una Hyper Golden Apple!"));
                                return;
                            }
                            event.getPlayer().sendMessage(TextFormat.showWithPrefix("&a¡Has obtenido contenedores de vida extra!"));
                            event.getPlayer().getPersistentDataContainer().set(new NamespacedKey(Permadeath.getPlugin(), "hyper_one"), PersistentDataType.BYTE, (byte) 1);
                        } else {
                            boolean ateOne = event.getPlayer().getPersistentDataContainer().has(new NamespacedKey(plugin, "hyper_one"), PersistentDataType.BYTE);
                            boolean ateTwo = event.getPlayer().getPersistentDataContainer().has(new NamespacedKey(plugin, "hyper_two"), PersistentDataType.BYTE);

                            if (!ateOne) {
                                event.getPlayer().sendMessage(TextFormat.showWithPrefix("&a¡Has obtenido contenedores de vida extra! &e(Hyper Golden Apple 1/2)"));
                                event.getPlayer().getPersistentDataContainer().set(new NamespacedKey(plugin, "hyper_one"), PersistentDataType.BYTE, (byte) 1);
                            } else {
                                if (ateTwo) {
                                    event.getPlayer().sendMessage(TextFormat.showWithPrefix("&a¡Has obtenido contenedores de vida extra! &e(Hyper Golden Apple 2/2)"));
                                    return;
                                }
                                event.getPlayer().sendMessage(TextFormat.showWithPrefix("&a¡Has obtenido contenedores de vida extra! &e(Hyper Golden Apple 2/2)"));
                                event.getPlayer().getPersistentDataContainer().set(new NamespacedKey(Permadeath.getPlugin(), "hyper_two"), PersistentDataType.BYTE, (byte) 1);
                            }
                        }
                    } else if (event.getItem().getItemMeta().displayName().equals(TextFormat.write("&6Super Golden Apple +"))) {
                        Player player = event.getPlayer();
                        int min = 60 * 5;

                        if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * min, 0));
                    }
                }
            }
        }

        if (plugin.getDay() >= 50) {
            if (event.getItem().getType() == Material.MILK_BUCKET) {
                if (event.getPlayer().hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
                    PotionEffect effect = event.getPlayer().getPotionEffect(PotionEffectType.SLOW_FALLING);
                    Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {
                        event.getPlayer().addPotionEffect(effect);
                    }, 10L);
                }
            }

            if (event.getItem().getType() == Material.PUMPKIN_PIE)
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 5, 0));

            if (event.getItem().getType() == Material.SPIDER_EYE)
                Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {
                    event.getPlayer().removePotionEffect(PotionEffectType.POISON);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 0));
                }, 5L);

            if (event.getItem().getType() == Material.PUFFERFISH)
                Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {

                    event.getPlayer().removePotionEffect(PotionEffectType.NAUSEA);
                    event.getPlayer().removePotionEffect(PotionEffectType.POISON);
                    event.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 3));
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 2));
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, Integer.MAX_VALUE, 1));
                }, 5L);

            if (event.getItem().getType() == Material.ROTTEN_FLESH)
                Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {

                    event.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 1));
                }, 5L);


            if (event.getItem().getType() == Material.POISONOUS_POTATO)
                Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {

                    event.getPlayer().removePotionEffect(PotionEffectType.POISON);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 0));
                }, 5L);
        }

        if (plugin.getDay() >= 60) {
            ItemStack item = event.getItem();

            if (item.getType() == Material.PUMPKIN_PIE)
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 3));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && Permadeath.getPlugin().getDay() >= 60)
            event.getPlayer().setCooldown(Material.ENDER_PEARL, 6 * 20);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld().getPersistentDataContainer().has(plugin.getEnd().getKey()))
            createRegenZone(event.getPlayer().getLocation());

    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {


        if (Permadeath.getPlugin().getDay() >= 40)
            if (event.getChunk().getWorld().getPersistentDataContainer().has(plugin.getEnd().getKey()))
                for (Entity entity : event.getChunk().getEntities()) {
                    if (entity instanceof ItemFrame frame) {
                        if (frame.getItem().getType() == Material.ELYTRA) {
                            ItemStack s = new ItemProperties(Material.ELYTRA)
                                    .setDamage(431)
                                    .build();
                            frame.setItem(s);
                        }
                    }
                }

    }

    private void createRegenZone(Location playerZone) {
        EndManager data = plugin.getEndData();

        if (!data.getConfig().getBoolean("end-options.create-regen-zone")) {
            Location added = playerZone.add(-10, 0, 0);
            Location generate = Permadeath.getPlugin().getEnd().getHighestBlockAt(added).getLocation();

            if (generate.getY() == -1)
                generate.setY(playerZone.getY());

            Block centerBlock = Permadeath.getPlugin().getEnd().getBlockAt(generate);
            generateZone(true, generate);
            generateZone(false, generate);

            centerBlock.getRelative(BlockFace.UP).setType(Material.RED_CARPET);
            centerBlock.getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .setType(Material.SEA_LANTERN);
            centerBlock.getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .getRelative(BlockFace.UP)
                    .setType(Material.RED_CARPET);

            AreaEffectCloud area = (AreaEffectCloud) Permadeath.getPlugin().getEnd().spawnEntity(centerBlock.getRelative(BlockFace.UP).getLocation(), EntityType.AREA_EFFECT_CLOUD);
            area.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 0), false);
            area.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 0), false);
            area.setDuration(999999);
            area.setParticle(Particle.BLOCK, Material.AIR.createBlockData());
            area.setRadius(4.0F);

            data.getConfig().set("end-options.created-regen-zone", true);
            data.getConfig().set("end-options.regen-zone-location", TextFormat.parseLocation(area.getLocation()));
            data.saveFile();
            data.reloadFile();

            Bukkit.getScheduler().runTaskLaterAsynchronously(Permadeath.getPlugin(), () -> {
                for (Entity entity : Permadeath.getPlugin().getEnd().getEntities()) {
                    if (entity.getType() == EntityType.ENDERMAN || entity.getType() == EntityType.CREEPER) {
                        Block b = entity.getLocation().getBlock().getRelative(BlockFace.DOWN);

                        int random = new Random(4).nextInt();

                        List<Block> change = new ArrayList<>();

                        switch (random) {
                            case 0 -> {
                                change.add(b.getRelative(BlockFace.NORTH));
                                change.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                                change.add(b.getRelative(BlockFace.SOUTH));
                                change.add(b.getRelative(BlockFace.SOUTH_EAST));
                                change.add(b.getRelative(BlockFace.SOUTH_WEST));
                                change.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                                change.add(b.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                                change.add(b.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
                            }
                            case 1 -> {
                                change.add(b.getRelative(BlockFace.NORTH));
                                change.add(b.getRelative(BlockFace.NORTH_EAST));
                                change.add(b);
                            }
                            case 2 -> {
                                change.add(b.getRelative(BlockFace.SOUTH));
                                change.add(b.getRelative(BlockFace.SOUTH_EAST));
                                change.add(b);
                            }
                            case 3 -> {
                                change.add(b.getRelative(BlockFace.NORTH));
                                change.add(b.getRelative(BlockFace.NORTH_EAST));
                                change.add(b);
                                change.add(b.getRelative(BlockFace.SOUTH));
                                change.add(b.getRelative(BlockFace.EAST));
                            }
                            case 4 -> {
                                change.add(b.getRelative(BlockFace.SOUTH));
                                change.add(b.getRelative(BlockFace.NORTH_WEST));
                                change.add(b);
                                change.add(b.getRelative(BlockFace.NORTH));
                                change.add(b.getRelative(BlockFace.WEST));
                            }
                        }

                        for (Block all : change) {
                            Location used = Permadeath.getPlugin().getEnd().getHighestBlockAt(
                                    new Location(Permadeath.getPlugin().getEnd(), all.getX(), all.getY(), all.getZ())
                            ).getLocation();

                            Block now = plugin.getEnd().getBlockAt(used);

                            if (now.getType() == Material.END_STONE)
                                now.setType(Material.END_STONE_BRICKS);
                        }
                    }
                }
            },100L);
        }
    }


    private void generateZone(boolean execute, Location generate) {
        if (execute) {
            List<Block> blocks = new ArrayList<>();
            Block centerBlock = Permadeath.getPlugin().getEnd().getBlockAt(generate);
            blocks.add(centerBlock);

            blocks.add(plugin.getEnd().getBlockAt(generate).getRelative(BlockFace.EAST));
            blocks.add(plugin.getEnd().getBlockAt(generate).getRelative(BlockFace.WEST));

            blocks.add(centerBlock.getRelative(BlockFace.NORTH));
            blocks.add(centerBlock.getRelative(BlockFace.NORTH_WEST));
            blocks.add(centerBlock.getRelative(BlockFace.NORTH_EAST));

            blocks.add(centerBlock.getRelative(BlockFace.SOUTH));
            blocks.add(centerBlock.getRelative(BlockFace.SOUTH_WEST));
            blocks.add(centerBlock.getRelative(BlockFace.SOUTH_EAST));

            for (Block all : blocks) all.setType(Material.RED_WOOL);
        } else {
            List<Block> blocks = new ArrayList<>();
            Block centerBlockOfWool = plugin.getEnd().getBlockAt(generate);

            Block corner1 = centerBlockOfWool.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST);

            blocks.add(corner1);
            blocks.add(corner1.getRelative(BlockFace.WEST));
            blocks.add(corner1.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));
            blocks.add(corner1.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));

            blocks.add(corner1.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));

            blocks.add(corner1.getRelative(BlockFace.SOUTH));
            blocks.add(corner1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH));
            blocks.add(corner1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH));

            Block southC = corner1.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH);
            blocks.add(southC);

            blocks.add(southC.getRelative(BlockFace.WEST));
            blocks.add(southC.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));
            blocks.add(southC.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST));

            Block finalC = southC.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST);
            blocks.add(finalC);

            blocks.add(finalC.getRelative(BlockFace.NORTH));
            blocks.add(finalC.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
            blocks.add(finalC.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));

            for (Block all : blocks)
                all.setType(Material.RED_GLAZED_TERRACOTTA);
        }
    }

    public ItemStack[] clearMatrix() {
        return new ItemStack[] {
                new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)
        };
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRestrictCrafting(PrepareItemCraftEvent event) {
        CraftPrepareManager data = new CraftPrepareManager(event);

        data.runCheckForBeginningRelic();
        data.runCheckForLifeOrb();
        data.runCheckForGoldenApplePlus();

        CraftingInventory inventory = event.getInventory();
        ItemStack result = event.getInventory().getResult();

        if (result.getType().name().toLowerCase().contains("netherite_")
                && !result.getItemMeta().isUnbreakable()
                && plugin.getDay() >= 25)
            inventory.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRestrictSmithing(PrepareSmithingEvent event) {
        SmithingPrepareManager data = new SmithingPrepareManager(event);

        SmithingInventory inventory = event.getInventory();
        ItemStack result = event.getInventory().getResult();

        if (result.getType().name().toLowerCase().contains("netherite_")
                && !result.getItemMeta().isUnbreakable()
                && plugin.getDay() >= 25)
            inventory.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (inventory.getResult() != null) {
            ItemStack item = event.getRecipe().getResult();
            if (event.isCancelled() || event.getResult() != Event.Result.ALLOW) return;
            if (item.hasItemMeta()) {
                if (plugin.isEndRelic(item)) {
                    ItemMeta meta = item.getItemMeta();

                    item.setItemMeta(meta);

                    event.setCurrentItem(item);
                    return;
                }

                ItemStack[] matrix = {
                        new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)
                };

                if (item.isSimilar(PermadeathItems.BEGINNING_RELIC) || item.isSimilar(PermadeathItems.ORB_LIFE)) {
                    if (event.getWhoClicked() instanceof Player) {
                        event.getInventory().setMatrix(matrix);

                        event.getWhoClicked().setItemOnCursor(item);
                    }
                }

                if (item.isSimilar(PermadeathItems.HYPER_GOLDEN_APPLE_PLUS)) {
                    if (event.getWhoClicked() instanceof Player) {
                        event.getInventory().setMatrix(matrix);

                        event.getWhoClicked().setItemOnCursor(item);
                    }
                }
            }
        }
    }

    private class CraftPrepareManager {
        private PrepareItemCraftEvent event;
        private ItemStack result;
        private ItemMeta meta;

        CraftPrepareManager(PrepareItemCraftEvent event) {
            this.event = event;
            this.result = event.getInventory().getResult();
            this.meta = result.getItemMeta();
        }

        public void runCheckForBeginningRelic() {
            if (result == null) return;

            if (result.isSimilar(PermadeathItems.BEGINNING_RELIC)) {
                int diamondBlocks = 0;
                int r = 0;
                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item != null)
                        if(item.getType() == Material.DIAMOND_BLOCK)
                            if (item.getAmount() >= 32)
                                diamondBlocks++;
                    if (plugin.isEndRelic(item))
                        r++;
                }

                if (diamondBlocks < 4 || r < 1)
                    event.getInventory().setResult(null);
                if (diamondBlocks >= 4 && r >= 1)
                    event.getInventory().setResult(PermadeathItems.BEGINNING_RELIC);
            }

        }

        public void runCheckForGoldenApplePlus() {
            if (result == null) return;

            if (meta.getCustomModelDataComponent().getStrings().contains("ultra_golden_apple_plus")) {
                if (plugin.getDay() < 80) {
                    int found = 0;

                    for (ItemStack item : event.getInventory().getMatrix())
                        if (item.getType() == Material.GOLD_BLOCK)
                            if (item.getAmount() >= 8)
                                found = found + 1;

                    if (found >= 8)
                        event.getInventory().setResult(PermadeathItems.ULTRA_GOLDEN_APPLE_PLUS);
                } else {

                }
            }

            if (meta.getCustomModelDataComponent().getStrings().contains("hyper_golden_apple_plus")) {
                if (plugin.getDay() < 60) {
                    int found = 0;

                    for (ItemStack item : event.getInventory().getMatrix())
                        if (item.getType() == Material.GOLD_BLOCK)
                            if (item.getAmount() >= 8)
                                found = found + 1;



                    if (found >= 8)
                        event.getInventory().setResult(PermadeathItems.HYPER_GOLDEN_APPLE_PLUS);
                    else
                        event.getInventory().setResult(null);

                } else {
                    int found = 0;
                    boolean enoughGaps = false;

                    for (ItemStack item : event.getInventory().getMatrix()) {
                        if (item != null)
                            if (item.getType() == Material.GOLD_BLOCK)
                                if (item.getAmount() >= 8)
                                    found += 1;

                        if (item.getType() == Material.GOLDEN_APPLE && item.getAmount() == 64) enoughGaps = true;
                    }

                    if (found >= 8 && enoughGaps)
                        event.getInventory().setResult(PermadeathItems.HYPER_GOLDEN_APPLE_PLUS);
                    else
                        event.getInventory().setResult(null);

                }
            }

            if (meta.getCustomModelDataComponent().getStrings().contains("super_golden_apple_plus")) {

                int found = 0;
                for (ItemStack item : event.getInventory().getMatrix())
                    if (item != null)
                        if (item.getType() == Material.GOLD_INGOT)
                            if (item.getAmount() >= 8)
                                found++;

                if (found < 8) {
                    event.getInventory().setResult(null);
                    return;
                }
                if (found >= 8) {
                    event.getInventory().setResult(PermadeathItems.SUPER_GOLDEN_APPLE_PLUS);
                }
            }
        }

        public void runCheckForLifeOrb() {
            if (result == null) return;
            if (!result.isSimilar(PermadeathItems.ORB_LIFE)) return;
            if (!plugin.getLifeOrbEvent().isRunning()) return;
            int items = 0;

            for (ItemStack s : event.getInventory().getMatrix())
                if (s.getType() == Material.HEART_OF_THE_SEA)
                    items++;
                else {
                    if (s.getAmount() >= 64)
                        items++;
                }


            if (items < 9)
                event.getInventory().setResult(null);
            if (items >= 9)
                event.getInventory().setResult(PermadeathItems.ORB_LIFE);
        }
    }

    private class SmithingPrepareManager {
        private PrepareSmithingEvent event;
        private ItemStack result;
        private ItemMeta meta;

        SmithingPrepareManager(PrepareSmithingEvent event) {
            this.event = event;
            this.result = event.getInventory().getResult();
            this.meta = result.getItemMeta();
        }

        public void runCheckInfernalNetheritePiece() {
            if (plugin.isInfernalNetheritePiece(result)) {
                int infernalNetheriteFound = 0;
                boolean foundPiece;

                ItemStack armor = event.getInventory().getResult();


            }
        }

    }
}
