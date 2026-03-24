package com.crimsoncrips.alexsmobsinteraction.misc.interfaces;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public interface ChildnParent_Interface {

    UUID getChildUUID();

    void setChildUUID(UUID id);

    UUID getParentUUID();

    void setParentUUID(UUID id);

    Entity getChild();

}
