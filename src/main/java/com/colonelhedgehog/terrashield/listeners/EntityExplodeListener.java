package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class EntityExplodeListener implements Listener
{
    private TerraShield plugin;

    public EntityExplodeListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event)
    {
        final float size = event.getYield();
        final Location epicenter = event.getLocation();
        final TSLocation location = new TSLocation(epicenter);

        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        event.setCancelled(true);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Zone zone : zoneHandler.getZones())
                {
                    if (zoneHandler.isPointInZone(zone, location))
                    {
                        final ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("explode");

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (flag.isAdmins() && flag.isMembers() && flag.isAllPlayers())
                                {
                                    epicenter.getWorld().createExplosion(epicenter, size);
                                }
                            }
                        }.runTask(plugin);

                        return;
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
