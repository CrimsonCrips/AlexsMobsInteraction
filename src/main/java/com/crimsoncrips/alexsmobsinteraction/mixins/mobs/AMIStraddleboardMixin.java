package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.ManualLootUtil;
import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;


@Mixin(EntityStraddleboard.class)
public abstract class AMIStraddleboardMixin extends Entity implements PlayerRideableJumping {


    public AMIStraddleboardMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityStraddleboard;discard()V"), remap = false)
    private void registerGoals(CallbackInfo ci, @Local boolean drop) {
       if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLE_SCAVENGE_ENABLED.get() && !drop && this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(0.25, -2, 0.25)).iterator().hasNext()){
           Iterator<LivingEntity> var4 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(0.25, -2, 0.25)).iterator();

           ManualLootUtil.spawnLoot(AMILootTables.OBSIDIAN_EXTRACT,var4.next(),var4.next(),0);
       }
    }

}
