package dev.yeruza.plugin.permadeath.api.mongodb.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public record ServerConfig(@BsonId String _id, @BsonProperty String name) {

}
