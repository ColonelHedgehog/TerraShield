package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class InventoryClickListener implements Listener
{
    private TerraShield plugin;

    public InventoryClickListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        ZoneHandler zoneHandler = plugin.getZoneHandler();

        ItemStack clicked = event.getCurrentItem();

        if(!zoneHandler.isZoneMarkerTool(clicked))
        {
            return;
        }

        event.setCancelled(true);

        if(event.getClick() == ClickType.DROP)
        {
            HumanEntity human = event.getWhoClicked();
            human.getInventory().remove(clicked);
        }
    }
}
