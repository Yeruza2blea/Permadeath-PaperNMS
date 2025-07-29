package dev.yeruza.plugin.permadeath.nms.main.entities.boss_hunter;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.scheduler.BukkitRunnable;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class OverAkkomind extends BossFighter<ServerPlayer> {
    public static final EnumSet<ClientboundPlayerInfoUpdatePacket.Action> ACTIONS = EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME
    );

    public static final float MAX_HEALTH = 2000F;
    public static final float DAMAGE = 10F;

    public static final GameProfile profile = new GameProfile(UUID.randomUUID(), Component.literal("SuperAkkomente").getString());

    public static AttributeSupplier.Builder createAttributes() {
        return Player.createAttributes().add(Attributes.ATTACK_DAMAGE, DAMAGE).add(Attributes.MAX_HEALTH, MAX_HEALTH);
    }

    public OverAkkomind(Location where) {
        super(where, "over_akkomind");

        this.entity = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
        entity.setPos(CraftLocation.toVec3(where));


    }

    @Override
    public void spawnEntity() {
        World world = level.getWorld();
        ServerGamePacketListenerImpl game = entity.connection;

        Vec3 coords = CraftLocation.toVec3(where);

        game.send(new ClientboundPlayerInfoUpdatePacket(ACTIONS, List.of(entity)));
        game.send(new ClientboundAddEntityPacket(
                entity.getId(),
                entity.getUUID(),
                coords.x,
                coords.y,
                coords.z,
                90F,
                0F,
                entity.getType(),
                0,
                Vec3.ZERO,
                0F
        ));



        world.spawnParticle(Particle.PORTAL, where, 5);

        if (Permadeath.getDayLiteral() < 55) {
            BukkitRunnable thread = new BukkitRunnable() {
                @Override
                public void run() {

                }
            };

            thread.runTask(Permadeath.getPlugin());
        }
    }

    public void attackTarget(Collection<org.bukkit.entity.Entity> entities) {

    }

    public org.bukkit.entity.Player getEntity() {
        return entity.getBukkitEntity();
    }

}
