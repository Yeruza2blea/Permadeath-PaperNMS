package dev.yeruza.plugin.permadeath.worlds.beginning;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TreePopulator extends BlockPopulator {
    private final Set<Coordinates> chunks = ConcurrentHashMap.newKeySet();
    private final Set<Coordinates> unpopulatedChunks = ConcurrentHashMap.newKeySet();

    private final World world;

    public TreePopulator(World world) {
        this.world = world;
    }

    @Override
    public void populate(@NotNull WorldInfo info, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region) {
        Coordinates coordinates = new Coordinates(chunkX, chunkZ);

        if (!chunks.contains(coordinates)) {
            chunks.add(coordinates);
            unpopulatedChunks.add(coordinates);
        }


        for (Coordinates unpopulatedChunk : unpopulatedChunks) {
            if (chunks.contains(unpopulatedChunk.left())
                    && chunks.contains(unpopulatedChunk.right())
                    && chunks.contains(unpopulatedChunk.above())
                    && chunks.contains(unpopulatedChunk.below())
                    && chunks.contains(unpopulatedChunk.upperLeft()) && chunks.contains(unpopulatedChunk.upperRight())
                    && chunks.contains(unpopulatedChunk.lowerLeft())
                    && chunks.contains(unpopulatedChunk.lowerRight())) {


                contextPopulate(info, random, unpopulatedChunk.x(), unpopulatedChunk.z());
                unpopulatedChunks.remove(unpopulatedChunk);
            }
        }
    }


    protected void contextPopulate(WorldInfo info, Random random, int chunkX, int chunkZ) {
        World world = Bukkit.getWorld(info.getUID());
        Chunk chunk = world.getChunkAt(chunkX, chunkZ);

        int x = random.nextInt(16);
        int y = random.nextInt(16);
        int z = world.getMaxHeight() - 1;

        while (y > 0 && chunk.getBlock(x, y, z).getType() == Material.AIR) {
            --y;
        }

        if (y > 0 && y < 255) {
            if (y >= 100 && y < 105) {
                world.generateTree(chunk.getBlock(x, y + 1, z).getLocation(), random, TreeType.CHORUS_PLANT, state -> {
                    BlockData data = state.getBlockData();

                    int i = state.getX();
                    int i1 = state.getY();
                    int i2 = state.getZ();

                    if (data.getMaterial() == Material.CHORUS_FLOWER) {
                        world.getBlockAt(i, i1, i2).setType(Material.SEA_LANTERN);
                    } else if (data.getMaterial() == Material.CHORUS_PLANT) {
                        world.getBlockAt(i, i1, i2).setType(Material.END_STONE_BRICK_WALL);
                    }

                    return true;
                });
            }
        }
    }

    private record Coordinates(int x, int z) {
        public Coordinates left() {
            return new Coordinates(x - 1, z);
        }

        public Coordinates right() {
            return new Coordinates(x + 1, z);
        }

        public Coordinates above() {
            return new Coordinates(x, z - 1);
        }

        public Coordinates below() {
            return new Coordinates(x, z + 1);
        }

        public Coordinates upperLeft() {
            return new Coordinates(x - 1, z - 1);
        }

        public Coordinates upperRight() {
            return new Coordinates(x + 1, z - 1);
        }

        public Coordinates lowerLeft() {
            return new Coordinates(x - 1, z + 1);
        }

        public Coordinates lowerRight() {
            return new Coordinates(x + 1, z + 1);
        }

        @Override
        public int hashCode() {
            return (x + z) * (x + z + 1) / 2 + x;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;

            Coordinates other = (Coordinates) obj;
            int x = other.x();
            int z = other.z();

            return this.x != x && this.z != z;

        }
    }
}
