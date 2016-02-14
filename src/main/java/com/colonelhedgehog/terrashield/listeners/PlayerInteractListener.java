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

        if (!zoneHandler.isZoneMarkerTool(event.getItem()))
        {
            return;
        }
        Player player = event.getPlayer();

        if(!player.hasPermission("terrashield.tool"))
        {
            player.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use the §6Zone Marker Tool§c!");
            return;
        }

        event.setCancelled(true);

        int maxZoneCount = plugin.getConfig().getInt("Settings.Limits.Count");
        TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

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

        player.sendMessage("§e§lLocation set. §aCurrent zone cuboid selection is:");

        boolean bothNotSet = false;

        if (location1 != null)
        {
            player.sendMessage("§8- §6Corner #1: §eX: §a" + location1.getX() + "§e, Y: §a" + location1.getY() + "§e, Z: §a" + location1.getZ());
        }
        else
        {
            player.sendMessage("§8- §4Corner #2: §cNot set. §eRight click §cwith the §6Zone Marker Tool§c.");
            bothNotSet = true;
        }

        if (location2 != null)
        {
            player.sendMessage("§8- §6Corner #2: §eX: §a" + location2.getX() + "§e, Y: §a" + location2.getY() + "§e, Z: §a" + location2.getZ());
        }
        else
        {
            player.sendMessage("§8- §4Corner #2: §cNot set. §eLeft click §cwith the §6Zone Marker Tool§c.");
            bothNotSet = true;
        }


        if (bothNotSet)
        {
            return;
        }

        player.sendMessage(TerraShield.Prefix + "§aYou have created a zone selection. " +
                "Now you can use §e/ts zone create <name> §ato create it as a TerraShield zone.");
    }
}
