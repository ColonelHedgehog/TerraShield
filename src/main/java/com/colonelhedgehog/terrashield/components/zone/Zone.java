package com.colonelhedgehog.terrashield.components.zone;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.TSZoneMember;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * TerraShield
 * Uses TSLocations for speed. No reliance on the BukkitAPI.
 * Created by ColonelHedgehog on 2/13/16.
 */
public class Zone implements Iterable<TSLocation>
{
    private TSLocation start;
    private TSLocation end;
    private ZoneFlagSet zoneFlagSet;
    private List<TSZoneMember> zoneMembers;
    private List<TSPlayer> bannedPlayers;


    /**
     * Converts two locations to the light, easy-to-use TSLocation
     *
     * @param start
     * @param end
     */

    public Zone(Location start, Location end)
    {
        // the this() call must be the first statement. :(
        this(new TSLocation(start.getBlockX(), start.getBlockY(), start.getBlockZ()),
                new TSLocation(end.getBlockX(), end.getBlockY(), end.getBlockZ()));
    }

    public Zone(TSLocation start, TSLocation end)
    {
        this.start = start;
        this.end = end;
        this.zoneFlagSet = new ZoneFlagSet();
        this.zoneMembers = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();
    }

    /**
     * Converts serialized information into a zone.
     *
     * @param map The information map.
     */
    public Zone(HashMap<String, Object> map)
    {
        // This is an "endpoint" so no need to worry about synchronization.
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // Todo this.
            }
        }.runTaskAsynchronously(TerraShield.getInstance());
    }

    public HashMap<String, HashMap<String, Object>> serialize()
    {
        HashMap<String, HashMap<String, Object>> map = new HashMap<>();

        HashMap<String, Object> startMap = new HashMap<>();
        startMap.put("x", start.getX());
        startMap.put("y", start.getY());
        startMap.put("z", start.getZ());

        HashMap<String, Object> endMap = new HashMap<>();
        endMap.put("x", end.getX());
        endMap.put("y", end.getY());
        endMap.put("z", end.getZ());

        HashMap<String, Object> membersWithRoles = new HashMap<>();

        for (TSZoneMember member : zoneMembers)
        {
            membersWithRoles.put(member.getPlayer().getUUID().toString(), member.getRole().name());
        }

        HashMap<String, Object> zoneFlags = new HashMap<>();

        for (ZoneFlagSet.ZoneFlag permission : zoneFlagSet.getZoneFlags())
        {
            zoneFlags.put(permission.getName(), permission.isAllPlayers() + "," + permission.isMembers() + "," + permission.isAdmins());
        }


        map.put("startLocation", startMap);
        map.put("endLocation", endMap);
        map.put("members", membersWithRoles);
        map.put("flags", zoneFlags);

        return map;
    }

    public TSLocation getStartLocation()
    {
        return start;
    }

    public TSLocation getEndLocation()
    {
        return end;
    }

    public List<TSLocation> getTSLocations()
    {
        Iterator<TSLocation> iterator = this.iterator();
        List<TSLocation> tsLocations = new ArrayList<>();

        while (iterator.hasNext())
        {
            tsLocations.add(iterator.next());
        }

        return tsLocations;
    }

    @Override
    public Iterator<TSLocation> iterator()
    {
        int x1 = start.getX();
        int x2 = end.getX();
        int y1 = start.getX();
        int y2 = end.getX();
        int z1 = start.getZ();
        int z2 = end.getZ();

        return new ZoneIterator(x1, y1, z1, x2, y2, z2);
    }

    public boolean hasAccessToFlag(TSZoneMember member, String flag)
    {
        if (member.getRole() == ZoneRole.OWNER)
        {
            return true; // Can do no matter what.
        }

        ZoneFlagSet.ZoneFlag zoneFlag = zoneFlagSet.getZoneFlagByName(flag);

        if (zoneFlag == null)
        {
            return false;
        }

        if (member.getRole() == ZoneRole.ADMIN)
        {
            if (zoneFlag.isAdmins())
            {
                return true;
            }
        }
        else if (member.getRole() == ZoneRole.MEMBER)
        {
            if (zoneFlag.isMembers())
            {
                return true;
            }
        }

        return false;
    }

    public TSZoneMember getZoneMemberByTSPlayer(TSPlayer tsPlayer)
    {
        for (TSZoneMember member : zoneMembers)
        {
            if (member.getPlayer().equals(tsPlayer))
            {
                return member;
            }
        }

        return null;
    }

    public ZoneRole getZoneRole(TSPlayer tsPlayer)
    {
        for (TSZoneMember member : zoneMembers)
        {
            if (tsPlayer.equals(member.getPlayer()))
            {
                return member.getRole();
            }
        }

        return null;
    }

    public List<TSZoneMember> getMembersWithRole(ZoneRole role)
    {
        List<TSZoneMember> tsPlayers = new ArrayList<>();

        for (TSZoneMember zoneMember : zoneMembers)
        {
            if (zoneMember.getRole() == role)
            {
                tsPlayers.add(zoneMember);
            }
        }

        return tsPlayers;
    }

    public List<TSPlayer> getBannedPlayers()
    {
        return bannedPlayers;
    }

    private class ZoneIterator implements Iterator<TSLocation>
    {
        private int baseX, baseY, baseZ;
        private int x, y, z;
        private int sizeX, sizeY, sizeZ;

        public ZoneIterator(int x1, int y1, int z1, int x2, int y2, int z2)
        {
            this.baseX = x1;
            this.baseY = y1;
            this.baseZ = z1;
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;

            this.x = 0;
            this.y = 0;
            this.z = 0;
        }


        public boolean hasNext()
        {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        public TSLocation next()
        {
            TSLocation tsLocation = new TSLocation(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);

            if (++x >= this.sizeX)
            {
                this.x = 0;

                if (++this.y >= this.sizeY)
                {
                    this.y = 0;
                    ++this.z;
                }
            }
            return tsLocation;
        }

        public void remove()
        {
            // No need to do anything here.
        }
    }
}
