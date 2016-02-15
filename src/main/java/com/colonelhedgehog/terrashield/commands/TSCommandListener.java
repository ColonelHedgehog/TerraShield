package com.colonelhedgehog.terrashield.commands;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class TSCommandListener implements CommandExecutor
{
    private TerraShield plugin;

    public TSCommandListener(TerraShield plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args)
    {
        if (!sender.hasPermission("terrashield.command"))
        {
            sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + "§c!");
            return false;
        }

        if (!(sender instanceof Player))
        {
            sender.sendMessage(TerraShield.Prefix + "§4Error: §cThis command is meant for players only.");
            return false;
        }

        final Player player = (Player) sender;
        final ZoneHandler zoneHandler = plugin.getZoneHandler();

        if (args.length == 0)
        {
            sender.sendMessage(TerraShield.Prefix + "§4Error: §cToo few arguments. Please use §e/" + label + " help§c for help.");
            return false;
        }

        if (args[0].equalsIgnoreCase("tool"))
        {
            FileConfiguration config = plugin.getConfig();

            if (args.length == 1)
            {
                if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR)
                {
                    player.sendMessage(TerraShield.Prefix + "§4Error: §cYour hand must be free to give you this tool!");
                    return false;
                }

                ItemStack itemStack = zoneHandler.getZoneMarkerTool();
                player.setItemInHand(itemStack);
                player.sendMessage(TerraShield.Prefix + "§aYou have been given the §eZone Marker Tool§a.");

                return true;
            }

            if (args[1].equalsIgnoreCase("set"))
            {
                ItemStack hand = player.getItemInHand();

                if (hand == null)
                {
                    player.sendMessage(TerraShield.Prefix + "§4Error: §cYou have to have an item in your hand to use this command.");

                    return false;
                }

                config.set("Settings.Zone Marker Tool.Material", hand.getType());
                config.set("Settings.Zone Marker Tool.Data", hand.getDurability());
                plugin.saveConfig();

                player.sendMessage(TerraShield.Prefix + "§aThe §eZone Marker Tool §ahas been set to the item in your hand.");
            }
        }
        else if (args[0].equalsIgnoreCase("zone"))
        {
            if (args.length > 1)
            {
                if (args[1].equalsIgnoreCase("create"))
                {
                    // Todo prevent spam.

                    final String name;

                    if (args.length > 2)
                    {
                        name = flatten(2, args);
                    }
                    else
                    {
                        player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease specify a name! §e/" + label + "zone create <name with spaces>");
                        return false;
                    }

                    TSPlayerHandler handler = plugin.getTSPlayerHandler();
                    final TSPlayer tsPlayer = handler.getTSPlayer(player);

                    if (tsPlayer == null || tsPlayer.getCurrentLocation1() == null || tsPlayer.getCurrentLocation2() == null)
                    {
                        player.sendMessage(TerraShield.Prefix + "§4Error: §cYou haven't made a valid selection yet. Please use §e/ts tool §cand mark a selection.");
                        return false;
                    }

                    final TSLocation location1 = tsPlayer.getCurrentLocation1();
                    final TSLocation location2 = tsPlayer.getCurrentLocation2();

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            if (location1.getWorldUID() != location2.getWorldUID())
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cLocations of zone corners must be in the same world!");
                                return;
                            }

                            player.sendMessage(TerraShield.Prefix + "§eValidating zone. It will be checked against other zones nearby to be sure it's not overlapping any of them. This may take a little bit.");


                            for (Zone zone : zoneHandler.getZonesByTSPlayer(tsPlayer))
                            {
                                if(zone.getName().equalsIgnoreCase(name))
                                {
                                    player.sendMessage(TerraShield.Prefix + "§cSorry, another zone by that name has already been created by you.");
                                    return;
                                }
                            }

                            if (!zoneHandler.verifyCanCreate(location1, location2))
                            {
                                player.sendMessage(TerraShield.Prefix + "§cSorry, this zone overlaps another zone!");
                                return;
                            }

                            Zone zone = new Zone(null, location1, location2); // Pass UUID as null. It will make one for us.
                            zone.setName(name);

                            zoneHandler.loadZone(zone);
                            player.sendMessage("§aThis zone does not overlap another zone. It has been created!");

                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if(args[1].equalsIgnoreCase("delete"))
                {
                    String name;

                    if(args.length > 2)
                    {
                        player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease specify a name! §e/" + label + " zone create <name with spaces>");

                        name = flatten(2, args);
                    }
                    else
                    {

                        return false;
                    }
                }
                else
                {
                    player.sendMessage("§4Error: §cNo zone command found! Try using §e/" + label + " help §cto find the command you're looking for.");
                }
            }
            else
            {
                player.sendMessage("§4Error: §cToo few arguments! Try using §e/" + label + " help §cto find the command you're looking for.");
            }
        }
        else if (args[0].equalsIgnoreCase("help"))
        {
            if (args.length > 1)
            {
                sendHelp(player, 1);
                return true;
            }

            sendHelp(player, 1);
        }
        else
        {
            player.sendMessage("§4Error: §cNo sub-command found! Try using §e/" + label + " help §cto find the command you're looking for.");
        }

        return true;
    }

    private void sendHelp(Player player, int page)
    {
        --page;
    }

    private String flatten(int i, String[] args)
    {
        String flattened = "";

        for (; i < args.length; i++)
        {
            flattened += args[i] + " ";
        }

        return flattened.trim();
    }
}
