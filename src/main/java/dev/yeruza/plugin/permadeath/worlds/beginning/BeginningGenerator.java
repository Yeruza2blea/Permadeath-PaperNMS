package dev.yeruza.plugin.permadeath.worlds.beginning;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.worlds.BeginningWorldEdit;

import java.util.*;

public class BeginningGenerator extends ChunkGenerator {
    public static final int HEIGHT = 100;
    private static final boolean SMALL_ISLANDS_ENABLED = true;
    private final SplittableRandom random = new SplittableRandom();


    @Override
    public void generateNoise(@NotNull WorldInfo info, @NotNull Random cRandom, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData data) {
        SimplexOctaveGenerator lowGenerator = new SimplexOctaveGenerator(new Random(info.getSeed()), 8);

        lowGenerator.setScale(0.02D);

        for (int x = 0; x < 16; ++x)
            for (int z = 0; z < 16; ++z) {
                double noise = lowGenerator.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5, 0.5);

                if (noise <= 0) {
                    if (Permadeath.isHasWorldEdit() && SMALL_ISLANDS_ENABLED && x == 8 && z == 8)
                        if (random.nextInt(20) == 0) {
                            int otherX = x;
                            int otherZ = z;

                            Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {
                               BeginningWorldEdit manager = new BeginningWorldEdit(Permadeath.getPlugin(), (World) info);
                               manager.generateIsland(chunkX * 16 + otherX, chunkZ * 16 + otherZ, HEIGHT, random);
                            }, 20);
                        }
                    continue;
                }

                int chance = Permadeath.getPlugin().getConfig().getInt("toggles.the-beginning.ytic-generate-chances");
                if (chance == 1000000 || chance <= 1)
                    chance = 100000;

                if (Permadeath.isHasWorldEdit() && random.nextInt(chance) == 0) {
                    int finalX = x;
                    int finalZ = z;

                    Bukkit.getScheduler().runTaskLater(Permadeath.getPlugin(), () -> {
                        BeginningWorldEdit manager = new BeginningWorldEdit(Permadeath.getPlugin(), (World) info);

                        manager.generateYtic(chunkX * 16 + finalX, chunkZ * 16 + finalZ, HEIGHT);
                        }, 20);
                }

                for (int i = 0; i < noise / 3; i++)
                    data.setBlock(x, i + HEIGHT, z, Material.PURPUR_BLOCK);

                for (int i = 0; i < noise; i++)
                    data.setBlock(x, HEIGHT - i - 1, z, Material.PURPUR_BLOCK);
            }
    }


    @NotNull
    @Override
    public List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return Collections.singletonList(new TreePopulator(world));
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }
}
