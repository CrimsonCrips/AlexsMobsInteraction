package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.FollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityHummingbird.class)
public class AIHummingBird extends Mob {

    public boolean noFollow = false;

    protected AIHummingBird(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void HummingbirdGoals(CallbackInfo ci){
        ci.cancel();
        EntityHummingbird hummingbird = (EntityHummingbird)(Object)this;
        goalSelector.addGoal(1, new BreedGoal(hummingbird, 1));
        goalSelector.addGoal(2, new TemptGoal(hummingbird, 1, Ingredient.of(ItemTags.FLOWERS), false));
        goalSelector.addGoal(3, new FollowParentGoal(hummingbird, 1));
        Object aiUseFeeder = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityHummingbird$AIUseFeeder",
                new Class[] {EntityHummingbird.class,EntityHummingbird.class},
                new Object[] {hummingbird,hummingbird}
        );
        goalSelector.addGoal(4, (Goal)aiUseFeeder);
        goalSelector.addGoal(4, new HummingbirdAIPollinate(hummingbird){
            public boolean canUse() {
                if(AInteractionConfig.hummingbirdpolinate) {
                    return super.canUse() && level().isDay();
                } else {
                    return super.canUse();
                }
            }
        });
        goalSelector.addGoal(5, new HummingbirdAIWander(hummingbird, 16, 6, 15, 1));
        goalSelector.addGoal(6, new FloatGoal(this));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(hummingbird, LivingEntity.class, 7.0F, 1.2, 1.4,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.HUMMINGBIRDFEAR)));
        if(AInteractionConfig.hummingfollowflutter) {
            goalSelector.addGoal(8, new FollowNearestGoal(hummingbird, EntityFlutter.class, 10, 1.2, (entity) -> true) {
                public boolean canUse() {
                    return !noFollow;
                }
            });
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void Tick(CallbackInfo ci) {
        if(AInteractionConfig.hummingfollowflutter){
            if (random.nextDouble() < 0.001 && !noFollow || level().isNight()) noFollow = true;
            if (random.nextDouble() < 0.005 && noFollow && level().isDay()) noFollow = false;
        }
    }

}
