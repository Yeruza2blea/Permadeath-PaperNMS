package dev.yeruza.plugin.permadeath.nms.main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.util.List;
import java.util.Optional;

public abstract class NmsBlock {
    public static final MinecraftServer SERVER = ((CraftServer) Bukkit.getServer()).getServer();
    public static final ResourceLocation BLOCK_ID = CraftNamespacedKey.toMinecraft(Permadeath.withCustomNamespace("nms_block"));

    protected final ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, SERVER.registryAccess(), new CompoundTag());
    protected final ValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, SERVER.registryAccess());


    private ItemProperties editor;
    private ItemStack dropItem;

    protected KeyId<String> key;
    protected final ResourceLocation id;

    public NmsBlock(String id, ItemStack dropItem) {
        this.id = CraftNamespacedKey.toMinecraft(Permadeath.withCustomNamespace(id));
        this.key = KeyId.create(BLOCK_ID, id);
        this.editor = new ItemProperties(dropItem);
        this.dropItem = dropItem;

    }

    public NmsBlock(org.bukkit.block.BlockState state) {
        this.id = CraftNamespacedKey.toMinecraft(Permadeath.withCustomNamespace("unknown"));
        dropItem = List.copyOf(state.getBlock().getDrops()).getFirst();
    }

    private Location toFace(Block block, BlockFace face) {
        Location loc = block.getLocation();

        switch (face) {
            case DOWN -> loc.setY(loc.getY() - 1);
            case EAST -> loc.setX(loc.getX() + 1);
            case NORTH -> loc.setZ(loc.getZ() - 1);
            case SOUTH -> loc.setZ(loc.getZ() + 1);
            case UP -> loc.setY(loc.getY() + 1);
            case WEST -> loc.setX(loc.getX() - 1);
        }

        return loc;
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (isCustomBlock(event.getBlock().getLocation())) {
            BlockState block = ((CraftBlock) event.getBlock()).getNMS();
            Player player = event.getPlayer();

            player.stopSound(Sound.BLOCK_SPAWNER_BREAK);

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0, 0.5, 0), dropItem);
            onSoundBreak(event.getBlock().getLocation(), Sound.BLOCK_NETHERITE_BLOCK_BREAK);
        }
    }

    public void onBlockPlace(PlayerInteractEvent event) {
        Location location = toFace(event.getClickedBlock(), event.getBlockFace());
        ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();

        PacketContainer container = new PacketContainer(PacketType.Play.Server.ANIMATION);
        container.getIntegers().write(0, event.getPlayer().getEntityId());
        container.getIntegers().write(1, 1);

        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), container);


        Block placed = location.getBlock();
        placed.setType(Material.SPAWNER);

        BlockPos bp = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        SpawnerBlockEntity spawner = (SpawnerBlockEntity) world.getBlockEntity(bp);
        BaseSpawner base = spawner.getSpawner();

        CraftEntitySnapshot snapshot = createData(location);

        base.spawnRange = 0;
        base.spawnCount = 0;
        base.requiredPlayerRange = 0;
        base.maxNearbyEntities = 0;
        base.nextSpawnData = new SpawnData(snapshot.getData(), Optional.empty(), Optional.empty());
        base.save(output);


        base.load(world, bp, input);

        onSoundPlace(location, Sound.BLOCK_NETHERITE_BLOCK_PLACE);
    }

    public boolean isCustomBlock(Location pos) {
        BlockPos bp = new BlockPos(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        SpawnerBlockEntity spawner = (SpawnerBlockEntity) ((CraftWorld) pos.getWorld()).getHandle().getBlockEntity(bp);

        CompoundTag nbtSpawner = spawner.getUpdateTag(spawner.getLevel().registryAccess());
        boolean dataFound = nbtSpawner != null && nbtSpawner.contains("Id");

        if (!dataFound) {
            if (pos.getBlock().getState() instanceof CreatureSpawner customBlock) {
                if (customBlock.getSpawnedType() == org.bukkit.entity.EntityType.ARMOR_STAND) {
                    dataFound = true;
                }
            }
        }

        return dataFound;
    }

    protected void onSoundPlace(Location loc, Sound placing) {
        loc.getWorld().playSound(loc, placing, 1, 1);
    }

    protected void onSoundBreak(Location loc, Sound placing) {
        loc.getWorld().playSound(loc, placing, 1, 1);
    }

    protected CraftEntitySnapshot createData(Location location) {
        CraftEntitySnapshot snapshot = CraftEntitySnapshot.create(new CompoundTag(), org.bukkit.entity.EntityType.ARMOR_STAND);

        ArmorStand stand = ((CraftArmorStand) snapshot.createEntity(location)).getHandle();
        stand.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(dropItem));
        stand.setInvisible(true);
        stand.setMarker(true);

        return snapshot;
    }
}
