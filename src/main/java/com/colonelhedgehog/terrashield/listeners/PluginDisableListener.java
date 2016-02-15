package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.core.TerraShield;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/15/16.
 */
public class PluginDisableListener implements Listener
{
    private TerraShield plugin;

    public PluginDisableListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event)
    {
        // Event actions
    }
}
