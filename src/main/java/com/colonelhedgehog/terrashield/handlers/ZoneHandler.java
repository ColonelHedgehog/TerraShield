package com.colonelhedgehog.terrashield.handlers;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.TSZoneMember;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneFlagSet;
import com.colonelhedgehog.terrashield.components.zone.ZoneRole;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.mongodb.Driver;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class ZoneHandler
{
    private TerraShield plugin;
    private List<Zone> zones;
    private Driver driver;

    public ZoneHandler(TerraShield plugin, Driver driver)
    {
        this.plugin = plugin;
        this.driver = driver;

        this.zones = new ArrayList<>();
    }

    public List<Zone> getZones()
    {
        return this.zones;
    }

    public boolean isZoneMarkerTool(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return false;
        }

        return getZoneMarkerTool().isSimilar(itemStack);
    }

    public void loadZone(Zone zone)
    {
        if (!zones.contains(zone))
        {
            zones.add(zone);
        }

        //todo: zone.startZoneTask();
    }

    public void removeZone(Zone zone)
    {
        // todo: zone removal
        zones.remove(zone);
    }

    public ItemStack getZoneMarkerTool()
    {
        FileConfiguration config = plugin.getConfig();

        String materialString = config.getString("Settings.Zone Marker Tool.Material");
        Material material = Material.getMaterial(materialString);
        short data = (short) config.getInt("Settings.Zone Marker Tool.Byte");

        ItemStack itemStack = new ItemStack(material, 1, data);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§6§lZone Marker Tool");

        List<String> lore = new ArrayList<>();
        lore.add("§aRight-click§e to set the §bfirst §ecorner.");
        lore.add("§aLeft-click§e to set the §bsecond §ecorner.");
        lore.add("§7§oDrop this item from your inventory");
        lore.add("§7§oto remove it.");

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public List<Zone> getZonesByTSPlayer(TSPlayer tsPlayer, boolean ownerOnly)
    {
        List<Zone> zones = new ArrayList<>();

        for (Zone zone : zones)
        {
            ZoneRole role = zone.getZoneRole(tsPlayer);

            //plugin.getLogger().info("Checking for zone " + zone.getName());
            //plugin.getLogger().info("Getting zone role of player " + role);

            if (role != null && role != ZoneRole.ALL)
            {
                if (ownerOnly && role != ZoneRole.OWNER)
                {
                    continue;
                }

                //plugin.getLogger().info("Role was owner so adding");

                zones.add(zone);
            }
        }

        return zones;
    }

    /**
     * Checks zones against other ones to see if it overlaps.
     * Would be pretty stupid to make this run on the Bukkit thread lol.
     *
     * @param location1 Corner #1.
     * @param location2 Corner #2.
     */
    public boolean verifyCanCreate(TSLocation location1, TSLocation location2)
    {
        for (Zone zone : zones)
        {
            TSLocation location3 = zone.getStartLocation();
            TSLocation location4 = zone.getEndLocation();
            if (zone.getStartLocation().getWorldUID() != location1.getWorldUID())
            {
                continue;
            }

            // Notice that I don't check for the Y coordinate.
            // Well, this is because I don't want people claiming
            // underneath/above others' zones.

            if (location3.getX() > location4.getX())
            {
                int tempx = location3.getX();

                location3.setX(location4.getX());
                location4.setX(tempx);
            }

            if (location3.getZ() < location4.getZ())
            {
                int tempz = location4.getZ();
                location4.setZ(location3.getZ());
                location3.setZ(tempz);
            }

            if (zonesOverlap(location1, location2, location3, location4))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if two zones (zone1: location1, location2; zone2: location3, location4) overlap.
     * Assumes that loc1 = (minX,maxZ), loc2 = (maxX, minZ) Same for loc3 and loc4 respectively
     *
     * @param location1 Zone1 corner1
     * @param location2 Zone1 corner2
     * @param location3 Zone2 corner1
     * @param location4 Zone2 corner2
     * @return Whether or not they overlap.
     */
    public boolean zonesOverlap(TSLocation location1, TSLocation location2, TSLocation location3, TSLocation location4)
    {
        // If one rectangle is beside the other
        if (location1.getX() > location4.getX() || location3.getX() > location2.getX())
        {
            return false; // It cannot overlap.
        }

        // If one rectangle is above other
        if (location1.getZ() < location4.getZ() || location3.getZ() < location2.getZ())
        {
            return false; // It cannot overlap.
        }

        return true;
    }

    public boolean isPointInZone(Zone zone, TSLocation input)
    {
        TSLocation location = input.clone();

        TSLocation upper = zone.getStartLocation().clone(); // X1<X2, Z1>Z2
        TSLocation lower = zone.getEndLocation().clone(); // X2>X1, Z2<Z1

        return location.getX() >= upper.getX() && location.getX() <= lower.getX() &&
                location.getZ() <= upper.getZ() && location.getZ() >= lower.getZ();

    }

    public void loadZonesFromCollection(MongoCollection<Document> zoneDocs)
    {
        List<Zone> zones = new ArrayList<>();
        for (Document zoneDoc : zoneDocs.find().into(new ArrayList<Document>()))
        {
            zones.add(loadZoneFromCollection(zoneDoc));
        }

        this.zones.addAll(zones);
    }

    public Zone loadZoneFromCollection(Document zoneDoc)
    {
        String name = zoneDoc.getString("name");

        UUID uuid = zoneDoc.get("uuid", UUID.class);

        Document startLocationMap = zoneDoc.get("startLocation", Document.class);
        Document endLocationMap = zoneDoc.get("endLocation", Document.class);
        Document members = zoneDoc.get("members", Document.class);
        Document zoneFlags = zoneDoc.get("flags", Document.class);

        TSLocation startLocation = new TSLocation(startLocationMap.get("worldUID", UUID.class),
                (int) startLocationMap.get("x"),
                (int) startLocationMap.get("y"),
                (int) startLocationMap.get("z"));

        TSLocation endLocation = new TSLocation(endLocationMap.get("worldUID", UUID.class),
                (int) endLocationMap.get("x"),
                (int) endLocationMap.get("y"),
                (int) endLocationMap.get("z"));

        Zone zone = new Zone(uuid, startLocation, endLocation);
        zone.setName(name);

        TSPlayerHandler playerHandler = plugin.getTSPlayerHandler();

        for (Object entry : members.entrySet())
        {
            Map.Entry mapEntry = (Map.Entry) entry;

            UUID memberId = (UUID.fromString((String) mapEntry.getKey()));
            //plugin.getLogger().info("Loading entries from mongo! UUID: " + memberId.toString());
            ZoneRole role = ZoneRole.valueOf((String) mapEntry.getValue());

            TSPlayer player = new TSPlayer(memberId);
            playerHandler.addTSPlayer(player);

            TSZoneMember zoneMember = new TSZoneMember(player, zone);
            //plugin.getLogger().info("Created new TSPlayer with TSZoneMember " + memberId.toString() + " role " + role.name());

            zoneMember.setRole(role);
            zone.addZoneMember(zoneMember);
        }

        ZoneFlagSet set = zone.getZoneFlagSet();

        for (Object entry : zoneFlags.entrySet())
        {
            Map.Entry mapEntry = (Map.Entry) entry;

            String flagName = (String) mapEntry.getKey();
            String serialized = (String) mapEntry.getValue();

            String[] split = serialized.split(",");

            boolean playerFlag = Boolean.parseBoolean(split[0]);
            boolean memberFlag = Boolean.parseBoolean(split[1]);
            boolean adminFlag = Boolean.parseBoolean(split[2]);

            ZoneFlagSet.ZoneFlag flag = set.getZoneFlagByName(flagName);
            flag.setAllPlayers(playerFlag);
            flag.setMembers(memberFlag);
            flag.setAdmins(adminFlag);
        }

        return zone;
    }

    public void saveZoneToCollection(MongoCollection<Document> zones, Zone zone, long time)
    {
        Document serialized = zone.serialize();
        serialized.put("time", time);

        // Insert where uuid is the same as the zone's uuid.
        Document query = new Document();
        query.put("uuid", zone.getUUID());

        UpdateOptions updateOptions = new UpdateOptions();
        updateOptions.upsert(true);

        zones.replaceOne(query, serialized, updateOptions);
    }

    public Zone getZoneByTSPlayerAndName(TSPlayer tsPlayer, String name)
    {
        for (Zone search : getZonesByTSPlayer(tsPlayer, true))
        {
            if (search.getName().startsWith(name))
            {
                return search;
            }
        }

        return null;
    }
}
