package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AMIHarvestCarrots extends MoveToBlockGoal {
    private final EntityBunfungus bunfungus;

    public AMIHarvestCarrots(EntityBunfungus bunfungus) {
        super(bunfungus, 0.7F, 16);
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
            if (level.getBlockState(blockPos).getBlock() instanceof CarrotBlock) {
                int i = level.getBlockState(blockPos).getValue(CarrotBlock.AGE);
                level.destroyBlock(blockPos, true, bunfungus);
                bunfungus.heal(i);
                stop();
            }
            this.nextStartTick = 10;
        }

    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos).getBlock() instanceof CarrotBlock;
    }

    @Override
    public double acceptedDistance() {
        return 2.2D;
    }

    public boolean canHarvest(EntityBunfungus bunfungus){
        return bunfungus.level().isDay() && !bunfungus.isSleeping();
    }
}