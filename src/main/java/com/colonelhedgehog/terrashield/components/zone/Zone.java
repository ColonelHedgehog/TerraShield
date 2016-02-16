package com.colonelhedgehog.terrashield.components.zone;

import com.colonelhedgehog.terrashield.components.TSPlayer;
import com.colonelhedgehog.terrashield.components.TSZoneMember;
import com.colonelhedgehog.terrashield.core.TerraShield;
import com.colonelhedgehog.terrashield.utils.TSLocation;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
    private String name;
    private UUID uuid;

    /**
     * Converts two locations to the light, easy-to-use TSLocation
     *
     * @param start
     * @param end
     */

    public Zone(UUID uuid, Location start, Location end)
    {
        // the this() call must be the first statement. :(
        this(uuid, new TSLocation(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ()),
                new TSLocation(end.getWorld(), end.getBlockX(), end.getBlockY(), end.getBlockZ()));
    }

    public Zone(UUID uuid, TSLocation start, TSLocation end)
    {
        this.start = start;
        this.end = end;
        this.zoneFlagSet = new ZoneFlagSet();
        this.zoneMembers = new ArrayList<>();
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;

        if (start.getX() > end.getX())
        {
            int tempx = start.getX();

            start.setX(end.getX());
            end.setX(tempx);
        }

        if (start.getZ() < end.getZ())
        {
            int tempz = end.getZ();
            end.setZ(start.getZ());
            start.setZ(tempz);
        }

        //boolean bool = start.getX() < end.getX() && start.getZ() > end.getZ();

        //Bukkit.broadcastMessage("ZONE CORNERS SET RIGHT? " + bool);
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

    public Document serialize()
    {
        Document serialized = new Document();

        Document startMap = new Document();
        startMap.put("worldUID", start.getWorldUID());
        startMap.put("x", start.getX());
        startMap.put("y", start.getY());
        startMap.put("z", start.getZ());

        Document endMap = new Document();
        endMap.put("worldUID", end.getWorldUID());
        endMap.put("x", end.getX());
        endMap.put("y", end.getY());
        endMap.put("z", end.getZ());

        Document membersWithRoles = new Document();

        for (TSZoneMember member : zoneMembers)
        {
            TSPlayer player = member.getPlayer();
            UUID uuid = player.getUUID();

            ZoneRole role = member.getRole();

            membersWithRoles.put(String.valueOf(uuid), role.name());
        }

        Document zoneFlags = new Document();

        for (ZoneFlagSet.ZoneFlag permission : zoneFlagSet.getZoneFlags())
        {
            zoneFlags.put(permission.getName(), permission.isAllPlayers() + "," + permission.isMembers() + "," + permission.isAdmins());
        }

        serialized.put("name", name);
        serialized.put("uuid", uuid);
        serialized.put("startLocation", startMap);
        serialized.put("endLocation", endMap);
        serialized.put("members", membersWithRoles);
        serialized.put("flags", zoneFlags);

        return serialized;
    }

    public String getName()
    {
        return name;
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

        return new ZoneIterator(start.getWorldUID(), x1, y1, z1, x2, y2, z2);
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

        return ZoneRole.ALL;
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

    public void setName(String name)
    {
        this.name = name;
    }

    public ZoneFlagSet getZoneFlagSet()
    {
        return zoneFlagSet;
    }

    public List<TSZoneMember> getZoneMembers()
    {
        return zoneMembers;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public void addZoneMember(TSZoneMember zoneMember)
    {
        zoneMembers.add(zoneMember);
    }

    public void addZoneMembers(List<TSZoneMember> members)
    {
        zoneMembers.addAll(members);
    }

    public void removeZoneMember(TSZoneMember zoneMember)
    {
        zoneMembers.remove(zoneMember);
    }

    public void removeZoneMembers(List<TSZoneMember> members)
    {
        zoneMembers.removeAll(members);
    }

    private class ZoneIterator implements Iterator<TSLocation>
    {
        private UUID worldUID;
        private int baseX, baseY, baseZ;
        private int x, y, z;
        private int sizeX, sizeY, sizeZ;

        public ZoneIterator(UUID worldUID, int x1, int y1, int z1, int x2, int y2, int z2)
        {
            this.worldUID = worldUID;
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
            TSLocation tsLocation = new TSLocation(this.worldUID, this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);

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
