package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCockroach.class)
public class AICockroach extends Mob {

    protected AICockroach(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CentipedeGoals(CallbackInfo ci){
        EntityCockroach cockroach = (EntityCockroach)(Object)this;
        if(AInteractionConfig.skittishcockroaches) {
            this.goalSelector.addGoal(5, new AvoidEntityGoal<>(cockroach, LivingEntity.class, 2.0F, 1.5, 2, (livingEntity) -> {
                return !(livingEntity instanceof EntityCockroach);
            }));
        } else {
            this.goalSelector.addGoal(4, new AvoidEntityGoal<>(cockroach, EntityCentipedeHead.class, 16, 1.3D, 1.0D));
            this.goalSelector.addGoal(4, new AvoidEntityGoal<>(cockroach, Player.class, 8, 1.3D, 1.0D) {
                public boolean canUse() {
                    return !cockroach.isBreaded() && super.canUse();
                }
            });
        }
        this.goalSelector.addGoal(3, new AvoidBlockGoal(cockroach, 4,1,1.2,(pos) -> {
            BlockState state = level().getBlockState(pos);
            return state.is(AInteractionTagRegistry.CENTIPEDE_BLOCK_FEAR);
        }));
    }
}
