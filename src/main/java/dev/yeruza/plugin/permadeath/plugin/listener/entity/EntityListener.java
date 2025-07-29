package dev.yeruza.plugin.permadeath.plugin.listener.entity;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.data.PlayerManager;

import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

public class EntityListener implements Listener {
    private final Permadeath plugin;

    public EntityListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVB(VehicleDestroyEvent event) {
        if (event.getVehicle().getPersistentDataContainer().has(Permadeath.withCustomNamespace("death_module"), PersistentDataType.BYTE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            if (creeper.hasMetadata("nether_creeper"))
                if (event.blockList() != null) {
                    for (Block block : event.blockList())
                        if (block.getType() != Material.BEDROCK)
                            block.setType(Material.MAGMA_BLOCK);

                    event.setCancelled(true);
                }
        }
    }
    @EventHandler
    public void onBreakSkull(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack item = event.getItem().getItemStack();

            if (item.getType() == Material.PLAYER_HEAD) {

                SkullMeta meta = (SkullMeta) item.getItemMeta();
                PlayerManager man = new PlayerManager(meta.getOwningPlayer(), plugin);
                man.craftHead(item);
            }

            if (item.getType() == Material.STRUCTURE_VOID) {
                event.setCancelled(true);
                event.getItem().remove();
            }

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING && plugin.getDay() >= 50)
            if (event.getEntity() instanceof Player) {
                if (plugin.getDay() < 40)
                    event.setDamage(5.0D);
                else
                    event.setDamage(10.0D);

            }

        if (event.getEntity().getType() == EntityType.ITEM && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
            Item item = (Item) event.getEntity();
            if (item.getItemStack().getType() == Material.SHULKER_SHELL)
                event.setCancelled(true);
        }

        if (event.getEntity() instanceof Creeper || event.getEntity() instanceof Ghast) {
            teleport(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (plugin.getDay() >= 50) {
            if (event.getEntity() instanceof Player player && event.getDamager() instanceof PolarBear bear) {
                bear.setAI(false);

                player.getWorld().playSound(bear.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);

                final Location loc = bear.getLocation();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    loc.getWorld().createExplosion(loc, 1.5F, true, false, bear);
                    bear.remove();
                }, 10L);

                event.setCancelled(true);
            }

            if (event.getEntity() instanceof Player player && event.getDamager() instanceof LlamaSpit) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30 * 20, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0));

                player.setVelocity(player.getVelocity().multiply(3));
            }
        }

        if (plugin.getDay() >= 60)
            if (event.getDamager() instanceof Drowned)
                event.setDamage(event.getDamage() * 3);

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {

            if (plugin.getDay() >= 40) {

                event.setCancelled(false);
            } else if (plugin.getDay() <= 39) {

                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Fireball fireball) {
            if (fireball.getShooter() instanceof Ghast ghast) {
                if (ghast.getPersistentDataContainer().has(Permadeath.withCustomNamespace("floating_demon"), PersistentDataType.BYTE)) {
                    if (event.getEntity() instanceof LivingEntity liv) {
                        liv.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 5, 49));
                        liv.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 20, 4));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFireBallHit(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Ghast ghast && plugin.getDay() >= 25) {
            int yield = (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END || Permadeath.getPlugin().getDay() >= 50 ? 6 : ThreadLocalRandom.current().nextInt(3, 5 + 1));

            if (ghast.getPersistentDataContainer().has(Permadeath.withCustomNamespace("floating_demon"), PersistentDataType.BYTE))
                yield = 0;

            if (event.getEntity() instanceof Fireball fireball)
                fireball.setYield(yield);
        }
    }

    private void teleport(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        double locX = entity.getLocation().getX();
        double locY = entity.getLocation().getY();
        double locZ = entity.getLocation().getZ();

        SplittableRandom random = new SplittableRandom();


        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            if (plugin.getMobFactory().hasData(event.getEntity(), "ender_creeper") || plugin.getMobFactory().hasData(event.getEntity(), "ender_quantum_creeper")) {
                teleport(entity, locX, locY, locZ, random);
                event.setCancelled(true);
            }

            if (plugin.getMobFactory().hasData(event.getEntity(), "ender_ghast") && random.nextInt(101) <= 20) {
                teleport(entity, locX, locY, locZ, random);
                event.setCancelled(true);
            }

            if (plugin.getMobFactory().hasData(event.getEntity(), "tp_ghast") && random.nextInt(101) <= 20) {
                teleport(entity, locX, locY, locZ, random);
                event.setCancelled(true);
            }

            if (plugin.getMobFactory().hasData(event.getEntity(), "ender_skeleton") && random.nextInt(101) >= 20) {
                teleport(entity, locX, locY, locZ, random);
                event.setCancelled(true);
            }
        }
    }

    private boolean teleport(Entity entity, double x, double y, double z, SplittableRandom random) {
        for (int i = 0; i < 64; ++i) {
            if (eq(entity, x, y, z, random))
                return true;
        }
        return false;
    }

    boolean eq(Entity entity, double locX, double locY, double locZ, SplittableRandom random) {
        World world = entity.getWorld();

        double x = locX + (random.nextDouble() - 0.5D) * 64.0D;
        double y = locY + (double) (random.nextInt(64) - 32);
        double z = locZ + (random.nextDouble() - 0.5D) * 64.0D;

        Block block = world.getBlockAt((int) x, (int) y, (int) z);

        while (block.getY() > 0 && block.getType().isAir())
            block = block.getRelative(BlockFace.DOWN);

        if (block.getY() <= 0)
            return false;

        return entity.teleport(new Location(world, x, block.getY() + 1, z));
    }
}
