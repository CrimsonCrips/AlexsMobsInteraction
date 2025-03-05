package com.crimsoncrips.alexsmobsinteraction.misc.interfaces;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface AsmonRoach {


    boolean isGod();

    Entity getWorshiping();

    void setWorshippingUUID(@Nullable UUID uniqueId);

}
