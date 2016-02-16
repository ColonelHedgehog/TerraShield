package com.colonelhedgehog.terrashield.components.zone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/13/16.
 */
public class ZoneFlagSet
{
    private HashMap<String, ZoneFlag> zoneFlags;

    public ZoneFlagSet()
    {
        zoneFlags = new HashMap<>();

        zoneFlags.put("pvp", new ZoneFlag("pvp", false, false, true));
        zoneFlags.put("damage", new ZoneFlag("damage", false, false, true));
        zoneFlags.put("edit", new ZoneFlag("edit", false, true, true));
        zoneFlags.put("explosion", new ZoneFlag("explosion", false, false, false));
        zoneFlags.put("enter", new ZoneFlag("enter", true, true, true));
        zoneFlags.put("endpearl", new ZoneFlag("endpearl", true, true, true));
        zoneFlags.put("liquid_flow", new ZoneFlag("liquid_flow", true, true, true));
        zoneFlags.put("interact", new ZoneFlag("interact", true, true, true));
        zoneFlags.put("speak", new ZoneFlag("speak", true, true, true));
        zoneFlags.put("drop", new ZoneFlag("drop", true, true, true));

    }

    public Set<String> getZoneFlagNames()
    {
        return zoneFlags.keySet();
    }

    public ZoneFlag getZoneFlagByName(String name)
    {
        if(zoneFlags.containsKey(name.toLowerCase()))
        {
            return zoneFlags.get(name);
        }

        return null;
    }

    public Collection<ZoneFlag> getZoneFlags()
    {
        return zoneFlags.values();
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

        public void set(ZoneRole role, boolean setting)
        {
            if (role == ZoneRole.OWNER || role == ZoneRole.ADMIN)
            {
                setAdmins(setting);
            }
            else if (role == ZoneRole.MEMBER)
            {
                setMembers(setting);
            }
            else if (role == ZoneRole.ALL)
            {
                setAllPlayers(setting);
            }
        }

        public boolean getForRole(ZoneRole role)
        {
            if(role == null || role == ZoneRole.ALL)
            {
                return isAllPlayers();
            }
            else if(role == ZoneRole.OWNER || role == ZoneRole.ADMIN)
            {
                return isAdmins();
            }
            else if(role == ZoneRole.MEMBER)
            {
                return isMembers();
            }

            return false;
        }
    }
}
