package com.crimsoncrips.alexsmobsinteraction.compat;

import crystalspider.soulfired.api.FireManager;
import net.minecraft.world.entity.Entity;

public class SoulFiredCompat {
    public static void setOnFire(Entity entity, int duration)
    {
        FireManager.setOnFire(entity, duration, FireManager.SOUL_FIRE_TYPE);
    }
}