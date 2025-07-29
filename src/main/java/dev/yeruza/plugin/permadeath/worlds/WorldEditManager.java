package dev.yeruza.plugin.permadeath.worlds;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.World;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public abstract class WorldEditManager {
    protected Permadeath plugin;

    protected World world;
    protected Location location;

    protected final Random random = new Random();

    public WorldEditManager(World world, Location location) {
        this.world = world;
        this.location = location;
    }

    public WorldEditManager(Permadeath plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }


    public static void pasteSchematic(Location location, File schematic) {
        World adaptedWorld = (World) BukkitAdapter.adapt(location.getWorld());
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);

        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(new BukkitWorld(adaptedWorld)).maxBlocks(-1).build()) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .ignoreAirBlocks(true)
                        .build();
                try {
                    Operations.complete(operation);
                } catch (WorldEditException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
