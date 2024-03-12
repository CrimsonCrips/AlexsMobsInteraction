package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityBlobfish.class)
public class AIBlobFish extends Mob {

    protected AIBlobFish(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlobFishGoals(CallbackInfo ci){
       if(AInteractionConfig.preyfear) {
            this.goalSelector.addGoal(3, new AvoidEntityGoal((EntityBlobfish) (Object) this, Player.class, 5.0F, 1.2, 1.4));
            this.goalSelector.addGoal(3, new AvoidEntityGoal((EntityBlobfish)(Object)this, EntityGiantSquid.class, 5.0F, 1.2, 1.4));
        }
    }
}
