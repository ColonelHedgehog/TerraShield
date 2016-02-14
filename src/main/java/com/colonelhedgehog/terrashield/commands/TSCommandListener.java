package com.colonelhedgehog.terrashield.commands;

import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(!sender.hasPermission("terrashield.command"))
        {
            sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + "§c!");
            return false;
        }

        if (!(sender instanceof Player))
        {
            sender.sendMessage(TerraShield.Prefix + "§4Error: §cThis command is meant for players only.");
            return false;
        }

        Player player = (Player) sender;
        ZoneHandler zoneHandler = plugin.getZoneHandler();

        if (args.length == 0)
        {
            sender.sendMessage(TerraShield.Prefix + "§4Error: §cToo few arguments. Please use §e/ts help§c for help.");
            return false;
        }

        if (args[0].equalsIgnoreCase("tool"))
        {
            FileConfiguration config = plugin.getConfig();

            if (args.length == 1)
            {

                ItemStack itemStack = zoneHandler.getZoneMarkerTool();

                if (player.getItemInHand() != null)
                {
                    player.sendMessage(TerraShield.Prefix + "§4Error: §cYour hand must be free to give you this tool!");
                    return false;
                }

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
        else if (args[0].equalsIgnoreCase("help"))
        {
            if (args.length > 1)
            {
                sendHelp(player, 1);
                return true;
            }

            sendHelp(player, 1);
        }

        return true;
    }

    private void sendHelp(Player player, int page)
    {
        --page;
    }
}
