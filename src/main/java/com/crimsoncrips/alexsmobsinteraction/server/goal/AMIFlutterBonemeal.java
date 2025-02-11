package com.crimsoncrips.alexsmobsinteraction.server.goal;

import java.util.EnumSet;

import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class AMIFlutterBonemeal extends Goal {

    protected final EntityFlutter mob;
    protected int nextStartTick;
    protected int tryTicks;
    private int maxStayTicks;
    protected BlockPos blockPos = BlockPos.ZERO;


    public AMIFlutterBonemeal(EntityFlutter pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    public boolean canUse() {
        boolean bool = false;
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
        } else if (mob.isTame()) {
            this.nextStartTick = this.nextStartTick(this.mob);
            bool = this.findNearestBlock();
        }
        return bool;
    }

    protected int nextStartTick(PathfinderMob pCreature) {
        return reducedTickDelay(200 + pCreature.getRandom().nextInt(200));
    }


    public boolean canContinueToUse() {
        return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= 1200 && this.isValidTarget(this.mob.level(), this.blockPos);
    }

    public void start() {
        this.tryTicks = 0;
        this.maxStayTicks = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        super.tick();
        spitToBlock(mob.blockPosition().above(10));

    }

    protected boolean findNearestBlock() {
        BlockPos blockpos = this.mob.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int k = 0; k <= 5; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < 14; ++l) {
                for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, i1, k - 1, j1);
                        if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidTarget(this.mob.level(), blockpos$mutableblockpos)) {
                            this.blockPos = blockpos$mutableblockpos;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos){
       return pLevel.getBlockState(pPos).getBlock() instanceof BonemealableBlock;
    };

    private void spitToBlock(BlockPos blockPos) {
        EntityPollenBall pollenBall = new EntityPollenBall(mob.level(), mob);
        pollenBall.shoot(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.5F, 13.0F);
        if (!mob.isSilent()) {
            mob.gameEvent(GameEvent.PROJECTILE_SHOOT);
            mob.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.LLAMA_SPIT, mob.getSoundSource(), 1.0F, 1.0F + (mob.getRandom().nextFloat() - mob.getRandom().nextFloat()) * 0.2F);
        }
        mob.level().addFreshEntity(pollenBall);
    }
}