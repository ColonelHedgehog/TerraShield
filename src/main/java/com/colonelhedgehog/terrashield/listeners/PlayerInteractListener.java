package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        final Player bplayer = event.getPlayer();

        if (!zoneHandler.isZoneMarkerTool(event.getItem()))
        {
            if (event.getClickedBlock() == null || bplayer.hasMetadata("exemptInt"))
            {
                if (bplayer.hasMetadata("exemptInt"))
                {
                    bplayer.removeMetadata("exemptInt", plugin);
                }
                return;
            }

            final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(bplayer);

            final Block block = event.getClickedBlock();
            final BlockFace face = event.getBlockFace();
            final ItemStack item = event.getItem();
            final Action action = event.getAction();
            final TSLocation location = new TSLocation(block.getLocation());

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    for (final Zone zone : zoneHandler.getZones())
                    {
                        if (zoneHandler.isPointInZone(zone, location))
                        {
                            final ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("interact");


                            if (!flag.getForRole(zone.getZoneRole(player)))
                            {
                                bplayer.sendMessage(TerraShield.Prefix + "§cYou can't interact here!");
                            }
                            else
                            {
                                new BukkitRunnable()
                                {

                                    @Override
                                    public void run()
                                    {
                                        bplayer.setMetadata("exemptInt", new FixedMetadataValue(plugin, true));

                                        // Rerun with "exempt" message, so this is all skipped.
                                        Bukkit.getPluginManager().callEvent(new PlayerInteractEvent(bplayer, action, item, block, face));
                                    }
                                }.runTask(plugin);
                            }


                            return;
                        }
                    }
                }
            }.runTaskAsynchronously(plugin);

            return;
        }

        if (!bplayer.hasPermission("terrashield.tool"))
        {
            bplayer.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use the §6Zone Marker Tool§c!");
            return;
        }

        event.setCancelled(true);

        int maxZoneCount = plugin.getConfig().getInt("Settings.Limits.Count");
        TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

        UUID uuid = event.getPlayer().getUniqueId();
        TSPlayer tsPlayer = playerHandler.getTSPlayer(uuid);

        boolean hasZones = true;

        if (tsPlayer == null)
        {
            hasZones = false;
            tsPlayer = new TSPlayer(uuid);
            //plugin.getLogger().info("TSPlayer null creating new!");

            playerHandler.addTSPlayer(tsPlayer);
        }

        List<Zone> zones = new ArrayList<>();

        // Prevent unnecessary looping.
        if (hasZones)
        {
            zones = zoneHandler.getZonesByTSPlayer(tsPlayer, true);
        }

        boolean tooMany = hasZones && zones.size() > maxZoneCount;

        if (tooMany)
        {
            bplayer.sendMessage(TerraShield.Prefix + "§4Error: §cYou own too many zones right now. Please remove one of your existing zones to make a new one.");

            return;
        }

        TSLocation location1 = tsPlayer.getCurrentLocation1();
        TSLocation location2 = tsPlayer.getCurrentLocation2();

        if (event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            location1 = new TSLocation(bplayer.getLocation());
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            location1 = new TSLocation(event.getClickedBlock().getLocation());
        }
        else if (event.getAction() == Action.LEFT_CLICK_AIR)
        {
            location2 = new TSLocation(bplayer.getLocation());
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            location2 = new TSLocation(event.getClickedBlock().getLocation());
        }

        tsPlayer.setCurrentLocation1(location1);
        tsPlayer.setCurrentLocation2(location2);

        bplayer.sendMessage(TerraShield.Prefix + "§e§lLocation set. §aCurrent zone cuboid selection is:");

        boolean bothNotSet = false;

        if (location1 != null)
        {
            bplayer.sendMessage(TerraShield.Prefix + "§8- §6Corner #1: §eX: §a" + location1.getX() + "§e, Y: §a" + location1.getY() + "§e, Z: §a" + location1.getZ());
        }
        else
        {
            bplayer.sendMessage(TerraShield.Prefix + "§8- §4Corner #1: §cNot set. §eRight click §cwith the §6Zone Marker Tool§c.");
            bothNotSet = true;
        }

        if (location2 != null)
        {
            bplayer.sendMessage(TerraShield.Prefix + "§8- §6Corner #2: §eX: §a" + location2.getX() + "§e, Y: §a" + location2.getY() + "§e, Z: §a" + location2.getZ());
        }
        else
        {
            bplayer.sendMessage(TerraShield.Prefix + "§8- §4Corner #2: §cNot set. §eLeft click §cwith the §6Zone Marker Tool§c.");
            bothNotSet = true;
        }

        if (bothNotSet)
        {
            return;
        }

        bplayer.sendMessage(TerraShield.Prefix + "§aYou have created a zone selection. " +
                "Now you can use §e/ts zone create <name> §ato create it as a TerraShield zone.");
    }
}
