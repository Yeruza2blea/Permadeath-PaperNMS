package dev.yeruza.plugin.permadeath;

import com.mongodb.client.MongoDatabase;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Equippable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.apache.logging.log4j.LogManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftWither;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.api.commands.MinecraftCommandHandler;
import dev.yeruza.plugin.permadeath.api.commands.staff.*;
import dev.yeruza.plugin.permadeath.api.commands.user.DayCommand;
import dev.yeruza.plugin.permadeath.api.commands.user.InfoCommand;
import dev.yeruza.plugin.permadeath.api.commands.user.MessagesCommand;
import dev.yeruza.plugin.permadeath.nms.main.NmsAccessor;
import dev.yeruza.plugin.permadeath.nms.main.NmsBlock;
import dev.yeruza.plugin.permadeath.nms.main.NmsHandler;
import dev.yeruza.plugin.permadeath.core.PluginManager;
import dev.yeruza.plugin.permadeath.data.*;
import dev.yeruza.plugin.permadeath.plugin.event.InventoryLockEvent;
import dev.yeruza.plugin.permadeath.plugin.event.LifeOrbEvent;
import dev.yeruza.plugin.permadeath.plugin.event.ShulkerShellEvent;
import dev.yeruza.plugin.permadeath.gaming.ApiPlaceHolder;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorKit;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorKits;
import dev.yeruza.plugin.permadeath.plugin.listener.block.BlockListener;
import dev.yeruza.plugin.permadeath.plugin.listener.entity.EntityListener;
import dev.yeruza.plugin.permadeath.plugin.listener.entity.HostilyEntityListener;
import dev.yeruza.plugin.permadeath.plugin.listener.entity.SkeletonListener;
import dev.yeruza.plugin.permadeath.plugin.listener.entity.SpawnListener;
import dev.yeruza.plugin.permadeath.plugin.listener.player.ChatListener;
import dev.yeruza.plugin.permadeath.plugin.listener.player.PlayerListener;
import dev.yeruza.plugin.permadeath.plugin.listener.player.SlotBlockListener;
import dev.yeruza.plugin.permadeath.plugin.listener.player.TotemListener;
import dev.yeruza.plugin.permadeath.plugin.listener.worlds.OverworldListener;
import dev.yeruza.plugin.permadeath.plugin.listener.worlds.PaperListener;
import dev.yeruza.plugin.permadeath.plugin.task.boss.demon.DemonEndTask;
import dev.yeruza.plugin.permadeath.utils.ApiResource;
import dev.yeruza.plugin.permadeath.utils.MobFactory;
import dev.yeruza.plugin.permadeath.utils.MongoDBDriver;
import dev.yeruza.plugin.permadeath.utils.TextFormat;
import dev.yeruza.plugin.permadeath.utils.log.GamingLogger;
import dev.yeruza.plugin.permadeath.utils.log.Log4Filter;
import dev.yeruza.plugin.permadeath.worlds.beginning.TheBeginningListener;
import dev.yeruza.plugin.permadeath.worlds.end.TheEndListener;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Filter;
import java.util.logging.Logger;

public final class Permadeath extends JavaPlugin implements Listener {
    public static final String PLUGIN_ID = "permadeath";
    public static final double MAX_HEALTH = 20.0D;

    private static boolean speedRun = false;
    private static String prefix = null;
    private static Permadeath plugin;

    private MongoDBDriver driver;

    private static boolean runPaper;
    private static boolean runWorldEdit;

    private final SplittableRandom random = new SplittableRandom();

    private World overWorld;
    private World nether;
    private World end;

    private TheBeginningListener beginning;

    private DemonEndTask endTask;
   // private CatTask catTask;

    private RecipeManager recipeData;
    private DataBaseManager dbData;
    private PlayerManager playerData;
    private BeginningManager beginningData;
    private MessageManager messageData;
    private DateManager dateData;
    private EndManager endData;


    private int playTime;


    private SpawnListener spawnHandler;
    private TheEndListener endHandler;

    private MobFactory factory;

    private final Map<Integer, Boolean> days = new HashMap<>();
    private final List<Player> effectPlayers = new ArrayList<>();

    private List<Cat> novaCats;

    private boolean loaded = false;

    private ShulkerShellEvent shellEvent;
    private LifeOrbEvent orbEvent;

    /// plugins
    private ApiPlaceHolder ph;

    private final GamingLogger logger = new GamingLogger(this);

    private String softwareName;

    public static NamespacedKey withCustomNamespace(String key) {
        return new NamespacedKey(plugin, key);
    }

    public static NamespacedKey withDefaultNamespace(String key) {
        return NamespacedKey.minecraft(key);
    }


    public Permadeath() {
        super();



    }

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        setupConsoleFilter();

        prefix = getConfig().contains("prefix") ? TextFormat.write(getConfig().getString("prefix")).content() : "&c&lPermadeath";
        speedRun = getConfig().getBoolean("speedrun-mode");
        tickAll();

        playTime = getConfig().getInt("dont-touch.play-time");
    }

    @Override
    public void onLoad() {
        try {
            PluginManager.init(this);
            PluginManager.getAccessor().registerHostileMobs();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            plugin.getLogger().severe(ex.getMessage());

        }
    }

    @Override
    public void onDisable() {
        getConfig().set("don't-touch.play-time", playTime);
        saveConfig();
        reloadConfig();

        plugin = null;
    }


    private void tickAll() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!getFile().exists()) {
                saveDefaultConfig();
            }

            if (!loaded) {
                startPlugin();
               // startDataBase();
                loadDefaultConfig();

                if (getConfig().getBoolean("toggles.replace-mobs-on-chunk-load")) {
                    for (World world : Bukkit.getWorlds()) {
                        for (LivingEntity entity : world.getLivingEntities())
                            spawnHandler.applyDayChanges(entity);
                    }
                }

                loaded = true;
            }


            dateData.tick();
            registerListeners();
            registerEvents();

            if (!Bukkit.getOnlinePlayers().isEmpty() && speedRun) {
                playTime++;

                if ((playTime % 3600) == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100.0F, 100.0F);
                    }
                }
            }

            if (getDay() >= 60 && !getConfig().getBoolean("don't-touch.event.life-orb-ended") && orbEvent.isRunning()) {
                if (speedRun)
                    orbEvent.setTimeLeft(60 * 8);
                orbEvent.setRunning(true);
            }

            tickEvents();
            tickPlayers();
            tickWorlds();
        }, 0, 30L);
    }

    private void tickWorlds() {
        if (getDay() >= 40) {
            for (World world : Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() != World.Environment.THE_END).toList())
                for (Ravager ravager : world.getEntitiesByClass(Ravager.class))
                    if (ravager.getPersistentDataContainer().has(new NamespacedKey(this, "ultra_ravager"), PersistentDataType.BYTE)) {
                        List<Block> blocks = ravager.getLineOfSight(null, 5);

                        for (Block block : blocks)
                            for (int i = -1; i < 1; i++)
                                for (int j = -1; j < 1; j++)
                                    for (int k = -1; k < 1; k++) {
                                        Block blockR = block.getRelative(i, j, k);

                                        if (blockR.getType() == Material.NETHERRACK) {
                                            blockR.setType(Material.AIR);
                                            blockR.getWorld().playSound(blockR.getLocation(), Sound.BLOCK_NETHERRACK_BREAK, 2.0F, 1.0F);
                                        }

                                    }

                    }
        }
    }

    private void tickPlayers() {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        overWorld.setHardcore(true);
        overWorld.setDifficulty(Difficulty.HARD);

        long weatherTicks = overWorld.getWeatherDuration() / 20;

        long hours = weatherTicks % 86400 / 3600;
        long minutes = (weatherTicks % 3600) / 60;
        long seconds = weatherTicks % 60;
        long days = weatherTicks / 86400;

        String moreTimer = days >= 1 ? String.format("%02d día(s) ", days) : "";
        final String timer = String.format(moreTimer + "%02d:%02d:%02d", hours, minutes, seconds);

        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();

            if (shellEvent.isRunning())
                if (shellEvent.getBossBar().getPlayers().contains(player))
                    shellEvent.addPlayer(player);

            if (orbEvent.isRunning())
                if (shellEvent.getBossBar().getPlayers().contains(player))
                    shellEvent.addPlayer(player);

            upHealthBoost(player);
            InventoryLockEvent.slotBlock(player);

            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            ServerGamePacketListenerImpl game = nmsPlayer.connection;

            if (speedRun) {
                StringBuilder time = new StringBuilder();

                if (overWorld.hasStorm()) {
                    time.append(messageData.getMessageByPlayer("server.{lang}.action-bar-message", player, value -> value.replace("{time_left}", timer))).append(" - ");
                }

                time.append(NamedTextColor.GRAY).append("Tiempo total: ").append(TextFormat.parseInterval(playTime));
                game.send(new ClientboundSystemChatPacket(Component.literal(time.toString()), true));
            } else{
                if (overWorld.hasStorm()) {
                    TextComponent text = messageData.getMessageByPlayer("server.{lang}.action-bar-message", player, value -> value.replace("{time_left}", timer));

                    game.send(new ClientboundSystemChatPacket(PaperAdventure.asVanilla(text), true));
                }
            }

            if (player.getWorld().getEnvironment() == World.Environment.THE_END && getDay() >= 30) {
                if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10 * 20, 9));
                }
            }

            if (getDay() >= 40) {
                if (player.getWorld().getName().equalsIgnoreCase("the_beginning")) {
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);

                }

                if (player.getWorld().hasStorm() && player.getGameMode() != GameMode.SPECTATOR) {
                    Location block = player.getWorld().getHighestBlockAt(player.getLocation().clone()).getLocation();
                    int highestY = block.getBlockY();

                    if (highestY < player.getLocation().getY()) {

                        int probability = random.nextInt(10000) + 1;

                        int blind = (getDay() < 50 ? 1 : 300);
                        if (probability <= blind) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 0));
                        }
                        if (getDay() >= 50) {
                            if (probability == 301) {
                                int duration = random.nextInt(17);
                                duration = duration + 3;
                                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration * 20, 0));
                            }
                        }
                    }
                }
            }

            if (getDay() >= 50) {
                if (endData != null && beginningData != null) {
                    World beginningWorld = beginning.getWorld();

                    if (!beginningData.killedEd()) {
                        Chunk chunk = beginningWorld.getBlockAt(0, 100, 0).getChunk();
                        for (int x = 0; x < 16; ++x) {
                            for (int y = beginningWorld.getMaxHeight() -1; y > 0; --y) {
                                for (int z = 0; z < 16; ++z) {
                                    Block block = chunk.getBlock(x, y, z);
                                    if (block.getType() == Material.END_GATEWAY || block.getType() == Material.BEDROCK)
                                        block.setType(Material.AIR);
                                }
                            }
                        }
                        if (!beginningWorld.getEntitiesByClass(EnderDragon.class).isEmpty()) {
                            for (EnderDragon dragon : beginningWorld.getEntitiesByClass(EnderDragon.class)) {
                                dragon.remove();
                            }
                            beginningData.setKilledED();
                        }
                    }

                }

                if (player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                    PotionEffect effect = player.getPotionEffect(PotionEffectType.SLOWNESS);

                    if (effect.getDuration() >= 4800 && !effectPlayers.contains(player)) {
                        int min = 600;
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, min * 20, 2));
                        effectPlayers.add(player);
                    }

                    if (effect.getDuration() == (4800 - 1)) {
                        effectPlayers.remove(player);
                    }
                }
            }

            if (player.getWorld().getEnvironment() == World.Environment.NETHER && getDay() < 60) {
                int random = this.random.nextInt(4500) + 1;
                if (random <= 10 && player.getWorld().getLivingEntities().size() < 100) {
                    Location playerLoc = player.getLocation().clone();


                    List<Location> spawns = new ArrayList<>();
                    spawns.add(playerLoc.clone().add(10, 25, -5));
                    spawns.add(playerLoc.clone().add(5, 25, 5));
                    spawns.add(playerLoc.clone().add(-5, 25, 5));

                    for (Location loc : spawns) {
                        if (world.getBlockAt(loc).getType() == Material.AIR && world.getBlockAt(loc.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            int randomEnties = this.random.nextInt(3) + 1;
                            for (int i = 0; i < randomEnties; ++i)
                                PluginManager.getHandler().spawnEntity(PigZombie.class, loc, CreatureSpawnEvent.SpawnReason.CUSTOM);
                        }
                    }
                }
            }

            if (getDay() >= 60) {
                if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOUL_SAND)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30 * 20, 2));


                Integer timeForWither = player.getPersistentDataContainer().get(new NamespacedKey(this, "wither"), PersistentDataType.INTEGER);
                if (timeForWither == null) timeForWither = 0;

                if (timeForWither % 1200 == 0 && player.getGameMode() == GameMode.SURVIVAL) {
                    timeForWither = 0;
                    Wither wither = player.getWorld().spawn(player.getLocation().clone().add(0, 5, 0), Wither.class);

                    WitherBoss witherPersonal = ((CraftWither) wither).getHandle();
                    witherPersonal.getAlternativeTarget(100);
                    witherPersonal.shouldRenderAtSqrDistance(100);
                }

                player.getPersistentDataContainer().set(new NamespacedKey(this, "wither_" + player.getName().toLowerCase()), PersistentDataType.INTEGER, ++timeForWither);

                if (getConfig().getBoolean("toggles.mike-creeper-spawn")) {
                    Location loc = player.getLocation().clone();

                    if (random.nextInt(30) == 0 && player.getNearbyEntities(30, 30, 30)
                            .stream()
                            .filter(Creeper.class::isInstance)
                            .map(Creeper.class::cast)
                            .toList()
                            .size() < 10) {


                        int px = (random.nextBoolean() ? -1 : 1) * (random.nextInt()) + 15;
                        int pz = (random.nextBoolean() ? -1 : 1) * (random.nextInt()) + 15;
                        int y = (int) loc.getY();

                        Block block = loc.getBlock().getWorld().getBlockAt(loc.getBlockX() + px, y, loc.getBlockZ() + pz);
                        Block blockUp = block.getRelative(BlockFace.UP);

                        if (block.getType() == Material.AIR && blockUp.getType() == Material.AIR)
                            getMobFactory().spawnEnderQuantumCreeper(blockUp.getLocation());
                    }
                }

                if (getConfig().getBoolean("toggles.changes.mike")) {

                }

                if (getConfig().getBoolean("toggles.changes.{nombre del pavo}-{sugerencia}")) {

                }
            }
        }


    }

    private void tickEvents() {
        if (orbEvent.isRunning()) {
            if (orbEvent.getTimeLeft() > 0) {

                orbEvent.reduceTime();

                int res = orbEvent.getTimeLeft();

                int hrs = res / 3600;
                int minAndSec = res % 3600;
                int min = minAndSec / 60;
                int sec = minAndSec % 60;

                String tiempo = String.format("%02d:%02d:%02d", hrs, min, sec);

                BarColor[] color = BarColor.values();

                orbEvent.getBossBar().setColor(color[random.nextInt(color.length)]);
                orbEvent.setTitle(TextFormat.write("&6&l" + tiempo + " para obtener el Life Orb"));
            } else {

                Bukkit.broadcast(TextFormat.showWithPrefix("&cSe ha acabado el tiempo para obtener el Life Orb, ¡sufrid! ahora tendreís 8 contenedores de vida menos."));
                orbEvent.setRunning(false);
                orbEvent.clearPlayers();
                orbEvent.setTimeLeft((speedRun ? 60 * 8 : 60 * 60 * 8));
                orbEvent.getBossBar().removeAll();

                getConfig().set("dont-touch.event.life-orb-ended", true);
                saveConfig();
                reloadConfig();
            }
        }

        if (shellEvent.isRunning()) {

            if (shellEvent.getTimeLeft() > 0) {

                shellEvent.setTimeLeft(shellEvent.getTimeLeft() - 1);

                int res = shellEvent.getTimeLeft();

                int hrs = res / 3600;
                int minAndSec = res % 3600;
                int min = minAndSec / 60;
                int sec = minAndSec % 60;

                String tiempo = String.format("%02d:%02d:%02d", hrs, min, sec);

                shellEvent.setTitle(TextFormat.write("&e&lX2 Shulker Shells: &b&n" + tiempo));
            } else {

                Bukkit.broadcast(TextFormat.write(prefix + "&eEl evento de &c&lX2 Shulker Shells &eha acabado."));
                shellEvent.setRunning(false);
                shellEvent.clearPlayers();
                shellEvent.setTimeLeft(60 * 60 * 4);
                shellEvent.getBossBar().removeAll();
            }
        }
    }



    private void startPlugin() {
       // dbData = new DataBaseManager(this);
        messageData = new MessageManager(this);
        dateData = new DateManager(this);


        factory = new MobFactory(this);

        /*
        ApiResource.saveFile(plugin,"schematics/the-beginning", "the-beginning_portal.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "the-beginning_ytic.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "island1.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "island2.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "island3.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "island4.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "island5.schem", true);
        ApiResource.saveFile(plugin,"schematics/the-beginning", "island6.schem", true);
        */
        try {
            if (Class.forName("org.spigotmc.SpigotConfig") != null) {
                softwareName = "SpigotMC (Compatible)";
                runPaper = false;
            }

        } catch (ClassNotFoundException e) {
            softwareName = "Bukkit";
        }

        try {
            if (Class.forName("com.destroystokyo.paper.PaperConfig") != null) {
                softwareName = "PaperMC (Compatible)";
                runPaper = true;
            }
        } catch (ClassNotFoundException e) {
            softwareName = "Bukkit";
        }

        if (softwareName.contains("Bukkit")) {
            System.out.println("El plugin no es compatible con CraftBukkit, solo SpigotMC o PaperSpigot");

            logger.sendMessageDisable("No es combatible con Bukkit");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        loadWorld();
        getLogger().info("Software: " + softwareName);

        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            runWorldEdit = true;
        } else {
            getLogger().warning("Se requiere el plugin WorldEdit");

        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            ph = new ApiPlaceHolder();
            ph.register();
        } else {
            getLogger().warning("Se requiere el plugin PlaceHolderApi");
        }

        loadListeners();
        loadCommands();



        generateOfflinePlayerData();

        logger.registerText("Se ha activado el plugin");
    }

    private void startDataBase() {
        dbData = new DataBaseManager(this);
        driver = new MongoDBDriver(plugin, dbData);
        driver.start();
        MongoDatabase database = driver.getDatabase();

        database.createCollection("discord");
        database.createCollection("clans");
        database.createCollection("users");

    }

    private void registerEvents() {
        shellEvent = new ShulkerShellEvent(this);
        orbEvent = new LifeOrbEvent(this);

        if (plugin.getDay() >= 60)
            getServer().getPluginManager().callEvent(shellEvent);
        getServer().getPluginManager().callEvent(orbEvent);
    }

    private void registerListeners() {
        if (!days.get(1)) {
            days.replace(1, true);

            getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        }

        if (dateData.getDay() >= 20 && !days.get(20)) {
            days.replace(20, true);

            getServer().getPluginManager().registerEvents(new HostilyEntityListener(this), this);
        }

        if (dateData.getDay() >= 30 && endHandler == null && endData == null && !days.get(30)) {
            days.replace(30, true);

            endHandler = new TheEndListener(this);
            endData = new EndManager(this);

            getServer().getPluginManager().registerEvents(endHandler, this);

            if (runPaper) {
                getServer().getPluginManager().registerEvents(new PaperListener(this), this);
            }

        }

        if (dateData.getDay() >= 40 && !days.get(40)) {
            days.replace(40, true);


            recipeData = new RecipeManager(this);
            recipeData.registerRecipes(40);
            getNmsHandler().spawnMobMushrooms();
            getServer().getPluginManager().registerEvents(new SlotBlockListener(this), this);

            if (runWorldEdit) {
                return;
            }

            beginningData = new BeginningManager(plugin);
            beginning = new TheBeginningListener(plugin);
        }

        if (dateData.getDay() >= 50 && !days.get(50)) {

            recipeData = new RecipeManager(this);
            recipeData.registerRecipes(50);


            recipeData.registerRecipes(50);
            days.replace(50, true);
        }

        if (dateData.getDay() >= 60 && !days.get(60)) {

            recipeData = new RecipeManager(this);
            recipeData.registerRecipes(50);


            recipeData.registerRecipes(60);
            days.replace(60, true);

        }

        if (dateData.getDay() >= 70 && !days.get(70)) {
            if (recipeData == null) {
                recipeData = new RecipeManager(this);
                recipeData.registerRecipes(70);
            }

            recipeData.registerRecipes(70);
            days.replace(70, true);

        }
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(this, this);

        spawnHandler = new SpawnListener(this);
        getServer().getPluginManager().registerEvents(spawnHandler, this);
        getServer().getPluginManager().registerEvents(new SkeletonListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new TotemListener(this), this);
        getServer().getPluginManager().registerEvents(new OverworldListener(this), this);

        days.put(1, false);
        days.put(20, false);
        days.put(30, false);
        days.put(40, false);
        days.put(50, false);
        days.put(60, false);
        days.put(70, false);
        days.put(80, false);
        days.put(90, false);
        days.put(100, false);
        days.put(120, false);
    }

    private void setupConsoleFilter() {
        try {
            Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
            org.apache.logging.log4j.core.Logger logger;
            logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
            logger.addFilter(new Log4Filter());
        } catch (ClassNotFoundException | NoClassDefFoundError | ClassCastException e) {
            Filter filter = new Log4Filter();
            getLogger().setFilter(filter);
            Logger.getLogger("Minecraft").setFilter(filter);
        }
    }

    private void loadCommands() {
        try (MinecraftCommandHandler handler = new MinecraftCommandHandler(this)) {
            handler.registerCommands(ItemCommand::new);
            handler.registerCommands(DayConfigCommand::new);
            handler.registerCommands(BanCommand::new);
            handler.registerCommands(CustomReloadCommand::new);
            handler.registerCommands(UnbanCommand::new);

            handler.registerCommands(DayCommand::new);
            handler.registerCommands(InfoCommand::new);
            handler.registerCommands(MessagesCommand::new);

        }
    }


    private void loadDefaultConfig() {
        File file = new File(getDataFolder(), "config.yml");

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ApiResource select = new ApiResource(plugin, file, config);

        select.set("config-version", 3);
        select.set("prefix", "&4&lPermadeath");
        select.set("ban-enabled", true);
        select.set("speedrun-mode", false);

        select.set("afk-options.anti-afk-enabled", true);
        select.set("afk-options.days-limit", 5);

        select.set("toggles.default-death-sounds-enabled", true);
        select.set("toggles.optifine", true);
        select.set("toggles.double-mob-cap", true);
        select.set("toggles.end.boss", "INITIAL");
        select.set("toggles.end.mob-spawn-limit", 70);
        select.set("toggles.end.ender-whiter-skeleton-count", 10);
        select.set("toggles.end.ender-blaze-count", 20);
        select.set("toggles.end.ender-ghast-count", 70);
        select.set("toggles.end.ender-creeper-count", 20);
        select.set("toggles.end.protect-spawn", false);
        select.set("toggles.end.protect-radius", 0);
        select.set("toggles.end.permadeath-demon.names.normal", "&c&lPERMADEATH &4&lGODMON");
        select.set("toggles.end.permadeath-demon.names.enraged", "&l&k! &r&6&lENRAGED PERMADEATH GODMON &l&k!");
        select.set("toggles.end.permadeath-demon.names.psycho", "&#&l&k!! &r&5&lPYSCHO PERMADEATH &d&lGODMON &l&k!!");
        select.set("toggles.end.permadeath-demon.health.normal", 3000);
        select.set("toggles.end.permadeath-demon.heath.enraged", 1200);
        select.set("toggles.end.permadeath-demon.heath.psycho", 600);
        select.set("toggles.nether.mob-spawn-limit", 70);
        select.set("toggles.nether.protect-spawn", false);
        select.set("toggles.nether.protect-radius", 0);
        select.set("toggles.nether.generate-pure-netherite", true);
        select.set("toggles.nether.change-drop-pure-netherite", 13);
        select.set("toggles.the-beginning.ytic-generate-chances", 50000);
        select.set("toggles.the-beginning.hostile-mobs", true);
        select.set("toggles.the-beginning.portal.self", List.of(0, 0));
        select.set("toggles.the-beginning.portal.self", List.of(5000, 5000));
        select.set("toggles.player-skulls", true);
        select.set("toggles.op-ban", true);
        select.set("toggles.spider-effect", true);
        select.set("toggles.optimization-mob-spawn", true);
        select.set("toggles.replace-mobs-on-chunk-load", true);
        select.set("toggles.quantum-explosion-power", 60);
        select.set("toggles.cat-supernova.destroy-blocks", true);
        select.set("toggles.cat-supernova.fire", false);
        select.set("toggles.cat-supernova.explosion", 200);

        select.set("server.options.messages.coords-enable", true);
        select.set("server.options.messages.death-default", "&7{player} ha muerto y ahora descanza en paz.");
        select.set("server.options.messages.death-custom-path", "lang/message/custom-death");

        select.set("world-options.main.overworld", "&aOne Life");
        select.set("world-options.main.nether", "&4Last Bastion");
        select.set("world-options.main.end", "&5The End");

        select.set("totem-options.enable", true);
        select.set("totem-options.not-enough-totems", "&7¡%player% no tenía suficientes tótems en el inventario!");
        select.set("totem-options.player-fail-message.totem", "&7¡El tótem de &c{player} &7ha fallado!");
        select.set("totem-options.player-fail-message.totems", "&7¡Los tótems de &c{player} &7han fallado!");
        select.set("totem-options.player-used-message.player-used-message.totem", "&7El jugador {player} ha consumido un tótem (Probabilidad: {totem_fail} {percent} {number})");
        select.set("totem-options.player-used-message.player-used-message.totems", "&7El jugador {player} ha consumido {amount} tótems (Probabilidad: {totem_fail} {percent} {number})");

        select.set("totem-options.fail-list", List.of(
                "[30-39]: 1",
                "[40-49]: 3",
                "[50-59]: 5",
                "[60-69]: 15",
                "[70-79]: 30",
                "[80-99]: 50",
                "[100-120]: 70"
        ));

        select.save();
        select.load();
    }


    private void loadWorld() {
        NamespacedKey key = new NamespacedKey(this, "world_id");

        if (Bukkit.getWorld(getConfig().getString("world-options.main.overworld")) == null) {
            for (World world : Bukkit.getWorlds())
                if (world.getEnvironment() == World.Environment.NORMAL) {
                    this.overWorld = world;
                    this.overWorld.getPersistentDataContainer().set(key, PersistentDataType.STRING, "overworld");
                    break;
                }
        } else {
            overWorld = Bukkit.getWorld("world");
            overWorld.getPersistentDataContainer().set(key, PersistentDataType.STRING, "overworld");
        }

        if (Bukkit.getWorld(getConfig().getString("world-options.main.nether")) == null) {
            for (World world : Bukkit.getWorlds())
                if (world.getEnvironment() == World.Environment.NETHER) {
                    this.nether = world;
                    this.nether.getPersistentDataContainer().set(key, PersistentDataType.STRING, "nether");
                    break;
                }

        } else {
            nether = Bukkit.getWorld("world_nether");
            nether.getPersistentDataContainer().set(key, PersistentDataType.STRING, "nether");
        }


        if (Bukkit.getWorld(getConfig().getString("world-options.main.the-end")) == null) {
            for (World world : Bukkit.getWorlds())
                if (world.getEnvironment() == World.Environment.THE_END) {
                    this.end = world;
                    this.end.getPersistentDataContainer().set(key, PersistentDataType.STRING, "the_end");
                    break;
                }
        } else {
            end = Bukkit.getWorld("world_the_end");
            end.getPersistentDataContainer().set(key, PersistentDataType.STRING, "the_end");
        }

        boolean doubleCap = getConfig().getBoolean("toggles.double-mod-cap") && getDay() >= 10;
        if (doubleCap)
            Bukkit.getConsoleSender().sendMessage(TextFormat.write(prefix + "&eDuplicando la mob-cap en todos los mundos."));

        for (World world : Bukkit.getWorlds()) {
            if (doubleCap) {
                world.setSpawnLimit(SpawnCategory.MONSTER, 140);
            }

            if (isRunningPaper()) {
                ServerLevel nmsWorld = ((CraftWorld) world).getHandle();
                nmsWorld.paperConfig().entities.behavior.disableCreeperLingeringEffect = true;
                Bukkit.getConsoleSender().sendMessage(TextFormat.write(prefix + "&eDeshabilitando Creeper-Lingering-Effect"));

            }
        }

    }

    public void reload(CommandSender sender) {
        loadDefaultConfig();
        reloadConfig();

       // messageData.reloadFile();
        dateData.reloadDate();
        loadWorld();
        logger.registerText("El plugin se ha recargado");
    }

    public void generateOfflinePlayerData() {
        for (OfflinePlayer off :  Bukkit.getOfflinePlayers()) {
            if (off == null)
                return;

            playerData = new PlayerManager(off, this);
            playerData.generateDay();
        }
    }

    public void upHealthBoost(Player player) {
        double value = getAvailableMaxHeath(player);

        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(value);
    }

    public double getAvailableMaxHeath(Player player) {
        int currentPurePieces = 0;
        int currentInfernalPieces = 0;
        int currentAlmorityPieces = 0;

        int playerAte = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(this, "golden_apple_plus_ate"), PersistentDataType.INTEGER, 20);


        for (ItemStack contents : player.getInventory().getArmorContents()) {
            if (isPureNetheritePiece(contents))
                currentPurePieces++;
            if (isInfernalNetheritePiece(contents))
                currentInfernalPieces++;
            if (isAlmorityPiece(contents))
                currentAlmorityPieces++;
        }

        double maxHealth = MAX_HEALTH;

        if (playerAte == 1)
            maxHealth += 4.0;
        if (playerAte == 2)
            maxHealth += 4.0;
        if (playerAte == 3)
            maxHealth += 6.0;

        if (currentInfernalPieces >= 4) {
            maxHealth += 10.0D;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 3, 2));
        }
        if (currentPurePieces >= 4) {
            maxHealth += 12.0D;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 3, 1));
        }

        if (currentAlmorityPieces >= 4) {
            maxHealth += 14.0D;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 3, 3));
        }


        if (getDay() >= 40) {
            maxHealth -= 8.0;
            if (getDay() >= 60) {
                maxHealth -= 8.0;
                if (getDay() >= 70)
                    maxHealth -= 10.0;

                if (!checkForLifeOrb(player))
                    maxHealth -= 16.0;
            }
        }
        return Math.max(maxHealth, 0.000001D);
    }

    public boolean checkForLifeOrb(Player player) {
        if (orbEvent.isRunning()) {
            return true;
        } else for (ItemStack item : player.getInventory().getContents()) {
                if (item.getType() == Material.BROWN_DYE && item.getItemMeta().getCustomModelDataComponent().getStrings().contains("orb_life"))
                    return true;
        }
        return false;
    }

    public void explodeCat(Cat cat) {
        if (novaCats.contains(cat))
            return;

        novaCats.add(cat);

        boolean canContinue = !Bukkit.getOnlinePlayers().isEmpty();

        if (novaCats.size() > 2)
            canContinue = false;

        if (!canContinue) {
            cat.remove();
            return;
        }

        World world = cat.getWorld();
        Location loc = cat.getLocation().clone();
        Chunk chunk = loc.getChunk();

        if (!chunk.isForceLoaded())
            chunk.setForceLoaded(true);
        if (!chunk.isLoaded())
            chunk.load();

        Bukkit.broadcast(TextFormat.withCodef("&cUn gato supernova va a explotar en: %d %d %d (%s).", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), cat.getWorld().getName()));
        Bukkit.getServer().getScheduler().runTaskLater(this, () -> {
            if (cat == null) {
                if (chunk.isForceLoaded())
                    chunk.setForceLoaded(false);
                if (chunk.isLoaded())
                    chunk.unload();
                return;
            }
            if (!novaCats.contains(cat)) {
                if (chunk.isForceLoaded())
                    chunk.setForceLoaded(false);
                if (chunk.isLoaded())
                    chunk.unload();
                return;
            }

            float power = (float) getConfig().getInt("toggles.cat-supernova.explosion");
            boolean breakBlocks = getConfig().getBoolean("toggles.cat-supernova.destroy-blocks");
            boolean placeFire = getConfig().getBoolean("toggles.cat-supernova.fire");

            world.createExplosion(loc, power, placeFire, breakBlocks, cat);
            novaCats.remove(cat);
            cat.remove();

            if (chunk.isForceLoaded())
                chunk.setForceLoaded(false);
            if (chunk.isLoaded())
                chunk.unload();

        }, 60000);
    }

    public void addDeathTrainEffects(LivingEntity entity) {
        int day = (int) getDay();

        if (entity instanceof Player) return;

        if (day >= 25) {
            int lvl = 0;

            if (day >= 50 && day < 70)
                lvl = 1;

            if (day >= 70 && day < 90)
                lvl = 2;

            if (day >= 90 && day < 100)
                lvl = 3;

            //lvl = (getDay() >= 50 ? 1 : 0);


            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, lvl));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, lvl));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, lvl));

            if (getDay() >= 50 && getDay() < 60) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, lvl + 1));
            }

            if (getDay() >= 100) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, lvl + 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, lvl + 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
            }
        }
    }

    public void addDeathTrainEffects(Player player) {
        if (getDay() >= 75) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 90,4));
        }

    }



    public boolean isEndRelic(ItemStack stack) {
        if (stack == null)
            return false;
        if (!stack.hasItemMeta())
            return false;

        ItemMeta meta = stack.getItemMeta();

        return stack.getType() == Material.LIGHT_BLUE_DYE && meta.getCustomModelDataComponent().getStrings().contains("end_relic");
    }

    public boolean isBeginningRelic(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        CustomModelDataComponent data = stack.getItemMeta().getCustomModelDataComponent();

        return stack.getType() == Material.CYAN_DYE && data.getStrings().contains("beginning_relic");
    }

    public boolean isNetheritePiece(ItemStack armor) {
        if (armor == null) return false;

        return isArmorType(armor);
    }

    public boolean isPureNetheritePiece(ItemStack armor) {
        return isArmorCustom(armor, ArmorKits.PURE_NETHERITE);
    }

    public boolean isInfernalNetheritePiece(ItemStack armor) {
        if (armor == null) return false;

        if (armor.hasData(DataComponentTypes.EQUIPPABLE)) {
            Equippable equippable = armor.getData(DataComponentTypes.EQUIPPABLE);
            Key assetId = equippable.assetId();

            return armor.getType() == Material.ELYTRA && assetId.equals(new NamespacedKey(this, "infernal_netherite_elytra"));
        }

        return isArmorCustom(armor, ArmorKits.INFERNAL_NETHERITE);
    }

    public boolean isAlmorityPiece(ItemStack armor) {
        return isArmorCustom(armor, ArmorKits.ALMORITY);
    }

    private boolean isArmorCustom(ItemStack piece, ArmorKit kit) {
        if (piece == null) return false;

        if (piece.hasData(DataComponentTypes.EQUIPPABLE) && piece.hasData(DataComponentTypes.DYED_COLOR) && isArmorType(piece)) {
            Equippable equippable = piece.getData(DataComponentTypes.EQUIPPABLE);
            DyedItemColor dyed = piece.getData(DataComponentTypes.DYED_COLOR);
            Key assetId = equippable.assetId();

            Optional<Color> color = kit.color();

            if (dyed.color().equals(color.get())) return true;
            
            return assetId.equals(kit.assetId());
        }

        return false;
    }

    private boolean isArmorType(ItemStack piece) {
        return piece.getType() == Material.NETHERITE_HELMET || piece.getType() == Material.NETHERITE_CHESTPLATE || piece.getType() == Material.NETHERITE_LEGGINGS || piece.getType() == Material.NETHERITE_BOOTS;
    }

    public void setEndTask(DemonEndTask task) {
        this.endTask = task;
    }


    public void setNovaCats(List<Cat> novaCats) {
        this.novaCats = novaCats;

    }

    public static Permadeath getPlugin() {
        return plugin;
    }

    public NmsHandler getNmsHandler() {
        return PluginManager.getHandler();
    }

    public NmsAccessor getNmsAccesor() {
        return PluginManager.getAccessor();
    }

    public NmsAccessor getNmsEntity(LivingEntity entity) {
        return PluginManager.getNmsEntity(entity);
    }


    public NmsBlock getNmsBlock(BlockState type) {
        return PluginManager.getNmsBlock(type);
    }

    public static boolean isOptifineEnabled() {
        if (plugin == null)
            return false;
        return plugin.getConfig().getBoolean("toggles.optifine");
    }

    public static boolean isRunningPaper() {
        return runPaper;
    }

    public static boolean isHasWorldEdit() {
        return runWorldEdit;
    }

    public static String showPrefix() {
        return prefix;
    }

    public static boolean isSpeedRunMode() {
        return speedRun;
    }

    public World getOverWorld() {
        return overWorld;
    }

    public World getEnd() {
        return end;
    }

    public World getNether() {
        return nether;
    }

    public LifeOrbEvent getLifeOrbEvent() {
        return orbEvent;
    }

    public ShulkerShellEvent getShulkerShellEvent() {
        return shellEvent;
    }

    public DataBaseManager getDbData() {
        return dbData;
    }

    public DateManager getDateData() {
        return dateData;
    }


    public BeginningManager getBeginningData() {
        return beginningData;
    }

    public MessageManager getMessageData() {
        return messageData;
    }

    public EndManager getEndData() {
        return endData;
    }


    public List<Player> getEffectPlayers() {
        return effectPlayers;
    }

    public int getPlayTime() {
        return playTime;
    }

    public long getDay() {
        return dateData.getDay();
    }

    public GamingLogger getDataLogger() {
        return logger;
    }

    public static double getDayLiteral() {
        return plugin.dateData.getDay();
    }

    public TheBeginningListener getBeginning() {
        return beginning;
    }

    public MobFactory getMobFactory() {
        return factory;
    }

    public DemonEndTask getEndTask() {
        return endTask;
    }

    public NmsBlock getBlocks() {
        return null;
    }

    public SpawnListener getSpawnListener() {
        return spawnHandler;
    }

    public MongoDBDriver getMongoDriver() {
        return driver;
    }
}
