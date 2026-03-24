package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bone_serpent;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBasicInterfaces;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.BonePartInterface;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.ChildnParent_Interface;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityBoneSerpent.class)
public abstract class AMIBoneSerpent extends Monster implements ChildnParent_Interface {


    protected AMIBoneSerpent(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityBoneSerpent boneSerpent = (EntityBoneSerpent)(Object)this;
        if(AlexsMobsInteraction.TARGETS_CONFIG.BONE_SERPENT_ENABLED.get()) {
            boneSerpent.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(boneSerpent, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.NETHER_KILL)));
        }

    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityBoneSerpentPart;setInitialPartPos(Lnet/minecraft/world/entity/Entity;)V"))
    private void alexsMobsInteraction$tick(CallbackInfo ci, @Local LivingEntity partParent,@Local EntityBoneSerpentPart part) {
        if (partParent instanceof EntityBoneSerpentPart part1){
            ((BonePartInterface)part1).setChildUUID(part.getUUID());
        }
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        float damage = p_21017_;
        EntityBoneSerpent boneSerpent = (EntityBoneSerpent)(Object)this;
        if (boneSerpent.getChild() instanceof EntityBoneSerpentPart e1 && AlexsMobsInteraction.COMMON_CONFIG.BODY_SHIELDING_ENABLED.get()) {
            if (((BonePartInterface)e1).getChild() instanceof EntityBoneSerpentPart e2 && !e2.isTail()){
                damage = 0;
                ((BonePartInterface)e1).detectChildLoop();
                this.playSound(SoundEvents.WITHER_BREAK_BLOCK, 0.2f, this.getVoicePitch());
            }
        }
        return super.hurt(p_21016_, damage);
    }

}
