package dev.yeruza.plugin.permadeath.api.mongodb.models;


import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;

public record ServerClan(ServerUser leader, String name, List<String> description, UUID id, List<ServerUser> members, NamedTextColor color) implements MongoModel {
    private static int count;
    public static int getClanCount() {
        return count;
    }

    public ServerClan {
        count++;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
        return new Document()
                .append("leader", leader.name())
                .append("name", name)
                .append("description", description)
                .append("uuid", id)
                .append("members", members.stream().map(ServerUser::name).toList())
                .toBsonDocument(aClass, codecRegistry);
    }


}
