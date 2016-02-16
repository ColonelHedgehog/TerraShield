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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/15/16.
 */
public class EntityDamageByEntityListener implements Listener
{
    private TerraShield plugin;

    public EntityDamageByEntityListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
        {
            return;
        }

        final Player entity = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();
        final TSLocation location = new TSLocation(entity.getLocation());
        final ZoneHandler zoneHandler = plugin.getZoneHandler();
        final TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

        final TSPlayer player = playerHandler.getTSPlayer(entity);
        //final TSPlayer attacker = playerHandler.getTSPlayer(damager);
        final double damage = event.getFinalDamage();
        event.setCancelled(true);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (final Zone zone : zoneHandler.getZones())
                {
                    if (zoneHandler.isPointInZone(zone, location))
                    {
                        final ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("damage");

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (!flag.getForRole(zone.getZoneRole(player)))
                                {
                                    damager.sendMessage(TerraShield.Prefix + "§cYou can't hurt other people here!");

                                }
                                else
                                {
                                    entity.damage(damage);
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
