package dev.yeruza.plugin.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.Language;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlayerManager extends SettingsManager {
    private String name;

    private String day;
    private String time;
    private String cause;
    private String coords;

    public final Player player;


    public PlayerManager(@Nullable OfflinePlayer player, Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "server/players.yml"));
        this.player = player.getPlayer();
        this.name = player.getName();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                plugin.getLogger().warning("Ha ocurrido un error al crear el archivo 'players.yml'");
            }
        }

        if (config.contains("players." + name)) {
            this.day = config.getString("players." + name + ".day");
            this.time = config.getString("players." + name + ".time");
            this.cause = config.getString("players." + name + ".cause");
            this.coords = config.getString("players." + name + ".coords");
        } else {
            this.day = "";
            this.time = "";
            this.cause = "";
            this.coords = "";
        }

        if (Bukkit.getPlayer(name) != null) {
            Player playerRegister = Bukkit.getPlayer(name);

            addDefault("players." + name + ".uuid", playerRegister.getUniqueId());
        }

        if (!config.contains("players." + name + ".hp")) {
            config.set("players." + name + ".hp", 0);
        }

        saveFile();
        reloadFile();
    }

    private void addDefault(String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value.toString());
        } else {
            if (path.equalsIgnoreCase("players." + name + ".language")) {
                Language constant = Language.valueOf(config.getString("players." + name + ".language").toUpperCase());
                String lang = constant.name().toLowerCase();

                if (!lang.equalsIgnoreCase("spanish") && !lang.equalsIgnoreCase("english")) {
                    config.set("players." + name + ".language", "spanish");
                    saveFile();
                    reloadFile();
                }
            }
        }
    }

    public ItemStack craftHead(ItemStack item) {
        return new ItemProperties(item)
                .setName("&5&l" + name)
                .setLore(
                        "&c&lHA SIDO PERMABANEADO",
                        "&cFecha de baneo: " + day,
                        "&cHora de baneo: " + time,
                        "&cCausa de baneo: &b" + cause
                )
                .build();
    }

    public ItemStack craftHead() {
        return new ItemProperties(Material.PLAYER_HEAD)
                .setName("&5&l" + name)
                .setHead(player.getPlayerProfile())
                .setLore(
                        "&c&lHA SIDO PERMABANEADO",
                        "&cFecha de baneo: &b" + day,
                        "&cHora de baneo: &b" + time,
                        "&cCausa de baneo: &b" + cause
                )
                .build();

    }

    public void setAutoDeathCause(EntityDamageEvent.DamageCause lastDamage) {
        String cause = switch (lastDamage) {
            case WITHER -> "&0Efecto Wither";
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "Explosión";
            case DRAGON_BREATH -> "&dEnder Dragon (Breath)";
            case ENTITY_ATTACK -> "Mobs";
            case DROWNING -> "Ahogamiento";
            case FALL -> "Caída";
            case FIRE -> "Fuego";
            case LAVA -> "Lava";
            case LIGHTNING -> "Trueno";
            case POISON -> "Veneno";
            case VOID -> "Vació";
            case SUFFOCATION -> "Sofocado";
            case SUICIDE -> "Suicidio";
            case THORNS -> "Espinas";
            case PROJECTILE -> "Proyectil";
            default -> "Causa desconocida.";
        };

        setBanCause(cause);
    }

    public void generateDay() {
        long days = plugin.getDay();

        if (config.contains("players." + name + "last-day")) return;
        setLastDay(days);
    }

    public void setBanTime(String format) {
        this.time = format;
        config.set("players." + name + ".time", time);
        saveFile();
        reloadFile();
    }

    public void setLastDay(long days) {
        config.set("players." + name + ".last-day", days);

        saveFile();
        reloadFile();
    }

    public void setDeathDay() {
        LocalDate dateNow = LocalDate.now();

        int month = dateNow.getMonthValue();
        int day = dateNow.getDayOfMonth();


        StringBuilder message = new StringBuilder();

        if (month < 10) {
            message.append(dateNow.getYear()).append("-0").append(month).append("-");
        } else {
            message.append(dateNow.getYear()).append("-").append(month).append("-");
        }

        if (day < 10) {
            message.append(message).append(0).append(day);

        } else {
            message.append(message).append(day);
        }

        setBanDay(new String(message));
    }

    public void setBanCause(String cause) {
        this.cause = cause;
        config.set("players." + name + ".cause", cause);
        saveFile();
        reloadFile();
    }

    public void setBanDay(String day) {
        this.day = day;
        config.set("players." + name + ".day", day);
        saveFile();
        reloadFile();
    }

    public void setDeathTime() {
        LocalDateTime date = LocalDateTime.now();

        int sec = date.getSecond();
        int min = date.getMinute();
        int hour = date.getHour();

        StringBuilder format = new StringBuilder();
        String fSec, fMin, fHour;

        if (sec < 10) {
            fSec = "0" + sec;
        } else {
            fSec = String.valueOf(sec);
        }
        if (min < 10) {
            fMin = "0" + min;
        } else {
            fMin = String.valueOf(min);
        }
        if (hour < 10) {
            fHour = "0" + hour;
        } else {
            fHour = String.valueOf(hour);
        }
        StringBuilder msg = format.append(fHour).append(':').append(fMin).append(':').append(fSec);

        setBanTime(new String(msg));
    }

    public void setDeathCoords(Location where) {
        int x = where.getBlockX();
        int y = where.getBlockY();
        int z = where.getBlockZ();

        String coords = x + "|" + y + "|" + z;

        this.coords = coords;
        config.set("players." + name + ".coords", coords);

        saveFile();
        reloadFile();
    }

    public void setExtraPh(int hp) {
        config.set("players." + name + ".hp", hp);
        saveFile();
        reloadFile();
    }

    public void setLanguage(Language language) {
        config.set("players." + name + ".language", language.name().toUpperCase());

        saveFile();
        reloadFile();
    }

    public String getName() {
        return name;
    }

    public String getBanTime() {
        return config.getString("players." + name + ".time");
    }

    public String getBanCause() {
        return config.getString("players." + name + ".cause");
    }

    public int getExtraPH() {
        return config.getInt("players." + name + ".hp");
    }

    public String getCoords() {
        return config.getString("players." + name + ".coords");
    }

    public int getLastDay() {
        generateDay();

        return config.getInt("players" + name + ".last-day");
    }

    public Language getLanguage() {
        addDefault("players." + name + ".language", Language.SPANISH);

        return Language.valueOf(config.getString("players." + name + ".language").toUpperCase());

    }

    public void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Ha ocurrido un error al guardar el archivo 'players.yml'");
        }
    }

    public void reloadFile() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("[ERROR] Ha ocurrido un error al guardar el archivo 'players.yml'");
        }
    }

}