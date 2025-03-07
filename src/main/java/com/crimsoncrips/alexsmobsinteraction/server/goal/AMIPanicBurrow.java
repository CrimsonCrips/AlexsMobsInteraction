package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.Tags;

import java.util.EnumSet;

public class AMIPanicBurrow extends PanicGoal {

    protected final EntityRainFrog mob;



    public AMIPanicBurrow(EntityRainFrog pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
        mob = pMob;
    }

    @Override
    public void start() {
        super.start();
        AMIUtils.awardAdvancement(mob.getLastHurtByMob(),"burrow_away","burrow");
    }

    @Override
    public void stop() {
        super.stop();
        if (!isRunning && !this.shouldPanic() && mob.getBlockStateOn().is(Blocks.SAND)) {
            mob.setBurrowed(true);
        }
    }
}