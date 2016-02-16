package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class LiquidFlowListener implements Listener
{
    private TerraShield plugin;

    public LiquidFlowListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLiquidFlow(final BlockFromToEvent event)
    {
        final Block block = event.getToBlock();

        if (block.isLiquid())
        {
            final ZoneHandler zoneHandler = plugin.getZoneHandler();

            final TSLocation location = new TSLocation(block.getLocation());
            //final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(event.get);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    for (Zone zone : zoneHandler.getZones())
                    {
                        if (zoneHandler.isPointInZone(zone, location))
                        {
                            ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("liquid_flow");

                            if (!flag.isAllPlayers() || !flag.isMembers() || !flag.isAdmins())
                            {
                                new BukkitRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        block.setType(Material.AIR);
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
}
