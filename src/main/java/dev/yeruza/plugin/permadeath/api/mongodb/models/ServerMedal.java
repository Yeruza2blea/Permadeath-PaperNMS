package dev.yeruza.plugin.permadeath.api.mongodb.models;

import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Color;

import java.util.List;

public record ServerMedal(String name, List<String> description, char charId, Color color) implements MongoModel {

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> aClass, CodecRegistry codecRegistry) {
        return null;
    }
}
