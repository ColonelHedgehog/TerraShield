package com.colonelhedgehog.terrashield.commands;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.TSZoneMember;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.components.zone.ZoneRole;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.handlers.TSPlayerHandler;
import com.colonelhedgehog.terrashield.handlers.ZoneHandler;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, final String label, final String[] args)
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
            if (!sender.hasPermission("terrashield.command.tool"))
            {
                sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " tool§c!");
                return false;
            }

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
                if (!sender.hasPermission("terrashield.command.tool.set"))
                {
                    sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " tool set§c!");
                    return false;
                }

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
            if (!sender.hasPermission("terrashield.command.zone"))
            {
                sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone§c!");
                return false;
            }

            if (args.length > 1)
            {
                if (args[1].equalsIgnoreCase("create"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.create"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone create§c!");
                        return false;
                    }

                    final String name;

                    if (args.length > 2)
                    {
                        name = args[2].replace("_", " ");
                    }
                    else
                    {
                        player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease specify a name! §e/" + label + "zone create <name_with_spaces>");
                        return false;
                    }

                    TSPlayerHandler handler = plugin.getTSPlayerHandler();
                    final TSPlayer tsPlayer = handler.getTSPlayer(player);

                    if (tsPlayer == null || tsPlayer.getCurrentLocation1() == null || tsPlayer.getCurrentLocation2() == null)
                    {
                        player.sendMessage(TerraShield.Prefix + "§4Error: §cYou haven't made a valid selection yet. Please use §e/ts tool §cand mark a selection.");
                        return false;
                    }


                    if (tsPlayer.isSelecting())
                    {
                        player.sendMessage("§4Error: §cUnable to complete this command right now. Try again in a second.");
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
                                tsPlayer.setSelecting(false);
                                return;
                            }

                            player.sendMessage(TerraShield.Prefix + "§eValidating zone. It will be checked against other zones nearby to be sure it's not overlapping any of them. This may take a little bit.");


                            for (Zone zone : zoneHandler.getZonesByTSPlayer(tsPlayer, true))
                            {
                                if (zone.getName().equalsIgnoreCase(name))
                                {
                                    player.sendMessage(TerraShield.Prefix + "§cSorry, another zone by that name has already been created by you.");
                                    tsPlayer.setSelecting(false);
                                    return;
                                }
                            }

                            if (!zoneHandler.verifyCanCreate(location1, location2))
                            {
                                player.sendMessage(TerraShield.Prefix + "§cSorry, this zone overlaps another zone!");
                                tsPlayer.setSelecting(false);
                                return;
                            }

                            Zone zone = new Zone(null, location1, location2); // Pass UUID as null. It will make one for us.
                            TSZoneMember zoneMember = new TSZoneMember(tsPlayer, zone);
                            zoneMember.setRole(ZoneRole.OWNER);
                            zone.addZoneMember(zoneMember);
                            zone.setName(name);

                            zoneHandler.loadZone(zone);
                            player.sendMessage("§aThis zone does not overlap another zone. It has been created!");
                            tsPlayer.setSelecting(false);

                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equalsIgnoreCase("delete"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.delete"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone delete§c!");
                        return false;
                    }

                    final String name;

                    if (args.length > 2)
                    {
                        name = args[2].replace("_", " ");
                    }
                    else
                    {
                        player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease specify a name! §e/" + label + " zone create <name with spaces>");

                        return false;
                    }

                    final UUID uuid = player.getUniqueId();

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            TSPlayerHandler tsPlayerHandler = plugin.getTSPlayerHandler();
                            TSPlayer tsPlayer = tsPlayerHandler.getTSPlayer(uuid);

                            Zone zone = zoneHandler.getZoneByTSPlayerAndName(tsPlayer, name);

                            if (zone == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo zone with a name starting with \"§e" + name + "§c\" was found!");
                                return;
                            }

                            zoneHandler.removeZone(zone);
                            player.sendMessage(TerraShield.Prefix + "§aZone \"§e" + zone.getName() + "§a\" was successfully deleted.");
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equals("list"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.list"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone list§c!");
                        return false;
                    }

                    final TSPlayerHandler tsPlayerHandler = plugin.getTSPlayerHandler();

                    final UUID uuid = player.getUniqueId();

                    final HashMap<UUID, String> worldNames = new HashMap<>();

                    //Bukkit.broadcastMessage("Testing for /ts zone list");

                    for (World world : Bukkit.getWorlds())
                    {
                        worldNames.put(world.getUID(), world.getName());
                    }

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {

                                TSPlayer tsPlayer = tsPlayerHandler.getTSPlayer(uuid);

                                //Bukkit.broadcastMessage("Got TS player: " + tsPlayer);

                                final List<Zone> zones = new ArrayList<>(zoneHandler.getZonesByTSPlayer(tsPlayer, false));

                                //Bukkit.broadcastMessage("Got zones: " + zones);

                                if (zones.isEmpty())
                                {
                                    player.sendMessage(TerraShield.Prefix + "§4Error: §cNo zones found!");
                                    //Bukkit.broadcastMessage("Zones empty!");

                                    return;
                                }

                                player.sendMessage("§a§lYour Zones: §m----------------------------------------------------");

                                for (Zone zone : zones)
                                {
                                    TSLocation location1 = zone.getStartLocation();
                                    TSLocation location2 = zone.getEndLocation();

                                    UUID worldUUID = zone.getStartLocation().getWorldUID();
                                    player.sendMessage("§8- §9Role: §b" + zone.getZoneRole(tsPlayer).toString() + "§9: \"§b" + zone.getName() + "§9\" in §b" + worldNames.get(worldUUID) + "§9, §dfrom §9" +
                                            "§9X1: §b" + location1.getX() + "§9, " +
                                            "Y1: §b" + location1.getY() + "§9, " +
                                            "Z1: §b" + location1.getZ() + "§9 " +
                                            " §dto " +
                                            "X2: §b" + location2.getX() + "§9, " +
                                            "Y2: §b" + location2.getY() + "§9, " +
                                            "Z2: §b" + location2.getZ() + "§9.");
                                }
                            }
                            catch (ConcurrentModificationException cme)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cUpdating some information, please wait a minute before using this command again.");
                            }
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equalsIgnoreCase("info"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.info"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone info§c!");
                        return false;
                    }

                    final UUID uuid = player.getUniqueId();
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {

                            String name;

                            if (args.length > 2)
                            {
                                name = args[2].replace("_", " ");
                            }
                            else
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease specify a zone name! §e/" + label + " zone info <name_with_spaces>");

                                return;
                            }

                            TSPlayer tsPlayer = plugin.getTSPlayerHandler().getTSPlayer(uuid);

                            Zone zone = zoneHandler.getZoneByTSPlayerAndName(tsPlayer, name);

                            if (zone == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo zone by this name was found!");
                                return;
                            }

                            TSLocation location1 = zone.getStartLocation();
                            TSLocation location2 = zone.getEndLocation();

                            player.sendMessage("§aName: §e" + zone.getName());
                            player.sendMessage("X1: §b" + location1.getX() + "§9, " +
                                    "Y1: §b" + location1.getY() + "§9, " +
                                    "Z1: §b" + location1.getZ() + "§9 " +
                                    " §dto " +
                                    "X2: §b" + location2.getX() + "§9, " +
                                    "Y2: §b" + location2.getY() + "§9, " +
                                    "Z2: §b" + location2.getZ() + "§9.");

                            player.sendMessage("§aMembers:");

                            for (TSZoneMember member : zone.getZoneMembers())
                            {
                                String color = "§e";

                                if (member.getPlayer().getUUID().equals(uuid))
                                {
                                    color = "§b";
                                }

                                player.sendMessage("§8- " + color + member.getPlayer().getUUID().toString() + "§6, §e" + member.getRole().name());
                            }
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equals("flag"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.flag"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone flag§c!");
                        return false;
                    }

                    final UUID uuid = player.getUniqueId();
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {

                            String name;

                            if (args.length > 2)
                            {
                                name = args[2].replace("_", " ");
                            }
                            else
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease specify a zone name! §e/" + label + " zone flag <name_with_spaces> <flag name> <role> <true/false>");

                                return;
                            }

                            TSPlayer tsPlayer = plugin.getTSPlayerHandler().getTSPlayer(uuid);

                            Zone zone = zoneHandler.getZoneByTSPlayerAndName(tsPlayer, name);

                            if (zone == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo zone by this name was found!");
                                return;
                            }

                            if (args.length <= 5)
                            {
                                //player.sendMessage(TerraShield.Prefix + "§4Error: §cPlease also specify a flag name, player role, and setting (true/false)! §e/" + label + " zone flag <name with spaces> <flag name> <role> <true/false>");
                                printTable(player, zone);
                                return;
                            }

                            String flag = args[3].toLowerCase();
                            ZoneFlagSet.ZoneFlag zoneFlag = zone.getZoneFlagSet().getZoneFlagByName(flag);

                            if (zoneFlag == null)
                            {
                                Set<String> zoneFlags = zone.getZoneFlagSet().getZoneFlagNames();
                                String options = Arrays.toString(zoneFlags.toArray(new String[zoneFlags.size()]));

                                player.sendMessage("§4Error: §cNo zone flag \"§e" + flag + "§c\" was found! Options: " + options);
                                return;
                            }

                            String role = args[4].toUpperCase();

                            ZoneRole zoneRole = null;

                            try
                            {
                                zoneRole = ZoneRole.valueOf(role);
                            }
                            catch (IllegalArgumentException ignored)
                            {

                            }

                            if (zoneRole == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo zone role by the name of \"§e" + role + "§c\" could be found!");
                                return;
                            }

                            boolean setting;

                            try
                            {
                                setting = Boolean.parseBoolean(args[5]);
                            }
                            catch (Exception e)
                            {
                                player.sendMessage("§4Error: §cWhat is \"§e" + args[5] + "§c\"? Try one of these: §6" + Arrays.toString(ZoneRole.values()));
                                return;
                            }

                            zoneFlag.set(zoneRole, setting);

                            player.sendMessage(TerraShield.Prefix + "§aSuccess! §e" + flag + " §aset to §e" + setting + " §afor §e" + role + "S§a.");
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equalsIgnoreCase("add"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.add"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone add§c!");
                        return false;
                    }

                    final UUID uuid = player.getUniqueId();
                    final TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            if (args.length <= 4)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cToo few arguments! Usage: §e/ts zone add <zone_name> <player> <role>");
                                return;
                            }

                            String zoneName = args[2];
                            String playerName = args[3];
                            String roleName = args[4];

                            Player bplayer = Bukkit.getPlayer(playerName);

                            if (bplayer == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo player by that name was found!");
                                return;
                            }

                            TSPlayer added = playerHandler.getTSPlayer(bplayer);
                            TSPlayer tsPlayer = playerHandler.getTSPlayer(uuid);
                            ZoneRole role = ZoneRole.valueOf(roleName.toUpperCase());

                            if (role == null || role == ZoneRole.ALL)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cInvalid role name!");
                                return;
                            }

                            Zone zone = zoneHandler.getZoneByTSPlayerAndName(tsPlayer, zoneName);

                            if (zone == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§cNo zone was found by this name.");
                                return;
                            }

                            if (zone.getZoneRole(tsPlayer) != null && zone.getZoneRole(tsPlayer) != ZoneRole.ALL)
                            {
                                TSZoneMember member = zone.getZoneMemberByTSPlayer(added);

                                member.setRole(role);
                                player.sendMessage(TerraShield.Prefix + "§aZone role for \"§e" + playerName + "§a\" has been updated!");
                                return;
                            }

                            TSZoneMember member = new TSZoneMember(added, zone);
                            member.setRole(role);
                            zone.addZoneMember(member);

                            player.sendMessage(TerraShield.Prefix + "§a\"§e" + playerName + "§a\" has been added to the zone as a: §6§l" + role);
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equalsIgnoreCase("remove"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.remove"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone remove§c!");
                        return false;
                    }

                    final UUID uuid = player.getUniqueId();

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {

                            TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

                            if (args.length <= 3)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cToo few arguments! Usage: §e/ts zone remove <zone_name> <player>");
                                return;
                            }

                            String zoneName = args[2];
                            String playerName = args[3];

                            Player bplayer = Bukkit.getPlayer(playerName);

                            if (bplayer == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo player by that name was found!");
                                return;
                            }

                            TSPlayer removed = playerHandler.getTSPlayer(bplayer);
                            TSPlayer tsPlayer = playerHandler.getTSPlayer(uuid);

                            Zone zone = zoneHandler.getZoneByTSPlayerAndName(tsPlayer, zoneName);

                            if (zone == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§cNo zone was found by this name.");
                                return;
                            }

                            ZoneRole zoneRole = zone.getZoneRole(removed);
                            if (zoneRole == null || zoneRole == ZoneRole.ALL || zoneRole == ZoneRole.BANNED)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cPlayer is not a member of your zone. Did you mean §e/ts zone ban§c?");
                                return;
                            }

                            zone.removeZoneMember(zone.getZoneMemberByTSPlayer(removed));
                            player.sendMessage(TerraShield.Prefix + "§aPlayer was removed from your zone.");
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else if (args[1].equalsIgnoreCase("ban"))
                {
                    if (!sender.hasPermission("terrashield.command.zone.ban"))
                    {
                        sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " zone ban§c!");
                        return false;
                    }

                    final UUID uuid = player.getUniqueId();

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {

                            TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

                            if (args.length <= 3)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cToo few arguments! Usage: §e/ts zone ban <zone_name> <player>");
                                return;
                            }

                            String zoneName = args[2];
                            String playerName = args[3];

                            Player bplayer = Bukkit.getPlayer(playerName);

                            if (bplayer == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cNo player by that name was found!");
                                return;
                            }

                            TSPlayer banned = playerHandler.getTSPlayer(bplayer);
                            TSPlayer tsPlayer = playerHandler.getTSPlayer(uuid);

                            Zone zone = zoneHandler.getZoneByTSPlayerAndName(tsPlayer, zoneName);

                            if (zone == null)
                            {
                                player.sendMessage(TerraShield.Prefix + "§cNo zone was found by this name.");
                                return;
                            }

                            ZoneRole zoneRole = zone.getZoneRole(banned);
                            if (zoneRole != null && zoneRole != ZoneRole.ALL && zoneRole != ZoneRole.BANNED)
                            {
                                player.sendMessage(TerraShield.Prefix + "§4Error: §cThis player is added to your zone. Use §e/" + label + " zone remove§c first.");
                                return;
                            }

                            if (zoneRole == ZoneRole.BANNED)
                            {
                                zone.removeZoneMember(zone.getZoneMemberByTSPlayer(banned));

                                player.sendMessage(TerraShield.Prefix + "§aPlayer was §dun§abanned from your zone.");
                                bplayer.sendMessage(TerraShield.Prefix + "§dYou were unbanned from §e" + zone.getName() + "§d.");
                            }
                            else
                            {
                                TSZoneMember member = new TSZoneMember(banned, zone);
                                member.setRole(ZoneRole.BANNED);

                                zone.addZoneMember(member);
                                player.sendMessage(TerraShield.Prefix + "§aPlayer was banned from your zone.");
                                bplayer.sendMessage(TerraShield.Prefix + "§cYou were banned from §e" + zone.getName() + "§c.");
                            }
                        }
                    }.runTaskAsynchronously(plugin);
                }
                else
                {
                    player.sendMessage("§4Error: §cNo zone command found! Try using §e/" + label + " help §cto find the command you're looking for.");
                }
            }
            else
            {
                player.sendMessage(TerraShield.Prefix + "§4Error: §cToo few arguments! Use §e/" + label + " help §cfor help.");
            }
        }
        else if (args[0].equalsIgnoreCase("help"))
        {
            if (!sender.hasPermission("terrashield.command.help"))
            {
                sender.sendMessage(TerraShield.Prefix + "§4Error: §cYou're not allowed to use §e/" + label + " help§c!");
                return false;
            }

            if (args.length > 1)
            {
                int num = 1;

                try
                {
                    num = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException nfe)
                {
                    player.sendMessage(TerraShield.Prefix + "§eUnknown page requested.");
                }
                sendHelp(player, num);
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

    private void printTable(Player player, Zone zone)
    {
        List<ZoneFlagSet.ZoneFlag> flags = new ArrayList<>(zone.getZoneFlagSet().getZoneFlags());

        final Object[][] table = new String[flags.size() + 1][];


        table[0] = new String[]{"§b§l§nFLAG NAME", "§c§l§nADMIN", "§e§l§nMEMBER", "§a§l§nALL"};

        for (int i = 0; i < flags.size(); i++)
        {
            ZoneFlagSet.ZoneFlag flag = flags.get(i);
            String admin = (flag.isAdmins() ? "§a" : "§c") + flag.isAdmins();
            String members = (flag.isMembers() ? "§a" : "§c") + flag.isMembers();
            String all = (flag.isAllPlayers() ? "§a" : "§c") + flag.isAllPlayers();

            table[i + 1] = new String[]{flag.getName(), admin, members, all};
        }

        for (final Object[] row : table)
        {
            player.sendMessage(String.format("%15s%15s%15s%15s", row));
        }
    }

    private void sendHelp(final Player player, final int page)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                player.sendMessage("§6§llUsages of §e/terrashield §8- §5Page §d" + page + " §5of §d2");

                if (page == 1)
                {
                    player.sendMessage("§8- §a/ts zone create <name_with_spaces> §8- §eCreates §aa TerraShield zone. Use a _ for a space.");
                    player.sendMessage("§8- §a/ts zone delete <name_with_spaces> §8- §dRemoves §ea TerraShield zone. Use a _ for a space.");
                    player.sendMessage("§8- §a/ts zone list §8- §eLists all the zones you have access to.");
                    player.sendMessage("§8- §a/ts zone info <zone_name> §8- §eGives information about a zone.");
                }
                else if (page == 2)
                {
                    player.sendMessage("§8- §a/ts zone flag <zone_name> §8- §eLists all the flags in a you own zone.");
                    player.sendMessage("§8- §a/ts zone flag <zone_name> <flag> <role> <value> §8- §eSets the value of a specific zone.");
                    player.sendMessage("§8- §a/ts zone add <zone_name> <player> <role> §8- §eAdds a player to your zone.");
                    player.sendMessage("§8- §a/ts zone remove <zone_name> <player> §8- §eRemoves a player from your zone.");
                }
                else
                {
                    player.sendMessage(TerraShield.Prefix + "§ePage not found!");
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
