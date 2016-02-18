package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class PlayerMoveListener implements Listener
{
    private TerraShield plugin;

    public PlayerMoveListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    // I hate this event :(
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final TSLocation to = new TSLocation(event.getTo());

        final Player bplayer = event.getPlayer();
        final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(bplayer.getUniqueId());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ZoneHandler zoneHandler = plugin.getZoneHandler();

                for (Zone zone : zoneHandler.getZones())
                {
                    if(zoneHandler.isPointInZone(zone, to))
                    {
                        ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("enter");

                        if (!flag.getForRole(zone.getZoneRole(player)))
                        {
                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    bplayer.sendMessage(TerraShield.Prefix + "§cYou're not allowed to enter here!");
                                    bplayer.damage(1);
                                }
                            }.runTask(plugin);
                        }

                        return;
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
