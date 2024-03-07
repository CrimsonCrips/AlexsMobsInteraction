package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AITargetFood;
import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;


@Mixin(EntityCatfish.class)
public abstract class AICatfish extends Mob {


    protected AICatfish(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void CatfishGoals(CallbackInfo ci) {
        ci.cancel();
        EntityCatfish catfish = (EntityCatfish) (Object) this;
        this.goalSelector.addGoal(1, new TryFindWaterGoal(catfish));
        this.goalSelector.addGoal(2, new PanicGoal(catfish, 1D));
        this.goalSelector.addGoal(3, new AITargetFood(catfish));
        this.goalSelector.addGoal(4, new TemptGoal(catfish, 1.0D, Ingredient.of(Items.SEA_LANTERN), false));
        Object aiFascinateLanternGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityCatfish$FascinateLanternGoal",
                new Class[]{EntityCatfish.class, EntityCatfish.class},
                new Object[]{catfish, this}
        );
        this.goalSelector.addGoal(2, (Goal) aiFascinateLanternGoal);
        this.goalSelector.addGoal(6, new AnimalAISwimBottom(catfish, 1F, 7));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>((EntityCatfish) (Object) this, LivingEntity.class, 4.0F, 1.1, 1.3, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FISHFEAR)));

    }



}

