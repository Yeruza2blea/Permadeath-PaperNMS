package dev.yeruza.plugin.permadeath.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.*;
import java.util.logging.Level;

public class ApiResource {
    public static void writeFile(Plugin plugin, String outPath, String path, boolean replaced) {
        if (!path.isEmpty()) {
            path = path.replace('\\', '/');
            InputStream in = plugin.getResource(path);
            if (in != null) {
                int lastIndex = path.lastIndexOf(47); // "/"
                String finalPath = outPath + path.substring(Math.max(0, lastIndex));

                File outFile = new File(plugin.getDataFolder(), finalPath);

                if (outFile.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdirs();
                    }
                }

                try {
                    if (!outFile.exists() && replaced) {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, e);
                }
            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }


    public static void saveFile(Plugin plugin, String outPath, String path, boolean replaced) {
        if (!path.isEmpty()) {
            path = path.replace('\\', '/');
            InputStream in = plugin.getResource(path);
            if (in != null) {
                int lastIndex = path.lastIndexOf(47); // "/"
                String finalPath = outPath + path.substring(Math.max(0, lastIndex));

                File outFile = new File(plugin.getDataFolder(), finalPath);

                if (outFile.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdirs();
                    }
                }

                try {

                    if (!outFile.exists() && replaced)  {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, e);
                }
            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    private final Permadeath plugin;
    private File file;
    private FileConfiguration fc;

    public ApiResource(Permadeath plugin, File file, FileConfiguration fc) {
        this.plugin = plugin;
        this.file = file;
        this.fc = fc;
    }

    public void create(String filename, boolean saveResource) {
        file = new File(plugin.getDataFolder(), filename);
        fc = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                if (!saveResource)
                    file.createNewFile();
                else {
                    boolean exists = plugin.getResource(filename) == null;

                    plugin.saveResource(filename, exists);
                }

                load();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void load() {
        try {
            fc.load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            fc.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <E> void set(String path, E e) {
        if (fc.contains(path))
            fc.set(path, e);
    }

    public static ApiResource call(Permadeath plugin, File file, FileConfiguration fc) {
        return new ApiResource(plugin, file, fc);
    }
}

