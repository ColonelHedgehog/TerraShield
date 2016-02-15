package com.colonelhedgehog.terrashield.handlers;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneRole;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class ZoneHandler
{
    private TerraShield plugin;
    private List<Zone> zones;

    public ZoneHandler(TerraShield plugin)
    {
        this.plugin = plugin;
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

        if (!itemStack.hasItemMeta())
        {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasDisplayName())
        {
            return false;
        }

        if (!itemMeta.getDisplayName().equals("§6§lZone Tool"))
        {
            return false;
        }

        FileConfiguration config = plugin.getConfig();

        String materialString = config.getString("Settings.Zone Marker Tool.Material");
        Material material = Material.getMaterial(materialString);

        if (material != itemStack.getType())
        {
            return false;
        }

        short data = (short) config.getInt("Settings.Zone Marker Tool.Byte");

        if (data != itemStack.getDurability())
        {
            return false;
        }

        return true;
    }

    public ItemStack getZoneMarkerTool()
    {
        FileConfiguration config = plugin.getConfig();

        String materialString = config.getString("Settings.Zone Marker Tool.Material");
        Material material = Material.getMaterial(materialString);
        short data = (short) config.getInt("Settings.Zone Marker Tool.Byte");

        ItemStack itemStack = new ItemStack(material, 0, data);

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

    public List<Zone> getZonesByTSPlayer(TSPlayer tsPlayer)
    {
        List<Zone> zones = new ArrayList<>();

        for (Zone zone : zones)
        {
            ZoneRole role = zone.getZoneRole(tsPlayer);

            if (role != null)
            {
                if (role == ZoneRole.OWNER)
                {
                    zones.add(zone);
                }
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

            if (zonesOverlap(location1, location2, location3, location4))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if two zones (zone1: location1, location2; zone2: location3, location4) overlap.
     * @param location1 Zone1 corner1
     * @param location2 Zone1 corner2
     * @param location3 Zone2 corner1
     * @param location4 Zone2 corner2
     * @return Whether or not they overlap.
     */
    public boolean zonesOverlap(TSLocation location1, TSLocation location2, TSLocation location3, TSLocation location4)
    {
        if (location2.getZ() == location3.getZ() && location1.getZ() == location4.getZ() && location2.getX() == location3.getX() && location1.getX() == location4.getX())
        {
            return false;
        }

        return true;
    }

    public boolean isPointInZone(Zone zone, TSLocation input)
    {
        TSLocation location = input.clone();
        location.setY(0);

        TSLocation upper1 = zone.getStartLocation().clone();
        TSLocation upper2 = upper1.clone();
        upper1.setY(0);
        upper2.setY(0);

        TSLocation lower1 = zone.getEndLocation().clone();
        TSLocation lower2 = lower1.clone();
        lower1.setY(0);
        lower2.setY(0);

        upper2.setX(upper1.getZ());
        lower2.setZ(upper1.getX());


        Vector vector1 = new Vector(upper1.getX(), 0, upper1.getZ());
        Vector vector2 = new Vector(upper2.getX(), 0, upper2.getZ());
        Vector vector3 = new Vector(lower1.getX(), 0, lower1.getZ());
        Vector vector4 = new Vector(lower2.getX(), 0, lower2.getZ());
        Vector point = new Vector(location.getX(), 0, location.getZ());

        Vector vec1Minus4 = vector1.subtract(vector4);
        Vector vec3Minus4 = vector3.subtract(vector4);
        Vector centralVector = point.multiply(2).subtract(vector1).subtract(vector3);    // TWO_P_C=2P-C, C=Center of rectangle

        return (vec3Minus4.dot(centralVector.subtract(vec3Minus4)) <= 0 && vec3Minus4.dot(centralVector.add(vec3Minus4)) >= 0) &&
                (vec1Minus4.dot(centralVector.subtract(vec1Minus4)) <= 0 && vec1Minus4.dot(centralVector.add(vec1Minus4)) >= 0);

    }

}
