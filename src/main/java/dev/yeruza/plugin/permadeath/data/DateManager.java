package dev.yeruza.plugin.permadeath.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateManager extends SettingsManager {
    private String date;
    private LocalDate startDate;
    private LocalDate currentDate;

    public DateManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "config.yml"));
        this.currentDate = LocalDate.now();

        prepareFile();
        this.date = config.getString("date");

        try {
            this.startDate = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            Bukkit.getConsoleSender().sendMessage(TextFormat.showWithPrefixf("&4&lERROR: &eLa fecha en config.yml estaba mal configurada &7(%s)&e.", config.getString("date")));
            Bukkit.getConsoleSender().sendMessage(TextFormat.showWithPrefix("&eSe ha establecido el día: &b1"));

            this.startDate = LocalDate.parse(getDateForDayOne());
            config.set("date", getDateForDayOne());
            saveFile();
            reloadFile();
        }
    }

    public void tick() {
        LocalDate now = LocalDate.now();

        if (currentDate.isBefore(now)) {
            this.currentDate = now;
        }

    }

    public void reloadDate() {
        date = config.getString("date");
        startDate = LocalDate.parse(date);
        currentDate = LocalDate.now();
    }

    public long getDay() {
        if (Permadeath.isSpeedRunMode()) {
            return plugin.getPlayTime() / 3600;
        }

        return startDate.until(currentDate, ChronoUnit.DAYS);
    }

    public String getDateForDayOne() {
        LocalDate date = currentDate.minusDays(1);

        return String.format(date.getYear() + "-%02d-%02d", date.getMonthValue(), date.getDayOfMonth());
    }

    public void setDay(CommandSender sender, String argv) {
        int nD = 0;

        try {
            int day = Integer.parseInt(argv);
            if (day > 120 || day < 0) {
            } else {
                nD = day;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(TextFormat.write("&cNecesitas ingresar un número válido."));
        }
        if (nD == 0) {
            sender.sendMessage(TextFormat.write("&cHas ingresado un número no válido, o ni siquiera un número."));
            return;
        }

        LocalDate a = currentDate.minusDays(nD);
        setNewDate(String.format(a.getYear() + "-%02d-%02d", a.getMonthValue(), a.getDayOfMonth()));

        sender.sendMessage(TextFormat.write("&eSe han actualizado los días a: &7" + nD));
        sender.sendMessage(TextFormat.write("&c&lNota importante: &7Algunos cambios pueden requerir un reinicio y la fecha puede no ser exacta."));

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permadeath:pd-reload");



        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
                if (offline == null) return;

                if (offline.isBanned()) return;

                PlayerManager manager = new PlayerManager(offline, plugin);
                manager.setLastDay(this.getDay());
            }
        }

    }

    public void setNewDate(String date) {
        config.set("date", date);
        saveFile();
        reloadFile();
    }

    private void prepareFile() {
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
            config.set("date", getDateForDayOne());
        }

        if (config.getString("date").isEmpty()) {
            config.set("date", getDateForDayOne());
        }

        saveFile();
        reloadFile();
    }

    public void saveFile() {
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reloadFile() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }
}
