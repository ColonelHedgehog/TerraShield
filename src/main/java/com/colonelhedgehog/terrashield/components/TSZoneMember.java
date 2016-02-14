package com.colonelhedgehog.terrashield.components;

import com.colonelhedgehog.terrashield.components.zone.Zone;
import com.colonelhedgehog.terrashield.components.zone.ZoneRole;

/**
 * TerraShield
 * Created by ColonelHedgehog on 2/14/16.
 */
public class TSZoneMember
{
    private Zone zone;
    private ZoneRole role;
    private TSPlayer player;

    public TSZoneMember(TSPlayer player)
    {
        this.player = player;
    }

    public TSPlayer getPlayer()
    {
        return player;
    }

    public ZoneRole getRole()
    {
        return role;
    }

    public void setRole(ZoneRole role)
    {
        this.role = role;
    }

    public Zone getZone()
    {
        return zone;
    }
}
