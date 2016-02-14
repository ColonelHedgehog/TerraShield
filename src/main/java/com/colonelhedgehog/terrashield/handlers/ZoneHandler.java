package com.colonelhedgehog.terrashield.handlers;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneRole;
import com.colonelhedgehog.terrashield.core.TerraShield;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        if(itemStack == null)
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

        for(Zone zone : zones)
        {
            ZoneRole role = zone.getZoneRole(tsPlayer);

            if(role != null)
            {
                if(role == ZoneRole.OWNER)
                {
                    zones.add(zone);
                }
            }
        }

        return zones;
    }
}
