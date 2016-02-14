package com.colonelhedgehog.terrashield.components.zone;

import java.util.ArrayList;
import java.util.List;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class ZoneFlagSet
{
    private List<ZoneFlag> zoneFlags;

    public ZoneFlagSet()
    {
        zoneFlags = new ArrayList<>();
        zoneFlags.add(new ZoneFlag("pvp", false, false, true));
        zoneFlags.add(new ZoneFlag("damage", false, false, true));
        zoneFlags.add(new ZoneFlag("edit", false, true, true));
        zoneFlags.add(new ZoneFlag("explosion", false, false, false));
        zoneFlags.add(new ZoneFlag("enter", true, true, true));
        zoneFlags.add(new ZoneFlag("endpearl", true, true, true));
        zoneFlags.add(new ZoneFlag("liquid_flow", true, true, true));
        zoneFlags.add(new ZoneFlag("interact", true, true, true));
        zoneFlags.add(new ZoneFlag("speak", true, true, true));
        zoneFlags.add(new ZoneFlag("drop", true, true, true));

    }

    public ZoneFlag getZoneFlagByName(String name)
    {
        for(ZoneFlag permission : zoneFlags)
        {
            if(permission.getName().equalsIgnoreCase(name))
            {
                return permission;
            }
        }

        return null;
    }

    public List<ZoneFlag> getZoneFlags()
    {
        return zoneFlags;
    }

    public class ZoneFlag
    {
        private String name;
        private boolean allPlayers;
        private boolean members;
        private boolean admins;

        public ZoneFlag(String name, boolean allPlayers, boolean members, boolean admins)
        {
            this.name = name;
            this.allPlayers = allPlayers;
            this.members = members;
            this.admins = admins;
        }

        public boolean isAdmins()
        {
            return admins;
        }

        public boolean isMembers()
        {
            return members;
        }

        public boolean isAllPlayers()
        {
            return allPlayers;
        }

        public void setAllPlayers(boolean allPlayers)
        {
            this.allPlayers = allPlayers;
        }

        public void setMembers(boolean members)
        {
            this.members = members;
        }

        public void setAdmins(boolean admins)
        {
            this.admins = admins;
        }

        public String getName()
        {
            return name;
        }
    }
}
