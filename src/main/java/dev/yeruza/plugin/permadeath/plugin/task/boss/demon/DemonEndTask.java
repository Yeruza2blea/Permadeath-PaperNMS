package dev.yeruza.plugin.permadeath.plugin.task.boss.demon;


import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;
import dev.yeruza.plugin.permadeath.worlds.end.DemonAttacks;
import dev.yeruza.plugin.permadeath.worlds.end.DemonPhase;

import java.util.*;

public class DemonEndTask extends BukkitRunnable {

    private Map<Location, Integer> regenTime = new HashMap<>();
    private Location teleportLoc;
    private DemonAttacks currentAttacks = DemonAttacks.NONE;
    private DemonPhase currentPhase = DemonPhase.NORMAL;
    private DemonMovesTask currentMoves;

    private EnderDragon demon;
    private Permadeath plugin;

    private Location eggLoc;

    private int timeForTnT = 30;
    private int nextDragonAttack = 20;
    private int lightingDuration = 5;
    private int nightVisionDuration = 5;
    private int timeForEnd360 = 20;

    private boolean nightVision = false;
    private boolean died;
    private boolean attack360 = false;
    private boolean lightingRain = false;
    private boolean canMakeAnAttack = true;
    private boolean decided = false;

    private final SplittableRandom random = new SplittableRandom();

    public DemonEndTask(Permadeath plugin, EnderDragon demon) {
        this.plugin = plugin;
        this.demon = demon;

        int y = plugin.getEnd().getMaxHeight() - 1;
        while (y > 0 && plugin.getEnd().getBlockAt(0, y, 0).getType() != Material.BEDROCK) {
            --y;
        }

        eggLoc = plugin.getEnd().getHighestBlockAt(new Location(plugin.getEnd(), 0, y, 0)).getLocation();

        int health = plugin.getConfig().getInt("toggles.end.permadeath-demon.health.normal");

        demon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        demon.setHealth(health);

        teleportLoc = eggLoc.clone().add(0, 2, 0);
        teleportLoc.setPitch(demon.getLocation().getPitch());

        for (Ghast entity : demon.getWorld().getEntitiesByClass(Ghast.class))
            entity.remove();
    }

    @Override
    public void run() {
        if (died || demon.isDead()) {
            plugin.setEndTask(null);
            cancel();
            return;
        }

        tickTntAttack();
        tickLightingRain();
        tickNightVision();
        tick360Attack();
        tickDemonPhase();
        tickRandomLighting();
        tickEndCrystals();
        tickDemonAttacks();
    }

    private void tickEndCrystals() {
        if (!regenTime.isEmpty()) {
            for (Location loc : regenTime.keySet()) {
                int time = regenTime.get(loc);
                if (time >= 1) {
                    regenTime.replace(loc, time, time - 1);
                } else {
                    EnderCrystal crystal = loc.getWorld().spawn(loc, EnderCrystal.class);
                    crystal.getPersistentDataContainer().set(Permadeath.withCustomNamespace("super_crystal"), PersistentDataType.BYTE, (byte) 1);

                    for (Player player : loc.getWorld().getPlayers()) {
                        player.showTitle(TextFormat.createTitle("&c&k! &5&l[&4&l!&5&l] %&c&k!", "&c&lSe ha reestablecido un cristal del end", 10, 5, 20));

                        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 0F, 1.0F);
                    }

                    regenTime.remove(loc);
                    if (loc.getWorld().getBlockAt(loc) != null) {
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.BEDROCK || loc.getWorld().getBlockAt(loc).getType() == Material.AIR)
                            return;

                        loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void tickRandomLighting() {
        int x = (random.nextBoolean() ? 1 : -1) * random.nextInt(21);
        int z = (random.nextBoolean() ? 1 : -1) * random.nextInt(21);
        int y = plugin.getEnd().getHighestBlockYAt(x, z);

        if (y < 0) return;

        plugin.getEnd().strikeLightning(new Location(plugin.getEnd(), x, y, z));
    }

    private void tickDemonPhase() {
        switch (currentPhase) {
            case NORMAL -> {
                demon.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 5));
            }

            case ENRAGED -> {
                demon.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 7));
                demon.customName(TextFormat.write(plugin.getConfig().getString("toggles.end.permadeath-demon.names.enraged")));
            }
            case PSYCHO -> {
                demon.customName(TextFormat.write(plugin.getConfig().getString("toggles.end.permadeath-demon.names.psycho")));
                demon.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 9));
            }

            case DEAD -> {

            }
        }

        if (currentPhase == DemonPhase.ENRAGED) {
            demon.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, Integer.MAX_VALUE, 7));
            demon.customName(TextFormat.write(plugin.getConfig().getString("toggles.end.permadeath-demon.names.enraged")));
        } else
            demon.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, Integer.MAX_VALUE, 5));

    }

    private void tick360Attack() {
        if (demon.getLocation().distance(eggLoc) >= 10.0D && decided)
            decided = false;

        if (demon.getLocation().distance(eggLoc) <= 3.0D && !decided) {

            decided = true;
            demon.setRotation(demon.getLocation().getPitch(), 0);

            if (random.nextInt(10) <= 7)
                attack360 = true;
        }

        if (attack360) {
            canMakeAnAttack = false;
            if (timeForEnd360 >= 1)
                timeForEnd360 = timeForEnd360 - 1;

            if (timeForEnd360 >= 16) {
                if (demon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL) {
                    demon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);
                }
                demon.teleport(teleportLoc);
            }

            if (timeForEnd360 == 15) {
                currentMoves = new DemonMovesTask(plugin, demon, teleportLoc);
                currentMoves.runTaskTimer(plugin, 5L, 5L);
            }

            if (timeForEnd360 == 0) {
                if (currentMoves != null) {
                    currentMoves.cancel();
                    currentMoves = null;
                }

                canMakeAnAttack = true;
                timeForEnd360 = 20;
                attack360 = false;
                demon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
            }
        }
    }

    private void tickLightingRain() {
        if (lightingRain) {
            if (lightingDuration >= 1) {
                canMakeAnAttack = false;
                lightingDuration -= 1;

                for (Player all : plugin.getEnd().getPlayers()) {

                    plugin.getEnd().strikeLightning(all.getLocation());

                    if (currentPhase == DemonPhase.ENRAGED)
                        all.damage(1.0D);

                }
            } else {
                lightingRain = false;
                lightingDuration = 5;
                canMakeAnAttack = true;
            }
        }
    }

    private void tickTntAttack() {
        timeForTnT -= 1;

        if (timeForTnT == 0) {
            if (demon.getPhase() != EnderDragon.Phase.DYING && !attack360 && demon.getLocation().distance(eggLoc) >= 15) {
                spawnTnt(3, -3);
                spawnTnt(3, 3);
                spawnTnt(3, 0);
                spawnTnt(-3, 3);
                spawnTnt(-3, -3);
                spawnTnt(-3, 0);
            }
            timeForTnT = 30 + random.nextInt(61);
        }
    }

    private void spawnTnt(int x, int z) {
          TNTPrimed tnt = (TNTPrimed) demon.getWorld().spawnEntity(demon.getLocation().add(x, 0, z), EntityType.TNT);
          tnt.setFuseTicks(60);
          tnt.setYield(tnt.getYield() * 2);
          tnt.setMetadata("tnt-demon", new FixedMetadataValue(plugin, 1));
    }

    private void tickNightVision() {
        if (nightVision) {
            if (nightVisionDuration >= 1)
                nightVisionDuration--;
            else {
                for (Player all : plugin.getEnd().getPlayers()) {
                    switch (currentPhase) {
                        case NORMAL -> {
                            Location highest = plugin.getEnd().getHighestBlockAt(all.getLocation()).getLocation().add(0, 1, 0);

                            AreaEffectCloud eff = (AreaEffectCloud) plugin.getEnd().spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);

                            eff.setParticle(Particle.DAMAGE_INDICATOR);
                            eff.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 5, 1), false);
                            eff.setRadius(3.0F);
                        }
                        case ENRAGED -> {
                            Location highest = plugin.getEnd().getHighestBlockAt(all.getLocation()).getLocation();

                            AreaEffectCloud eff = (AreaEffectCloud) plugin.getEnd().spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);

                            eff.setParticle(Particle.DAMAGE_INDICATOR);
                            eff.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 5, 1), false);
                            eff.setRadius(3.0F);
                        }
                        case PSYCHO -> {

                        }
                    }
                }

                nightVision = false;
                canMakeAnAttack = true;
            }
        }
    }

    private void tickDemonAttacks() {
        if (nextDragonAttack >= 1)
            nextDragonAttack = nextDragonAttack - 1;
        else if (nextDragonAttack == 0) {
            if (currentPhase == DemonPhase.NORMAL)
                nextDragonAttack = 60;
            else
                nextDragonAttack = 40;
        }

        if (canMakeAnAttack) {
            chooseAnAttack();
        } else {
            currentAttacks = DemonAttacks.NONE;
        }

        if (currentAttacks == DemonAttacks.NONE)
            return;

        if (currentAttacks == DemonAttacks.ENDERMAN_BUFF) {
            int endermansChoosed = 0;
            List<Enderman> endermen = new ArrayList<>();

            for (Enderman man : plugin.getEnd().getEntitiesByClass(Enderman.class)) {

                Location backUp = man.getLocation();
                backUp.setY(0);

                if (eggLoc.distance(backUp) <= 35) {
                    if (endermansChoosed < 4) {
                        endermansChoosed += 1;
                        endermen.add(man);
                    }
                }
            }
            if (!endermen.isEmpty())
                for (Enderman mans : endermen) {
                    AreaEffectCloud effect = (AreaEffectCloud) plugin.getEnd().spawnEntity(plugin.getEnd().getHighestBlockAt(mans.getLocation()).getLocation().add(0, 1, 0), EntityType.AREA_EFFECT_CLOUD);
                    effect.setRadius(10.0F);
                    effect.setParticle(Particle.HAPPY_VILLAGER);
                    effect.setColor(Color.GREEN);

                    effect.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 999999, 0), false);

                    mans.setInvulnerable(true);
                }

        } else if (currentAttacks == DemonAttacks.LIGHTING_RAIN) {
            lightingRain = true;
            lightingDuration = 5;
        } else if (currentAttacks == DemonAttacks.NIGHT_VISION) {
            nightVision = true;
            nightVisionDuration = 5;
            for (Player all : plugin.getEnd().getPlayers())
                all.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 7, 0));
        }
    }

    public void chooseAnAttack() {
        int randomTicks = random.nextInt(25);

        if (randomTicks <= 3)
            currentAttacks = DemonAttacks.LIGHTING_RAIN;
        else if (randomTicks >= 4 && randomTicks <= 15)
            currentAttacks = DemonAttacks.ENDERMAN_BUFF;
        else if (randomTicks >= 15 && randomTicks <= 25)
            currentAttacks = DemonAttacks.NIGHT_VISION;

    }

    public void setDied(boolean died) {
        this.died = died;
    }

    public void setCurrentPhase(DemonPhase phase) {
        currentPhase = phase;
    }

    public EnderDragon getPermadeathDemon() {
        return demon;
    }

    public Permadeath getMain() {
        return plugin;
    }

    public boolean isDied() {
        return died;
    }

    public DemonPhase getCurrentDemonPhase() {
        return currentPhase;
    }

    public Map<Location, Integer> getRegenTime() {
        return regenTime;
    }
}
