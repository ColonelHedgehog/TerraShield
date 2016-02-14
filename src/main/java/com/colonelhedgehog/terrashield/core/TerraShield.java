package com.colonelhedgehog.terrashield.core;

import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.listeners.InventoryClickListener;
import com.colonelhedgehog.terrashield.listeners.PlayerInteractListener;
import com.colonelhedgehog.terrashield.mongodb.Driver;
import com.mongodb.MongoCredential;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
    private com.colonelhedgehog.terrashield.handlers.TSPlayerHandler tsPlayerHandler;

    @Override
    public void onEnable()
    {
        instance = this;
        zoneHandler = new ZoneHandler(this);
        tsPlayerHandler = new TSPlayerHandler();

        registerEvents();
        connectToMongoDB();
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
            }
        }.runTaskAsynchronously(this);
    }

    private void registerEvents()
    {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryClickListener(this), this);
        manager.registerEvents(new PlayerInteractListener(this), this);
    }

    private void registerCommands()
    {
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
}
