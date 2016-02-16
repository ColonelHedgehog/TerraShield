package com.colonelhedgehog.terrashield.handlers;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class TSPlayerHandler
{
    private List<TSPlayer> tsPlayers;

    public TSPlayerHandler()
    {
        this.tsPlayers = new ArrayList<>();
    }

    public List<TSPlayer> getTSPlayers()
    {
        return tsPlayers;
    }

    public TSPlayer getTSPlayer(Player player)
    {
        return getTSPlayer(player.getUniqueId());
    }

    public TSPlayer getTSPlayer(UUID uuid)
    {
        //Bukkit.broadcastMessage("Looking for " + uuid.toString());
        for (TSPlayer tsPlayer : tsPlayers)
        {
            //Bukkit.broadcastMessage("Checking player " + tsPlayer.getUUID().toString());
            if (tsPlayer.getUUID() == uuid)
            {
                return tsPlayer;
            }
        }

        return null;
    }

    public void addTSPlayer(TSPlayer tsPlayer)
    {
        tsPlayers.add(tsPlayer);
        //Bukkit.broadcastMessage("ADDING TSPLAYER: " + tsPlayer.getUUID());
    }
}
