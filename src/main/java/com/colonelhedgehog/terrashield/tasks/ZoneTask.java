package com.colonelhedgehog.terrashield.tasks;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneRole;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/18/16.
 */
public class ZoneTask extends BukkitRunnable
{
    private TerraShield instance;
    private ZoneHandler zoneHandler;
    private TSPlayerHandler playerHandler;

    HashMap<UUID, TSLocation> locations;

    public ZoneTask(TerraShield instance)
    {
        this.instance = instance;
        this.zoneHandler = instance.getZoneHandler();
        this.playerHandler = instance.getTSPlayerHandler();
        this.locations = new HashMap<>();
    }

    @Override
    public void run()
    {
        for (final Player online : Bukkit.getOnlinePlayers())
        {
            UUID uuid = online.getUniqueId();

            final TSLocation location = new TSLocation(online.getLocation());

            final TSLocation oldLocation = locations.get(uuid) == null ? location : locations.get(uuid);

            locations.put(uuid, oldLocation);

            for (Zone zone : zoneHandler.getAllZones())
            {
                if (zoneHandler.isPointInZone(zone, location))
                {
                    final TSPlayer player = playerHandler.getTSPlayer(uuid);

                    ZoneRole role = zone.getZoneRole(player);

                    if (!zone.getZoneFlagSet().getZoneFlagByName("enter").getForRole(role))
                    {
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if(oldLocation.equals(location))
                                {
                                    online.sendMessage(TerraShield.Prefix + "§cYou're not allowed to enter here! Leave!");
                                }

                                online.damage(2);
                            }
                        }.runTask(instance);
                    }

                    break;
                }
            }
        }
    }
}
