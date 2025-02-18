package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.MobTargetItemGoal;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class AMIFungusBonemeal extends MoveToBlockGoal {

    EntityCrimsonMosquito crimsonMosquito;
    private boolean reachedTarget;

    public AMIFungusBonemeal(EntityCrimsonMosquito pMob, double pSpeedModifier, int pSearchRange) {
        super(pMob, pSpeedModifier, pSearchRange,6);
        crimsonMosquito = pMob;
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget(this.mob.level(), this.blockPos) && crimsonMosquito.getBloodLevel() > 0;
    }

    @Override
    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return this.findNearestBlock() && crimsonMosquito.isFlying() && crimsonMosquito.getBloodLevel() > 0;
        }
    }

    @Override
    public void start() {
        crimsonMosquito.getMoveControl().setWantedPosition(this.blockPos.getX() + 0.5F,this.blockPos.getY() + 1, this.blockPos.getZ() + 0.5D, this.speedModifier);

    }

    @Override
    public void tick() {
        BlockPos fungus = this.getMoveToTarget();
        reachedTarget = fungus.closerToCenterThan(crimsonMosquito.position(), this.acceptedDistance());

        Level level = crimsonMosquito.level();
        RandomSource randomSource = crimsonMosquito.getRandom();

        crimsonMosquito.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(blockPos));

        if (this.isReachedTarget() && level.getBlockState(blockPos).getBlock() instanceof FungusBlock fungusBlock) {
            if (fungusBlock.isBonemealSuccess(level,randomSource,blockPos,level.getBlockState(blockPos))) {
                crimsonMosquito.setBloodLevel(crimsonMosquito.getBloodLevel() - 1);
                fungusBlock.performBonemeal((ServerLevel) level,randomSource,blockPos,level.getBlockState(blockPos));
            }

            double d1 = randomSource.nextInt(-1,2);
            crimsonMosquito.setPos(crimsonMosquito.getX() + d1,crimsonMosquito.getY() + d1,crimsonMosquito.getZ() + d1);
            level.levelEvent(1505, blockPos, 15);
            this.stop();
        }

    }

    protected boolean isReachedTarget() {
        return this.reachedTarget;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos);
        return blockState.getBlock() instanceof FungusBlock;
    }

    public void stop() {
        super.stop();
        this.blockPos = BlockPos.ZERO;
    }

    public double acceptedDistance() {
        return 2F;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(200 + crimsonMosquito.getRandom().nextInt(400));
    }
}
