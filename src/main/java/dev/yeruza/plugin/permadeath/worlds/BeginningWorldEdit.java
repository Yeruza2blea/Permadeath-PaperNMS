package dev.yeruza.plugin.permadeath.worlds;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.SplittableRandom;

public final class BeginningWorldEdit extends WorldEditManager {
    public BeginningWorldEdit(Permadeath plugin, World world) {
        super(plugin, world);
    }

    public void generateIsland() {

    }

    public void generateIsland(int x, int y, int z, SplittableRandom random) {
        Clipboard clipboard;
        File file = switch (random.nextInt(6)) {
            case 0  -> new File(plugin.getDataFolder(), "schematics/the-beginning/island1.schem");
            case 1 -> new File(plugin.getDataFolder(), "schematics/the-beginning/island2.schem");
            case 2 -> new File(plugin.getDataFolder(), "schematics/the-beginning/island3.schem");
            case 3 -> new File(plugin.getDataFolder(), "schematics/the-beginning/island4.schem");
            case 4 -> new File(plugin.getDataFolder(), "schematics/the-beginning/island5.schem");
            case 5 -> new File(plugin.getDataFolder(), "schematics/the-beginning/island6.schem");
            default -> new File(plugin.getDataFolder(), "schematics/the-beginning/island7.schem");
        };

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(new BukkitWorld(world)).maxBlocks(-1).build()) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y + 20, z))
                    .build();

            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }


    }

    public void generateYtic(int x, int y, int z) {
        Clipboard clipboard;
        File file = new File(Permadeath.getPlugin().getDataFolder(), "schematics/the-beginning/the-beginning_ytic.schem");

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(new BukkitWorld(world)).maxBlocks(-1).build()) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y + 34, z))
                    .ignoreAirBlocks(true)
                    .copyEntities(true)
                    .build();

            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public void generatePortal(Location location, boolean canGen) {
        if (plugin.getBeginningData().generatedOverworldBeginningPortal() && canGen) {
            int x = Permadeath.getPlugin().getConfig().getInt("the-beginning.x-limit");
            int z = Permadeath.getPlugin().getConfig().getInt("the-beginning.z-limit");

            int ranX = new Random().nextInt(x);
            int ranZ = new Random().nextInt(z);

            if (random.nextBoolean())
                ranX *= -1;
            if (random.nextBoolean())
                ranZ *= -1;

            Location loc = new Location(plugin.getOverWorld(), ranX, 0, ranZ);

            int highestBlockAt = plugin.getOverWorld().getHighestBlockAt(loc).getY();
            if (highestBlockAt == -1)
                highestBlockAt = 50;

            highestBlockAt += 15;

            loc.setY(highestBlockAt);
            pasteSchematic(loc, new File(plugin.getDataFolder(), "schematics/the-beginning/the-beginning_portal.schem"));
            plugin.getBeginningData().setOverWorldPortal(loc);
        }

        if (!plugin.getBeginningData().generatedBeginningPortal() && !canGen) {
            Bukkit.getWorld("the_beginning").loadChunk(location.getChunk());

            pasteSchematic(location, new File(plugin.getDataFolder(), "schematics/the-beginning/the-beginning_portal.schem"));
            plugin.getBeginningData().setBeginningPortal(location);
        }
    }
}
