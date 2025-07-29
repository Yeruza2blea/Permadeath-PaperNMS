package dev.yeruza.plugin.permadeath.plugin.task;


import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class CatTask extends BukkitRunnable {
    private final Permadeath plugin;
    private Location mobLoc;

    private int time = 5;

    public CatTask(Location catLoc, Permadeath plugin) {
        this.mobLoc = catLoc;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (time > 0) {
            Bukkit.broadcast(TextFormat.write("&eUn gato galáctico invocará un mob al azar en: &b" + time));

            for (Player all : Bukkit.getOnlinePlayers()) {
                all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100.0F, 100.0F);
            }

            time -= 1;
        }

        if (time == 0) {
            List<EntityType> entities = new ArrayList<>();
            String entityName = "";

            entities.add(EntityType.CAT);
            entities.add(EntityType.PUFFERFISH);
            entities.add(EntityType.RAVAGER);
            entities.add(EntityType.ENDER_DRAGON);
            entities.add(EntityType.SKELETON);
            entities.add(EntityType.SLIME);
            entities.add(EntityType.MAGMA_CUBE);
            entities.add(EntityType.WITCH);
            entities.add(EntityType.SPIDER);
            entities.add(EntityType.SILVERFISH);
            entities.add(EntityType.ENDERMITE);
            entities.add(EntityType.PHANTOM);
            entities.add(EntityType.GHAST);
            entities.add(EntityType.CREEPER);
            entities.add(EntityType.SHULKER);
            entities.add(EntityType.GIANT);
            entities.add(EntityType.WITHER_SKELETON);

            int randomTicks = new Random().nextInt(entities.size());
            EntityType type = entities.get(randomTicks);

            SplittableRandom random = new SplittableRandom();


            switch (type) {
                case CAT -> {
                    Cat cat = (Cat) mobLoc.getWorld().spawnEntity(mobLoc, EntityType.CAT);
                    cat.setAdult();
                    cat.customName(TextFormat.write("&6Gato Supernova"));
                    Permadeath.getPlugin().explodeCat(cat);

                }
                case RAVAGER -> {
                    plugin.getMobFactory().spawnUltraRavager(mobLoc);
                }
                case CREEPER -> {
                    int i;
                    if (plugin.getDay() < 60)
                        i = random.nextInt(3) + 1;
                    else
                        i = random.nextInt(2) + 1;

                    if (i == 1)
                        plugin.getMobFactory().spawnEnderQuantumCreeper(mobLoc);

                    if (i == 2)
                        plugin.getMobFactory().spawnQuantumCreeper(mobLoc);

                    if (i == 3)
                        plugin.getMobFactory().spawnEnderCreeper(mobLoc);
                }
                case GIANT -> {
                    plugin.getNmsHandler().spawnCustomEntity("CustomGiant", mobLoc, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }
                case WITHER_SKELETON -> {
                    plugin.getMobFactory().spawnWitherSkeletonEmperor(mobLoc);
                    WitherSkeleton skeleton = plugin.getNmsHandler().spawnEntity(WitherSkeleton.class, mobLoc, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    EntityEquipment eq = skeleton.getEquipment();

                    plugin.getNmsEntity(skeleton).setMaxHealth(80.0D);

                    skeleton.customName(TextFormat.write("&6Wither Skeleton Emperador"));
                    skeleton.setCollidable(false);

                    ItemStack item = new ItemStack(Material.BLACK_BANNER, 1);
                    BannerMeta meta = (BannerMeta) item.getItemMeta();
                    List<Pattern> patterns = new ArrayList<>();

                    patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRAIGHT_CROSS));
                    patterns.add(new Pattern(DyeColor.BLACK, PatternType.BRICKS));
                    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
                    patterns.add(new Pattern(DyeColor.YELLOW, PatternType.FLOWER));
                    patterns.add(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
                    patterns.add(new Pattern(DyeColor.RED, PatternType.GRADIENT_UP));
                    meta.setPatterns(patterns);
                    item.setItemMeta(meta);

                    eq.setHelmet(item);
                    eq.setHelmetDropChance(0);
                    eq.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
                    eq.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
                    eq.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
                    eq.setItemInMainHand(new ItemProperties(Material.BOW).addEnchant(Enchantment.PUNCH, 5).addEnchant(Enchantment.POWER, 100).build());
                    eq.setItemInMainHandDropChance(0);
                }
                case SKELETON -> {
                    plugin.getNmsHandler().spawnEntity(Skeleton.class, mobLoc, CreatureSpawnEvent.SpawnReason.NATURAL);
                }
            }

            Bukkit.broadcast(TextFormat.write("&3Un gato galático ha invocado un(a)" + entityName + " &7(" + mobLoc.getX() + ", " + mobLoc.getY() + ", " + mobLoc.getZ()));
            cancel();
        }
    }
}
