package dev.yeruza.plugin.permadeath.plugin.listener.entity;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.nms.main.entities.DeathModule;
import dev.yeruza.plugin.permadeath.core.PluginManager;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;
import dev.yeruza.plugin.permadeath.plugin.item.PotionProperties;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorProperties;
import dev.yeruza.plugin.permadeath.plugin.item.tool.ToolProperties;
import dev.yeruza.plugin.permadeath.plugin.task.CatTask;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class SpawnListener implements Listener {
    private final Permadeath plugin;

    private final SplittableRandom random;
    private final List<Cat> novaCats;

    private final boolean optimizeSpawns;

    public SpawnListener(Permadeath plugin) {
        this.plugin = plugin;
        random = new SplittableRandom();
        novaCats = new ArrayList<>();
        optimizeSpawns = plugin.getConfig().getBoolean("toggles.optimize-mob-spawns");
        plugin.setNovaCats(novaCats);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        Location location = event.getLocation();
        World world = location.getWorld();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if (optimizeSpawns && world.getEnvironment() == World.Environment.NORMAL)
            if (reason == CreatureSpawnEvent.SpawnReason.NATURAL || reason == CreatureSpawnEvent.SpawnReason.CUSTOM)
                if (entityType == EntityType.COD)
                    if (Stream.of(location.getChunk().getEntities())
                            .filter(e -> e.getType() == entityType)
                            .map(entityType.getEntityClass()::cast)
                            .toList().size() >= 8)
                        event.setCancelled(true);

        if (optimizeSpawns && world.getEnvironment() != World.Environment.THE_END && entity instanceof Monster && world.getEntitiesByClass(Monster.class).size() >= 220)
            event.setCancelled(true);

        if (event.isCancelled()) return;

        spawnBeginningMobs(event);
        spawnNetheriteMobs(event);

        plugin.addDeathTrainEffects(entity);

        if (entity instanceof Spider || entityType == EntityType.SKELETON) {
            if (plugin.getConfig().getBoolean("toggles.spider-effect") && entity instanceof Spider)
                addMobEffects(entity, 100);
            if (plugin.getDay() >= 20) {
                if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                spawnSkeletonClass(entity, location);
            }
        }

        if (plugin.getDay() >= 20) {
            if (entityType == EntityType.PHANTOM) {
                Phantom phantom = (Phantom) entity;
                int pSize = plugin.getDay() < 50 ? 9 : 18;

                plugin.getNmsEntity(phantom).setMaxHealth(plugin.getNmsEntity(phantom).getMaxHealth() * 2);

                if (plugin.getDay() >= 40) {
                    Skeleton skeleton = plugin.getNmsHandler().spawnEntity(Skeleton.class, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                    phantom.addPassenger(skeleton);
                }

                if (plugin.getDay() >= 50) {
                    int r = plugin.getDay() < 60 ? 1 : 25;

                    if (random.nextInt(101) <= r) {
                        for (int i = 0; i < 4; i++)
                            plugin.getNmsHandler().spawnCustomGhast(event.getLocation(), true);
                    }
                    addMobEffects(entity, 3);
                }

                phantom.setSize(pSize);
            }

            if (event.getEntityType() == EntityType.ZOMBIFIED_PIGLIN) {
                if (plugin.getDay() >= 50) {
                    event.setCancelled(true);
                    return;
                }

                PigZombie zombiePiglin = (PigZombie) entity;

                zombiePiglin.setAngry(true);

                if (plugin.getDay() >= 30 && plugin.getDay() < 40) {
                    EntityEquipment equipment = zombiePiglin.getEquipment();

                    ItemStack[] armor = {
                            new ItemStack(Material.DIAMOND_BOOTS),
                            new ItemStack(Material.DIAMOND_LEGGINGS),
                            new ItemStack(Material.DIAMOND_CHESTPLATE),
                            new ItemStack(Material.DIAMOND_HELMET)
                    };

                    equipment.setArmorContents(armor);
                }

                if (plugin.getDay() >= 40) {
                    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                    if (plugin.getDay() >= 60 && world.getEnvironment() == World.Environment.NETHER) {
                        event.setCancelled(true);
                        return;
                    }

                    int randomProb = random.nextInt(99) + 1;
                    int count = plugin.getDay() < 50 ? 5 : 20;

                    if (randomProb >= count) {
                        EntityEquipment equipment = zombiePiglin.getEquipment();
                        int clazzSkeleton = ThreadLocalRandom.current().nextInt();

                        if (clazzSkeleton == 1) {
                            plugin.getMobFactory().spawnUltraRavager(event.getLocation());
                            event.setCancelled(true);
                        }

                        if (clazzSkeleton == 2) {
                            ItemStack[] armor = ArmorProperties.pieces(
                                    new ArmorProperties(Material.NETHERITE_BOOTS, Color.YELLOW),
                                    new ArmorProperties(Material.NETHERITE_LEGGINGS, Color.YELLOW),
                                    new ArmorProperties(Material.NETHERITE_CHESTPLATE, Color.YELLOW),
                                    new ArmorProperties(Material.NETHERITE_HELMET, Color.YELLOW)
                            );

                            equipment.setArmorContents(armor);

                            plugin.getNmsEntity(zombiePiglin).setMaxHealth(zombiePiglin.getHealth());
                            Bee bee = plugin.getNmsHandler().spawnCustomEntity("SpecialBee", event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);

                            zombiePiglin.setCollidable(true);
                            bee.setCollidable(true);

                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                zombiePiglin.teleport(bee.getLocation());
                                bee.addPassenger(zombiePiglin);
                            }, 10L);
                        }

                        if (clazzSkeleton == 3) {
                            zombiePiglin.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(8.0D);
                            zombiePiglin.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));

                            Ghast ghast = plugin.getNmsHandler().spawnCustomGhast(event.getLocation(), false);
                            ghast.getPersistentDataContainer().set(Permadeath.withCustomNamespace("tp_ghast"), PersistentDataType.BYTE, (byte) 1);

                            ghast.setCollidable(true);
                            zombiePiglin.setCollidable(true);

                            ghast.addPassenger(zombiePiglin);
                        }

                        if (clazzSkeleton == 4) {
                            MagmaCube cube = plugin.getNmsHandler().spawnEntity(MagmaCube.class, event.getLocation(), CreatureSpawnEvent.SpawnReason.SLIME_SPLIT);
                            cube.setSize(1);

                            plugin.getNmsEntity(zombiePiglin).setMaxHealth( 1.0D);
                            zombiePiglin.setCollidable(true);
                            cube.addPassenger(zombiePiglin);
                        }

                        if (clazzSkeleton == 5) {
                            ItemStack[] armor = ArmorProperties.pieces(
                                    new ArmorProperties(Material.NETHERITE_BOOTS, Color.GRAY),
                                    new ArmorProperties(Material.NETHERITE_LEGGINGS, Color.GRAY),
                                    new ArmorProperties(Material.NETHERITE_CHESTPLATE, Color.GRAY),
                                    new ArmorProperties(Material.NETHERITE_HELMET, Color.GRAY)
                            );

                            equipment.setArmorContents(armor);


                            plugin.getNmsEntity(zombiePiglin).setMaxHealth(zombiePiglin.getHealth());
                            Pig pig = plugin.getNmsHandler().spawnCustomEntity("SpecialPig", entity.getLocation(), CreatureSpawnEvent.SpawnReason.SPAWNER);

                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                pig.setSaddle(true);
                                zombiePiglin.teleport(pig.getLocation());
                                pig.addPassenger(zombiePiglin);
                            }, 10L);
                        } else {
                            EntityEquipment eq = zombiePiglin.getEquipment();
                            ItemStack[] armor = ArmorProperties.pieces(
                                    Material.DIAMOND_BOOTS,
                                    Material.DIAMOND_LEGGINGS,
                                    Material.DIAMOND_CHESTPLATE,
                                    Material.DIAMOND_HELMET
                            );

                            eq.setArmorContents(armor);
                        }
                    }
                }
            }

            if (plugin.getDay() >= 25) {
                if (entity instanceof Ravager ravager) {
                    if (plugin.getDay() < 40) {
                        ravager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                        ravager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0));
                        ravager.setRemoveWhenFarAway(true);
                    }
                }

                if (entity.getEquipment() != null && (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.ZOMBIE)) {
                    ItemStack[] contents = entity.getEquipment().getArmorContents().clone();

                    int index = 0;
                    for (ItemStack armor : contents) {
                        if (armor != null && armor.getType().name().toLowerCase().contains("netherite_") && !armor.getItemMeta().isUnbreakable()) {
                            contents[index] = null;
                        }
                        index++;
                    }
                    entity.getEquipment().setArmorContents(contents);
                }
            }

            if (plugin.getDay() >= 30) {
                if (entity instanceof Silverfish || entity instanceof Endermite)
                    addMobEffects(entity, 100);
                if (entity instanceof Enderman) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, plugin.getDay() < 60 ? 1 : 9));

                    if (plugin.getDay() >= 40) {
                        if (world.getEnvironment() == World.Environment.NETHER) {
                            event.setCancelled(true);
                            Creeper c = plugin.getMobFactory().spawnEnderCreeper(event.getLocation());
                            c.setMetadata("nether_creeper", new FixedMetadataValue(plugin, true));
                        }

                        if (random.nextInt(100) <= 4 && world.getEnvironment() == World.Environment.NORMAL) {
                            PluginManager.spawnNmsEntity(event.getLocation(), DeathModule.class);
                            event.setCancelled(true);
                        }
                    }
                }

                if (entity instanceof Squid) {
                    event.setCancelled(true);

                    if (location.getWorld().getNearbyEntities(location, 20, 20, 20).stream().filter(e -> e instanceof Guardian).map(Guardian.class::cast).toList().size() < 20) {
                        Guardian g = (Guardian) entity.getWorld().spawnEntity(event.getLocation(), EntityType.GUARDIAN);
                        g.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    }
                }

                if (entity instanceof IronGolem) {
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
                    if (plugin.getDay() >= 40) {

                        entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, plugin.getDay() < 60 ? 0 : 3));

                        if (plugin.getDay() >= 50) {
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, plugin.getDay() < 60 ? 1 : 3));
                        }
                    }
                }

                if (entity instanceof Bat) {
                    event.setCancelled(true);
                    if (location.getWorld().getLivingEntities().stream()
                            .filter(e -> e instanceof Blaze)
                            .map(Blaze.class::cast)
                            .toList().size() < 30) {
                        Blaze g = (Blaze) entity.getWorld().spawnEntity(event.getLocation(), EntityType.BLAZE);
                        g.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
                    }
                }

                if (entity instanceof Creeper creeper) {
                    creeper.setPowered(true);

                    if (plugin.getDay() >= 40) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));

                        if (plugin.getDay() >= 50) {

                            if (plugin.getDay() < 60) {
                                int r = random.nextInt(10);
                                if (r <= 1) {
                                    plugin.getMobFactory().spawnEnderCreeper(location);

                                } else {
                                    plugin.getMobFactory().spawnQuantumCreeper(location);
                                }
                            } else {
                                plugin.getMobFactory().spawnEnderQuantumCreeper(location);
                                creeper.setMaxFuseTicks(creeper.getMaxFuseTicks() / 2);
                            }
                        }
                    }
                }

                if (entity instanceof Pillager pillager) {
                    pillager.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

                    pillager.getEquipment().setItemInMainHand(new ItemProperties(Material.CROSSBOW).addEnchant(Enchantment.QUICK_CHARGE, 4).build());
                    pillager.getEquipment().setItemInMainHandDropChance(0);

                    if (plugin.getDay() >= 50) {
                        int prob = random.nextInt(100);
                        if (prob == 0) {
                            event.setCancelled(true);
                            event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.EVOKER);
                        }
                    }
                }
            }

            if (plugin.getDay() >= 40) {
                if (entity.getType() == EntityType.GUARDIAN) {
                    if (plugin.getDay() >= 60) {
                        event.setCancelled(true);

                        if (location.getWorld().getNearbyEntities(location, 20, 20, 20).stream()
                                .filter(e -> e instanceof ElderGuardian)
                                .map(ElderGuardian.class::cast)
                                .toList().size() < 5)
                            event.getLocation().getWorld().spawn(event.getLocation(), ElderGuardian.class);
                        return;
                    }

                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
                }

                if (event.getEntityType() == EntityType.SPIDER) {
                    event.setCancelled(true);

                    plugin.getNmsHandler().spawnEntity(CaveSpider.class, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                    entity.customName(TextFormat.write("&6Araña Inmortal"));
                }

                if (event.getEntityType() == EntityType.ZOMBIE) {
                    event.setCancelled(true);

                    if (world.getNearbyEntities(location, 15, 15, 15).stream()
                            .filter(e -> e instanceof Vindicator)
                            .map(Vindicator.class::cast)
                            .toList().size() < 5) {
                        Vindicator vindicator = (Vindicator) event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.VINDICATOR);
                        vindicator.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0));
                        plugin.getNmsEntity(vindicator).setMaxHealth(plugin.getNmsEntity(vindicator).getMaxHealth() * 2);
                    }
                }

                if (entityType == EntityType.WOLF) {
                    event.setCancelled(true);
                    plugin.getNmsHandler().spawnEntity(Cat.class, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                }

                if (entityType == EntityType.CAT && entity instanceof Cat cat) {
                    if (plugin.getDay() < 50) {
                        entity.customName(TextFormat.write("&6Gato Supernova"));
                        plugin.explodeCat(cat);
                    } else {
                        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM){
                            plugin.explodeCat(cat);
                        } else {
                            event.getEntity().customName(TextFormat.write("&6Gato Galáctico"));
                        }
                    }
                }

                if (plugin.getDay() >= 50) {
                    event.setCancelled(true);

                    Ravager ultraRavager = plugin.getNmsHandler().spawnCustomEntity("UltraRavager", event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                    ultraRavager.customName(TextFormat.write("&6Ultra Ravager"));
                    ultraRavager.setCustomNameVisible(true);
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                    plugin.getNmsEntity(ultraRavager).setMaxHealth(500.0D);
                }
            }

            if (entity instanceof Chicken) {
                if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                    event.setCancelled(true);
                    plugin.getNmsHandler().spawnEntity(Ravager.class, event.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                    return;
                }

                if (plugin.getDay() >= 50) {
                    event.setCancelled(true);
                    event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.SILVERFISH);
                }
            }

            if (entity instanceof Witch) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                plugin.getNmsEntity(entity).setMaxHealth(entity.getHealth() * 2);

                entity.customName(TextFormat.write("&6Bruja Imposible"));
            }
        }

        if (plugin.getDay() >= 50) {
            if (entity instanceof Vindicator vindicator) {
                vindicator.getEquipment().setItemInMainHand(new ItemProperties(Material.DIAMOND_AXE).addEnchant(Enchantment.SHARPNESS, 5).build());
            }

            if (event.getEntityType() == EntityType.VEX) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2));
            }

            if (event.getEntityType() == EntityType.BLAZE) {
                plugin.getNmsEntity(entity).setMaxHealth(200.0D);
            }

            if (entity.getType() == EntityType.COD) {
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
                Cod cod = (Cod) entity;
                cod.getEquipment().setItemInMainHand(new ItemProperties(Material.WOODEN_SWORD).addEnchant(Enchantment.SHARPNESS, 50).addEnchant(Enchantment.KNOCKBACK, 100).build());
                cod.getEquipment().setItemInMainHandDropChance(0.0f);
                cod.customName(TextFormat.write("&6Bacalao de la Muerte"));
            }

            if (entity.getType() == EntityType.DROWNED) {
                entity.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
            }

            if (entity.getType() == EntityType.SALMON) {
                event.setCancelled(true);
                if (world.getNearbyEntities(location, 15, 15, 15).stream()
                        .filter(e -> e instanceof PufferFish)
                        .map(PufferFish.class::cast)
                        .toList().size() < 2) {
                    event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.PUFFERFISH);
                }
            }

            if (entity.getType() == EntityType.PUFFERFISH) {
                PufferFish fish = (PufferFish) entity;
                fish.customName(TextFormat.write("&6Pufferfish invulnerable"));
                fish.setInvulnerable(true);
            }

            if (entity.getType() == EntityType.WITHER_SKELETON) {
                WitherSkeleton skeleton = (WitherSkeleton) entity;
                EntityEquipment eq = skeleton.getEquipment();

                int prob = random.nextInt(plugin.getDay() < 60 ? 50 : 13) + 1;

                if (skeleton.getWorld().getEnvironment() == World.Environment.NETHER && prob == 5) {

                    plugin.getNmsEntity(skeleton).setMaxHealth(80.0D);

                    skeleton.customName(TextFormat.write("&6Wither Skeleton Emperador"));
                    skeleton.setCollidable(false);

                    ItemStack i = new ItemStack(Material.BLACK_BANNER, 1);
                    BannerMeta banner = (BannerMeta) i.getItemMeta();
                    List<Pattern> patterns = new ArrayList<>();

                    patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRAIGHT_CROSS));
                    patterns.add(new Pattern(DyeColor.BLACK, PatternType.BRICKS));
                    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
                    patterns.add(new Pattern(DyeColor.YELLOW, PatternType.FLOWER));
                    patterns.add(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
                    patterns.add(new Pattern(DyeColor.RED, PatternType.GRADIENT_UP));
                    banner.setPatterns(patterns);
                    i.setItemMeta(banner);

                    eq.setHelmet(i);
                    eq.setHelmetDropChance(0);
                    eq.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
                    eq.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
                    eq.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
                    eq.setItemInMainHand(new ItemProperties(Material.BOW).addEnchant(Enchantment.PUNCH, 5).addEnchant(Enchantment.POWER, 100).build());
                    eq.setItemInMainHandDropChance(0);

                    event.setCancelled(false);
                }
            }

            if (entity.getType() == EntityType.ZOMBIE) {
                Biome biome =  event.getLocation().getWorld().getBiome((int) event.getLocation().getX(), (int) event.getLocation().getY(), (int) event.getLocation().getZ());

                if (biome != null) {
                    int prob;

                    if (plugin.getDay() < 60)
                        prob = random.nextInt(500) + 1;
                    else
                        prob = random.nextInt(125) + 1;


                    if (event.getLocation().getBlock().getBiome() == Biome.PLAINS) {
                        if (prob == 5) {
                           plugin.getNmsHandler().spawnCustomEntity("CustomGiant", event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                           event.setCancelled(true);
                        }
                    }
                }
            }

            if (entity instanceof Ravager) {
                if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

                if (plugin.getDay() >= 50) {
                    Ravager ultraRavager = plugin.getNmsHandler().spawnCustomEntity("UltraRavager", event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                    ultraRavager.customName(TextFormat.write("&6Ultra Ravager"));
                    ultraRavager.setCustomNameVisible(true);
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));

                    plugin.getNmsEntity(ultraRavager).setMaxHealth(500.0F);

                    event.setCancelled(true);
                }
            }


        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (plugin.getConfig().getBoolean("toggles.replace-mobs-on-chunk-load")) {
            List<LivingEntity> entities =  Stream.of(event.getChunk().getEntities())
                    .filter(e -> e instanceof LivingEntity)
                    .map(LivingEntity.class::cast)
                    .toList();

            for (LivingEntity liv : entities) {
                applyDayChanges(liv);
            }

            if (plugin.getDay() >= 40 && plugin.getDay() < 50) {
                List<Cat> fuckingCats =  Stream.of(event.getChunk().getEntities())
                        .filter(e -> e instanceof Cat)
                        .map(Cat.class::cast)
                        .toList();

                for (Cat trash : fuckingCats) {
                    trash.customName(TextFormat.write("&6Gato SuperNova"));
                    plugin.explodeCat(trash);
                }
            }

        }
    }

    public void applyDayChanges(LivingEntity entity) {
        if (plugin.getDay() >= 30) {
            if (entity instanceof Squid) {
                entity.remove();
                Guardian guardian = (Guardian) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.GUARDIAN);
                guardian.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            }

            if (entity instanceof Bat) {
                entity.remove();
                Blaze blaze = (Blaze) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.BLAZE);
                blaze.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
            }
        }

        if (plugin.getDay() >= 40) {
            if (entity instanceof Cow || entity instanceof Sheep || entity instanceof Pig || entity instanceof MushroomCow || entity instanceof Goat) {
                if (!entity.getLocation().getWorld().getPersistentDataContainer().has(plugin.getOverWorld().getKey())) return;

                if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                    entity.remove();
                    plugin.getNmsHandler().spawnEntity(Ravager.class, entity.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                }

                if (plugin.getDay() >= 50) {
                    entity.remove();
                    Ravager ultraRavager = plugin.getNmsHandler().spawnCustomEntity("UltraRavager", entity.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);
                    ultraRavager.customName(TextFormat.write("&6Ultra Ravager"));
                    ultraRavager.setCustomNameVisible(true);
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    ultraRavager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                    plugin.getNmsEntity(ultraRavager).setMaxHealth( 500.0D);
                }
            }
        }

        if (entity instanceof Chicken) {

            if (plugin.getDay() < 50 && plugin.getDay() >= 40) {
                entity.remove();
                plugin.getNmsHandler().spawnEntity(Ravager.class, entity.getLocation(), CreatureSpawnEvent.SpawnReason.NATURAL);
                return;
            }

            if (plugin.getDay() >= 50) {
                entity.getLocation().getWorld().spawnEntity(entity.getLocation(), EntityType.SILVERFISH);
                entity.remove();
            }
        }

        if (plugin.getDay() >= 60) {
            if (entity.getType() == EntityType.VILLAGER) {

                if (random.nextBoolean())
                    entity.getLocation().getWorld().spawn(entity.getLocation(), Vex.class);
                else
                    entity.getLocation().getWorld().spawn(entity.getLocation(), Vindicator.class);
                entity.remove();

            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (plugin.getDay() >= 20) {
            LivingEntity mob = event.getEntity();

            if (plugin.getDay() < 40) {
                if (mob instanceof IronGolem || mob instanceof PigZombie || mob instanceof Ghast || mob instanceof Guardian || mob instanceof Enderman || mob instanceof Witch || mob instanceof WitherSkeleton || mob instanceof Evoker || mob instanceof Phantom || mob instanceof Slime || mob instanceof Drowned || mob instanceof Blaze)
                    event.getDrops().clear();


                if (event.getEntity().getKiller() == null) return;
                Player killer = event.getEntity().getKiller();
                if (mob instanceof Ravager) {

                    int prob = random.nextInt(100) + 1;
                    int needed = 1;

                    if (plugin.getDay() >= 25)
                        needed = 20;


                    int randomSentence = ThreadLocalRandom.current().nextInt(1, 5);

                    if (prob <= needed) {
                        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
                        mob.getWorld().dropItem(mob.getLocation(), totem);

                        killer.sendMessage(ChatColor.YELLOW + "¡Un tótem!");
                        killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, -5);

                    } else if (randomSentence == 1) {
                        killer.sendMessage(ChatColor.RED + "Vaya que mala suerte, ese ravager no tenia nada :(");
                    } else if (randomSentence == 2) {
                        killer.sendMessage(ChatColor.RED + "¡Porras... otro ravager sin suerte!");
                    } else if (randomSentence == 3) {
                        killer.sendMessage(ChatColor.RED + "Nada... hoy no hay totem :(");
                    } else if (randomSentence == 4) {
                        killer.sendMessage(ChatColor.RED + "¡Hoy no es tu día!");
                    }
                }

            } else {

                if (mob instanceof IronGolem || mob instanceof Ghast || mob instanceof Guardian || mob instanceof Enderman || mob instanceof Witch || mob instanceof WitherSkeleton || mob instanceof Evoker || mob instanceof Phantom || mob instanceof Slime || mob instanceof Drowned || mob instanceof Blaze) {
                    event.getDrops().clear();
                }

                if (event.getEntityType() == EntityType.CAT || event.getEntityType() == EntityType.OCELOT) {

                    if (novaCats.contains(event.getEntity())) {
                        novaCats.remove(event.getEntity());
                    }
                    if (event.getEntity().getCustomName() == null)
                        return;
                    if (event.getEntity().getPersistentDataContainer().has(Permadeath.withCustomNamespace("cat_supernova"))) {

                        Location l = event.getEntity().getLocation();
                        int x = (int) l.getX();
                        int y = (int) l.getY();
                        int z = (int) l.getZ();

                        Bukkit.broadcast(TextFormat.write("&cLa maldición de un Gato Galáctico ha comenzado en: " + x + ", " + y + ", " + z));
                        CatTask task = new CatTask(event.getEntity().getLocation(), plugin);
                        task.runTaskTimer(plugin, 0, 20L);
                    }
                }

                if (mob.getType() == EntityType.ZOMBIFIED_PIGLIN) {
                    if (mob.getCustomName() == null) {
                        event.getDrops().clear();
                        return;
                    }

                    if (mob.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Carlos el Esclavo")) {
                        int r = random.nextInt(100) + 1;
                        int chance = plugin.getDay() < 60 ? 100 : 33;

                        if (r <= chance)
                            event.getDrops().add(new ItemStack(Material.GOLD_INGOT, 32));
                    }
                }

                if (mob instanceof Villager) {
                    if (mob.getCustomName() == null) return;
                    if (mob.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Jess la Emperatriz")) {
                        int r = random.nextInt(100) + 1;
                        int prob = 100;
                        if (plugin.getDay() >= 60) prob = 33;

                        if (r <= prob)
                            event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 2));

                    }
                }

                if (event.getEntity() instanceof Ravager ravager) {

                    if (ravager.getCustomName() == null) return;
                    if (ravager.getPersistentDataContainer().has(Permadeath.withCustomNamespace("ultra_ravager"))) {
                        if (ravager.getWorld().getEnvironment() != World.Environment.NETHER)
                            event.getDrops().clear();
                        else {
                            if ((random.nextInt(100) + 1) <= (plugin.getDay() < 60 ? 100 : 33))
                                event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));

                        }
                    }
                }
            }

            if (plugin.getDay() < 60 && plugin.getDay() >= 50) {
                if (event.getEntity().getType() == EntityType.GIANT)
                    event.getDrops().add(new ItemProperties(Material.BOW).setName("&b&lArco de Gigante").addEnchant(Enchantment.POWER, 10).build());


                if (event.getEntity().getType() == EntityType.WITHER_SKELETON) {
                    if (event.getEntity().getCustomName() == null) return;
                    if (event.getEntity().getPersistentDataContainer().has(Permadeath.withCustomNamespace("wither_skeleton_emperor"))) {

                        if (plugin.getDay() < 60) {
                            int prob = (int) (Math.random() * 100) + 1;
                            if (prob <= 50)
                                event.getDrops().add(null);

                        }
                    }
                }
            }
            runNetheriteCheck(event);
        }
    }

    private void spawnBeginningMobs(CreatureSpawnEvent event) {
        World theBeginning = event.getLocation().getWorld();
        Location location = event.getLocation();

        if (plugin.getDay() < 0) return;
        if (plugin.getBeginning() == null) return;
        if (plugin.getBeginning().getWorld() == null) return;
        if (!theBeginning.getPersistentDataContainer().has(plugin.getBeginning().getKey())) return;

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
            if (theBeginning.getLivingEntities().size() > 70) return;
        }

        int p = random.nextInt(101);

        if (p <= 60) {
            WitherSkeleton skeleton = plugin.getNmsHandler().spawnEntity(WitherSkeleton.class, event.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);

            skeleton.getEquipment().setChestplate(new ArmorProperties(Material.NETHERITE_CHESTPLATE, Color.fromRGB(255, 182,  193)).build());
            skeleton.getEquipment().setBoots(new ArmorProperties(Material.NETHERITE_BOOTS, Color.fromRGB(255, 182, 193)).build());

            int enchantLvl = (int) (Math.random() * 5) + 1;
            skeleton.getEquipment().setItemInMainHand(new ItemProperties(Material.NETHERITE_SWORD).addEnchant(Enchantment.SHARPNESS, enchantLvl).build());
            skeleton.getEquipment().setChestplateDropChance(0);
            skeleton.getEquipment().setBootsDropChance(0);

            skeleton.customName(TextFormat.write("&6Wither Skeleton Rosacéo"));
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
            plugin.getNmsEntity(skeleton).setMaxHealth(100.0D);
        }

        if (p > 60 && p <= 75) {
            Vex vex = theBeginning.spawn(location, Vex.class);
            vex.getEquipment().setHelmet(new ItemProperties(Material.HONEY_BLOCK).addEnchant(Enchantment.PROTECTION, 4).build());
            vex.getEquipment().setItemInMainHand(new ItemProperties(Material.END_CRYSTAL).addEnchant(Enchantment.SHARPNESS, 15).build());
            vex.getEquipment().setHelmetDropChance(0);
            vex.getEquipment().setItemInMainHandDropChance(0);

            vex.customName(TextFormat.write("&6Vex Definitivo"));
        }

        if (p > 75 && p <= 79) {
            Ghast ghast = plugin.getNmsHandler().spawnCustomGhast(event.getLocation().add(0, 5, 0), true);
            plugin.getNmsEntity(ghast).setMaxHealth(150.0D);
            ghast.customName(TextFormat.write("&6Ender Ghast Definitivo"));
        }

        if (p >= 80) {
            Creeper creeper = plugin.getMobFactory().spawnEnderQuantumCreeper(event.getLocation());
            plugin.getNmsEntity(creeper).setMaxHealth(100.0D);
            creeper.setExplosionRadius(7);
        }
    }

    private void spawnSkeletonClass(LivingEntity liv, Location loc) {
        int bound = (plugin.getDay() < 60 ? 5 : 7) + 1;
        int randomClass = random.nextInt(bound);

        LivingEntity spider = null;
        AbstractSkeleton skeleton = null;
        World world = loc.getWorld();

        if (liv instanceof CaveSpider) return;

        if (liv instanceof Spider) {
            if (randomClass == 5 || randomClass == 2)
                skeleton = world.spawn(loc, WitherSkeleton.class);
            else
                skeleton = world.spawn(loc, Skeleton.class);
            spider = liv;
        } else if (liv instanceof Skeleton) {
            skeleton = (Skeleton) liv;

            if (randomClass == 5 || randomClass == 2) {
                skeleton.remove();
                skeleton = world.spawn(loc, WitherSkeleton.class);
            }
        }

        EntityEquipment equipment = skeleton.getEquipment();

        ItemStack[] pieces = {};

        ItemStack mainHand = null;
        ItemStack offHand = null;
        float mainDrop = 0.0f;
        float offDrop = 0.0f;
        float armorDropChance = 0.8F;

        Enchantment armorEnchant = null;
        int armorEnchantLvl = 0;

        double health = 20.0D;
        String name = "";
        String id = "";

        if (plugin.getDay() >= 30) {
            offHand = new PotionProperties(Material.TIPPED_ARROW)
                .setBasePotionType(PotionType.STRENGTH)
                .build();
        }
        if (plugin.getDay() >= 60 && random.nextInt(101) == 1) {
            if (skeleton != null)
                skeleton.remove();

            plugin.getMobFactory().spawnUltraSkeleton(loc);

            skeleton = world.spawn(loc, WitherSkeleton.class);

            skeleton.getEquipment().setItemInMainHand(
                new ItemProperties(Material.BOW)
                    .addEnchant(Enchantment.POWER, 32765)
                    .build()
            );
            skeleton.getEquipment().setItemInMainHandDropChance(0.0f);
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

            skeleton.setRemoveWhenFarAway(false);

            id = "ultra_skeleton_definitve";
            name = "&6Ultra Esqueleto Definitivo";
            plugin.getNmsEntity(skeleton).setMaxHealth(400.0D);

        } else {
            switch (randomClass) {
                case 1 -> {
                    pieces = ArmorProperties.pieces(
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_HELMET
                    );
                    mainHand = new ItemStack(Material.BOW);

                    health = (plugin.getDay() < 30 ? 20.0D : plugin.getDay() < 50 ? 40.0D : 100.0D);

                    if (plugin.getDay() >= 30) {
                        armorEnchant = Enchantment.PROTECTION;
                        armorEnchantLvl = 4;

                        if (plugin.getDay() >= 60)
                            armorEnchantLvl = 5;

                        if (plugin.getDay() >= 80)
                            armorEnchantLvl = 6;

                        if (plugin.getDay() >= 60)
                            armorDropChance = 0.0f;
                    }
                }
                case 2 -> {
                    pieces = ArmorProperties.pieces(
                        Material.CHAINMAIL_BOOTS,
                        Material.CHAINMAIL_LEGGINGS,
                        Material.CHAINMAIL_CHESTPLATE,
                        Material.CHAINMAIL_HELMET
                    );

                    int punchLvl = (plugin.getDay() < 30 ? 20 : plugin.getDay() < 50 ? 30 : 50);
                    int powerLvl = (plugin.getDay() < 50 ? 25 : plugin.getDay() < 60 ? 40 : 110);

                    health = (plugin.getDay() < 50 ? 25 : plugin.getDay() < 60 ? 40 : 110);
                    mainHand = new ItemProperties(Material.BOW).addEnchant(Enchantment.PUNCH, punchLvl).build();

                    if (plugin.getDay() >= 30)
                        mainHand = new ItemProperties(mainHand).addEnchant(Enchantment.POWER, powerLvl).build();
                }
                case 3 -> {
                    pieces = ArmorProperties.pieces(
                        Material.IRON_BOOTS,
                        Material.IRON_LEGGINGS,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_HELMET
                    );

                    int fireAspectLvl = (plugin.getDay() < 30 ? 2 : plugin.getDay() < 50 ? 10 : 20);
                    int sharpnessLvl = plugin.getDay() < 60 ? 25 : 100;
                    Material material = (plugin.getDay() < 30 ? Material.IRON_AXE : Material.DIAMOND_AXE);

                    mainHand = new ItemProperties(material).addEnchant(Enchantment.FIRE_ASPECT, fireAspectLvl).build();
                    health = (plugin.getDay() < 30 ? 20.0D : plugin.getDay() < 60 ? 40.0D : 100.0D);

                    if (plugin.getDay() >= 50)
                        mainHand = new ItemProperties(mainHand).addEnchant(Enchantment.SHARPNESS, sharpnessLvl).build();
                }
                case 4 -> {
                    pieces = ArmorProperties.pieces(
                        Material.GOLDEN_BOOTS,
                        Material.GOLDEN_LEGGINGS,
                        Material.GOLDEN_CHESTPLATE,
                        Material.GOLDEN_HELMET
                    );

                    int sharpnessLvl1 = (plugin.getDay() < 30 ? 20 : plugin.getDay() < 50 ? 25 : plugin.getDay() < 60 ? 50 : 100);

                    mainHand = ToolProperties.toolWithData(Material.CROSSBOW, data -> data.addEnchant(Enchantment.SHARPNESS, sharpnessLvl1));
                    health = plugin.getDay() < 60 ? 40.0D : 60.0D;

                    if (plugin.getDay() >= 30)
                        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (plugin.getDay() < 60 ? 1 : 3)));
                }
                case 5 -> {
                    pieces = ArmorProperties.pieces(
                        new ArmorProperties(Material.NETHERITE_BOOTS, Color.fromRGB(0xFF0000)),
                        new ArmorProperties(Material.NETHERITE_LEGGINGS, Color.fromRGB(0xFF0000)),
                        new ArmorProperties(Material.NETHERITE_CHESTPLATE, Color.fromRGB(0xFF0000)),
                        new ArmorProperties(Material.NETHERITE_HELMET, Color.fromRGB(0xFF0000))
                    );
                    armorDropChance = 0.0f;

                    int bowPowerLvl = (plugin.getDay() < 30 ? 10 : plugin.getDay() < 50 ? 50 : plugin.getDay() < 60 ? 60 : 150);

                    mainHand = ToolProperties.toolWithData(Material.BOW, data -> data.addEnchant(Enchantment.POWER, bowPowerLvl));
                    // = new ItemProperties(Material.BOW).addEnchant(Enchantment.POWER, bowPowerLvl).build();
                    health = plugin.getDay() < 60 ? 40.0D : 60.0D;
                }
                case 6 -> {
                    pieces = ArmorProperties.pieces(
                        new ArmorProperties(Material.NETHERITE_BOOTS, Color.BLUE),
                        new ArmorProperties(Material.NETHERITE_LEGGINGS, Color.BLUE),
                        new ArmorProperties(Material.NETHERITE_CHESTPLATE, Color.BLUE),
                        new ArmorProperties(Material.NETHERITE_HELMET, Color.BLUE)
                    );
                    mainHand = ToolProperties.simple(Material.BOW);

                    armorDropChance = 0.0f;
                    name = "&6Ultra Esqueleto Demoníaco";
                    id = "demon_skeleton";

                    health = 100.0D;
                }
                case 7 -> {
                    pieces = ArmorProperties.pieces(
                        new ArmorProperties(Material.NETHERITE_BOOTS, Color.GREEN),
                        new ArmorProperties(Material.NETHERITE_LEGGINGS, Color.GREEN),
                        new ArmorProperties(Material.NETHERITE_CHESTPLATE, Color.GREEN),
                        new ArmorProperties(Material.NETHERITE_HELMET, Color.GREEN)
                    );

                    mainHand = ToolProperties.simple(Material.BOW);
                    offHand = new PotionProperties(Material.TIPPED_ARROW)
                        .setPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3600, 2))
                        .setPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 0))
                        .setPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3600, 0))
                        .setPotionEffect(new PotionEffect(PotionEffectType.POISON, 3600, 2))
                        .build();
                    armorDropChance = 0.0F;

                    health = 100.0D;
                    name = "&6Ultra Esqueleto Científico";
                }
                default -> {
                    pieces = ArmorProperties.pieces(
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_HELMET
                    );

                    if (plugin.getDay() < 30) {
                        health = 20.0D;
                        if (plugin.getDay() < 50)
                            health = 40.0D;
                    }


                    if (plugin.getDay() >= 30) {
                        armorEnchant = Enchantment.PROTECTION;

                        if (plugin.getDay() < 60) {
                            armorEnchantLvl = 4;
                        } else {
                            armorEnchantLvl = 5;
                        }
                    }
                }
            }
        }

        if (armorEnchant != null) {
            pieces[0] = new ItemProperties(pieces[0]).addEnchant(armorEnchant, armorEnchantLvl).build();
            pieces[1] = new ItemProperties(pieces[1]).addEnchant(armorEnchant, armorEnchantLvl).build();
            pieces[2] = new ItemProperties(pieces[2]).addEnchant(armorEnchant, armorEnchantLvl).build();
            pieces[3] = new ItemProperties(pieces[3]).addEnchant(armorEnchant, armorEnchantLvl).build();
        }

        equipment.setArmorContents(pieces);


        if (mainHand != null)
            equipment.setItemInMainHand(mainHand);

        if (offHand != null)
            equipment.setItemInOffHand(offHand);

        if (!name.isEmpty()) {
            skeleton.customName(TextFormat.write(name));
        }
        if (!id.isEmpty()) {
            skeleton.getPersistentDataContainer().set(Permadeath.withCustomNamespace(id), PersistentDataType.BYTE, (byte) 1);
        }
        plugin.getNmsEntity(skeleton).setMaxHealth(health);
        skeleton.setHealth(health);

        equipment.setItemInMainHandDropChance(mainDrop);
        equipment.setItemInOffHandDropChance(offDrop);

        equipment.setHelmetDropChance(armorDropChance);
        equipment.setChestplateDropChance(armorDropChance);
        equipment.setLeggingsDropChance(armorDropChance);
        equipment.setBootsDropChance(armorDropChance);

        if (spider != null)
            spider.addPassenger(skeleton);
    }

    private void spawnNetheriteMobs(CreatureSpawnEvent event) {
        if (plugin.getDay() < 25) return;

        if (event.getEntityType() == EntityType.SLIME) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;

            Slime slime = (Slime) event.getEntity();
            double health = (plugin.getDay() < 50 ? slime.getHealth() * 2 : slime.getHealth() * 4);

            slime.setSize((plugin.getDay() < 50) ? 15 : 16);
            plugin.getNmsEntity(slime).setMaxHealth(health);
            slime.setCustomName(ChatColor.GOLD + "GIGA Slime");
        }

        if (event.getEntityType() == EntityType.MAGMA_CUBE) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;

            int size = (plugin.getDay() < 50 ? 16 : 17);

            if (event.getLocation().getWorld().getEntitiesByClass(MagmaCube.class).stream().filter(entity -> entity.getSize() == size).toList().size() >= 10) {
                event.setCancelled(true);
                return;
            }

            MagmaCube magmaCube = (MagmaCube) event.getEntity();

            magmaCube.setSize(size);
            magmaCube.setCustomName(ChatColor.GOLD + "GIGA MagmaCube");

            if (plugin.getDay() >= 50) {
                plugin.getNmsEntity(magmaCube).setMaxHealth(plugin.getNmsEntity(magmaCube).getMaxHealth() * 2);
            }
        }

        if (event.getEntityType() == EntityType.GHAST) {
            if (event.getLocation().getWorld().getEnvironment() != World.Environment.THE_END) {
                if (plugin.getDay() < 40) {
                    double health = ThreadLocalRandom.current().nextDouble(44, 61);

                    Ghast ghastDemon = (Ghast) event.getEntity();
                    plugin.getNmsEntity(ghastDemon).setMaxHealth(health);
                    ghastDemon.setHealth(health);
                } else {
                    int r = random.nextInt();

                    double health = ThreadLocalRandom.current().nextDouble(44, 61);

                    LivingEntity ghastDemon = event.getEntity();
                    plugin.getNmsEntity(ghastDemon).setMaxHealth(health);
                    ghastDemon.setHealth(health);

                    if (r <= 75) {
                        ghastDemon.setCustomName(ChatColor.GOLD + "Demonio flotante");
                        ghastDemon.getPersistentDataContainer().set(Permadeath.withDefaultNamespace("floating_demon"), PersistentDataType.BYTE, (byte) 1);
                    } else {
                        ghastDemon.setCustomName(ChatColor.GOLD + "Ghast Demoníaco");
                        ghastDemon.getPersistentDataContainer().set(Permadeath.withCustomNamespace("demonic_ghast"), PersistentDataType.BYTE, (byte) 1);
                    }
                }
            }
        }
    }


    private boolean isACat(Entity entity) {
        return entity.getType() == EntityType.CAT || entity.getType() == EntityType.OCELOT;
    }

    private void addMobEffects(LivingEntity entity, int force) {
        if (plugin.getDay() < 10) return;

        List<PotionEffect> effects = new LinkedList<>();
        effects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        effects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 3));
        effects.add(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 3));
        effects.add(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        effects.add(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 4));
        effects.add(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0));
        effects.add(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
        effects.add(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2));

        if (plugin.getDay() < 50)
            effects.add(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));


        int times = force == 100 ? plugin.getDay() < 25 ? random.nextInt(plugin.getDay() < 20 ? 3 : 4) + 1 : 5 : force;
        for (int i = 0; i < times; ++i) {
            PotionEffect potion = effects.get(random.nextInt(effects.size()));

            if (entity.hasPotionEffect(potion.getType())) {
                i--;
            }

            entity.addPotionEffect(potion);
        }
    }

    private void runNetheriteCheck(EntityDeathEvent event) {
        if (plugin.getDay() < 25 || plugin.getDay() >= 30 || event.getEntity().getKiller() == null)
            return;

        LivingEntity mob = event.getEntity();

        int randomProb = ThreadLocalRandom.current().nextInt(1, 101);


    }

    private static class EntityEntry {
        String name;
    }
}
