package com.colonelhedgehog.terrashield.listeners;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/16/16.
 */
public class BlockPlaceBreakListener implements Listener
{
    private TerraShield plugin;

    public BlockPlaceBreakListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        final Block oldBlock = event.getBlock();
        final Block newBlock = event.getBlockPlaced();

        final TSLocation to = new TSLocation(newBlock.getLocation());

        final Player bplayer = event.getPlayer();
        final ItemStack hand = bplayer.getItemInHand().clone();

        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(bplayer);

        event.setCancelled(true);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (final Zone zone : zoneHandler.getZones())
                {
                    if (zoneHandler.isPointInZone(zone, to))
                    {
                        final ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("edit");

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (!flag.getForRole(zone.getZoneRole(player)))
                                {
                                    bplayer.sendMessage(TerraShield.Prefix + "§cYou're not allowed to place blocks here!");
                                }
                                else
                                {
                                    if (hand.getAmount() > 1)
                                    {
                                        hand.setAmount(hand.getAmount() - 1);
                                        bplayer.setItemInHand(hand);
                                    }
                                    else
                                    {
                                        oldBlock.getState().setType(newBlock.getState().getType());
                                        oldBlock.getState().setData(newBlock.getState().getData());
                                    }
                                }
                            }
                        }.runTask(plugin);

                        return;
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onBlockPlaceBreak(BlockBreakEvent event)
    {
        final Block broken = event.getBlock();

        final TSLocation to = new TSLocation(broken.getLocation());

        final Player bplayer = event.getPlayer();

        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        final TSPlayer player = plugin.getTSPlayerHandler().getTSPlayer(bplayer);

        final ItemStack item = bplayer.getItemInHand() == null ? new ItemStack(Material.STICK) : bplayer.getItemInHand();

        event.setCancelled(true);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (final Zone zone : zoneHandler.getZones())
                {
                    if (zoneHandler.isPointInZone(zone, to))
                    {
                        final ZoneFlagSet.ZoneFlag flag = zone.getZoneFlagSet().getZoneFlagByName("edit");

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (!flag.getForRole(zone.getZoneRole(player)))
                                {
                                    bplayer.sendMessage(TerraShield.Prefix + "§cYou're not allowed to break blocks here!");
                                }
                                else
                                {
                                    broken.breakNaturally(item);
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
