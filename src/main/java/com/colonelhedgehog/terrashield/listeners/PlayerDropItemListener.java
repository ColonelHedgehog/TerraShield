package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class PlayerDropItemListener implements Listener
{
    private TerraShield plugin;

    public PlayerDropItemListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event)
    {
        Item item = event.getItemDrop();
        final Player bplayer = event.getPlayer();

        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        if (zoneHandler.isZoneMarkerTool(item.getItemStack()))
        {
            event.getItemDrop().remove();
            bplayer.sendMessage(TerraShield.Prefix + "§b§oRemoved the §e§oZone Marker Tool §b§ofrom your inventory.");
            return;
        }

        final TSLocation location = new TSLocation(event.getItemDrop().getLocation());
        final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(bplayer);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Zone zone : zoneHandler.getAllZones())
                {
                    if (zoneHandler.isPointInZone(zone, location))
                    {
                        ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("drop");

                        if (!flag.getForRole(zone.getZoneRole(player)))
                        {
                            bplayer.sendMessage(TerraShield.Prefix + "§cYou're not allowed to drop items here!");

                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    event.getItemDrop().remove();
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
