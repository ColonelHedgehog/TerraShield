package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class PlayerJoinListener implements Listener
{
    private TerraShield plugin;

    public PlayerJoinListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        TSPlayerHandler handler = plugin.getTSPlayerHandler();

        TSPlayer player = handler.getTSPlayer(event.getPlayer());

        // I figure I can store a TSPlayer instance during the entire runtime.
        if(player == null)
        {
            handler.addTSPlayer(new TSPlayer(event.getPlayer()));
        }
    }
}
