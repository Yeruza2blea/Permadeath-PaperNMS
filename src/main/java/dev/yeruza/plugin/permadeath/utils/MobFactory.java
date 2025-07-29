package dev.yeruza.plugin.permadeath.utils;

import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorProperties;
import dev.yeruza.plugin.permadeath.plugin.item.utils.base.BannerProperties;

import java.util.List;
import java.util.Map;

public final class MobFactory {
    private final Permadeath plugin;

    public MobFactory(Permadeath plugin) {
        this.plugin = plugin;
    }

    public AbstractSkeleton spawnSkeletonWithClass(Location location, int selected) {
        AbstractSkeleton skeleton = location.getWorld().spawn(location, AbstractSkeleton.class);

        World world = location.getWorld();

        ItemStack mainHand, offHand;


        switch (selected) {
            case 1 -> {

            }
            case 2 -> {

            }
            case 3 -> {

            }
            case 4 -> {

            }
            case 5 -> {

            }
            default -> {

            }
        }

        EntityEquipment equipment = skeleton.getEquipment();

        return skeleton;
    }

    public WitherSkeleton spawnWitherSkeletonEmperor(Location location) {
        WitherSkeleton emperor = location.getWorld().spawn(location, WitherSkeleton.class);
        emperor.customName(TextFormat.write("&6Wither Skeleton Emperador"));

        EntityEquipment eq = emperor.getEquipment();

        plugin.getNmsEntity(emperor).setMaxHealth(80.0D);

        BannerProperties banner = new BannerProperties(Material.BLACK_BANNER, List.of(
                new Pattern(DyeColor.YELLOW, PatternType.STRAIGHT_CROSS),
                new Pattern(DyeColor.BLACK, PatternType.BRICKS),
                new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE),
                new Pattern(DyeColor.YELLOW, PatternType.FLOWER),
                new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP),
                new Pattern(DyeColor.RED, PatternType.GRADIENT_UP)
        ));


        ItemProperties bow = new ItemProperties(Material.BOW)
                .setCustomModelData(String.class, List.of("bow_emperor"))
                .addEnchants(Map.of(Enchantment.PUNCH,5, Enchantment.POWER, 100));

        ItemStack[] armor = ArmorProperties.pieces(
                new ArmorProperties(Material.GOLDEN_BOOTS, new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.EYE)),
                new ArmorProperties(Material.GOLDEN_LEGGINGS, new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.EYE)),
                new ArmorProperties(Material.GOLDEN_CHESTPLATE, new ArmorTrim(TrimMaterial.REDSTONE, TrimPattern.EYE)),
                banner
        );

        eq.setHelmetDropChance(0);
        eq.setChestplateDropChance(0);
        eq.setLeggingsDropChance(0);
        eq.setBootsDropChance(0);
        eq.setArmorContents(armor);
        eq.setItemInMainHand(bow.build());
        eq.setItemInMainHandDropChance(0);

        return emperor;
    }

    public WitherSkeleton spawnUltraSkeleton(Location location) {
        WitherSkeleton ultra = location.getWorld().spawn(location, WitherSkeleton.class);

        ultra.customName(TextFormat.write("&6Ultra Esqueleto Definitivo"));
        ultra.getEquipment().setItemInMainHand(
                new ItemProperties(Material.BOW)
                        .setName("&6Arco de la muerte")
                        .setCustomModelData(String.class,"bow_definitive")
                        .addEnchant(Enchantment.POWER, 32765)
                        .build()
        );

        plugin.getNmsEntity(ultra).setMaxHealth(200.0D);

        ultra.getEquipment().setItemInMainHandDropChance(0.0f);
        ultra.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        addPersistentData(ultra, "ultra_skeleton_definitve");
        ultra.setRemoveWhenFarAway(false);


        return ultra;
    }

    public Ravager spawnUltraRavager(Location location) {
        Ravager ravager = location.getWorld().spawn(location, Ravager.class);
        ravager.getPersistentDataContainer().set(Permadeath.withCustomNamespace("ultra_ravager"), PersistentDataType.BYTE, (byte) 1);
        PigZombie carlos = location.getWorld().spawn(location, PigZombie.class);
        carlos.getPersistentDataContainer().set(Permadeath.withCustomNamespace("carlos"), PersistentDataType.BYTE, (byte) 1);
        Villager jess = location.getWorld().spawn(location, Villager.class);
        jess.getPersistentDataContainer().set(Permadeath.withCustomNamespace("jess"), PersistentDataType.BYTE, (byte) 1);

        carlos.addPassenger(jess);
        ravager.addPassenger(carlos);

        plugin.getNmsEntity(jess).setMaxHealth(500.0D);
        plugin.getNmsEntity(carlos).setMaxHealth(150.0D);
        plugin.getNmsEntity(ravager).setMaxHealth(240.0D);

        jess.setCustomName(ChatColor.GREEN + "Jess la Emperatriz");
        carlos.setCustomName(ChatColor.GREEN + "Carlos el Esclavo");
        ravager.setCustomName(ChatColor.GREEN + "Ultra Ravager");

        jess.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_APPLE, 2));
        jess.getEquipment().setItemInMainHandDropChance(0);

        carlos.getEquipment().setItemInMainHand(new ItemStack(Material.GOLD_INGOT, 32));
        carlos.getEquipment().setItemInMainHandDropChance(0);

        ravager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        ravager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));

        jess.setRemoveWhenFarAway(true);
        ravager.setRemoveWhenFarAway(true);
        carlos.setRemoveWhenFarAway(true);

        return ravager;
    }

    public Creeper spawnEnderCreeper(Location location) {
        Creeper creeper = location.getWorld().spawn(location, Creeper.class);

        creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        creeper.customName(TextFormat.write("&6Ender Creeper"));
        addPersistentData(creeper, "ender_creeper");

        return creeper;
    }

    public Creeper spawnQuantumCreeper(Location location) {
        Creeper creeper = location.getWorld().spawn(location, Creeper.class);

        creeper.customName(TextFormat.write("&6Quantom Creeper"));
        creeper.setExplosionRadius(plugin.getConfig().getInt("toggles.quantum-explosion-power"));
        addPersistentData(creeper, "quantom_creeper");

        return creeper;
    }

    public Creeper spawnEnderQuantumCreeper(Location location) {
        Creeper creeper = location.getWorld().spawn(location, Creeper.class);

        creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        creeper.customName(TextFormat.write("&6Ender Quantum Creeper"));
        addPersistentData(creeper, "ender_quantum_creeper");
        creeper.setExplosionRadius(plugin.getConfig().getInt("toggles.quantum-explosion-power"));

        return creeper;
    }

    public boolean hasData(Entity entity, String id) {
        return entity.getPersistentDataContainer().has(new NamespacedKey(plugin, "nms_entity"), PersistentDataType.STRING);
    }

    public void addPersistentData(Entity entity, String id) {
        entity.getPersistentDataContainer().set(new NamespacedKey(plugin, "nms_entity"), PersistentDataType.STRING, id);
    }
}
