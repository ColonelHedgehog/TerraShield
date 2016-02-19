package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class PlayerTeleportListener implements Listener
{
    private TerraShield plugin;

    public PlayerTeleportListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event)
    {
        final Location location = event.getFrom();
        final TSLocation to = new TSLocation(event.getTo());

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
        {
            return;
        }

        final Player bplayer = event.getPlayer();

        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(bplayer);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Zone zone : zoneHandler.getAllZones())
                {
                    if (zoneHandler.isPointInZone(zone, to))
                    {
                        ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("endpearl");

                        if (!flag.getForRole(zone.getZoneRole(player)))
                        {
                            bplayer.sendMessage(TerraShield.Prefix + "§cYou're not allowed to throw end-pearls items here!");

                            new BukkitRunnable()
                            {
                                @Override
                                public void run()
                                {
                                    bplayer.teleport(location);
                                    bplayer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
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
