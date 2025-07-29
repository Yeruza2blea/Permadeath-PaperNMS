package dev.yeruza.plugin.permadeath.utils.log;

import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GamingLogger {
    private Permadeath plugin;
    private File file;

    public GamingLogger(Permadeath plugin) {
        file = new File(plugin.getDataFolder(), "logs.text");

        if (!file.exists()) {
            try {
                file.createNewFile();
                file.setReadOnly();
            } catch (IOException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }


        this.plugin = plugin;


    }

    public void registerText(String reason) {
        LocalDate date = LocalDate.now();
        LocalDateTime time = LocalDateTime.now();
        String formattedDate = String.format("[%02d/%02d/%02d] ", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
        String formattedTime = String.format("%02d:%02d:%02d ", time.getHour(), time.getMinute(), time.getSecond());

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(formattedDate.concat(formattedTime).concat(reason));
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning(e.getMessage());
        }


    }

    public void sendMessageDisable(String reason) {
        registerText("El plugin se ha pagado: ".concat(reason));
    }

    public void sendMessageEnable(String reason) {
        registerText("El plugin se ha activado: ".concat(reason));
    }
}
