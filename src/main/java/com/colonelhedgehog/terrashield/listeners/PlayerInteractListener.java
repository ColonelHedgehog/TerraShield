package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class PlayerInteractListener implements Listener
{
    private TerraShield plugin;

    public PlayerInteractListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        ZoneHandler zoneHandler = plugin.getZoneHandler();

        if(!zoneHandler.isZoneMarkerTool(event.getItem()))
        {
            return;
        }

        event.setCancelled(true);

        int maxZoneCount = plugin.getConfig().getInt("Settings.Limits.Count");
        TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

        Player player = event.getPlayer();
        TSPlayer tsPlayer = playerHandler.getTSPlayer(event.getPlayer());

        boolean hasZones = true;

        if (tsPlayer == null)
        {
            hasZones = false;
            tsPlayer = new TSPlayer(event.getPlayer());

            playerHandler.addTSPlayer(tsPlayer);
        }

        List<Zone> zones = new ArrayList<>();

        // Prevent unnecessary looping.
        if (hasZones)
        {
            zones = zoneHandler.getZonesByTSPlayer(tsPlayer);
        }

        boolean tooMany = hasZones && zones.size() > maxZoneCount;

        if (tooMany)
        {
            player.sendMessage(TerraShield.Prefix + "§4Error: §cYou own too many zones right now. Please remove one of your existing zones to make a new one.");

            return;
        }

        TSLocation location1 = tsPlayer.getCurrentLocation1();
        TSLocation location2 = tsPlayer.getCurrentLocation2();

        if (event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            location1 = new TSLocation(player.getLocation());
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            location1 = new TSLocation(event.getClickedBlock().getLocation());
        }
        else if (event.getAction() == Action.LEFT_CLICK_AIR)
        {
            location2 = new TSLocation(player.getLocation());
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            location2 = new TSLocation(event.getClickedBlock().getLocation());
        }

        if(location1 == null)
        {
            return;
        }
        else if(location2 == null)
        {
            return;
        }

        player.sendMessage(TerraShield.Prefix + "§aYou have created a zone selection. Now you can use §e/ts zone create <name> §ato");
    }
}
