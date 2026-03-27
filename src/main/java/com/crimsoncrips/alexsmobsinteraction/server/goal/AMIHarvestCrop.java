package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.CropBlock;

public class AMIHarvestCrop extends MoveToBlockGoal {
    private final EntityBunfungus bunfungus;

    public AMIHarvestCrop(EntityBunfungus bunfungus) {
        super(bunfungus, 0.7F, 8);
        this.bunfungus = bunfungus;
    }

    public boolean canUse() {
        if (this.nextStartTick <= 0) {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(bunfungus.level(), bunfungus)) {
                return false;
            }

        }

        return super.canUse() && canHarvest(bunfungus);
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && canHarvest(bunfungus);
    }


    public void tick() {
        super.tick();

        if (this.isReachedTarget()){
            Level level = bunfungus.level();
            if (level.getBlockState(blockPos).getBlock() instanceof CropBlock) {
                AMIUtils.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER,bunfungus,8,0.4);
                level.destroyBlock(blockPos, true, bunfungus);
                bunfungus.heal(2);
                stop();
            }
            this.nextStartTick = 10;
        }

    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos).getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(pLevel.getBlockState(pPos));
    }

    @Override
    public double acceptedDistance() {
        return 2.2D;
    }

    public boolean canHarvest(EntityBunfungus bunfungus){
        return bunfungus.level().isDay() && !bunfungus.isSleeping();
    }
}