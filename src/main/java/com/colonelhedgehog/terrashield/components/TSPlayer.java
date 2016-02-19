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

    private TSLocation location;
    private TSLocation currLoc1;
    private TSLocation currLoc2;
    private boolean selecting;

    public TSPlayer(Player player)
    {
        this(player.getUniqueId());
    }

    public TSPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.selecting = false;
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

    public boolean isSelecting()
    {
        return selecting;
    }

    public void setSelecting(boolean selecting)
    {
        this.selecting = selecting;
    }

    public boolean isOnline(boolean convertToBplayer)
    {
        if (!convertToBplayer)
        {
            for (Player online : Bukkit.getOnlinePlayers())
            {
                if (online.getUniqueId() == uuid)
                {
                    return true;
                }
            }
        }
        else
        {
            return Bukkit.getOfflinePlayer(uuid).isOnline();
        }

        return false;
    }
}
