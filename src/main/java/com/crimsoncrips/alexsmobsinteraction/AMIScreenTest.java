package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMISkelewagInterface;
import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AMIScreenTest extends Screen {

    protected AMIScreenTest(Component pTitle) {
        super(pTitle);
    }
}