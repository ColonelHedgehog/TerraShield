package com.colonelhedgehog.terrashield.handlers;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class TSPlayerHandler
{
    private List<TSPlayer> tsPlayers;

    public List<TSPlayer> getTSPlayers()
    {
        return tsPlayers;
    }

    public TSPlayer getTSPlayer(Player player)
    {
        UUID uuid = player.getUniqueId();

        for (TSPlayer tsPlayer : tsPlayers)
        {
            if (tsPlayer.getUUID() == uuid)
            {
                return tsPlayer;
            }
        }

        return null;
    }

    public TSPlayer getOrCreateTSPlayer(Player player)
    {
        TSPlayer tsPlayer;

        tsPlayer = getTSPlayer(player);

        if(tsPlayer == null)
        {
            return new TSPlayer(player);
        }

        return tsPlayer;
    }

    public void addTSPlayer(TSPlayer tsPlayer)
    {
        tsPlayers.add(tsPlayer);
    }
}
