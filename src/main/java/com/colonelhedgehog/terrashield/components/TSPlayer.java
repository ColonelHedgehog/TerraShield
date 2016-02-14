package com.colonelhedgehog.terrashield.components;

import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class TSPlayer
{
    private UUID uuid;

    private TSLocation currLoc1;
    private TSLocation currLoc2;

    public TSPlayer(Player player)
    {
        this.uuid = player.getUniqueId();
    }

    public Player toPlayer()
    {
        return Bukkit.getPlayer(this.uuid);
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public TSLocation getCurrentLocation1()
    {
        return currLoc1;
    }

    public void setCurrentLocation1(TSLocation currLoc1)
    {
        this.currLoc1 = currLoc1;
    }

    public TSLocation getCurrentLocation2()
    {
        return currLoc2;
    }

    public void setCurrentLocation2(TSLocation currLoc2)
    {
        this.currLoc2 = currLoc2;
    }
}
