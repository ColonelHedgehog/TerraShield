package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

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
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        Item item = event.getItemDrop();
        ZoneHandler zoneHandler = plugin.getZoneHandler();

        if(zoneHandler.isZoneMarkerTool(item.getItemStack()))
        {
            event.getItemDrop().remove();
            event.getPlayer().sendMessage(TerraShield.Prefix + "§b§oRemoved the §e§oZone Marker Tool §b§ofrom your inventory.");
        }
    }
}
