package dev.yeruza.plugin.permadeath.api.mongodb.models;

import org.bson.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.util.*;

public record ServerUser (
        @BsonId @BsonRepresentation(BsonType.OBJECT_ID) ObjectId _id,
        @BsonProperty("username") String name,
        @BsonProperty("id") UUID id,
        @BsonProperty("rank") Optional<ServerRank> myRank,
        @BsonProperty("medals") List<ServerMedal> myMedals
) implements MongoModel {
    public static final Map<UUID, ServerUser> BY_NAME = new LinkedHashMap<>();

    public static ServerUser of(Player player) {
        return new ServerUser(player.getPlayerProfile(), Optional.empty());
    }

    public ServerUser(Player player, ServerRank rank) {
        this(player.getPlayerProfile(), Optional.of(rank));
    }

    public ServerUser(PlayerProfile profile, Optional<ServerRank> rank) {
        this(new ObjectId(), profile.getName(), profile.getUniqueId(), rank, List.of());
    }

    public void boostRank() {

    }

    public void addMedal(ServerMedal medal) {
        myMedals.add(medal);
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
        return new Document()
                .append("name", new BsonString(name))
                .append("uuid", new BsonString(id.toString()))
                .append("rank", myRank.isPresent() ? myRank.get() : "")
                .append("medals", myMedals)
                .toBsonDocument();
    }
}
