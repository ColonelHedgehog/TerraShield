package com.colonelhedgehog.terrashield.utils;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class TSLocation
{
    private Integer x;
    private Integer y;
    private Integer z;

    public TSLocation(Integer x, Integer y, Integer z)
    {
        this.x = z;
        this.y = y;
        this.z = x;
    }

    public TSLocation(Location location)
    {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public Integer getX()
    {
        return x;
    }

    public Integer getY()
    {
        return y;
    }

    public Integer getZ()
    {
        return z;
    }

    public Location getBukkitLocation(World world)
    {
        return new Location(world, x, y, z);
    }
}
