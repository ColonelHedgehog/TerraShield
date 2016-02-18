package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class AsyncPlayerChatListener implements Listener
{
    private TerraShield plugin;

    public AsyncPlayerChatListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event)
    {
        final Player entity = event.getPlayer();

        final TSLocation location = new TSLocation(entity.getLocation());
        final ZoneHandler zoneHandler = plugin.getZoneHandler();
        final TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

        final TSPlayer player = playerHandler.getTSPlayer(entity);

        if (zoneHandler.getZones().isEmpty())
        {
            return;
        }

        for (Zone zone : zoneHandler.getZones())
        {
            if (zoneHandler.isPointInZone(zone, location))
            {
                ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("chat");

                if (!flag.getForRole(zone.getZoneRole(player)))
                {
                    entity.sendMessage(TerraShield.Prefix + "§cYou're not allowed to speak here!");
                    event.setCancelled(true);
                }
                return;
            }
        }
    }
}
