package dev.yeruza.plugin.permadeath.worlds.beginning;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.block.PermadeathBlocks;
import dev.yeruza.plugin.permadeath.data.BeginningManager;
import dev.yeruza.plugin.permadeath.utils.TextFormat;
import dev.yeruza.plugin.permadeath.worlds.BeginningWorldEdit;

public class TheBeginningListener implements Listener {
    private final Permadeath plugin;
    private World theBeginning;

    private BeginningManager data;
    private BeginningWorldEdit manager;
    private BeginningLootTable loot;

    private boolean closed = false;

    public TheBeginningListener(Permadeath plugin) {
        this.plugin = plugin;
        theBeginning = null;
        this.data = plugin.getBeginningData();


        if (plugin.getDay() >= 40) {
            createWorld();
            this.manager = new BeginningWorldEdit(plugin, theBeginning);
            this.loot = new BeginningLootTable();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (Permadeath.isRunningPaper()) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY) return;
        if (world.getName().equalsIgnoreCase(plugin.getOverWorld().getName())) {
            event.setCanCreatePortal(true);
        }

        if (world.getName().equalsIgnoreCase(theBeginning.getName()) && world.getPersistentDataContainer().has(theBeginning.getKey(), PersistentDataType.STRING)) {
            if (player.getLocation().getBlock().getState() instanceof EndGateway gateway) {
                gateway.setExitLocation(null);
                gateway.update();
                player.getLocation().getBlock().getState().update();
            }

            event.setCanCreatePortal(false);

        }
    }

    @EventHandler
    public void onTeleport(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY) return;

        if (closed) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getDay() < 50) {
            if (player.getWorld().getPersistentDataContainer().has(plugin.getOverWorld().getKey()) || player.getWorld().getName().equalsIgnoreCase(theBeginning.getName())) {
                event.getPlayer().setNoDamageTicks(event.getPlayer().getMaximumNoDamageTicks());
                event.getPlayer().damage(event.getPlayer().getHealth() + 1.0D, (Entity) null);
                event.getPlayer().setNoDamageTicks(0);
                Bukkit.broadcast(TextFormat.write("&c&lEl jugador &4&l" + event.getPlayer().getName() + " &c&lentró a TheBeginning antes de tiempo."));

            }
            return;
        }

        if (player.getWorld().getName().equalsIgnoreCase(plugin.getOverWorld().getName())) {
            event.getPlayer().sendMessage("&eBienvenido a The Beginning");
            event.getPlayer().teleport(theBeginning.getSpawnLocation());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                event.getPlayer().teleport(theBeginning.getSpawnLocation());
            }, 20L);
        }

        int x = (int) player.getLocation().getX();
        int z = (int) player.getLocation().getZ();


        if (player.getWorld().getPersistentDataContainer().has(theBeginning.getKey()) && x != 200 && z != 200) {
            if (player.getLocation().getBlock().getState() instanceof EndGateway gateway) {
                gateway.setExitLocation(null);
                gateway.update();
                player.getLocation().getBlock().getState().update();
            }
        }

        event.getPlayer().teleport(plugin.getOverWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        event.setCancelled(true);
    }

    private void createWorld() {
        if (Bukkit.getWorld("the_beginning") == null) {
            WorldCreator world = new WorldCreator("the_beginning");

            world.environment(World.Environment.THE_END);
            world.generator(new BeginningGenerator());
            world.generateStructures(false);

            theBeginning = world.createWorld();
            theBeginning.getPersistentDataContainer().set(Permadeath.withCustomNamespace("world_id"), PersistentDataType.STRING, "the_beginning");
            if (plugin.getConfig().getBoolean("toggles.double-mob-cap"))
                theBeginning.setSpawnLimit(SpawnCategory.MONSTER, 140);


            theBeginning.setGameRule(GameRule.MOB_GRIEFING, false);

        } else {
            theBeginning = Bukkit.getWorld("the_beginning");
            theBeginning.getPersistentDataContainer().set(Permadeath.withDefaultNamespace("world_id"), PersistentDataType.STRING, "the_beginning");
        }
    }

    public void closeWorld() {
        if (theBeginning == null) return;

        for (Player player : theBeginning.getPlayers()) {
            player.teleport(plugin.getOverWorld().getSpawnLocation());
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0F, 1.0F);
        }

        Bukkit.broadcast(TextFormat.showWithPrefix("&eThe Beginning ha cerrado temporalmente (DeathTrain)."));
        closed = true;
    }

    @EventHandler
    public void onCreatePortal(PortalCreateEvent event) {
        if (event.getWorld().getPersistentDataContainer().has(theBeginning.getKey())) {
            for (BlockState state : event.getBlocks()) {
                Block block = state.getBlock();

                if (block.getType() == Material.END_GATEWAY || block.getType() == Material.BEDROCK || state instanceof EndGateway) {
                    if (block.getChunk().getX() == 0 && block.getChunk().getZ() == 0) {
                        event.getBlocks().remove(state);
                        state.setType(Material.AIR);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getPersistentDataContainer().has(theBeginning.getKey()))
            if (event.getBlock().getState() instanceof Chest chest)
                populateChest(chest);

        if (event.getBlock().getType() == Material.SPAWNER) {
            plugin.getNmsBlock(PermadeathBlocks.INFERNAL_NETHERITE_BLOCK).onBlockBreak(event);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null && player.getWorld().getPersistentDataContainer().has(theBeginning.getKey())) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getState() instanceof Chest chest) {
                    populateChest(chest);
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && plugin.getBlocks() != null) {
            if (event.getClickedBlock().getType() == Material.CHEST) return;

            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();

            if (item.getType() == Material.DIAMOND && meta.getCustomModelDataComponent().getStrings().contains("infernal_netherite_block")) {

                plugin.getNmsBlock(PermadeathBlocks.INFERNAL_NETHERITE_BLOCK).onBlockPlace(event);
                if (item.getAmount() > 0)
                    item.setAmount(item.getAmount() + 1);
                else
                    item = null;

                ItemStack finalItem = item;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    event.getPlayer().getInventory().setItemInMainHand(finalItem);
                    event.getPlayer().updateInventory();
                });
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        if (theBeginning == null) return;

        if (event.getPlayer().getWorld().getPersistentDataContainer().has(theBeginning.getKey()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onWater(BlockDispenseEvent event) {
        if (theBeginning == null) return;

        if (event.getItem() != null)
            if (event.getItem().getType() == Material.BUCKET || event.getItem().getType() == Material.WATER_BUCKET)
                if (event.getBlock().getWorld().getPersistentDataContainer().has(theBeginning.getKey()))
                    event.setCancelled(true);

    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {

        if (theBeginning == null) return;

        if (event.isCancelled() || event.getSpawner() == null)
            return;
        CreatureSpawner spawner = event.getSpawner();
        if (event.getEntity().getWorld().getPersistentDataContainer().has(theBeginning.getKey())) {
            if (event.getEntityType() != EntityType.ARMOR_STAND) {
                spawner.setSpawnedType(event.getEntityType());
                spawner.update();
            }
            if (event.getEntityType() == EntityType.WITHER && event.getEntity() instanceof Wither wither) {
                wither.setRemoveWhenFarAway(true);
            }
            if (event.getEntityType() == EntityType.GHAST && event.getEntity() instanceof Ghast) {
                Ghast ghast = plugin.getNmsHandler().spawnCustomGhast(event.getLocation().add(0, 5, 0), true);
                ghast.customName(TextFormat.write("&6Ender Ghast Definitivo"));
                plugin.getNmsEntity(ghast).setMaxHealth(150.0D);
                event.setCancelled(true);
            }
            if (event.getEntityType() == EntityType.CREEPER && event.getEntity() instanceof Creeper creeper) {

                event.getEntity().customName(TextFormat.write("&6Quantum Creeper"));
                creeper.setPowered(true);
                creeper.getPersistentDataContainer().set(new NamespacedKey(plugin, "quantum_creeper"), PersistentDataType.BYTE, (byte) 1);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (theBeginning == null) return;

        if (event.getPlayer().getWorld().getPersistentDataContainer().has(theBeginning.getKey())) {
            if (event.getBlock().getState() instanceof Chest chest) {
                BeginningManager data = plugin.getBeginningData();
                data.addPopulatedChest(chest.getLocation());
            }
        }
    }

    private void populateChest(Chest chest) {

        if (data.getConfig().contains("beginning-options.populated-chests")) {
            if (data.hasPopulatedChest(chest.getLocation())) return;
            if (plugin.getDay() < 65) {

                loot.populateChest(chest, this);
            }
            data.addPopulatedChest(chest.getLocation());
        }
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void generatePortal(Location spawnCoords, boolean canGen) {
        if (spawnCoords == null)
            return;

        BeginningWorldEdit manager = new BeginningWorldEdit(plugin, spawnCoords.getWorld());
        manager.generatePortal(spawnCoords, canGen);
    }

    public boolean isClosed() {
        return closed;
    }

    public World getWorld() {
        return theBeginning;
    }

    public NamespacedKey getKey() {
        return theBeginning.getKey();
    }
}

