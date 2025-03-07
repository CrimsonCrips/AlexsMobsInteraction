package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIFarseerEffects;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityFarseer.class)
public class AMIFarseer extends Mob {


    protected AMIFarseer(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityFarseer farseer = (EntityFarseer)(Object)this;
        if(AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()){
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Raider.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Villager.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, WanderingTrader.class, 3, false, true, null));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (getTarget() instanceof Player player && ((AMIFarseerEffects)player).getFarseerTime() == 0){
            ((AMIFarseerEffects)player).setFarseerTime(100);
            AMIUtils.awardAdvancement(player,"alterred","alter");
        }
    }


}
