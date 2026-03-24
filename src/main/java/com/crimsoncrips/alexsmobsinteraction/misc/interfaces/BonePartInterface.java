package com.crimsoncrips.alexsmobsinteraction.misc.interfaces;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface BonePartInterface extends ChildnParent_Interface {

    void detectChildLoop();
}
