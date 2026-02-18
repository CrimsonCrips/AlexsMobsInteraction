package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
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
public abstract class AMIBoneSerpent extends Monster {


    protected AMIBoneSerpent(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityBoneSerpent boneSerpent = (EntityBoneSerpent)(Object)this;
        if(AlexsMobsInteraction.COMMON_CONFIG.SERPENT_FEAR_ENABLED.get()) {
            boneSerpent.goalSelector.addGoal(7, new AvoidEntityGoal<>(boneSerpent, EntityLaviathan.class, 10.0F, 1.6, 2));
        }
        if(AlexsMobsInteraction.TARGETS_CONFIG.BONE_SERPENT_ENABLED.get()) {
            boneSerpent.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(boneSerpent, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.NETHER_KILL)));
        }

    }

}
