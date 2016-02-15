package com.colonelhedgehog.terrashield.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class TSLocation
{
    private UUID worldUID;
    private int x;
    private int y;
    private int z;

    /**
     * This is my ultra light-weight implementation of a location.
     * No data further than primitive integers and UUIDs is saved.
     * Excellent for quick comparisons and Async tasks.
     *
     * @param world Bukkit world.
     * @param x     X coordinates.
     * @param y     Y coordinates.
     * @param z     Z coordinates.
     */
    public TSLocation(World world, int x, int y, int z)
    {
        this(world.getUID(), x, y, z);
    }

    /**
     * A constructor if you don't have the world data.
     *
     * @param worldUID Bukkit world UUID (accessed with World#getUID())
     * @param x        X coordinates.
     * @param y        Y coordinates.
     * @param z        Z coordinates.
     */
    public TSLocation(UUID worldUID, int x, int y, int z)
    {
        this.worldUID = worldUID;
        this.x = z;
        this.y = y;
        this.z = x;
    }

    /**
     * Quick deconstructor that converts Locations to TSLocations.
     *
     * @param location Bukkit location.
     */
    public TSLocation(Location location)
    {
        this.worldUID = location.getWorld().getUID();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    /**
     * Used to access the World UID.
     *
     * @return UUID of world. Convert to a world with Bukkit#getWorld(uuid: UUID)
     */
    public UUID getWorldUID()
    {
        return worldUID;
    }

    public Location getBukkitLocation()
    {
        return new Location(Bukkit.getWorld(worldUID), x, y, z);
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public TSLocation clone()
    {
        return new TSLocation(worldUID, x, y, z);
    }
}
