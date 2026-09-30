package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bone_serpent;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.BonePartInterface;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.ChildnParent_Interface;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.*;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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



    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityBoneSerpentPart;setInitialPartPos(Lnet/minecraft/world/entity/Entity;)V"))
    private void alexsMobsInteraction$tick(CallbackInfo ci, @Local LivingEntity partParent,@Local EntityBoneSerpentPart part) {
        if (partParent instanceof EntityBoneSerpentPart part1){
            AMIAttachments.setUUID(part1,AMIAttachments.CHILD_UUID,part.getUUID());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        EntityBoneSerpent boneSerpent = (EntityBoneSerpent)(Object)this;
        boolean shielded = AlexsMobsInteraction.COMMON_CONFIG.BODY_SHIELDING_ENABLED.get() && amount > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && boneSerpent.getChild() instanceof EntityBoneSerpentPart e1 && ((BonePartInterface)e1).getChild() instanceof EntityBoneSerpentPart e2 && !e2.isTail();
        boolean hurt = super.hurt(source, shielded ? 0 : amount);
        if (shielded && hurt && boneSerpent.getChild() instanceof BonePartInterface firstPart) {
            firstPart.detectChildLoop();
            this.playSound(SoundEvents.WITHER_BREAK_BLOCK, 0.2f, this.getVoicePitch());
        }
        return hurt;
    }

}
