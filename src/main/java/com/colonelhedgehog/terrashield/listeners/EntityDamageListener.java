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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/15/16.
 */
public class EntityDamageListener implements Listener
{
    private TerraShield plugin;

    public EntityDamageListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(final EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
        {
            return;
        }

        final Player entity = (Player) event.getEntity();
        final TSLocation location = new TSLocation(entity.getLocation());
        final ZoneHandler zoneHandler = plugin.getZoneHandler();
        final TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

        final ItemStack[] armor = entity.getInventory().getArmorContents();

        final TSPlayer player = playerHandler.getTSPlayer(entity);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Zone zone : zoneHandler.getZones())
                {
                    if (zoneHandler.isPointInZone(zone, location))
                    {
                        ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("damage");

                        if (flag.getForRole(zone.getZoneRole(player)))
                        {
                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    entity.setHealth(entity.getHealth() + event.getFinalDamage());
                                    entity.getInventory().setArmorContents(armor);
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
