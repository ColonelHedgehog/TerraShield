package com.colonelhedgehog.terrashield.mongodb;

import com.colonelhedgehog.terrashield.core.TerraShield;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.logging.Logger;

/**
 * @author Funergy
 */
public class Driver
{
    private MongoClient mc;
    private MongoDatabase terrashield_db;

    public Driver(MongoCredential mongoCredential)
    {
        TerraShield plugin = TerraShield.getInstance();
        Logger logger = plugin.getLogger();
        FileConfiguration config = plugin.getConfig();

        logger.info("MongoDB: Setting up the driver...");

        String mongoIP = config.getString("MongoDB.IP");
        int mongoPort = config.getInt("MongoDB.Port");

        //String uri = "mongodb://" + username + ":" + password + "@" + config.getString("MongoDB.IP") + ":" + config.getInt("MongoDB.Port");
        //MongoClientURI mongoClientURI = new MongoClientURI(uri);
        mc = new MongoClient(new ServerAddress(mongoIP, mongoPort), Collections.singletonList(mongoCredential));
        logger.info("MongoDB: Connected to your MongoDB successfully.");
        logger.info("MongoDB: Loading databases...");
        terrashield_db = mc.getDatabase(config.getString("MongoDB.Database"));
        logger.info("MongoDB: Done!");
    }

    public void close()
    {
        final TerraShield plugin = TerraShield.getInstance();

        mc.close();
        System.out.println("[TerraShield][INFO] MongoDB: Closed connection. Good bye!");

    }

    public MongoClient getMongoClient()
    {
        return mc;
    }

    public MongoDatabase getDatabase()
    {
        return terrashield_db;
    }
}
