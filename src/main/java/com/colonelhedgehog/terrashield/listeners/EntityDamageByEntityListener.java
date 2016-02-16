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
import org.bukkit.inventory.ItemStack;
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

        final ItemStack[] armor = entity.getInventory().getArmorContents();
        final ItemStack itemInHand = damager.getItemInHand();

        final TSPlayer player = playerHandler.getTSPlayer(entity);
        //final TSPlayer attacker = playerHandler.getTSPlayer(damager);

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
                                    damager.setItemInHand(itemInHand);
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
