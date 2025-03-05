package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIGrizzlyBearInterface;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AMIGrizzlyScavenge extends MoveToBlockGoal {

    EntityGrizzlyBear grizzlyBear;
    private boolean reachedTarget;

    public AMIGrizzlyScavenge(EntityGrizzlyBear pMob, double pSpeedModifier, int pSearchRange) {
        super(pMob, pSpeedModifier, pSearchRange);
        grizzlyBear = pMob;
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget(this.mob.level(), this.blockPos) && !grizzlyBear.isTame() && !grizzlyBear.isBaby() && !grizzlyBear.isHoneyed() && !grizzlyBear.isEating() && (((AMIGrizzlyBearInterface)grizzlyBear).getNoHoney() >= 10000 || !AlexsMobsInteraction.COMMON_CONFIG.HONEYLESS_HUNTING_ENABLED.get());
    }

    @Override
    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return this.findNearestBlock() && this.canContinueToUse();
        }
    }

    @Override
    public void tick() {
        BlockPos container = this.getMoveToTarget();
        reachedTarget = container.closerToCenterThan(grizzlyBear.position(), this.acceptedDistance());

        Level level = grizzlyBear.level();
        RandomSource randomSource = grizzlyBear.getRandom();

        grizzlyBear.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(blockPos));


        if (this.isReachedTarget() && grizzlyBear.getAnimation() == EntityGrizzlyBear.NO_ANIMATION) {
            if (randomSource.nextBoolean()){
                grizzlyBear.setAnimation(EntityGrizzlyBear.ANIMATION_SWIPE_L);
            } else {
                grizzlyBear.setAnimation(EntityGrizzlyBear.ANIMATION_SWIPE_R);
            }
        }

        if (this.isReachedTarget() && grizzlyBear.getAnimation() != EntityGrizzlyBear.NO_ANIMATION && grizzlyBear.getAnimationTick() == 8){
           if (randomSource.nextDouble() < 0.3 || grizzlyBear.hasEffect(MobEffects.DAMAGE_BOOST)) {
               level.destroyBlock(blockPos,true);
               this.stop();
           }
           grizzlyBear.playSound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 0.5f, 1);
        }

    }

    protected boolean isReachedTarget() {
        return this.reachedTarget;
    }

    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return chestHasFood(worldIn,pos);
    }

    public boolean chestHasFood(LevelReader levelReader, BlockPos pos) {
        if (levelReader.getBlockState(pos).getBlock() instanceof BaseEntityBlock) {
            BlockEntity entity = levelReader.getBlockEntity(pos);
            if (entity instanceof Container inventory) {
                try {
                    if (!inventory.isEmpty()) {
                        for (int i = 0; i < inventory.getContainerSize(); i++) {
                            if (inventory.getItem(i).is(AMTagRegistry.GRIZZLY_FOODSTUFFS)) {
                                return true;
                            }
                        }
                    }
                } catch (Exception e) {
                    AlexsCaves.LOGGER.warn("Alex's Caves stopped a " + entity.getClass().getSimpleName() + " from causing a crash during access");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void stop() {
        super.stop();
        this.blockPos = BlockPos.ZERO;
    }

    public double acceptedDistance() {
        return 2F;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(500 + grizzlyBear.getRandom().nextInt(200));
    }
}
