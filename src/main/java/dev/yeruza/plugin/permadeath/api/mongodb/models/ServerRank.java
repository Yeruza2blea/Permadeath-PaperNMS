package dev.yeruza.plugin.permadeath.api.mongodb.models;

import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;
import org.bukkit.Color;
import dev.yeruza.plugin.permadeath.gaming.client.Permissions;

import java.util.List;

public record ServerRank(
        @BsonId @BsonRepresentation(BsonType.OBJECT_ID) ObjectId _id,
        @BsonProperty String name,
        @BsonProperty List<String> description,
        @BsonProperty Color color,
        @BsonProperty List<Permissions> permissions
)  {
    public ServerRank(String name, String description, Color color, Permissions permissions) {
        this(new ObjectId(), name, List.of(description), color, List.of(permissions));
    }
    public ServerRank(String name, String description, Color color, List<Permissions> permissions) {
        this(new ObjectId(), name, List.of(description), color, permissions);
    }

    public ServerRank(String name, List<String> description, Color color, Permissions permissions) {
        this(new ObjectId(), name, description, color, List.of(permissions));
    }
}
