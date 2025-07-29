package dev.yeruza.plugin.permadeath.nms.main.entities.peace;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.profile.PlayerTextures;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.PermadeathItems;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;

public class Npc extends MobChilling<ServerPlayer> {
    protected Display.TextDisplay title;

    public Npc(Location where, String value) {
        super(where, value);

        this.entity = new ServerPlayer(server, level, new GameProfile(UUID.randomUUID(), ""), ClientInformation.createDefault());
        entity.setPos(CraftLocation.toVec3(where));
        entity.setItemInHand(InteractionHand.MAIN_HAND, CraftItemStack.asNMSCopy(PermadeathItems.AMON_ROD));

        Vec3 coords = CraftLocation.toVec3(where);

        ServerGamePacketListenerImpl game = entity.connection;
        game.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entity));
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
    }

    @Override
    public void spawnEntity() {
        try {
            URI uri = new URI("");
            getEntity().getPlayerProfile().getTextures().setSkin(uri.toURL(), PlayerTextures.SkinModel.SLIM);
        } catch (URISyntaxException | MalformedURLException e) {
            Permadeath.getPlugin().getLogger().severe(e.getMessage());
        }

        this.title = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        ServerEntity text = new ServerEntity(level, title, 0, false, packet -> {}, (p, id) -> {}, Set.of());
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(title, text);
    }

    public void lookAt(org.bukkit.entity.Player bukkitPlayer, Location eye) {

    }

    @Override
    public org.bukkit.entity.Player getEntity() {
        return (org.bukkit.entity.Player) super.getEntity();
    }
}
