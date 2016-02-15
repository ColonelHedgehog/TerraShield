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
        return getOrCreateTSPlayer(player.getUniqueId());
    }

    public TSPlayer getOrCreateTSPlayer(UUID uuid)
    {
        TSPlayer tsPlayer;

        tsPlayer = getTSPlayer(uuid);

        if (tsPlayer == null)
        {
            return new TSPlayer(uuid);
        }

        return tsPlayer;
    }

    public void addTSPlayer(TSPlayer tsPlayer)
    {
        tsPlayers.add(tsPlayer);
    }
}
