package dev.yeruza.plugin.permadeath.worlds.end;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.task.boss.demon.DemonEndTask;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class TheEndListener implements Listener {
    private Permadeath plugin;

    private List<Entity> enderCreepers;
    private List<Entity> enderGhasts;

    private List<Location> alreadyExploded = new ArrayList<>();

    private List<Enderman> invulnerable = new ArrayList<>();

    private SplittableRandom random;

    public TheEndListener(Permadeath plugin) {
        this.plugin = plugin;

        enderCreepers = new ArrayList<>();
        enderGhasts = new ArrayList<>();
        random = new SplittableRandom();
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (isTheEnd(event.getEntity().getLocation())) {
            if (event.getEntity() instanceof TNTPrimed) {
                if (!(event.getEntity() instanceof TNTPrimed))
                    return;
                if (event.getEntity().getCustomName() == null)
                    return;
                if (!event.getEntity().getPersistentDataContainer().has(Permadeath.withCustomNamespace("tnt_demon")))
                    return;

                event.setRadius(15.0F);
            }
        }
    }

    @EventHandler
    public void onEffectApply(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud area = event.getEntity();

        if (isTheEnd(area.getLocation())) {
            if (area.getParticle() == Particle.HAPPY_VILLAGER) {
                for (Entity all : event.getAffectedEntities()) {
                    if (all instanceof Player)
                        event.setCancelled(true);
                    else if (all.getType() == EntityType.ENDERMAN) {
                        Enderman man = (Enderman) all;
                        invulnerable.add(man);
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                            if (man == null)
                                return;
                            invulnerable.remove(man);
                        }, 20 * 15);
                        event.setCancelled(true);
                    }
                }
            }

            if (area.getParticle() == Particle.SMOKE)
                for (Entity all : event.getAffectedEntities()) {
                    if (all instanceof Player player) {
                        if (player.getLocation().distance(area.getLocation()) <= 3.0D) {
                            if (!player.getActivePotionEffects().isEmpty())
                                for (PotionEffect effect : player.getActivePotionEffects())
                                    player.removePotionEffect(effect.getType());

                        }
                    }
                }

        }
    }

    @EventHandler
    public void onDamageBE(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Enderman enderman) {
            if (invulnerable.contains(enderman)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDead(EntityDeathEvent event) {

        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            if (plugin.getEndTask() != null) {

                plugin.getEndTask().setDied(true);
                for (Player all : plugin.getEnd().getPlayers()) {
                    spawnFireworks(all.getLocation().add(0, 1, 0), 1);
                }
            }
        }

        Entity entity = event.getEntity();

        if (enderGhasts.contains(entity)) {
            enderGhasts.remove(entity);
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        if (enderCreepers.contains(entity)) {
            enderCreepers.remove(entity);
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        if (entity instanceof Shulker shulker && shulker.getColor() != DyeColor.RED) {
            boolean isSure = true;
            for (Entity near : event.getEntity().getNearbyEntities(2, 2, 2))
                if (near.getType() == EntityType.TNT)
                    isSure = false;


            if (isSure) {
                TNTPrimed tnt = (TNTPrimed) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.TNT);
                tnt.setFuseTicks(80);
                tnt.getPersistentDataContainer().set(Permadeath.withCustomNamespace("tnt_demon"), PersistentDataType.BYTE, (byte) 1);


                event.getDrops().clear();

                int randomProb = new Random().nextInt(99);
                randomProb = randomProb + 1;

                if (plugin.getDay() <= 39) {

                    if (randomProb <= 20) {

                        if (plugin.getShulkerShellEvent().isRunning())
                            event.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
                        else
                            event.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 1));

                    }
                } else if (plugin.getDay() >= 40) {

                    if (randomProb <= 2) {

                        if (plugin.getShulkerShellEvent().isRunning())
                            event.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 2));
                        else
                            event.getDrops().add(new ItemStack(Material.SHULKER_SHELL, 1));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (isTheEnd(entity.getLocation())) {
            if (event.getEntity().getType() == EntityType.END_CRYSTAL && plugin.getEndTask() != null) {
                if (alreadyExploded.contains(entity.getLocation())) return;

                EnderCrystal crystal = (EnderCrystal) event.getEntity();

                if (event.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK) {
                    int random = new Random().nextInt(plugin.getEndData().getTimeList().size());

                    plugin.getEndTask().getRegenTime().put(crystal.getLocation(), plugin.getEndData().getTimeList().get(random));

                    Location nLoc = event.getLocation().clone().add(0, 10, 0);
                    Ghast ghast = plugin.getNmsHandler().spawnCustomGhast(nLoc, true);
                    final Location location = event.getLocation();

                    enderGhasts.add(ghast);
                    alreadyExploded.add(nLoc);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (alreadyExploded.contains(location))
                            alreadyExploded.remove(location);
                    }, 100);


                    for (Player all : plugin.getEnd().getPlayers()) {
                        all.playSound(nLoc, Sound.ENTITY_WITHER_SPAWN, 100.0f, 100.0f);
                    }
                }
            }
        }

        if (event.getEntity() instanceof TNTPrimed tnt) {
            if (!tnt.getPersistentDataContainer().has(Permadeath.withCustomNamespace("super_crystal"))) return;

            if (event.blockList().isEmpty()) {
                Location eggDemon = new Location(plugin.getEnd(), 0, 0, 0);
                Location withY = plugin.getEnd().getHighestBlockAt(eggDemon).getLocation();

                if (event.getLocation().distance(withY) <= plugin.getConfig().getInt("toggles.end.protect-radius") && plugin.getConfig().getBoolean("toggles.end.protect-end-spawn")) {
                    event.blockList().clear();
                    event.setYield(0);
                    return;
                }

                List<FallingBlock> fallingBlocks = new ArrayList<>();
                List<Block> blocks = new ArrayList<>(event.blockList());

                for (Block block : blocks) {
                    float x = (float) (-0.2 + (float) (Math.random() * ((0.2 - -0.2) + 0.2)));
                    float y = -1 + (float) (Math.random() * ((1 - -1) + 1));
                    float z = (float) (-0.2 + (float) (Math.random() * ((0.2 - -0.2) + 0.2)));

                    if (block.getType() == Material.END_STONE || block.getType() == Material.END_STONE_BRICKS) {
                        BlockData data = block.getState().getBlockData();

                        FallingBlock fb = block.getWorld().spawn(block.getLocation(), FallingBlock.class, d -> d.setBlockData(data));
                        block.getState().setBlockData(data);
                        fb.setVelocity(new Vector(x, y, z));
                        fb.setDropItem(false);
                        fb.setMetadata("Exploded", new FixedMetadataValue(plugin, 1));
                        fallingBlocks.add(fb);

                        BukkitRunnable bukkit = new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Block block : blocks) {
                                    block.getState().update();
                                    this.cancel();
                                }
                            }
                        };
                        bukkit.runTaskLater(plugin, 2L);
                        event.blockList().clear();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDemonRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof EnderDragon demon)
            event.setAmount(event.getAmount() / 2);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        LivingEntity entity = event.getEntity();

        if (isTheEnd(entity.getLocation())) {
            if (plugin.getEndTask() == null) {
                for (EnderDragon demon : event.getLocation().getWorld().getEntitiesByClass(EnderDragon.class))
                    if (demon.isValid() && !demon.isDead()) {

                        demon.customName(TextFormat.write(plugin.getConfig().getString("toggles.end.permeadeath-demon.names.normal")));
                        demon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(plugin.getConfig().getInt("toggles.end.permadeath-demon.health.normal"));
                        demon.setHealth(plugin.getConfig().getInt("toggles.end.permadeath-demon.health.normal"));

                        plugin.setEndTask(new DemonEndTask(plugin, demon));
                        plugin.getEndTask().runTaskTimer(plugin, 0, 20L);
                    }
            } else {
                if (!plugin.getEndTask().isDied()) {
                    EnderDragon demon = plugin.getEndTask().getPermadeathDemon();
                    if (demon.getType() == EntityType.ENDER_DRAGON && demon.isValid() && !demon.isDead()) {
                        int health = plugin.getConfig().getInt("toggles.end.permadeath-demon.health.normal");
                        int enragedHealth = plugin.getConfig().getInt("toggles.end.permadeath-demon.health.enraged");
                        int psychoHealth = plugin.getConfig().getInt("toggles.end.permadeath-demon.health.psycho");

                        if (enragedHealth < health || enragedHealth < 10)
                            enragedHealth = health;

                        if (demon.getHealth() <= enragedHealth) {
                            plugin.getEndTask().setCurrentPhase(DemonPhase.ENRAGED);
                        }
                        if (demon.getHealth() <= psychoHealth) {
                            plugin.getEndTask().setCurrentPhase(DemonPhase.PSYCHO);
                        }
                    }
                }
            }

            if (!(entity instanceof Enderman))
                return;

            int witherSkeletonMobCap = plugin.getConfig().getInt("toggles.end.ender-wither-skeleton-count");
            if (witherSkeletonMobCap < 1 || witherSkeletonMobCap > 1000)
                witherSkeletonMobCap = 20;

            int blazeMobCap = plugin.getConfig().getInt("toggles.end.ender-wither-skeleton-count");
            if (blazeMobCap < 1 || blazeMobCap > 1000)
                blazeMobCap = 20;

            int creeperMobCap = plugin.getConfig().getInt("toggles.end.ender-creeper-count");
            if (creeperMobCap < 1 || creeperMobCap > 1000)
                creeperMobCap = 40;

            int ghastMobCap = plugin.getConfig().getInt("toggles.end.ender-ghast-count");
            if (ghastMobCap < 1 || ghastMobCap > 1000)
                ghastMobCap = 170;




            int witherSkeletonProb = random.nextInt(witherSkeletonMobCap) + 1;
            int blazeProb = random.nextInt(blazeMobCap) + 1;
            int creeperProb = random.nextInt(creeperMobCap) + 1;
            int ghastProb = random.nextInt(ghastMobCap) + 1;

            if (witherSkeletonProb == 1) {
                if (plugin.getDay() >= 40) {

                }
            }

            if (blazeProb == 1) {
                if (plugin.getDay() >= 40) {

                }
            }

            if (creeperProb == 1) {
                if (plugin.getDay() < 60) {
                    plugin.getMobFactory().spawnEnderCreeper(event.getLocation());
                } else if (plugin.getDay() >= 60) {
                    plugin.getMobFactory().spawnEnderQuantumCreeper(event.getLocation());
                }
                event.setCancelled(true);
            }

            if (ghastProb == 1) {
                boolean demonDead = plugin.getEnd().getEntitiesByClass(EnderDragon.class).isEmpty();
                if (demonDead) {
                    plugin.getNmsHandler().spawnCustomGhast(event.getLocation(), true);
                    event.setCancelled(true);
                }
            }

        } else {
            if (event.getEntity() instanceof Enderman enderman) {
                if (plugin.getDay() >= 40 && (random.nextInt(100) + 1) == 1)
                    plugin.getNmsEntity(enderman).injectHostilePathfinders();
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!isTheEnd(event.getEntity().getLocation())) return;

        if (event.getHitBlock() != null) {
            if (event.getEntity() instanceof ShulkerBullet bullet) {

                if (bullet.getShooter() instanceof Shulker shulker) {

                    if (shulker.getLocation().distance(event.getHitBlock().getLocation()) >= 4.0) {
                    Location pos = event.getHitBlock().getLocation();

                    if (event.getHitBlockFace() == BlockFace.EAST)
                        pos = event.getHitBlock().getRelative(BlockFace.EAST).getLocation();

                    if (event.getHitBlockFace() == BlockFace.UP)
                        pos = event.getHitBlock().getRelative(BlockFace.UP).getLocation();

                    if (event.getHitBlockFace() == BlockFace.DOWN)
                        pos = event.getHitBlock().getRelative(BlockFace.DOWN).getLocation();

                    if (event.getHitBlockFace() == BlockFace.NORTH)
                        pos = event.getHitBlock().getRelative(BlockFace.NORTH).getLocation();

                    if (event.getHitBlockFace() == BlockFace.SOUTH)
                        pos = event.getHitBlock().getRelative(BlockFace.SOUTH).getLocation();

                    pos.getBlock().setType(Material.AIR);

                    TNTPrimed tnt = (TNTPrimed) shulker.getWorld().spawnEntity(pos, EntityType.TNT);

                    tnt.getPersistentDataContainer().set(Permadeath.withCustomNamespace("tnt_demon"), PersistentDataType.BYTE, (byte) 1);
                    tnt.setFuseTicks(40);
                    }
                }
            }
        }

        if (event.getHitEntity() != null) {
            if (event.getEntity() instanceof ShulkerBullet bullet) {

                if (bullet.getShooter() instanceof Shulker shulker) {
                    if (shulker.getLocation().getX() == event.getHitEntity().getLocation().getX() &&
                        shulker.getLocation().getY() == event.getHitEntity().getLocation().getY() &&
                        shulker.getLocation().getZ() == event.getHitEntity().getLocation().getZ()) return;

                    TNTPrimed tnt = (TNTPrimed) shulker.getWorld().spawnEntity(event.getHitEntity().getLocation(), EntityType.TNT);
                    tnt.getPersistentDataContainer().set(Permadeath.withCustomNamespace("tnt_demon"), PersistentDataType.BYTE, (byte) 1);
                    tnt.setFuseTicks(20);
                }
            }
        }
    }

    private void spawnFireworks(Location location, int amount) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
            fw2.setFireworkMeta(fwm);
        }
    }


    public boolean isTheEnd(Location loc) {
        return loc.getWorld().getPersistentDataContainer().has(plugin.getEnd().getKey(), PersistentDataType.STRING) && loc.getWorld().getEnvironment() == World.Environment.THE_END;
    }
}
