package com.colonelhedgehog.terrashield.core;

import com.colonelhedgehog.terrashield.commands.TSCommandListener;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.listeners.*;
import com.colonelhedgehog.terrashield.mongodb.Driver;
import com.colonelhedgehog.terrashield.tasks.ZoneTask;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class TerraShield extends JavaPlugin
{
    private static TerraShield instance;
    public static String Prefix = "§8[§fTerra§aShield§8] » §f";
    private Driver driver;
    private ZoneHandler zoneHandler;
    private TSPlayerHandler tsPlayerHandler;
    private ZoneTask zoneTask;

    @Override
    public void onEnable()
    {
        instance = this;
        tsPlayerHandler = new TSPlayerHandler();

        getConfig().options().copyDefaults(true);
        if (!new File(getDataFolder() + "/config.yml").exists())
        {
            getLogger().info("Saved a new config.yml!");
            saveDefaultConfig();
        }

        registerEvents();
        registerCommands();
        connectToMongoDB();

        // Initialized in connectToMongoDB() -> zoneHandler = new ZoneHandler(this, driver);
        // Same for running ZoneTask
    }

    @Override
    public void onDisable()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final long time = System.nanoTime();

                MongoCollection<Document> zones = driver.getDatabase().getCollection("zones");

                for (Zone zone : zoneHandler.getAllZones())
                {
                    zoneHandler.saveZoneToCollection(zones, zone, time);
                }

                Document query = new Document();
                Document condition = new Document();

                condition.put("$not", new Document("$gte", time));
                query.put("time", condition);

                // Delete any "stragglers." If it has not been saved (and thus its time is not being updated)
                // then we can assume it has been deleted. So bye bye!

                driver.getDatabase().getCollection("zones").deleteMany(query);
                driver.close();
            }
        });

        thread.start();
    }

    private void connectToMongoDB()
    {
        FileConfiguration config = getConfig();

        final String username = config.getString("MongoDB.Username");
        final String database = config.getString("MongoDB.Database");
        final String password = config.getString("MongoDB.Password");

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
                driver = new Driver(mongoCredential);

                MongoDatabase ts_m = driver.getDatabase();

                MongoIterable<String> collectionNames = ts_m.listCollectionNames();

                boolean exists = false;

                for (final String name : collectionNames)
                {
                    if (name.equalsIgnoreCase("zones"))
                    {
                        exists = true;
                        break;
                    }
                }

                if (exists)
                {
                    zoneHandler = new ZoneHandler(instance, driver);
                    zoneTask = new ZoneTask(instance);
                    zoneTask.runTaskTimerAsynchronously(instance, 0, 3);
                    zoneHandler.loadZonesFromCollection(ts_m.getCollection("zones"));
                }
                else
                {
                    getLogger().info("No collection for \"zones\" found. Creating a new one!");
                    ts_m.createCollection("zones");
                }
            }
        }.runTaskAsynchronously(this);
    }

    private void registerEvents()
    {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryClickListener(this), this);
        manager.registerEvents(new PlayerInteractListener(this), this);
        manager.registerEvents(new PlayerDropItemListener(this), this);
        manager.registerEvents(new EntityDamageListener(this), this);
        manager.registerEvents(new EntityDamageByEntityListener(this), this);
        manager.registerEvents(new PlayerJoinListener(this), this);
        manager.registerEvents(new PlayerTeleportListener(this), this);
        manager.registerEvents(new LiquidFlowListener(this), this);
        manager.registerEvents(new BlockPlaceBreakListener(this), this);
        manager.registerEvents(new AsyncPlayerChatListener(this), this);
        manager.registerEvents(new EntityExplodeListener(this), this);
    }

    private void registerCommands()
    {
        getServer().getPluginCommand("terrashield").setExecutor(new TSCommandListener(this));
    }

    public static TerraShield getInstance()
    {
        return instance;
    }

    public Driver getDriver()
    {
        return driver;
    }

    public ZoneHandler getZoneHandler()
    {
        return zoneHandler;
    }

    public TSPlayerHandler getTSPlayerHandler()
    {
        return tsPlayerHandler;
    }

    public ZoneTask getZoneTask()
    {
        return zoneTask;
    }
}
