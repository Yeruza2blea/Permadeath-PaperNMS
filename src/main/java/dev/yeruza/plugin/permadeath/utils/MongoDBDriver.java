package dev.yeruza.plugin.permadeath.utils;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.data.DataBaseManager;

public class MongoDBDriver {
    public static final int PORT = 9000;
    private final Permadeath plugin;


    private final ConnectionString connection;
    private MongoClient client;
    private MongoDatabase database;

    public MongoDBDriver(Permadeath plugin, DataBaseManager manager) {
        this.plugin = plugin;
        String url = String.format("mongodb+srv://%s:%s@%s/?retryWrites=true&w=majority&appName=%s", manager.getName(), manager.getPassword(), manager.getHost(), manager.getAppName());

        this.connection = new ConnectionString(url);
    }

    public final void start() {
        try (MongoClient client = MongoClients.create(connection)) {
            this.client = client;
            this.database = client.getDatabase(connection.getApplicationName() != null ? connection.getApplicationName() : "ServerDB");
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public ConnectionString getConnection() {
        return connection;
    }

    public MongoClient getClient() {
        return client;
    }
}
